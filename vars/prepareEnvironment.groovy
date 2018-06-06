def call(Map parameters = [:]) {
    def stageVars = parameters.get('stageVars', [:])

    if (stageVars['displayName']) {
        currentBuild.displayName = stageVars['displayName']
    }

    if (stageVars['description']) {
        currentBuild.description = stageVars['description']
    }

}