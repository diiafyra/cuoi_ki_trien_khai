pipeline {
    agent any

    environment {
        REGISTRY_CREDENTIALS = credentials('dockerhub-cred')
        REGISTRY_URL = 'https://index.docker.io/v1/'
        REGISTRY_USERNAME = 'afyra'

        IMAGE_BACKEND = "${REGISTRY_USERNAME}/cuoiki-backend"
        IMAGE_FRONTEND = "${REGISTRY_USERNAME}/cuoiki-frontend"
        COMPOSE_PROJECT_NAME = 'cuoiki'
        DOCKER_NETWORK = 'cuoiki_app-network'
    }

    stages {
        stage('🧹 Cleanup Conflicting Containers') {
            steps {
                echo '🧹 Removing containers except Jenkins itself...'
                sh """
                    docker ps -a --format "{{.ID}} {{.Names}}" | grep -v jenkins-7072 | awk '{print \$1}' | xargs -r docker rm -f || true
                """
            }
        }

        stage('🔧 Ensure Network Exists') {
            steps {
                echo '🔧 Ensuring app-network exists...'
                sh """
                    docker network inspect ${DOCKER_NETWORK} >/dev/null 2>&1 || docker network create ${DOCKER_NETWORK}
                """
            }
        }

        stage('🟡 Start Supporting Services') {
            steps {
                echo '🟡 Starting supporting services (db, sonarqube, prometheus, grafana)...'
                sh """
                    docker compose -p ${COMPOSE_PROJECT_NAME} down --remove-orphans || true
                    docker compose -p ${COMPOSE_PROJECT_NAME} up -d db sonar_db sonarqube prometheus grafana
                    sleep 30
                    docker network connect ${DOCKER_NETWORK} jenkins-7072 || true
                """
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
                    docker.withRegistry("${REGISTRY_URL}", "${REGISTRY_CREDENTIALS}") {
                        sh "docker build -t ${IMAGE_BACKEND} ./backend"
                        sh "docker build -t ${IMAGE_FRONTEND} ./frontend"
                        sh "docker push ${IMAGE_BACKEND}"
                        sh "docker push ${IMAGE_FRONTEND}"
                    }
                }
            }
        }

        stage('🚀 Deploy Entire Stack') {
            steps {
                echo '🚀 Rebuilding and starting all containers except Jenkins...'
                sh """
                    docker compose -p ${COMPOSE_PROJECT_NAME} down
                    docker compose -p ${COMPOSE_PROJECT_NAME} pull
                    docker compose -p ${COMPOSE_PROJECT_NAME} up -d --build db sonar_db sonarqube prometheus grafana backend frontend
                    docker network connect ${DOCKER_NETWORK} jenkins-7072 || true
                """
            }
        }
    }

    post {
        always {
            echo '🧼 Cleaning up unused Docker images...'
            sh 'docker image prune -f'
        }

        failure {
            mail to: 'quynhtrang.a3.w@gmail.com',
                subject: "❌ Jenkins Job Failed: ${env.JOB_NAME} [${env.BUILD_NUMBER}]",
                body: "🔗 See details: ${env.BUILD_URL}"
        }
    }
}
