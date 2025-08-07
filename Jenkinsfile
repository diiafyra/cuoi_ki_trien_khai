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
        stage('Code Scan - SonarQube') {
            steps {
                dir('backend') {
                    withSonarQubeEnv('SonarQube_sv') {
                        sh 'mvn sonar:sonar'
                    }
                }
            }
        }

        stage('Quality Gate') {
            steps {
                timeout(time: 1, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Build & Tag Docker Images') {
            parallel {
                stage('Backend') {
                    steps {
                        dir('backend') {
                            sh 'mvn clean package -DskipTests'
                            sh """
                                docker build -t ${IMAGE_BACKEND}:latest .
                            """
                        }
                    }
                }
                stage('Frontend') {
                    steps {
                        dir('frontend') {
                            sh "docker build -t ${IMAGE_FRONTEND}:latest ."
                        }
                    }
                }
            }
        }

        stage('Push Docker Images') {
            steps {
                withDockerRegistry([credentialsId: 'dockerhub-cred', url: 'https://index.docker.io/v1/']) {
                    script {
                        def images = [IMAGE_BACKEND, IMAGE_FRONTEND]
                        for (img in images) {
                            sh "docker push ${img}:latest"
                        }
                    }
                }
            }
        }

        stage('Deploy with Docker Compose') {
            steps {
                sh '''
                    docker-compose down || true
                    docker-compose up -d --build
                '''
            }
        }
    }

    post {
        always {
            echo 'Cleaning up dangling Docker images...'
            sh 'docker image prune -f'
        }
        failure {
            mail to: 'quynhtrang.a3.w@gmail.com',
                 subject: "Jenkins Job Failed: ${env.JOB_NAME} [${env.BUILD_NUMBER}]",
                 body: "Check Jenkins for details: ${env.BUILD_URL}"
        }
    }
}
