pipeline {
    agent any

    triggers {
        pollSCM('H/5 8-23 * * 1-5')
    }


    environment {
        REGISTRY_USERNAME = credentials('registryUsername')
        REGISTRY_PWD = credentials('registryPassword')
        CURRENT_DATE = sh(returnStdout: true, script: "date +'%Y%m%d'").trim()
        IMAGE_NAME = "18.138.19.85:8004/4-key-metrics-service:v${env.BUILD_NUMBER}-${env.CURRENT_DATE}"
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
                sh "docker login -u ${REGISTRY_USERNAME} -p ${REGISTRY_PWD} 18.138.19.85:8004"
                sh "docker push ${env.IMAGE_NAME} "
            }
        }

    }

}
