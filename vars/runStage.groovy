def call(Map parameters = [:]) {
    def stageName = parameters.get('stageName', 'pipeline-stage')
    def stageVars = parameters.get('stageVars', [:])
    def containerName = parameters.get('containerName')
    def containerScript = parameters.get('containerScript')
    def beforeRunMsg = parameters.get('beforeRunMsg',[:])
    def afterRunMsg = parameters.get('afterRunMsg', [:])
    def failedRunMsg = parameters.get('failedRunMsg', [:])

    stage(stageName) {
        handlePipelineStep(beforeRunMsg: beforeRunMsg, afterRunMsg: afterRunMsg, failedRunMsg: failedRunMsg) {
            executeInContainer(containerName: containerName, containerScript: containerScript, stageVars: stageVars)
        }
    }
}