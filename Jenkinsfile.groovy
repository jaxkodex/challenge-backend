node {
    stage('Checkout') {
        checkout scm
    }
    stage('Unit & Integration Tests') {
        withCredentials([file(credentialsID: 'firebase-credentials', variable: 'FILE')]) {
            sh 'cp $FILE ./src/main/resources/'
            sh './gradlew clean test --no-daemon'
        }
    }
    stage('Build') {
        withCredentials([file(credentialsID: 'firebase-credentials', variable: 'FILE')]) {
            sh 'cp $FILE ./src/main/resources/'
            sh './gradlew build --no-daemon'
        }
    }
}