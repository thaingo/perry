import groovy.transform.Field
@Library('jenkins-pipeline-utils') _

@Field
def githubCredentialsId = '433ac100-b3c2-4519-b4d6-207c029a103b'
@Field
def deAnsibleGithubUrl = 'git@github.com:ca-cwds/de-ansible.git'

releasePipeline();

def releasePipeline() {
  try {
    deployWithSmoke('integration')
  } catch(Exception exception) {
    currentBuild.result = "FAILURE"
    throw exception
  }
}

def checkoutStage() {
  stage('Checkout') {
    deleteDir()
    checkout scm
   }
}

def smokeTestStage(environment) {
  stage('Smoke Tests') {
    def serverArti = Artifactory.newServer url: 'http://pr.dev.cwds.io/artifactory'
    def rtGradle = Artifactory.newGradleBuild()
    rtGradle.tool = "Gradle_35"
    rtGradle.resolver server: serverArti
    rtGradle.useWrapper = true
    def gradlePropsText = """
      perry.health.check.url=https://web.integration.cwds.io/perry/system-information
      perry.url=https://web.integration.cwds.io/perry
      perry.username=
      perry.password=
      perry.json={}
      perry.threads.count=5
      selenium.grid.url=
      validate.repeat.count=2
    """
    writeFile file: "gradle.properties", text: gradlePropsText
    sh "docker build --file docker/DockerfileIntegrationTest -t testperry ."
    def buildInfo = rtGradle.run buildFile: 'build.gradle', tasks: 'clean smokeTest'
    publishHTML([allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true, reportDir: 'build/reports/tests/smokeTest', reportFiles: 'index.html', reportName: 'Integration Test Report', reportTitles: ''])
  }
}

def deployWithSmoke(environment) {
  node(environment) {
    checkoutStage()
    rollbackDeployOnFailure('perry', environment, githubCredentialsId, ansibleCommand(environment, env.version)) {
        deployToStage(environment, env.version)
        updateManifestStage(environment, env.version)
        smokeTestStage(environment)
        integrationTestStage(environment)
    }
    cleanWs()
    }
  }

def deployToStage(environment, version) {
  stage("Deploy to $environment") {
    ws {
      git branch: "master", credentialsId: githubCredentialsId, url: deAnsibleGithubUrl
      sh ansibleCommand(environment, version)
    }
  }
}

def ansibleCommand(environment, version){
    def ansiCommand = ''
    if (environment == 'integration') {
        ansiCommand = "ansible-playbook -e VERSION_NUMBER=$version -e NEW_RELIC_AGENT=$USE_NEWRELIC  -e OAUTH_STATE=true -i inventories/$environment/hosts.yml deploy-perry.yml --vault-password-file ~/.ssh/vault.txt -vv"
    } else {
        ansiCommand = "ansible-playbook -e VERSION_NUMBER=$version -e NEW_RELIC_AGENT=$USE_NEWRELIC -i inventories/$environment/hosts.yml deploy-perry.yml --vault-password-file ~/.ssh/vault.txt -vv"
    }
    ansiCommand
}

def updateManifestStage(environment, version) {
  stage('Update Manifest Version') {
    updateManifest("perry", environment, githubCredentialsId, version)
  }
}

def integrationTestStage(environment) {
  stage("Integration Test $environment") {
     if (environment == 'integration') {
     withCredentials([
         string(credentialsId: 'cals-app-smoke-email', variable: 'SMOKE_TEST_USER'),
         string(credentialsId: 'cals-app-smoke-password', variable: 'SMOKE_TEST_PASSWORD'),
         string(credentialsId: 'cals-app-smoke-verification-code', variable: 'SMOKE_VERIFICATION_CODE')
       ]){
     sh "docker run -e SMOKE_TEST_USER=$SMOKE_TEST_USER  -e SMOKE_TEST_PASSWORD=$SMOKE_TEST_PASSWORD -e SMOKE_VERIFICATION_CODE=$SMOKE_VERIFICATION_CODE testperry:latest"
     }
     }else {
     sh "docker run  testperry:latest"
     }
  }
}
