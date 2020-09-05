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

    stage("Deploy: Frontend") {
      when {
        expression {
           env.BRANCH_NAME == "addJenkinsCICD"
        }
      }
      steps {
        dir("./frontend") {
          sh "cp -r build /usr/share/nginx/leanCoffree/"
        }
      }
    }

  }
}