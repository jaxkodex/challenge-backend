node {
    stage('Checkout') {
        checkout scm
    }
    stage('Unit & Integration Tests') {
        sh './gradlew clean test --no-daemon'
    }
    stage('Build') {
        sh './gradlew build --no-daemon'
    }
}