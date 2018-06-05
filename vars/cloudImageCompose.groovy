def call(Map stageVars = [:]) {
    stage('cloud-image-compose') {
        handlePipelineStep {
            executeInContainer(containerName: 'cloud-image-compose', containerScript: '/tmp/virt-customize.sh',
                    stageVars: stageVars)
        }
    }
}