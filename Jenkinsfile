pipeline {
      agent any

      environment {
          DOCKER_IMAGE = 'ebay-app'
          CONTAINER_NAME = 'ebay-container'
          APP_PORT = '80'
          INTERNAL_PORT = '5000'
      }

      stages {
          stage('Checkout') {
              steps {
                  echo 'Checking out code from GitHub...'
                  checkout scm
              }
          }
          stage('Test') {
             options {
                  timeout(time: 20, unit: 'MINUTES')
              }
              steps {
                  sh 'mvn test'
              }
              post {
                  always {
                      junit testResultsPattern: 'target/surefire-reports/*.xml'
                  }
              }
          }
          stage('Build Docker Image') {
              steps {
                  echo 'Building Docker image...'
                  script {
                      sh '''
                          mvn clean package
                          if docker ps -a | grep -q ${CONTAINER_NAME}; then
                              echo "Stopping existing container..."
                              docker stop ${CONTAINER_NAME} || true
                              docker rm ${CONTAINER_NAME} || true
                          fi
                      '''

                      sh 'docker build -t ${DOCKER_IMAGE} .'
                  }
              }
          }

          stage('Deploy') {
              steps {
                  echo 'Deploying new container...'
                  script {
                      sh '''
                          docker run -d \
                            --name ${CONTAINER_NAME} \
                            -p ${APP_PORT}:${INTERNAL_PORT} \
                            -e SPRING_PROFILES_ACTIVE=production \
                            -e AWS_DB_HOST=ebay-postgres-db.cgzibuqw7bgx.us-east-2.rds.amazonaws.com \
                            -e AWS_DB_NAME=ebay_marketplace \
                            -e AWS_DB_USERNAME=ebayuser \
                            -e AWS_DB_PASSWORD=ebaypassword123 \
                            -e AWS_DB_PORT=5432 \
                            --restart unless-stopped \
                            ${DOCKER_IMAGE}
                      '''
                  }
              }
          }

          stage('Health Check') {
              steps {
                  echo 'Performing health check...'
                  script {
                      sleep 30
                      sh 'docker ps | grep ${CONTAINER_NAME}'
                      sh 'curl -f http://localhost:${APP_PORT}/ || exit 1'
                  }
              }
          }
      }

      post {
          success {
              echo 'Pipeline completed successfully!'
          }
          failure {
              echo 'Pipeline failed. Check logs for details.'
              script {
                  sh 'docker logs ${CONTAINER_NAME} || true'
              }
          }
          always {
              sh 'docker image prune -f || true'
          }
      }
  }
