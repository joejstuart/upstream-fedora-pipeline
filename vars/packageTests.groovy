def call(Map parameters = [:]) {
    def stageVars = parameters.get('stageVars', [:])
    def containerName = parameters.get('containerName', 'singlehost-test')
    def containerScript = parameters.get('containerScript', '/tmp/package-test.sh')
    def beforeRunMsg = parameters.get('beforeRunMsg')
    def afterRunMsg = parameters.get('afterRunMsg')
    def failedRunMsg = parameters.get('failedRunMsg')

    def currentStage = 'package-tests'
    stage(currentStage) {
        handlePipelineStep(beforeRunMsg: beforeRunMsg, afterRunMsg: afterRunMsg, failedRunMsg: failedRunMsg) {
            try {
                executeInContainer(containerName: containerName, containerScript: containerScript,
                        stageVars: stageVars)

            } catch (e) {
                if (fileExists("${env.WORKSPACE}/${currentStage}/logs/test.log")) {
                    // set currentBuild.result to update the message status
                    currentBuild.result = 'UNSTABLE'

                } else {
                    throw e
                }
            }
        }
    }

}
