def call(Map parameters = [:]) {
    def stageVars = parameters.get('stageVars', [:])
    def containerName = parameters.get('containerName', 'rpmbuild')
    def containerScript = parameters.get('containerScript', '/tmp/pull_old_task.sh')
    def beforeRunMsg = parameters.get('beforeRunMsg')
    def afterRunMsg = parameters.get('afterRunMsg')
    def failedRunMsg = parameters.get('failedRunMsg')

    stage('koji-build') {
        handlePipelineStep(beforeRunMsg: beforeRunMsg, afterRunMsg: afterRunMsg, failedRunMsg: failedRunMsg) {
            executeInContainer(containerName: containerName, containerScript: containerScript,
                    stageVars: stageVars)
        }
    }
}