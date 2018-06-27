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
//        stage('Deploy Cognito Mode') {
//            sh 'cd ansible ; ansible-playbook -e NEW_RELIC_AGENT=$USE_NEWRELIC -e VERSION_NUMBER=$APP_VERSION -i $inventory deploy-perry.yml --vault-password-file ~/.ssh/vault.txt -vv'
//            sleep(20)
//        }
        stage('Cognito Integration Tests') {
            def gradlePropsText = """
            perry.health.check.url=http://10.110.12.162:9082/manage/health
            perry.url=${PERRY_URL}
            perry.username=cwds.perry.test@gmail.com
            perry.password=CWS4kids!
            perry.json={}
            perry.threads.count=1
            selenium.grid.url=
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
    } catch (Exception e) {
        emailext attachLog: true, body: "Failed: ${e}", recipientProviders: [[$class: 'DevelopersRecipientProvider']],
                subject: "Perry CI pipeline failed", to: "Leonid.Marushevskiy@osi.ca.gov, Alex.Kuznetsov@osi.ca.gov"
    } finally {
        publishHTML([allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true, reportDir: 'integration-tests/build/reports/tests/testCognitoMode/', reportFiles: 'index.html', reportName: 'Cognito Integration Tests Reports', reportTitles: 'Cognito Integration tests summary'])
        publishHTML([allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true, reportDir: 'integration-tests/build/reports/tests/testDevMode/', reportFiles: 'index.html', reportName: 'Dev Integration Tests Reports', reportTitles: 'Dev Integration tests summary'])

        cleanWs()
    }
}
