node {
   def mvnHome
   stage('Preparation') { // for display purposes
    checkout scm: [$class: 'GitSCM', source: 'https://github.com/akbrant/PDFTextExtract.git', revision: 'fixtags', clean: true, credentialsId: '3ebac5f2c89bf6e6d5336f7174f4b637c18000a8'], poll: false
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
