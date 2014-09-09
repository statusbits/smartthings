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
 *  Revision History
 *  ----------------
 *  2014-08-21  V0.9.0  Initial check-in.
 */

definition(
    name: "Sceneplex",
    //namespace: "statusbits",
    namespace: "smartthings",
    author: "geko@statusbits.com",
    description: "Execute 'Hello, Home!' actions via REST API from any web client.",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    oauth: [displayName:"Sceneplex", displayLink:"http://statusbits.github.io"]
    )

preferences {
    page name:"pageSetup"
    page name:"pageAbout"
    page name:"pageEndpoints"
    page name:"pageAddButton"
    page name:"pageShowButtons"
    page name:"completeAddButton"
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

    if (state.buttons == null) {
        // First run - initialize state
        state.buttons = [:]
        state.buttonId = 0
        return pageAbout()
    }

    updateButtonList()

    def pageProperties = [
        name        : "pageSetup",
        title       : "Setup Menu",
        nextPage    : null,
        install     : true,
        uninstall   : state.installed
    ]

    return dynamicPage(pageProperties) {
        section {
            href "pageAbout", title:"About", description:"Tap to open"
            href "pageEndpoints", title:"Configure REST API Endpoints", description:"Tap to open"
            href "pageAddButton", title:"Add Scene Button", description:"Tap to open"
            if (state.buttons?.size()) {
                href "pageShowButtons", title:"Show Scene Buttons", description:"Tap to open"
            }
        }
        section([title:"Options", mobileOnly:true]) {
            label title:"Assign a name", required:false
            //mode title:"Set for specific mode(s)", required:false
        }
    }
}

private def pageAbout() {
    TRACE("pageAbout()")

    def textAbout =
        "Sceneplex allows you to execute 'Hello, Home' actions via REST " +
        "API from any web client."

    def pageProperties = [
        name        : "pageAbout",
        title       : "About",
        nextPage    : "pageSetup",
        install     : false,
        uninstall   : state.installed
    ]

    return dynamicPage(pageProperties) {
        section {
            paragraph textAbout
            paragraph "${textVersion()}\n${textCopyright()}"
        }
        section("License") {
            paragraph textLicense()
        }
    }
}

private def pageEndpoints() {
    TRACE("pageEndpoints()")

    def textAbout =
        "Sceneplex provides REST API that allows a web client to execute " +
        "up to twelve 'Hello, Home' actions using HTTP GET calls. Scroll " +
        "to the bottom of the page for the API URL info."

    def pageProperties = [
        name        : "pageEndpoints",
        title       : "Configure REST API Endpoints",
        nextPage    : "pageSetup",
        install     : false,
        uninstall   : state.installed
    ]

    return dynamicPage(pageProperties) {
        section {
            paragraph textAbout
        }
        section("Scenes") {
            def maxScenes = getMaxScenes()
            def hhActions = getHHActions()
            for (int n = 1; n <= maxScenes; n++) {
                input "scene_${n}", "enum", title:"Scene ${n}", metadata:[values:hhActions], required:false
            }
        }
        section("REST API Info") {
            paragraph "API Base URL:\nhttps://graph.api.smartthings.com/api/smartapps/installations/${app.id}"
            paragraph "Get Scene List:\n<base-url>/scenes"
            paragraph "Execute Scene <number>:\n<base-url>/scene/<number>"
            paragraph "Access Token:\n${getAccessToken()}"
            paragraph "App ID:\n${app.id}"
        }
    }
}

private def pageAddButton() {
    TRACE("pageAddButton()")

    def actions = getHHActions()

    def textAbout =
        "Sceneplex can create virtual switches (buttons) that allow " +
        "executing 'Hello, Home' actions from other smart apps, for " +
        "example IFTTT."

    def inputActionOn = [
        name        : "buttonActionOn",
        type        : "enum",
        title       : "Which scene when switch is On?",
        metadata    : [values:actions],
        required    : true
    ]

    def inputActionOff = [
        name        : "buttonActionOff",
        type        : "enum",
        title       : "Which scene when switch is Off?",
        metadata    : [values:actions],
        required    : false
    ]

    def pageProperties = [
        name        : "pageAddButton",
        title       : "Add Scene Button",
        nextPage    : "completeAddButton",
        install     : false,
        uninstall   : state.installed
    ]

    return dynamicPage(pageProperties) {
        section {
            paragraph textAbout
            input inputActionOn
            input inputActionOff
        }
    }
}

private def completeAddButton() {
    TRACE("completeAddButton()")

    def dni = createButtonId()
    def devParams = [
        name            : "Sceneplex Virtual Switch",
        label           : settings.buttonActionOn,
        completedSetup  : true
    ]

    log.trace "Creating child device: ${dni}, ${devParams}"
    try {
        //addChildDevice('smartthings', 'On/Off Button Tile', dni, null, devParams)
        addChildDevice('statusbits', 'Scene Switch', dni, null, devParams)

        // save button in the app state
        state.buttons[dni] = [
            actionOn    : settings.buttonActionOn,
            actionOff   : settings.buttonActionOff,
        ]
    } catch (e) {
        log.error "Cannot create child device. Error: ${e}"
    }

    return pageSetup()
}

private def pageShowButtons() {
    TRACE("pageShowButtons()")

    def textAbout =
        "Sceneplex can create virtual switches (buttons) that allow you to " +
        "execute 'Hello, Home' actions from other smart apps, for example " +
        "IFTTT."

    def pageProperties = [
        name        : "pageShowButtons",
        title       : "Scene Buttons",
        nextPage    : "pageSetup",
        install     : false,
        uninstall   : state.installed
    ]

    return dynamicPage(pageProperties) {
        section {
            paragraph textAbout
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

def uninstalled() {
    TRACE("uninstalled()")

    // delete all child devices
    def devices = getChildDevices()
    devices?.each {
        try {
            deleteChildDevice(it.deviceNetworkId)
        } catch (e) {
            log.error "Cannot delete device ${it.deviceNetworkId}. Error: ${e}"
        }
    }
}

def initialize() {
    log.trace "${app.name}. ${textVersion()}. ${textCopyright()}"
    STATE()

    state.installed = true
    updateButtonList()
    getAccessToken()
    log.debug "URI: https://graph.api.smartthings.com/api/token/${accessToken}/smartapps/installations/${app.id}/"
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

// Purge buttons that were removed manually
private def updateButtonList() {
    TRACE("updateButtonList()")

    state.buttons.each { k,v ->
        if (!getChildDevice(k)) {
            log.trace "Removing deleted button ${k}"
            state.buttons.remove(k)
        }
    }
}

private def createButtonId() {
    int id = atomicState.buttonId.toInteger() + 1
    state.buttonId = id

    return "SCENEPLEX.${id}"
}

private def getHHActions() {
    def actions = []
    location.helloHome?.getPhrases().each {
        actions << "${it.label}"
    }

    return actions.sort()
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
