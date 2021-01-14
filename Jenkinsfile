pipeline {
    agent any

    triggers {
        pollSCM('H/2 * * * *')
    }

    stages {
        stage('Clone Code') {
            steps {
                echo 'Clone Code...'
            }
        }

        stage('Build Project') {
            steps {
                echo 'Build Project...'
            }
        }

        stage('Build Image') {
            steps {
                echo 'Build Image...'
            }
        }

        stage('Deploy to DEV') {
            steps {
                echo 'Deploy to DEV...'
            }
        }

        stage('Deploy to UAT') {
            steps {
                echo 'Deploy to UAT...'
            }
        }
    }
}
