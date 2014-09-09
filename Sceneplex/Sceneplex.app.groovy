/**
 *  Sceneplex
 *
 *  Execute 'Hello, Home' actions via REST API from any web client.
 *
 *  --------------------------------------------------------------------------
 *
 *  Copyright (c) 2014 geko@statusbits.com
 *
 *  This program is free software: you can redistribute it and/or modify it
 *  under the terms of the GNU General Public License as published by the Free
 *  Software Foundation, either version 3 of the License, or (at your option)
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 *  or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 *  for more details.
 *
 *  You should have received a copy of the GNU General Public License along
 *  with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *  --------------------------------------------------------------------------
 *
 *  The latest version of this file can be found at:
 *  https://github.com/statusbits/smartthings/blob/master/Sceneplex/Sceneplex.app.groovy
 *
 *  Version: 0.9.0
 *  Date: 2014-08-21
 */

definition(
    name: "Sceneplex",
    namespace: "statusbits",
    author: "geko@statusbits.com",
    description: "Execute 'Hello, Home!' actions via REST API from any web client.",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    oauth: [displayName:"Sceneplex", displayLink:"http://statusbits.github.io"]
    )

preferences {
    page name:"pageSetup"
}

mappings {
    path("/scenes") {
        action: [
            GET: "listScenes"
        ]
    }

    path("/scene/:id") {
        action: [
            GET: "activateScene"
        ]
    }
}

private def pageSetup() {
    TRACE("pageSetup()")

    def pageProperties = [
        name        : "pageSetup",
        title       : "Sceneplex Setup",
        nextPage    : null,
        install     : true,
        uninstall   : state.installed
    ]

    return dynamicPage(pageProperties) {
        section {
            paragraph "${textVersion()}\n${textCopyright()}"
        }
        section("Scenes") {
            def maxScenes = getMaxScenes()
            def hhActions = getHHActions()
            for (int n = 1; n <= maxScenes; n++) {
                input "scene_${n}", "enum", title:"Scene ${n}", metadata:[values:hhActions], required:false
            }
        }
        section("REST API Info") {
            paragraph "App ID:\n${app.id}"
            paragraph "Access Token:\n${getAccessToken()}"
        }
        section("License") {
            paragraph textLicense()
        }
        section([title:"Options", mobileOnly:true]) {
            label title:"Assign a name", required:false
            //mode title:"Set for specific mode(s)", required:false
        }
    }
}

def installed() {
    TRACE("installed()")
    initialize()
}

def updated() {
    TRACE("updated()")
    unsubscribe()
    initialize()
}

def initialize() {
    log.trace "${app.name}. ${textVersion()}. ${textCopyright()}"
    STATE()

    state.installed = true
    getAccessToken()
    TRACE("URI: https://graph.api.smartthings.com/api/token/${accessToken}/smartapps/installations/${app.id}/")
}

def listScenes() {
    TRACE("listScenes()")

    def scenes = []
    def actions = location.helloHome?.getPhrases()
    actions.each() {
        scenes << [id : it.id, name : it.label]
    }

    return scenes
}

def activateScene() {
    TRACE("activateScene()")

    if (!params.id.isInteger()) {
        def msg = "Invalid scene ID - ${params.id}"
        log.error msg
        return restError(msg)
    }

    def id = params.id.toInteger()
    def action = settings."scene_${id}"
    if (!action) {
        def msg = "Scene ${id} not configured"
        log.error msg
        return restError(msg)
    }

    log.trace "Executing HelloHome action \'${action}\'"
    location.helloHome.execute(action)

    return [error:false, scene:action]
}

private restError(description) {
    def error = [
        error       : true,
        description : description
    ]

    return error
}

private def getHHActions() {
    def actions = []
    location.helloHome?.getPhrases().each {
        actions << "${it.label}"
    }

    return actions
}

private def getAccessToken() {
    if (atomicState.accessToken) {
        return atomicState.accessToken
    }

    def token = createAccessToken()
    TRACE("Created new access token: ${token})")

    return token
}

private def getAppUri() {
    return "https://graph.api.smartthings.com/api/smartapps/installations/${app.id}/"
}

private int getMaxScenes() {
    return 12
}

private def textVersion() {
    return "Version 0.9.0"
}

private def textCopyright() {
    return "Copyright (c) 2014 Statusbits.com"
}

private def textLicense() {
    def text =
        "This program is free software: you can redistribute it and/or " +
        "modify it under the terms of the GNU General Public License as " +
        "published by the Free Software Foundation, either version 3 of " +
        "the License, or (at your option) any later version.\n\n" +
        "This program is distributed in the hope that it will be useful, " +
        "but WITHOUT ANY WARRANTY; without even the implied warranty of " +
        "MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU " +
        "General Public License for more details.\n\n" +
        "You should have received a copy of the GNU General Public License " +
        "along with this program. If not, see <http://www.gnu.org/licenses/>."

    return text
}

private def TRACE(message) {
    log.debug message
}

private def STATE() {
    log.debug "settings: ${settings}"
    log.debug "state: ${state}"
}
