pipeline {
    agent any

    environment {
        REGISTRY_CREDENTIALS = credentials('dockerhub-cred')
        REGISTRY_URL = 'docker.io'
        REGISTRY_USERNAME = 'afyra'

        IMAGE_BACKEND = "${REGISTRY_USERNAME}/cuoiki-backend"
        IMAGE_FRONTEND = "${REGISTRY_USERNAME}/cuoiki-frontend"
        COMPOSE_PROJECT_NAME = 'cuoiki'
    }

    stages {

        stage('🧹 Cleanup Conflicting Containers') {
            steps {
                echo '🧹 Cleaning up possibly conflicting containers...'
                sh '''
                    docker rm -f sonarqube || true
                    docker rm -f sonar_db || true
                    docker rm -f db || true
                    docker rm -f backend || true
                    docker rm -f frontend || true
                    docker rm -f prometheus || true
                    docker rm -f grafana || true
                '''
            }
        }

        stage('Start Supporting Services') {
            steps {
                echo '🟡 Starting supporting services (db, sonarqube, prometheus, grafana)...'
                sh 'docker compose down --remove-orphans || true'
                sh 'docker compose up -d db sonarqube prometheus grafana'
                sh 'sleep 30' // đợi dịch vụ ổn định
                sh 'docker network connect shared-network jenkins-7072 || true'
            }
        }

        stage('🔍 Run SonarQube Code Scan') {
            steps {
                echo '🔍 Running SonarQube scan...'
                dir('backend') {
                    withSonarQubeEnv('SonarQube_sv') {
                        sh 'mvn clean verify sonar:sonar -Dsonar.projectKey=cuoiki-backend'
                    }
                }
            }
        }

        stage('🐳 Build and Push Docker Images') {
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

        stage('🚀 Deploy Entire Stack') {
            steps {
                echo '🚀 Rebuilding and starting all containers except Jenkins...'
                sh 'docker compose -p ${COMPOSE_PROJECT_NAME} down'
                sh 'docker compose -p ${COMPOSE_PROJECT_NAME} pull'
                sh 'docker compose -p ${COMPOSE_PROJECT_NAME} up -d --build db sonar_db sonarqube prometheus grafana backend frontend'
            }
        }
    }

    post {
        always {
            echo '🧹 Cleaning up unused Docker images...'
            sh 'docker image prune -f'
        }

        failure {
            mail to: 'quynhtrang.a3.w@gmail.com',
                subject: "❌ Jenkins Job Failed: ${env.JOB_NAME} [${env.BUILD_NUMBER}]",
                body: "🔗 See details: ${env.BUILD_URL}"
        }
    }
}
