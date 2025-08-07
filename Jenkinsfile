pipeline {
    agent any

    environment {
        REGISTRY_CREDENTIALS = credentials('dockerhub-cred')
        REGISTRY_URL = 'docker.io'
        REGISTRY_USERNAME = 'afyra'

        IMAGE_BACKEND = "${REGISTRY_USERNAME}/cuoiki-backend"
        IMAGE_FRONTEND = "${REGISTRY_USERNAME}/cuoiki-frontend"
    }

    stages {
        stage('Start Services') {
            steps {
                echo '🟡 Starting supporting services (db, sonarqube, prometheus, grafana)...'
                sh 'docker compose up -d db sonarqube prometheus grafana'
                sh 'sleep 30' // đợi cho các dịch vụ khởi động ổn định
            }
        }

        stage('Code Scan - SonarQube') {
            steps {
                echo '🔍 Running SonarQube Scan...'
                dir('backend') {
                    withSonarQubeEnv('SonarQube_sv') {
                        sh './mvnw clean verify sonar:sonar -Dsonar.projectKey=cuoiki-backend'
                    }
                }
            }
        }

        stage('Build and Push Docker Images') {
            steps {
                echo '🐳 Building and pushing Docker images...'
                script {
                    docker.withRegistry("https://${REGISTRY_URL}", 'dockerhub-cred') {
                        sh 'docker build -t ${IMAGE_BACKEND} ./backend'
                        sh 'docker build -t ${IMAGE_FRONTEND} ./frontend'
                        sh 'docker push ${IMAGE_BACKEND}'
                        sh 'docker push ${IMAGE_FRONTEND}'
                    }
                }
            }
        }

        stage('Deploy All Services') {
            steps {
                echo '🚀 Deploying entire stack with Docker Compose...'
                sh 'docker compose down'
                sh 'docker compose pull'
                sh 'docker compose up -d --build'
            }
        }
    }

  post {
    always {
      sh 'docker image prune -f'
    }
    failure {
      mail to: 'quynhtrang.a3.w@gmail.com',
           subject: "❌ Jenkins Job Failed: ${env.JOB_NAME} [${env.BUILD_NUMBER}]",
           body: "🔗 Details: ${env.BUILD_URL}"
    }
  }
}
