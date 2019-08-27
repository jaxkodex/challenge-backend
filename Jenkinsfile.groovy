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
    stage('Deploy') {
        withCredentials([file(credentialsId: 'firebase-credentials', variable: 'FIREBASE_CREDENTIALS')]) {
            sh "sed 's|FBC_SRC|'$FIREBASE_CREDENTIALS'|g' Dockerfile.tpl > Dockerfile"
            sh 'docker build -t jaxkodex/challenge-backend:1.0 .'
            sh 'docker push jaxkodex/challenge-backend:1.0'
//            sh 'docker run -d challenge:1.0'
        }
    }
}