pipeline {
    agent any

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

        stage('style check') {
        	steps {
//         		sh "npm run lint"
//         		sh "npm run lint:style"
							echo "im in style check"
        	}
        }

        stage('test') {
            steps {
//                 sh "npm test"
									echo "im in test"
                }
//             post {
//             	always {
//               	step([$class: 'CoberturaPublisher', coberturaReportFile: 'coverage/cobertura-coverage.xml'])
//               }
//             }
        }

        stage('build image') {

            steps {
                sh "docker build -t 4KeyMetricApp:v${env.BUILD_NUMBER}-${env.CURRENT_DATE} ."
            }
        }

        stage('deploy to dev') {
        	steps {
// 					sh "docker stack ..."
						echo "im in deploy to dev"
        	}
        }

        stage('deploy to uat') {
        	input {
          	message "Should we continue?"
          	ok "Yes, we should."
          	submitter "alice,bob"
          	parameters {
          	    string(name: 'PERSON', defaultValue: 'Mr Jenkins', description: 'Who should I say hello to?')
          	}
          }
        	steps {
      			sh "docker stack ..."
    				echo "im in deploy to uat"
        	}
        }
    }
}
