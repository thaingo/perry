@Library('jenkins-pipeline-utils') _

node('dora-slave') {
    def serverArti = Artifactory.server 'CWDS_DEV'
    def rtGradle = Artifactory.newGradleBuild()
    def github_credentials_id = '433ac100-b3c2-4519-b4d6-207c029a103b'
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
            checkout([$class: 'GitSCM', branches: [[name: '$branch']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: '433ac100-b3c2-4519-b4d6-207c029a103b', refspec: '$refspec', url: 'git@github.com:ca-cwds/perry.git']]])
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
            withSonarQubeEnv('Core-SonarQube') {
                buildInfo = rtGradle.run buildFile: 'build.gradle', switches: '--info', tasks: "sonarqube -DnewVersion=${newTag}".toString()
            }
        }
        stage('License Report') {
            buildInfo = rtGradle.run buildFile: 'build.gradle', tasks: 'downloadLicenses'
        }
        stage('Build Docker') {
            withDockerRegistry([credentialsId: '6ba8d05c-ca13-4818-8329-15d41a089ec0']) {
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
            perry.username=cwds.perry.test@gmail.com
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
            withDockerRegistry([credentialsId: '6ba8d05c-ca13-4818-8329-15d41a089ec0']) {
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
    } catch (Exception e) {
        emailext attachLog: true, body: "Failed: ${e}", recipientProviders: [[$class: 'DevelopersRecipientProvider']],
                subject: "Perry CI pipeline failed", to: "Leonid.Marushevskiy@osi.ca.gov, Alex.Kuznetsov@osi.ca.gov"
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
