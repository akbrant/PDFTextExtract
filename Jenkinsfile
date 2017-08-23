pipeline {
  agent any
  stages {
    stage('Clean') {
      steps {
        sh '/opt/apache-maven-3.5.0/bin/mvn -Dmaven.test.failure.ignore clean'
      }
    }
  }
}