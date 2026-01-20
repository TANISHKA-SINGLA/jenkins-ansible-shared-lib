def call(Map config = [:]) {

    pipeline {
        agent any

        environment {
            SLACK_CHANNEL = config.get('SLACK_CHANNEL_NAME', '#jenkins-alert')
            ENVIRONMENT   = config.get('ENVIRONMENT', 'prod')
            PLAYBOOK      = config.get('playbook', 'playbook.yml')
            INVENTORY     = config.get('inventory', 'inventory.ini')
        }

        stages {

            stage('Checkout Source Code') {
                steps {
                    echo "Source code already checked out by Jenkinsfile"
                }
            }

            stage('User Approval') {
                steps {
                    input message: "Deploy to ${ENVIRONMENT} environment?"
                }
            }

            stage('Ansible Playbook Execution') {
                steps {
                    sh """
                        ansible-playbook -i ${INVENTORY} ${PLAYBOOK}
                    """
                }
            }
        }

        post {
            success {
                echo "Deployment successful for ${ENVIRONMENT}"
            }
            failure {
                echo "Deployment failed for ${ENVIRONMENT}"
            }
        }
    }
}
