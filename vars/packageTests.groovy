def call() {
    def stageVars = pipelineData.stageVars(env.CI_MESSAGE)
    def currentStage = 'package-tests'
    stage(currentStage) {
        handlePipelineStep {
            try {
                executeInContainer(containerName: 'singlehost-test', containerScript: '/tmp/package-test.sh',
                        stageVars: stageVars['package-tests'])

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
