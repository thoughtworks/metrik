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
                try {
                    sh "./gradlew clean build"
                } catch (error) {
                    echo 'Something failed when building project with gradle!'
                    throw error
                }
            }
        }

        stage('Build Image') {
            steps {
                echo '-------------------------Build Image-------------------------'
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
}
