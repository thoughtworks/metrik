pipeline {
    agent any

    triggers {
        pollSCM('*/2 * * * 1-5')
    }

    environment {
        REGISTRY_HOST =  '18.138.19.85:8004'
        REGISTRY_CREDS = credentials('4km_private_registry')
        CURRENT_DATE = sh(returnStdout: true, script: "date +'%Y%m%d'").trim()
        IMAGE_NAME = "$REGISTRY_HOST/4key-metric-app:v$BUILD_NUMBER-$CURRENT_DATE"
        CONTAINER_NAME_DEV = '4km-app-dev'
        CONTAINER_NAME_UAT = '4km-app-uat'
    }

    stages {
        stage('Build Docker image') {
            steps {
                sh "docker build -t $IMAGE_NAME ."
            }
        }

        stage('Push Docker image') {
            steps {
                sh 'echo $REGISTRY_CREDS_PSW | docker login -u $REGISTRY_CREDS_USR --password-stdin $REGISTRY_HOST'
                sh "docker push $IMAGE_NAME"
            }
        }

        stage('Deploy to DEV') {
            steps {
                sh "if docker ps | grep '$CONTAINER_NAME_DEV'; then docker stop '$CONTAINER_NAME_DEV'; fi"
                sh "docker run --name '$CONTAINER_NAME_DEV' -d -p 80:80 $IMAGE_NAME"
            }
        }

        stage('Deploy to PROD') {
            steps {
                input 'Continue to deploy to PROD?'
                echo 'Deploy to PROD...'
            }
        }
    }
}
