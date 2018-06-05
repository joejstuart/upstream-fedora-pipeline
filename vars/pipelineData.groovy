import org.contralib.Utils


def stageVars(String ciMessage) {
    def utils = new Utils()
    def message = readJSON text: ciMessage

    def branches = utils.setBuildBranch(message['request'][1])
    def fed_repo = utils.repoFromRequest(message['request'][0])

    def stages = [
            'koji-build':

                    [
                            PROVIDED_KOJI_TASKID      : message['task_id'],
                            fed_branch                : branches[1],
                            fed_repo                  : fed_repo,
                            fed_rev                   : message['rev'],
                            rpm_repo                  : "${env.WORKSPACE}/${fed_repo}_repo"

                    ],

            'repoquery':

                    [
                            fed_branch                : branches[1],
                            fed_repo                  : fed_repo,
                            fed_rev                   : message['rev'],
                            rpm_repo                  : "${env.WORKSPACE}/${fed_repo}_repo"

                    ],


            'cloud-image-compose':

                    [
                            rpm_repo                 : "${env.WORKSPACE}/${fed_repo}_repo",
                            package                  : fed_repo,
                            branch                   : branches[0],
                            fed_branch               : branches[1]
                    ],

            'nvr-verify':

                    [
                            python3                  : 'yes',
                            rpm_repo                 : "/etc/yum.repos.d/${fed_repo}",
                            TEST_SUBJECTS            : "${env.WORKSPACE}/images/test_subject.qcow2"

                    ],

            'package-tests':

                    [
                            package                  : fed_repo,
                            python3                  : 'yes',
                            TAG                      : 'classic',
                            branch                   : branches[1],
                            build_pr_id              : (env.fed_pr_id) ?: '',
                            TEST_SUBJECTS            : "${env.WORKSPACE}/images/test_subject.qcow2"

                    ],

            'schedule build':
                    [
                            branch                   : branches[1],
                            PROVIDED_KOJI_TASKID     : message['task_id']

                    ],

            'upstream-fedora-pipeline-build-trigger':
                    [
                            fed_repo                 : fed_repo,
                            fed_branch               : branches[1],
                            fed_instance             : message['instance'],
                            branch                   : branches[0]
                    ],
            'default':
                    [
                            package_name              : fed_repo

                    ]


    ]

    return stages
}


def prTrigger(def stageVars) {
    validMessage = packagepipelineUtils.checkBranch(stageVars['branch'])
    testsExist = pipelineUtils.checkTests(stageVars['fed_repo'], stageVars['fed_id'], 'classic')
    // Function only returns false if comments exist,
    // but the latest was uninteresting
    commentTrigger = pipelineUtils.checkUpdatedPR(env.CI_MESSAGE, '[citest]')
    // create audit message file
    pipelineUtils.initializeAuditFile('messages/auditfile.json')

    return validMessage && testsExist && commentTrigger
}

def buildTrigger(def stageVars) {
    def targetBranch = packagepipelineUtils.checkBranch(stageVars['branch'])
    def testsExist = pipelineUtils.checkTests(stageVars['fed_repo'], stageVars['fed_branch'], 'classic')
    def primaryKoji = stageVars['fed_instance'] == "primary"
    pipelineUtils.initializeAuditFile('messages/auditfile.json')

    return targetBranch && testsExist && primaryKoji
}

/**
 * Library to set message fields to be published
 * @param messageType: ${MAIN_TOPIC}.ci.pipeline.allpackages.<defined-in-README>
 * @param artifact ${MAIN_TOPIC}.ci.pipeline.allpackages-${artifact}.<defined-in-README>
 * @return A Closure that returns a Map
 */
def setMessageFields(String messageType, String artifact, String ciMessage) {
    return {
        def utils = new Utils()
        def message = readJSON text: ciMessage
        def branches = utils.setBuildBranch(message['request'][1])
        def branch = branches[0]
        def fed_branch = branches[1]
        def fed_repo = utils.repoFromRequest(message['request'][0])

        def main_topic = null
        if (env.ghprbActualCommit != null && env.ghprbActualCommit != "master") {
            main_topic = env.MAIN_TOPIC ?: 'org.centos.stage'
        } else {
            main_topic = env.MAIN_TOPIC ?: 'org.centos.prod'
        }

        def topic = "${main_topic}.ci.pipeline.allpackages-${artifact}.${messageType}"

        // Create a HashMap of default message property keys and values
        // These properties should be applicable to ALL message types.
        // If something is applicable to only some subset of messages,
        // add it below per the existing examples.

        def messageProperties = [
                branch           : branch,
                build_id         : env.BUILD_ID,
                build_url        : env.JENKINS_URL + 'blue/organizations/jenkins/' + env.JOB_NAME + '/detail/' + env.JOB_NAME + '/' + env.BUILD_NUMBER + '/pipeline/',
                namespace        : message['namespace'],
                nvr              : env.nvr,
                original_spec_nvr: env.original_spec_nvr,
                ref              : 'x86_64',
                repo             : fed_repo,
                rev              : (artifact == 'build') ? "kojitask-${message['task_id']}" : message['rev'],
                status           : currentBuild.currentResult,
                test_guidance    : "''",
                topic            : topic,
                username         : message['username']
        ]

        // Add image type to appropriate message types
        if (messageType in ['image.queued', 'image.running', 'image.complete', 'image.test.smoke.queued', 'image.test.smoke.running', 'image.test.smoke.complete'
        ]) {
            messageProperties.type = messageType == 'image.running' ? "''" : 'qcow2'
        }

        // Create a string to hold the data from the messageProperties hash map
        String messagePropertiesString = ''

        messageProperties.each { k,v ->
            // Don't add a new line to the last item in the hash map when adding it to the messagePropertiesString
            if ( k == messageProperties.keySet().last()){
                messagePropertiesString += "${k}=${v}"
            } else {
                messagePropertiesString += "${k}=${v}\n"
            }
        }

        def messageContentString = ''

        return [ 'topic': topic, 'properties': messagePropertiesString, 'content': messageContentString ]

    }

}

