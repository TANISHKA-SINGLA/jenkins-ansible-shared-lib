def call(Map overrideConfig = [:]) {

    def config = [:]

    script {
        config = readYaml(
            text: libraryResource('ansible/config.yaml')
        )
    }

    // Allow Jenkinsfile to override config values
    config << overrideConfig

    stage('Ansible Execution') {
        sh """
        pwd
        ls -la
        ansible-playbook \
          -i ${config.INVENTORY_FILE} \
          ${config.ANSIBLE_PLAYBOOK}
        """
    }

    if (config.SLACK_CHANNEL_NAME) {
        slackSend(
            channel: config.SLACK_CHANNEL_NAME,
            message: "✅ ${config.ACTION_MESSAGE}"
        )
    }
}
