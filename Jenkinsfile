pipeline {
    agent { node 'dev' }

    triggers {
        pollSCM('*/2 * * * 1-5')
    }

    environment {
        CURRENT_DATE = sh(returnStdout: true, script: "date +'%Y%m%d'").trim()
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('test') {
            steps {
                sh "npm test"
                }
            }
            post {
            	always {
              	step([$class: 'CoberturaPublisher', coberturaReportFile: 'coverage/cobertura-coverage.xml'])
              }
            }
        }

        stage('build image') {

            steps {
                sh "docker build -t 4KeyMetricApp:v${env.BUILD_NUMBER}-${env.CURRENT_DATE} ."
            }
        }
    }
}
