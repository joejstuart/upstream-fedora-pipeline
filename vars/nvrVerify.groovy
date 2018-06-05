def call(Map stageVars = [:]) {
    stage('nvr-verify') {
        handlePipelineStep {
            executeInContainer(containerName: 'singlehost-test', containerScript: '/tmp/verify-rpm.sh',
                    stageVars: stageVars)

        }
    }
}