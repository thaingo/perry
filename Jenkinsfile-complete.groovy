@Library('jenkins-pipeline-utils') _

def notifyBuild(String buildStatus, Exception e){
  buildStatus =  buildStatus ?: 'SUCCESSFUL'

  // Default values
  def colorName = 'RED'
  def colorCode = '#FF0000'
  def subject = "${buildStatus}: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'"
  def summary = """*${buildStatus}*: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]':\nMore detail in console output at <${env.BUILD_URL}|${env.BUILD_URL}>"""
  def details = """${buildStatus}: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]':\n
    Check console output at ${env.BUILD_URL} """

  // Override default values based on build status
  if (buildStatus == 'STARTED'){
    color = 'YELLOW'
    colorCode = '#FFFF00'
  } else if (buildStatus == 'SUCCESSFUL'){
    color = 'GREEN'
    colorCode = '#00FF00'
  } else{
    color = 'RED'
    colorCode = '#FF0000'
    details +="<p>Error message ${e.message}, stacktrace: ${e}</p>"
    summary +="\nError message ${e.message}, stacktrace: ${e}"
  }

  emailext(
    subject: subject,
    body: details,
    attachLog: true,
    recipientProviders: [[$class: 'DevelopersRecipientProvider']],
    to: "Leonid.Marushevskiy@osi.ca.gov, Alex.Kuznetsov@osi.ca.gov"
  )
}

node('dora-slave') {
    def serverArti = Artifactory.server 'CWDS_DEV'
    def rtGradle = Artifactory.newGradleBuild()
    def github_credentials_id = '433ac100-b3c2-4519-b4d6-207c029a103b'
    def docker_credentials_id = '6ba8d05c-ca13-4818-8329-15d41a089ec0'
    newTag = '';
    triggerProperties = pullRequestMergedTriggerProperties('perry-master')
    properties([pipelineTriggers([triggerProperties]), buildDiscarder(logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '', numToKeepStr: '10')), disableConcurrentBuilds(), [$class: 'RebuildSettings', autoRebuild: false, rebuildDisabled: false],
                parameters([
                        string(defaultValue: 'latest', description: '', name: 'APP_VERSION'),
                        string(defaultValue: 'master', description: 'perry branch', name: 'branch'),
                        string(defaultValue: 'master', description: 'ansible branch', name: 'ansible_branch'),
                        string(defaultValue: '', description: 'Used for mergerequest default is empty', name: 'refspec'),
                        booleanParam(defaultValue: true, description: 'Enable NewRelic APM', name: 'USE_NEWRELIC'),
                        string(defaultValue: 'inventories/tpt2dev/hosts.yml', description: '', name: 'inventory'),
                        string(defaultValue: 'https://web.dev.cwds.io/perry', description: 'Perry base URL', name: 'PERRY_URL'),
                 ])
               ])
    try {
        stage('Preparation') {
            cleanWs()
            checkout scm
            checkout changelog: false, poll: false, scm: [$class: 'GitSCM', branches: [[name: '$ansible_branch']], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: 'ansible']], submoduleCfg: [], userRemoteConfigs: [[credentialsId: '433ac100-b3c2-4519-b4d6-207c029a103b', url: 'git@github.com:ca-cwds/de-ansible.git']]]
            rtGradle.tool = "Gradle_35"
            rtGradle.resolver repo: 'repo', server: serverArti
            rtGradle.useWrapper = true
        }
        stage('Increment Tag') {
          newTag = newSemVer()
        }
        stage('Build') {
            buildInfo = rtGradle.run buildFile: 'build.gradle', tasks: "clean jar -DnewVersion=${newTag}".toString()
        }
        stage('Unit Tests') {
            buildInfo = rtGradle.run buildFile: 'build.gradle', tasks: 'test jacocoTestReport', switches: "--info -DnewVersion=${newTag}".toString()
        }
        stage('SonarQube analysis') {
          lint(rtGradle)
        }
        stage('License Report') {
            buildInfo = rtGradle.run buildFile: 'build.gradle', tasks: 'downloadLicenses'
        }
        stage('Build Docker') {
            withDockerRegistry([credentialsId: docker_credentials_id]) {
                buildInfo = rtGradle.run buildFile: 'build.gradle', tasks: "publishLatestDocker -DnewVersion=${newTag}".toString()
            }
        }
        stage('Clean Workspace') {
            archiveArtifacts artifacts: '**/perry*.jar,readme.txt', fingerprint: true
        }
        stage('Deploy Cognito Mode') {
            sh 'cd ansible ; ansible-playbook -e NEW_RELIC_AGENT=$USE_NEWRELIC -e VERSION_NUMBER=$APP_VERSION -i $inventory deploy-perry.yml --vault-password-file ~/.ssh/vault.txt -vv'
            sleep(20)
        }
        stage('Cognito Integration Tests') {
            def gradlePropsText = """
            perry.health.check.url=http://10.110.12.162:9082/manage/health
            perry.url=${PERRY_URL}
            perry.username=donzano123+cap@gmail.com
            perry.password=CWS4kids!
            perry.json={}
            perry.threads.count=1
            selenium.grid.url=
            validate.repeat.count=2
            """
            writeFile file: "gradle.properties", text: gradlePropsText
            buildInfo = rtGradle.run buildFile: 'build.gradle', tasks: 'integrationTestProd --stacktrace'
        }
        stage('Deploy Dev Mode') {
            // TODO: Need to change Perry mode here to DEV
            sh 'sed -i \'s/perry_mode: "COGNITO"/perry_mode: "CLUSTERED_DEV"/\'  ansible/inventories/tpt2dev/group_vars/perry.yml'
            sh 'cd ansible ; ansible-playbook -e NEW_RELIC_AGENT=$USE_NEWRELIC -e VERSION_NUMBER=$APP_VERSION -i $inventory deploy-perry.yml --vault-password-file ~/.ssh/vault.txt -vv'
            sleep(20)
        }
        stage('Dev Integration Tests') {
            def gradlePropsText = """
            perry.health.check.url=http://10.110.12.162:9082/manage/health
            perry.url=${PERRY_URL}
            perry.username=
            perry.password=
            perry.json=
            perry.threads.count=1
            selenium.grid.url=http://grid.dev.cwds.io:4444/wd/hub
            validate.repeat.count=2
            """
            writeFile file: "gradle.properties", text: gradlePropsText
            buildInfo = rtGradle.run buildFile: 'build.gradle', tasks: 'integrationTestDev --stacktrace'
        }
        stage('Push artifacts') {
            // Artifactory
            rtGradle.deployer.deployArtifacts = true
            buildInfo = rtGradle.run buildFile: 'build.gradle', tasks: "publish -DRelease=true -DnewVersion=${newTag}".toString()
            rtGradle.deployer.deployArtifacts = false
            // Docker Hub
            withDockerRegistry([credentialsId: docker_credentials_id]) {
                buildInfo = rtGradle.run buildFile: 'build.gradle', tasks: "publishDocker -DRelease=true -DnewVersion=${newTag}".toString()
            }
        }
        stage('Tag Git') {
          tagGithubRepo(newTag, github_credentials_id)
        }
        stage('Trigger Security scan') {
            def props = readProperties  file: 'build/resources/main/version.properties'
            def build_version = props["build.version"]
            sh "echo build_version: ${build_version}"
            build job: 'tenable-scan', 
                parameters: [
                    [$class: 'StringParameterValue', name: 'CONTAINER_NAME', value: 'perry'],
                    [$class: 'StringParameterValue', name: 'CONTAINER_VERSION', value: "${build_version}"]
                ],
                wait: false 
        }
        stage('Deploy to Pre-int and Integration') {
            def mgmtJobParams = "version=\"$newTag\""
            def handle = triggerRemoteJob abortTriggeredJob: true, enhancedLogging: false, job: 'PreInt-Integration/deploy-perry', maxConn: 5, pollInterval: 20, parameters: "${mgmtJobParams}", remoteJenkinsName: "deploy-jenkins", useCrumbCache: true, useJobInfoCache: true
            echo 'Remote Status: ' + handle.getBuildStatus().toString()
//            withCredentials([usernameColonPassword(credentialsId: 'fa186416-faac-44c0-a2fa-089aed50ca17', variable: 'jenkinsauth')]) {
//              sh "curl -u $jenkinsauth 'https://jenkins.mgmt.cwds.io/job/PreInt-Integration/job/deploy-perry/buildWithParameters?token=trigger-perry-deploy&version=${newTag}'"
//            }
          }
    } catch (Exception e) {
        emailext attachLog: true, body: "Failed: ${e}", recipientProviders: [[$class: 'DevelopersRecipientProvider']],
                subject: "Perry CI pipeline failed", to: "admin@cwds.io"
    } finally {
        publishHTML([allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true, reportDir: 'build/reports/license', reportFiles: 'license-dependency.html', reportName: 'License Report', reportTitles: 'License summary'])
        publishHTML([allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true, reportDir: 'jwt-security/build/reports/tests/', reportFiles: 'index.html', reportName: 'jwt-security Report', reportTitles: 'jwt-security Report'])
        publishHTML([allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true, reportDir: 'api-security/build/reports/tests/', reportFiles: 'index.html', reportName: 'api-security Report', reportTitles: 'api-security Report'])
        publishHTML([allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true, reportDir: 'build/reports/tests/', reportFiles: 'index.html', reportName: 'Main project Report', reportTitles: 'Main project Report'])
        publishHTML([allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true, reportDir: 'integration-tests/build/reports/tests/testCognitoMode/', reportFiles: 'index.html', reportName: 'Cognito Integration Tests Reports', reportTitles: 'Cognito Integration tests summary'])
        publishHTML([allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true, reportDir: 'integration-tests/build/reports/tests/testDevMode/', reportFiles: 'index.html', reportName: 'Dev Integration Tests Reports', reportTitles: 'Dev Integration tests summary'])

        cleanWs()
    }
}
