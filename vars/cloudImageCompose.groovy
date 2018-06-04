def call() {
    def stageVars = pipelineData.stageVars(env.CI_MESSAGE)
    stage('cloud-image-compose') {
        handlePipelineStep {
            executeInContainer(containerName: 'cloud-image-compose', containerScript: '/tmp/virt-customize.sh',
                    stageVars: stageVars['cloud-image-compose'])
        }
    }
}