pipeline {
  agent any

  stages {

    stage("Build/Test: Backend") {
      steps {
        sh "./gradlew clean build"
      }
    }

    stage("Deploy: Backend") {
      when {
        expression {
           env.BRANCH_NAME == "main"
        }
      }
      steps {
          sh "cp backend.jar /writeToFolder/"
          sh "systemctl restart lean_coffree"
      }
    }
  }
}
