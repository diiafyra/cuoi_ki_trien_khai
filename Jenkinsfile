pipeline {
    agent any

    environment {
        REGISTRY_CREDENTIALS = credentials('dockerhub-cred')  // cấu hình Jenkins credentials trước
        REGISTRY_URL = 'docker.io'
        REGISTRY_USERNAME = 'afyra'
        IMAGE_BACKEND = "${REGISTRY_USERNAME}/cuoiki-backend"
        IMAGE_FRONTEND = "${REGISTRY_USERNAME}/cuoiki-frontend"
    }

    stages {

        stage('Code Scan - SonarQube') {
            steps {
                withSonarQubeEnv('SonarQube_sv') {
                    sh './mvnw sonar:sonar'
                }
            }
        }

        stage('Build Backend') {
            steps {
                dir('backend') {
                    sh './mvnw clean package -DskipTests'
                    sh 'docker build -t $IMAGE_BACKEND:latest .'
                }
            }
        }

        stage('Build Frontend') {
            steps {
                dir('frontend') {
                    sh 'docker build -t $IMAGE_FRONTEND:latest .'
                }
            }
        }

        stage('Push Docker Images') {
            steps {
                withDockerRegistry([ credentialsId: 'dockerhub-credentials', url: '' ]) {
                    sh 'docker push $IMAGE_BACKEND:latest'
                    sh 'docker push $IMAGE_FRONTEND:latest'
                }
            }
        }

        stage('Deploy with Docker Compose') {
            steps {
                sh 'docker-compose down || true'
                sh 'docker-compose up -d'
            }
        }
    }

    post {
        always {
            echo 'Cleaning up dangling images...'
            sh 'docker image prune -f'
        }
        failure {
            mail to: 'quynhtrang.a3.w@gmail.com',
                 subject: "Jenkins Job Failed: ${env.JOB_NAME} [${env.BUILD_NUMBER}]",
                 body: "Check Jenkins for details: ${env.BUILD_URL}"
        }
    }
}
