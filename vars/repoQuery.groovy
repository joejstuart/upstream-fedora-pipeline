def call(Map stageVars = [:]) {
    stage('repoquery') {
        handlePipelineStep {
            executeInContainer(containerName: 'rpmbuild', containerScript: '/tmp/repoquery.sh',
                    stageVars: stageVars)
        }
    }
}