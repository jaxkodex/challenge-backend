node {
    stage('Checkout') {
        checkout scm
    }
    stage('Unit & Integration Tests') {
        withCredentials([file(credentialsId: 'firebase-credentials', variable: 'FIREBASE_CREDENTIALS')]) {
            sh './gradlew clean test --no-daemon'
        }
    }
    stage('Build') {
        withCredentials([file(credentialsId: 'firebase-credentials', variable: 'FIREBASE_CREDENTIALS')]) {
            sh './gradlew build --no-daemon'
        }
    }
}