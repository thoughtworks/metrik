pipeline {
    agent { node 'dev' }

    triggers {
        pollSCM('H/5 8-23 * * 1-5')
    }

    environment {
        CURRENT_DATE = sh(returnStdout: true, script: "date +'%Y%m%d'").trim()
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('test') {
            steps {
                script {
                    def err = null
                    try {
                        sh "./gradlew clean build"
                    } catch (caughtError) {
                        err = caughtError
                    }
                }
            }
        }

        stage('coverage') {
            steps {
                jacoco(
                        execPattern: 'build/jacoco/test.exec',
                        classPattern: 'build/classes/java/main',
                        sourcePattern: 'src/main/java',
                        exclusionPattern: "${testCoverageExclusions}"
                )
            }
        }

        stage('build package') {
            steps {
                sh "./gradlew clean build -x test"
            }
        }

        stage('build image') {

            steps {
                sh "docker build -t ezltwkregdck001.azurecr.io/campaign-bff:v${env.BUILD_NUMBER}-${env.CURRENT_DATE} ."
            }
        }

        stage('deploy dev') {
            agent { label 'dev' }

            steps {
                milestone(1)

                lock('campaign-bff') {
                    sh "./deploy.sh run dev v${env.BUILD_NUMBER}-${env.CURRENT_DATE} "
                }
            }
        }
    }
}
