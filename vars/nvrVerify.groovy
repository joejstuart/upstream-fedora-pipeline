def call(Map parameters = [:]) {
    def stageVars = parameters.get('stageVars', [:])
    def containerName = parameters.get('containerName', 'singlehost-test')
    def containerScript = parameters.get('containerScript', '/tmp/verify-rpm.sh')
    def beforeRunMsg = parameters.get('beforeRunMsg',{})
    def afterRunMsg = parameters.get('afterRunMsg', {})
    def failedRunMsg = parameters.get('failedRunMsg', {})

    stage('nvr-verify') {
        handlePipelineStep(beforeRunMsg: beforeRunMsg, afterRunMsg: afterRunMsg, failedRunMsg: failedRunMsg) {
            executeInContainer(containerName: containerName, containerScript: containerScript,
                    stageVars: stageVars)

        }
    }
}