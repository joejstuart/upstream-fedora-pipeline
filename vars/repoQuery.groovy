def call() {
    def stageVars = pipelineData.stageVars(env.CI_MESSAGE)
    stage('repoquery') {
        handlePipelineStep {
            executeInContainer(containerName: 'rpmbuild', containerScript: '/tmp/repoquery.sh',
                    stageVars: stageVars['repoquery'])
        }
    }
}