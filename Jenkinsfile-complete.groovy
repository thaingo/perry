node('dora-slave') {
    def serverArti = Artifactory.server 'CWDS_DEV'
    def rtGradle = Artifactory.newGradleBuild()
    properties([buildDiscarder(logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '', numToKeepStr: '5')), disableConcurrentBuilds(), [$class: 'RebuildSettings', autoRebuild: false, rebuildDisabled: false],
                parameters([
                        string(defaultValue: 'SNAPSHOT', description: 'Release version (if not SNAPSHOT will be released to lib-release repository)', name: 'VERSION'),
                        string(defaultValue: 'latest', description: '', name: 'APP_VERSION'),
                        string(defaultValue: 'master', description: '', name: 'branch'),
                        string(defaultValue: '', description: 'Used for mergerequest default is empty', name: 'refspec'),
                        booleanParam(defaultValue: true, description: 'Default release version template is: <majorVersion>_<buildNumber>-RC', name: 'RELEASE_PROJECT'),
                        string(defaultValue: "", description: 'Fill this field if need to specify custom version ', name: 'OVERRIDE_VERSION'),
                        booleanParam(defaultValue: true, description: 'Enable NewRelic APM', name: 'USE_NEWRELIC'),
                        string(defaultValue: 'inventories/tpt2dev/hosts.yml', description: '', name: 'inventory'),
                        string(defaultValue: 'https://web.dev.cwds.io/perry', description: 'Perry base URL', name: 'PERRY_URL'),
                ]), pipelineTriggers([pollSCM('H/5 * * * *')])])
    try {
        stage('Preparation') {
            cleanWs()
            checkout([$class: 'GitSCM', branches: [[name: '$branch']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: '433ac100-b3c2-4519-b4d6-207c029a103b', refspec: '$refspec', url: 'git@github.com:ca-cwds/perry.git']]])
            checkout changelog: false, poll: false, scm: [$class: 'GitSCM', branches: [[name: '*/AARA-425']], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: 'ansible']], submoduleCfg: [], userRemoteConfigs: [[credentialsId: '433ac100-b3c2-4519-b4d6-207c029a103b', url: 'git@github.com:LeonidMarushevskyi/de-ansible.git']]]
            rtGradle.tool = "Gradle_35"
            rtGradle.resolver repo: 'repo', server: serverArti
            rtGradle.useWrapper = true
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
//        stage('Unit Tests') {
//            buildInfo = rtGradle.run buildFile: 'build.gradle', tasks: 'test jacocoTestReport', switches: '--info'
//        }
//        stage('SonarQube analysis') {
//            withSonarQubeEnv('Core-SonarQube') {
//                buildInfo = rtGradle.run buildFile: 'build.gradle', switches: '--info', tasks: 'sonarqube'
//            }
//        }
//        stage('License Report') {
//            buildInfo = rtGradle.run buildFile: 'build.gradle', tasks: 'downloadLicenses'
//        }
        stage('Build Docker') {
            withDockerRegistry([credentialsId: '6ba8d05c-ca13-4818-8329-15d41a089ec0']) {
                if (params.RELEASE_PROJECT) {
                    buildInfo = rtGradle.run buildFile: 'build.gradle', tasks: 'dockerPerryPublish -DRelease=$RELEASE_PROJECT -DBuildNumber=$BUILD_NUMBER -DCustomVersion=$OVERRIDE_VERSION'
                } else {
                    buildInfo = rtGradle.run buildFile: 'build.gradle', tasks: 'dockerPerryPublish -DRelease=false -DBuildNumber=$BUILD_NUMBER'
                }
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
            selenium.grid.url=http://grid.dev.cwds.io:4444/wd/hub
            """
            writeFile file: "gradle.properties", text: gradlePropsText
            buildInfo = rtGradle.run buildFile: 'build.gradle', tasks: 'integrationTestProd --stacktrace'
        }
        stage('Deploy Dev Mode') {
            // TODO: Need to change Perry mode here to DEV
            sh 'cd ansible ; ansible-playbook -e NEW_RELIC_AGENT=$USE_NEWRELIC -e VERSION_NUMBER=$APP_VERSION -i $inventory deploy-perry.yml --vault-password-file ~/.ssh/vault.txt -vv'
            sleep(20)
        }
        stage('Dev Integration Tests') {
            def gradlePropsText = """
            perry.health.check.url=http://10.110.12.162:9082/manage/health
            perry.url=${PERRY_URL}
            perry.threads.count=1
            selenium.grid.url=http://grid.dev.cwds.io:4444/wd/hub
            """
            writeFile file: "gradle.properties", text: gradlePropsText
            buildInfo = rtGradle.run buildFile: 'build.gradle', tasks: 'integrationTestDev --stacktrace'
        }
        stage('Push artifacts') {
            // Artifactory
            rtGradle.deployer.deployArtifacts = true
            if (params.RELEASE_PROJECT) {
                echo "!!!! PUSH RELEASE VERSION ${params.VERSION}"
                buildInfo = rtGradle.run buildFile: 'build.gradle', tasks: 'publish -DRelease=$RELEASE_PROJECT -DBuildNumber=$BUILD_NUMBER -DCustomVersion=$OVERRIDE_VERSION'
            } else {
                echo "!!!! PUSH SNAPSHOT VERSION"
                buildInfo = rtGradle.run buildFile: 'build.gradle', tasks: 'publish'
            }
            rtGradle.deployer.deployArtifacts = false
            // Docker Hub
            withDockerRegistry([credentialsId: '6ba8d05c-ca13-4818-8329-15d41a089ec0']) {
                if (params.RELEASE_PROJECT) {
                    buildInfo = rtGradle.run buildFile: 'build.gradle', tasks: 'publishDocker -DRelease=$RELEASE_PROJECT -DBuildNumber=$BUILD_NUMBER -DCustomVersion=$OVERRIDE_VERSION'
                } else {
                    buildInfo = rtGradle.run buildFile: 'build.gradle', tasks: 'publishDocker -DRelease=false -DBuildNumber=$BUILD_NUMBER'
                }
            }
        }
        stage('Tag Git') {
            sshagent (credentials: ['433ac100-b3c2-4519-b4d6-207c029a103b']) {
                if (params.RELEASE_PROJECT) {
                    echo "!!!! BUILD RELEASE VERSION"
                    // tagRepo('test-tags')
                    buildInfo = rtGradle.run buildFile: 'build.gradle', tasks: 'pushGitTag -DRelease=$RELEASE_PROJECT -DBuildNumber=$BUILD_NUMBER -DCustomVersion=$OVERRIDE_VERSION'
                } else {
                    echo "!!!! BUILD SNAPSHOT VERSION"
                    buildInfo = rtGradle.run buildFile: 'build.gradle', tasks: 'pushGitTag -DRelease=$RELEASE_PROJECT -DBuildNumber=$BUILD_NUMBER -DCustomVersion=$OVERRIDE_VERSION'
                }
            }
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
