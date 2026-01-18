def call() {

    def config = readYaml text: libraryResource('ansible/config.yaml')

    pipeline {
        agent any

        stages {

            stage('Clone') {
                steps {
                    git branch: config.GIT_BRANCH,
                        url: config.GIT_REPO_URL
                }
            }

            stage('User Approval') {
                when {
                    expression { config.KEEP_APPROVAL_STAGE }
                }
                steps {
                    input message: "Approve deployment to ${config.ENVIRONMENT}?"
                }
            }

            stage('Ansible Execution') {
                steps {
                    sh """
                    ansible-playbook \
                      -i ${config.INVENTORY_FILE} \
                      ${config.ANSIBLE_PLAYBOOK}
                    """
                }
            }
        }

        post {
            success {
                notifySlack(
                    config.SLACK_CHANNEL_NAME,
                    "SUCCESS: ${config.ACTION_MESSAGE}"
                )
            }
            failure {
                notifySlack(
                    config.SLACK_CHANNEL_NAME,
                    "FAILED: ${config.ACTION_MESSAGE}"
                )
            }
        }
    }
}
