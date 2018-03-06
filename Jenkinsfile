node ('dora-slave'){
   def serverArti = Artifactory.server 'CWDS_DEV'
   def rtGradle = Artifactory.newGradleBuild()
   properties([buildDiscarder(logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '', numToKeepStr: '5')), disableConcurrentBuilds(), [$class: 'RebuildSettings', autoRebuild: false, rebuildDisabled: false],
   parameters([
      string(defaultValue: 'SNAPSHOT', description: 'Release version (if not SNAPSHOT will be released to lib-release repository)', name: 'VERSION'),
      string(defaultValue: 'latest', description: '', name: 'APP_VERSION'),
      string(defaultValue: 'development', description: '', name: 'branch'),
      booleanParam(defaultValue: false, description: 'Default release version template is: <majorVersion>_<buildNumber>-RC', name: 'RELEASE_PROJECT'),
      string(defaultValue: "", description: 'Fill this field if need to specify custom version ', name: 'OVERRIDE_VERSION'),
      booleanParam(defaultValue: true, description: 'Enable NewRelic APM', name: 'USE_NEWRELIC'),
      string(defaultValue: 'inventories/tpt2dev/hosts.yml', description: '', name: 'inventory')
      ]), pipelineTriggers([pollSCM('H/5 * * * *')])])
  try {
   stage('Preparation') {
		  cleanWs()
		  git branch: '$branch', credentialsId: '433ac100-b3c2-4519-b4d6-207c029a103b', url: 'git@github.com:ca-cwds/perry.git'
		  rtGradle.tool = "Gradle_35"
		  rtGradle.resolver repo:'repo', server: serverArti
		  rtGradle.useWrapper = true
   }
   stage('Build'){
     if (params.RELEASE_PROJECT) {
         echo "!!!! BUILD RELEASE VERSION"
         def buildInfo = rtGradle.run buildFile: 'build.gradle', tasks: 'clean jar -DRelease=$RELEASE_PROJECT -DBuildNumber=$BUILD_NUMBER -DCustomVersion=$OVERRIDE_VERSION'
     } else {
         echo "!!!! BUILD SNAPSHOT VERSION"
         def buildInfo = rtGradle.run buildFile: 'build.gradle', tasks: 'clean jar'
     }
   }
   stage('Unit Tests') {
       buildInfo = rtGradle.run buildFile: 'build.gradle', tasks: 'test jacocoTestReport', switches: '--info'
   }
   stage('License Report') {
   		buildInfo = rtGradle.run buildFile: 'build.gradle', tasks: 'downloadLicenses'
   		publishHTML([allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true, reportDir: 'build/reports/license', reportFiles: 'license-dependency.html', reportName: 'License Report', reportTitles: 'License summary'])
   }

	stage ('Push to artifactory'){
    rtGradle.deployer.deployArtifacts = true
    if (params.RELEASE_PROJECT) {
        echo "!!!! PUSH RELEASE VERSION ${params.VERSION}"
        buildInfo = rtGradle.run buildFile: 'build.gradle', tasks: 'publish -DRelease=$RELEASE_PROJECT -DBuildNumber=$BUILD_NUMBER -DCustomVersion=$OVERRIDE_VERSION'
    } else {
        echo "!!!! PUSH SNAPSHOT VERSION"
        buildInfo = rtGradle.run buildFile: 'build.gradle', tasks: 'publish'
    }
    rtGradle.deployer.deployArtifacts = false
	}
	stage ('Build Docker'){
	   withDockerRegistry([credentialsId: '6ba8d05c-ca13-4818-8329-15d41a089ec0']) {
	       if (params.RELEASE_PROJECT) {
             buildInfo = rtGradle.run buildFile: 'build.gradle', tasks: 'publishDocker -DRelease=$RELEASE_PROJECT -DBuildNumber=$BUILD_NUMBER -DCustomVersion=$OVERRIDE_VERSION'
         } else {
             buildInfo = rtGradle.run buildFile: 'build.gradle', tasks: 'publishDocker -DRelease=false -DBuildNumber=$BUILD_NUMBER'
         }
     }
	}
	stage('Clean Workspace') {
		archiveArtifacts artifacts: '**/perry*.jar,readme.txt', fingerprint: true
		cleanWs()
	}
//	stage('Deploy Application'){
//	   checkout changelog: false, poll: false, scm: [$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: '433ac100-b3c2-4519-b4d6-207c029a103b', url: 'git@github.com:ca-cwds/de-ansible.git']]]
//	   sh 'ansible-playbook -e NEW_RELIC_AGENT=$USE_NEWRELIC -e VERSION_NUMBER=$APP_VERSION -i $inventory deploy-perry.yml --vault-password-file ~/.ssh/vault.txt -vv'
//  }
  stage('Smoke Tests') {
      git branch: 'master', url: 'https://github.com/ca-cwds/perry.git'
      sleep 40
      buildInfo = rtGradle.run buildFile: './build.gradle', tasks: 'smokeTest --stacktrace'
      publishHTML([allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true, reportDir: 'build/reports/tests/smokeTest', reportFiles: 'index.html', reportName: 'Smoke Tests Report', reportTitles: 'Smoke tests summary'])
    }

 } catch (Exception e)   {
       emailext attachLog: true, body: "Failed: ${e}", recipientProviders: [[$class: 'DevelopersRecipientProvider']],
       subject: "Perry CI pipeline failed", to: "Leonid.Marushevskiy@osi.ca.gov, Alex.Kuznetsov@osi.ca.gov"
 }finally {
	   publishHTML([allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true, reportDir: 'jwt-security/build/reports/tests', reportFiles: 'index.html', reportName: 'jwt-security Report', reportTitles: 'jwt-security Report'])
	   publishHTML([allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true, reportDir: 'shiro-security/build/reports/tests', reportFiles: 'index.html', reportName: 'shiro-security Report', reportTitles: 'shiro-security Report'])
	   publishHTML([allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true, reportDir: 'build/reports/tests', reportFiles: 'index.html', reportName: 'Main project Report', reportTitles: 'Main project Report'])
	   publishHTML([allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true, reportDir: 'build/reports/tests/smokeTest', reportFiles: 'index.html', reportName: 'Smoke Tests Reports', reportTitles: 'Smoke tests summary'])
       cleanWs()
 }
}

