def call(Map stageVars = [:]) {
    stage('koji-build') {
        handlePipelineStep {
            executeInContainer(containerName: 'rpmbuild', containerScript: '/tmp/pull_old_task.sh',
                    stageVars: stageVars)
        }
    }
}