def call() {
    def stageVars = pipelineData.stageVars(env.CI_MESSAGE)
    stage('koji-build') {
        handlePipelineStep {
            executeInContainer(containerName: 'rpmbuild', containerScript: '/tmp/pull_old_task.sh',
                    stageVars: stageVars['koji-build'])
        }
    }
}