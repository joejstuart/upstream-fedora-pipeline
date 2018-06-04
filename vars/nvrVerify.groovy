def call() {
    def stageVars = pipelineData.stageVars(env.CI_MESSAGE)
    stage('nvr-verify') {
        handlePipelineStep {
            executeInContainer(containerName: 'singlehost-test', containerScript: '/tmp/verify-rpm.sh',
                    stageVars: stageVars['nvr-verify'])

        }
    }
}