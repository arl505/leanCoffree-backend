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
        dir("./build/libs") {
          sh "cp leanCoffree_main.jar /writeToFolder/backend.jar"
          sh "systemctl restart lean_coffree"
        }
      }
    }
  }
}
