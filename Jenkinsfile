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
        REGISTRY_CREDS = credentials('4km_private_registry')
        CURRENT_DATE = sh(returnStdout: true, script: "date +'%Y%m%d'").trim()
        IMAGE_NAME = "${REGISTRY_HOST}/4-key-metrics-service:v${env.BUILD_NUMBER}-${env.CURRENT_DATE}"
        CONTAINER_NAME_DEV = "4km-backend-dev"
        CONTAINER_NAME_UAT = "4km-backend-uat"
    }

    stages {
        stage('Build Docker image') {
            steps {
                echo '-------------------------Build Image-------------------------'

                sh "docker build -t ${env.IMAGE_NAME} ."
            }
        }

        stage('Push Docker image') {
            steps {
                echo '-------------------------Push Image-------------------------'

                sh 'echo $REGISTRY_CREDS_PSW | docker login -u $REGISTRY_CREDS_USR --password-stdin $REGISTRY_HOST'
                sh "docker push ${env.IMAGE_NAME}"
            }
        }

        stage('Deploy to DEV') {
            steps {
                echo '-------------------------Deploy to DEV-------------------------'

                sh "docker container rm --force ${CONTAINER_NAME_DEV}"
                sh "docker run --name ${CONTAINER_NAME_DEV} -d -p 9000:9000 ${env.IMAGE_NAME}"
                sh "./check-container-status.sh ${CONTAINER_NAME_DEV}"
            }
        }

        stage('Deploy to UAT') {
            steps {
                echo '-------------------------Deploy to UAT-------------------------'

                sh "docker container rm --force ${CONTAINER_NAME_UAT}"
                sh "docker run --name ${CONTAINER_NAME_UAT} -d -p 9003:9000 ${env.IMAGE_NAME}"
                sh "./check-container-status.sh ${CONTAINER_NAME_UAT}"
            }
        }

    }

}
