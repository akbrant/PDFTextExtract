node {
   def mvnHome
   stage('Preparation') { // for display purposes
    checkout scm: [$class: 'GitSCM', source: 'https://github.com/akbrant/PDFTextExtract.git', revision: 'fixtags', clean: true, poll: false
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
