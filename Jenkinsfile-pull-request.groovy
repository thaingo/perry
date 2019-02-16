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
    def triggerProperties = githubPullRequestBuilderTriggerProperties()
    properties([buildDiscarder(logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '', numToKeepStr: '5')), disableConcurrentBuilds(), [$class: 'RebuildSettings', autoRebuild: false, rebuildDisabled: false],
    parameters([
                  string(defaultValue: 'SNAPSHOT', description: 'Release version (if not SNAPSHOT will be released to lib-release repository)', name: 'VERSION'),
                  string(defaultValue: 'latest', description: '', name: 'APP_VERSION'),
                  booleanParam(defaultValue: true, description: 'Default release version template is: <majorVersion>_<buildNumber>-RC', name: 'RELEASE_PROJECT'),
                  string(defaultValue: "", description: 'Fill this field if need to specify custom version ', name: 'OVERRIDE_VERSION'),
                  booleanParam(defaultValue: true, description: 'Enable NewRelic APM', name: 'USE_NEWRELIC'),
                  string(defaultValue: 'inventories/tpt2dev/hosts.yml', description: '', name: 'inventory')
                ]), pipelineTriggers([triggerProperties])
            ])
    try {
        stage('Preparation') {
            cleanWs()
            checkout scm
            rtGradle.tool = "Gradle_35"
            rtGradle.resolver repo: 'repo', server: serverArti
            rtGradle.useWrapper = true
        }
        stage('Verify SemVer Label') {
          checkForLabel("perry")
        }
        stage('Build') {
            if (params.RELEASE_PROJECT) {
                echo "!!!! BUILD RELEASE VERSION"
                buildInfo = rtGradle.run buildFile: 'build.gradle', tasks: 'clean jar -DRelease=$RELEASE_PROJECT -DBuildNumber=$BUILD_NUMBER -DCustomVersion=$OVERRIDE_VERSION'
            } else {
                echo "!!!! BUILD SNAPSHOT VERSION"
                buildInfo = rtGradle.run buildFile: 'build.gradle', tasks: 'clean jar'
            }
        }
        stage('Unit Tests') {
            buildInfo = rtGradle.run buildFile: 'build.gradle', tasks: 'test jacocoTestReport', switches: '--info'
        }
        stage('SonarQube analysis') {
          lint(rtGradle)
        }
        stage('License Report') {
            buildInfo = rtGradle.run buildFile: 'build.gradle', tasks: 'downloadLicenses'
        }

        stage('Clean Workspace') {
            archiveArtifacts artifacts: '**/perry*.jar,readme.txt', fingerprint: true
        }

    } catch (Exception e) {
          errorcode = e
          currentBuild.result = "FAIL"
          notifyBuild(currentBuild.result,errorcode)
          throw e;
    } finally {
        publishHTML([allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true, reportDir: 'build/reports/license', reportFiles: 'license-dependency.html', reportName: 'License Report', reportTitles: 'License summary'])
        publishHTML([allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true, reportDir: 'jwt-security/build/reports/tests', reportFiles: 'index.html', reportName: 'jwt-security Report', reportTitles: 'jwt-security Report'])
        publishHTML([allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true, reportDir: 'shiro-security/build/reports/tests', reportFiles: 'index.html', reportName: 'shiro-security Report', reportTitles: 'shiro-security Report'])
        publishHTML([allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true, reportDir: 'build/reports/tests', reportFiles: 'index.html', reportName: 'Main project Report', reportTitles: 'Main project Report'])
        publishHTML([allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true, reportDir: 'build/reports/tests/smokeTest', reportFiles: 'index.html', reportName: 'Smoke Tests Reports', reportTitles: 'Smoke tests summary'])
        cleanWs()
    }
}
