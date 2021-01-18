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
        DB_CREDS = credentials('4km_db_credential')
        CURRENT_DATE = sh(returnStdout: true, script: "date +'%Y%m%d'").trim()
        IMAGE_NAME = "${REGISTRY_HOST}/4-key-metrics-service:v${env.BUILD_NUMBER}-${env.CURRENT_DATE}"
    }

    stages {
        stage('Build Docker image') {
            steps {
                echo '-------------------------Build Image-------------------------'

                sh "docker build -t $IMAGE_NAME ."
            }
        }

        stage('Push Docker image') {
            steps {
                echo '-------------------------Push Image-------------------------'

                sh 'echo $REGISTRY_CREDS_PSW | docker login -u $REGISTRY_CREDS_USR --password-stdin $REGISTRY_HOST'
                sh 'docker push $IMAGE_NAME'
            }
        }

        stage('Deploy to DEV') {
            steps {
                echo '-------------------------Deploy to DEV-------------------------'

                sh '''
                    #!/bin/bash
                    container_name=4km-backend-dev
                    existing_container_id="$(docker ps -aqf name=$container_name)"
                    if [ "$existing_container_id" ]; then
                        docker container stop $existing_container_id
                        docker container rm $existing_container_id
                    fi
                    docker run --name $container_name -d -p 9000:9000 -e DB_USER=${DB_CREDS_USR} -e DB_PASSWORD=${DB_CREDS_PSW} --net=4km-docker_default $IMAGE_NAME
                    ./check-container-status.sh  $container_name 9000
                '''
            }
        }

        stage('Deploy to UAT') {
            steps {
                echo '-------------------------Deploy to UAT-------------------------'

                timeout(5) {
                    input 'Continue to deploy to UAT?'
                }

                sh '''
                    #!/bin/bash
                    container_name=4km-backend-uat
                    existing_container_id="$(docker ps -aqf name=$container_name)"
                    if [ "$existing_container_id" ]; then
                        docker container stop $existing_container_id
                        docker container rm $existing_container_id
                    fi
                    docker run --name $container_name -d -p 9003:9000 -e DB_USER=${DB_CREDS_USR} -e DB_PASSWORD=${DB_CREDS_PSW} --net=4km-docker_default $IMAGE_NAME
                    ./check-container-status.sh $container_name 9000
                '''
            }
        }

    }

    post {
        failure {
            echo 'Failed to deploy'
        }
    }

}
