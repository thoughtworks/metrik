pipeline {
    agent any

    triggers {
        pollSCM('H/2 * * * *')
    }

    options {
        timestamps()
    }

    environment {
        REGISTRY_HOST =  "18.138.19.85:8004"
        REGISTRY_USERNAME = credentials('registryUsername')
        REGISTRY_PWD = credentials('registryPassword')
        CURRENT_DATE = sh(returnStdout: true, script: "date +'%Y%m%d'").trim()
        IMAGE_NAME = "${REGISTRY_HOST}/4-key-metrics-service:v${env.BUILD_NUMBER}-${env.CURRENT_DATE}"
    }

    stages {
        stage('Clone Code') {
            steps {
                echo '-------------------------Clone Code-------------------------'

                checkout scm
            }
        }


        stage('Build Docker image') {
            steps {
                echo '-------------------------Build Image-------------------------'

                sh "docker build -t ${env.IMAGE_NAME} ."
            }
        }

        stage('Push Docker image') {
            steps {
                echo '-------------------------Push Image-------------------------'

                sh "docker login -u ${REGISTRY_USERNAME} -p ${REGISTRY_PWD} ${REGISTRY_HOST}"
                sh "docker push ${env.IMAGE_NAME}"
            }
        }

        stage('Deploy to DEV') {
            steps {
                echo '-------------------------Deploy to DEV-------------------------'

                sh "docker login -u ${REGISTRY_USERNAME} -p ${REGISTRY_PWD} ${REGISTRY_HOST}"
                sh "docker pull ${env.IMAGE_NAME}"

                sh "docker run --name 4km-backend-dev -p 9000:9000 ${env.IMAGE_NAME}"
            }
        }

    }

}
