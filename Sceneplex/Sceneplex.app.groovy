/**
 *  Sceneplex
 *
 *  This SmartApp exposes REST endpoints for activating SmartThings scenes
 *  (known as 'Hello Home!' Actions).
 *
 *  Copyright (c) 2014 Statusbits.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may
 *  not use this file except in compliance with the License. You may obtain a
 *  copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  License  for the specific language governing permissions and limitations
 *  under the License.
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
    description: "Activate SmartThings scenes (a.k.a. Hello Home Actions) from any web client.",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    oauth: [displayName:"Sceneplex", displayLink:"http://statusbits.github.io"]
    )

preferences {
    page name:"setupAuthenticate"
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

def setupAuthenticate() {
    TRACE("setupAuthenticate()")

    def clientId = "9e9abb86-0acc-47ec-ad6d-000d323f97a0"
    //def callbackUri = "https://graph.api.smartthings.com/oauth/callback"
    def callbackUri = "http://statusbits.github.io/stauth/sceneplex.html"
    //log.debug "callbackUri=${callbackUri}"

    def oauthUrl = "https://graph.api.smartthings.com/oauth/authorize?response_type=code&scope=app&client_id=${clientId}&redirect_uri=${callbackUri.encodeAsURL()}"
    log.debug "oauthUrl=${oauthUrl}"

    def pageProperties = [
        name:       "setupAuthenticate",
        title:      "Welcome!",
        nextPage:   null,
        install:    true,
        uninstall:  true
    ]

    return dynamicPage(pageProperties) {
        section("Configure") {
            href url:oauthUrl, style:"embedded", title:"Authenticate", description:"Go ahead, tap me.", required:false
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
    TRACE("initialize()")
    subscribe(app, onAppTouch)

    def scenes = listScenes()
    log.debug "Scenes: ${scenes}"
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
    log.debug "params: ${params}"

    def id = params.id
    if (id == null) {
        log.error "missing id"
        return
    }

    def action = location.helloHome?.getPhrases().find { it.id == id }
    if (id == null) {
        log.error "invalid action id ${id}"
        return
    }

    log.debug "id:${params}, action:${action.label}"


    [id:id, action:action.label]
}

def onAppTouch(evt) {
    TRACE("onAppTouch(${evt})")
}

private def TRACE(message) {
    log.debug message
    log.debug "settings: ${settings}"
    log.debug "state: ${state}"
}
