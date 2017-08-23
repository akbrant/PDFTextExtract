pipeline {
  agent any
  stages {
    stage('Clean') {
      steps {
        parallel(
          "Clean": {
            sh '/opt/apache-maven-3.5.0/bin/mvn\ -Dmaven.test.failure.ignore clean'
            
          },
          "Package": {
            sh '/opt/apache-maven-3.5.0/bin/mvn\ -Dmaven.test.failure.ignore package'
            
          }
        )
      }
    }
  }
}
