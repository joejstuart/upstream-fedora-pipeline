def call(Map stageVars = [:]) {
    def currentStage = 'package-tests'
    stage(currentStage) {
        handlePipelineStep {
            try {
                executeInContainer(containerName: 'singlehost-test', containerScript: '/tmp/package-test.sh',
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
