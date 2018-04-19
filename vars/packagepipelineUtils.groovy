import org.centos.pipeline.PackagePipelineUtils

/**
 * A class of methods used in the Jenkinsfile pipeline.
 * These methods are wrappers around methods in the TODO library.
 * TODO - Find out how to libraies are going to be structured
 */
class packagepipelineUtils implements Serializable {

    def packagePipelineUtils = new PackagePipelineUtils()

    // pass in from the jenkinsfile
    def cimetrics

    /**
     * Method to to find DIST_BRANCH to use for rpm NVRs
     * @return
     */
    def setDistBranch() {
        return packagePipelineUtils.setDistBranch()
    }

    /**
     * Method to set message fields to be published
     * @param messageType ${MAIN_TOPIC}.ci.pipeline.<defined-in-README>
     * @return
     */
    def setMessageFields(String messageType) {
        packagePipelineUtils.setMessageFields(messageType)
    }

    /**
     * Method to set default environmental variables. Performed once at start of Jenkinsfile
     * @param envMap Key/value pairs which will be set as environmental variables.
     * @return
     */
    def setDefaultEnvVars(Map envMap = null) {
        packagePipelineUtils.setDefaultEnvVars(envMap)
    }

    /**
     * Method to set stage specific environmental variables.
     * @param stage Current stage
     * @return
     */
    def setStageEnvVars(String stage) {
        packagePipelineUtils.setStageEnvVars(stage)
    }

    def prepareCredentials(String credentials) {
        packagePipelineUtils.prepareCredentials(credentials)
    }

    /**
     * Watch for messages
     * @param msg_provider jms-messaging message provider
     * @param message trigger message
     */
    def watchForMessages(String msg_provider, String message) {
        packagePipelineUtils.watchForMessages(msg_provider, message)
    }

    def ciPipeline(Closure body) {
        try {
            packagePipelineUtils.ciPipeline(body)
        } catch(e) {
            throw e
        } finally {
            cimetrics.writeToInflux()
        }
    }

    def handlePipelineStep(Map config, Closure body) {
        packagePipelineUtils.handlePipelineStep(config, body)
    }

    def timedPipelineStep(Map config, Closure body) {
        def measurement = timedMeasurement()
        cimetrics.timed measurement, config.stepName, {
            packagePipelineUtils.handlePipelineStep(config, body)
        }
    }

    def timedMeasurement() {
        return packagePipelineUtils.timedMeasurement()
    }

    /**
     * Function to check if fed_branch is master or fXX, XX > 19
     * @return bool
     */
    def checkBranch() {
        return packagePipelineUtils.checkBranch()
    }

    /**
     * 
     * @return
     */
    def repoFromRequest() {
        packagepipelineUtils.repoFromRequest(request)
    }

    /**
     * Check the fedora version number. Must be fc[2-9][0-9]
     * @param msgRelease
     * @return null or fedora release
     */
    def checkRelease(String msgRelease) {
        return packagepipelineUtils.checkRelease(msgRelease)
    }


}
