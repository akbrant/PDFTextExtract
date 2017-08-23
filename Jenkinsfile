node {
   def mvnHome
   stage('Preparation') { // for display purposes
    checkout scm: [$class: 'GitSCM', source: 'https://github.com/akbrant/PDFTextExtract.git', revision: 'fixtags', clean: true, credentialsId: '5d1db58f-462f-4a2c-b052-1007264212d2'], poll: false
   }
   stage('Clean') {
      if (isUnix()) {
         sh "'/opt/apache-maven-3.5.0/bin/mvn' -Dmaven.test.failure.ignore clean"
      }
   }
   stage('Package') {
      // Run the maven build
      if (isUnix()) {
         sh "'/opt/apache-maven-3.5.0/bin/mvn' -Dmaven.test.failure.ignore package"
      }
   }
}
