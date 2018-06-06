def call(Map parameters = [:]) {
    def stageVars = parameters.get('stageVars', [:])
    def containerName = parameters.get('containerName', 'cloud-image-compose')
    def containerScript = parameters.get('containerScript', '/tmp/virt-customize.sh')
    def beforeRunMsg = parameters.get('beforeRunMsg')
    def afterRunMsg = parameters.get('afterRunMsg')
    def failedRunMsg = parameters.get('failedRunMsg')

    stage('cloud-image-compose') {
        handlePipelineStep(beforeRunMsg: beforeRunMsg, afterRunMsg: afterRunMsg, failedRunMsg: failedRunMsg) {
            executeInContainer(containerName: containerName, containerScript: containerScript,
                    stageVars: stageVars)
        }
    }
}