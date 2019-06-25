import groovy.transform.Field
@Library('jenkins-pipeline-utils') _

@Field
def githubCredentialsId = '433ac100-b3c2-4519-b4d6-207c029a103b'
@Field
def deAnsibleGithubUrl = 'git@github.com:ca-cwds/de-ansible.git'

releasePipeline();

def releasePipeline() {
  try {
    deployWithSmoke('preint')
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
    if (environment == 'preint') {
      def gradlePropsText = """
        perry.health.check.url=https://web.preint.cwds.io/perry/system-information
        perry.url=https://web.preint.cwds.io/perry
        perry.username=
        perry.password=
        perry.json= { "user": "RACFID", "staffId": "0X5", "roles": [ "CWS-admin", "County-admin", "Supervisor" ], "county_code": "56", "county_cws_code": "1123", "county_name": "Ventura", "first_name": "Anna", "last_name": "Smith", "privileges": [ "CWS Case Management System", "Resource Management", "Resource Mgmt Placement Facility Maint", "Sealed", "Sensitive Persons", "Snapshot-rollout", "Hotline-rollout", "Facility-search-rollout", "RFA-rollout", "CANS-rollout", "CANS-staff-person-subordinates-read", "CANS-staff-person-read", "CANS-staff-person-clients-read", "CANS-client-read", "CANS-client-search", "CANS-assessment-read", "CANS-assessment-create", "CANS-assessment-in-progress-update", "CANS-assessment-completed-update", "CANS-assessment-in-progress-delete", "CANS-assessment-complete", "development-not-in-use" ] }
        perry.threads.count=5
        selenium.grid.url=
        validate.repeat.count=2
      """
       writeFile file: "gradle.properties", text: gradlePropsText
       sh "docker build --file docker/DockerfileIntegrationTestDev -t testperry ."
       def buildInfo = rtGradle.run buildFile: 'build.gradle', tasks: 'clean smokeTest'
       publishHTML([allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true, reportDir: 'build/reports/tests/integrationTest', reportFiles: 'index.html', reportName: 'Pre Integration Test Report', reportTitles: ''])
    } else {
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
      git branch: "DS-2919-perryfix", credentialsId: githubCredentialsId, url: deAnsibleGithubUrl
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

