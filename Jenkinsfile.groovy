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
        withCredentials([file(credentialsId: 'firebase-credentials', variable: 'FIREBASE_CREDENTIALS'),
                         usernamePassword(credentialsId: 'dockerhub-credentials', passwordVariable: 'password', usernameVariable: 'username'),
                         sshUserPrivateKey(credentialsId: 'jenkins-sshkey', keyFileVariable: 'SSH_KEY', passphraseVariable: 'SSH_PASS', usernameVariable: 'SSH_USERNAME')
        ]) {
            sh 'sudo docker build -t jaxkodex/challenge-backend:1.0 .'
            sh "cat $FIREBASE_CREDENTIALS > src/main/resources/firebase.json"
            sh 'sudo docker login -u $username -p $password'
            sh 'sudo docker push jaxkodex/challenge-backend:1.0'
            sh "ssh -i $SSH_KEY $SSH_USERNAME@api.acepta.xyz '/opt/bin/docker-compose -f /home/core/challenge-infra/dockercompose-app.yaml stop'"
            sh "ssh -i $SSH_KEY $SSH_USERNAME@api.acepta.xyz '/opt/bin/docker-compose -f /home/core/challenge-infra/dockercompose-app.yaml rm -f'"
            sh "ssh -i $SSH_KEY $SSH_USERNAME@api.acepta.xyz '/usr/bin/docker rmi jaxkodex/challenge-backend:1.0'"
            sh "ssh -i $SSH_KEY $SSH_USERNAME@api.acepta.xyz '/opt/bin/docker-compose -f /home/core/challenge-infra/dockercompose-app.yaml up -d'"
        }
    }
}