pipeline {
  agent any

  stages {

    stage("Build/Test: Frontend") {
      steps {
        dir("./frontend") {
          sh "yarn install"
          sh "yarn build"
        }
      }
    }

    stage("Build/Test: Backend") {
      steps {
        dir("./backend") {
          sh "./gradlew clean build"
        }
      }
    }

    stage("Deploy: Frontend") {
      when {
        expression {
           env.BRANCH_NAME == "addJoinSessionPOC"
        }
      }
      steps {
        dir("./frontend") {
          sh "cp -r build /usr/share/nginx/leanCoffree/"
        }
      }
    }

    stage("Deploy: Backend") {
      when {
        expression {
           env.BRANCH_NAME == "addJoinSessionPOC"
        }
      }
      steps {
        dir("./backend/build/libs") {
          sh "cp backend.jar /writeToFolder/"
          sh "systemctl restart lean_coffree"
        }
      }
    }

  }
}