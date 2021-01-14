pipeline {
    agent any

    triggers {
        pollSCM('H/2 * * * *')
    }

    options {
        timestamps()
    }

    stages {
        stage('Clone Code') {
            steps {
                echo '-------------------------Clone Code-------------------------'

                checkout scm
            }
        }

        stage('Build Project') {
            steps {
                echo '-------------------------Build Project-------------------------'

                sh "./gradlew clean build"
            }
        }

        stage('Build and Push Image') {
            steps {
                echo '-------------------------Build Image-------------------------'

                docker.withRegistry('https://18.138.19.85:8004', '43a2395c-e529-4624-a7f2-14e54f9bcabf') {
                    def image = docker.build("4-key-metrics-backend:${env.BUILD_ID}")
                    image.push()
                }
            }
        }

        stage('Deploy to DEV') {
            steps {
                echo '-------------------------Deploy to DEV-------------------------'
            }
        }

        stage('Deploy to UAT') {
            steps {
                echo '-------------------------Deploy to UAT-------------------------'
            }
        }
    }

    post {
        always {
            archiveArtifacts artifacts: 'build/libs/**/*.jar', fingerprint: true
            junit 'build/reports/jacoco/test/jacocoTestReport.xml'
        }
    }
}
