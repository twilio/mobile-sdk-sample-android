prepare = 3
unitTests = 8
archivingArtifacts = 3

future_release = 'future_release'
master = 'master'

body = """FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'. Check console output at "${env.BUILD_URL}"""
subject = "FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'"
emailList = env.APP_TEAM_EMAIL

properties([
  buildDiscarder(logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '', numToKeepStr: '5')),
  pipelineTriggers([
    upstream(
      threshold: 'SUCCESS',
      upstreamProjects: "TwilioAuth_Android_SDK/${env.BRANCH_NAME}"
    )
  ])
])
node('appium_ventspils_node') {
  try{
    if (env.BRANCH_NAME == future_release || env.BRANCH_NAME == master || currentBuild.rawBuild.getCause(hudson.model.Cause$UserIdCause)){
      stage 'Prepare'
        timeout(prepare) {
          checkout scm
          sh "wget 'https://drive.google.com/uc?export=download&id=0B7npE-gj0q8JTGRweW5hdHA4R3c' -O twilio-auth-sample/google-services.json"
      }
      stage 'Run Unit Tests'
        timeout(unitTests) {
          try{
            sh "echo 'running command: ./gradlew clean testDebugUnitTest uninstallDebug uninstallDebugAndroidTest connectedDebugAndroidTest assemble'"
            sh "./gradlew clean testDebugUnitTest uninstallDebug uninstallDebugAndroidTest connectedDebugAndroidTest assemble"
          } catch (e) {
            currentBuild.result = "FAILED"
            throw e
          } finally {
            publishUnitTests()
          }
      }
      stage 'Artifacts'
       timeout(archivingArtifacts) {
         archiveArtifacts artifacts: '**/*.apk'
       }
    }
    else {
        currentBuild.result = "NOT_BUILT"
    }
  } catch (e) {
    notifyFailed()
    currentBuild.result = "FAILED"
    throw e
  }
}

def notifyFailed() {
  if (env.BRANCH_NAME == future_release || env.BRANCH_NAME == master) {
    mail body: body, subject: subject, to: emailList
  }
}

def publishUnitTests() {
  publishHTML target: [
    allowMissing: true,
    alwaysLinkToLastBuild: true,
    keepAll: true,
    reportDir: 'twilio-auth-sample/build/reports/androidTests/connected',
    reportFiles: 'index.html',
    reportName: 'Android Test Reports'
  ]
  publishHTML target: [
    allowMissing: true,
    alwaysLinkToLastBuild: true,
    keepAll: true,
    reportDir: 'twilio-auth-sample/build/reports/tests/testDebugUnitTest',
    reportFiles: 'index.html',
    reportName: 'Unit Test Report'
  ]
}
