/**
 *  Remote Controller.
 *
 *  This smart app allows using remote controls, for example Aeon Labs
 *  Minimote, to execute Routines, change Modes and set Alarm System mode.
 *
 *  --------------------------------------------------------------------------
 *
 *  Copyright © 2015 Statusbits.com. All rights reserved.
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
 *  License for the specific language governing permissions and limitations
 *  under the License.
 *
 *  --------------------------------------------------------------------------
 *
 *  Version 1.0.0 (09/07/2015)
 */

import groovy.json.JsonSlurper

definition(
    name: "Remote Controller",
    namespace: "statusbits",
    author: "geko@statusbits.com",
    description: "Use remote controls to execute Routines, change Modes and set Alarm System mode.",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartthings-device-icons/unknown/zwave/remote-controller@2x.png",
    iconX2Url: "https://s3.amazonaws.com/smartthings-device-icons/unknown/zwave/remote-controller@2x.png",
    oauth: false
)

preferences {
    page(name:"pageSetup")
    page(name:"pageAddButton")
    page(name:"pageEditButton")
}

// Show "Setup Menu" page
private def pageSetup() {
    LOG("pageSetup()")

    def buttons = getButtons()

    def inputRemotes = [
        name:       "remotes",
        type:       "capability.button",
        title:      "Which remote controls?",
        multiple:   true,
        required:   false
    ]

    def hrefAbout = [
        url:        "http://statusbits.github.io/smartthings/",
        style:      "embedded",
        title:      "More information...",
        description:"http://statusbits.github.io/smartthings/",
        required:   false
    ]

    def pageProperties = [
        name:       "pageSetup",
        title:      "Setup",
        nextPage:   null,
        install:    true,
        uninstall:  state.installed ?: false
    ]

    return dynamicPage(pageProperties) {
        section() {
            input inputRemotes
            href "pageAddButton", title:"Add button", description:"Tap to open"
        }

        if (buttons.size() > 0) {
            section("Configure Buttons") {
            buttons.each() { button ->
                def n = button.button.toInteger()
                href "pageEditButton", params:[button:n], title:"Button ${n}",
                    description:"Tap to open"
                }
            }
        }

        section([title:"Options", mobileOnly:true]) {
            label title:"Assign a name", required:false
            //icon title:"Select icon", required:false
        }

        section("About") {
            paragraph "Version ${getVersion()}\n${textCopyright()}"
            href hrefAbout
        }

        section("License") {
            paragraph textLicense()
        }
    }
}

// Show "Add Button" setup page.
private def pageAddButton() {
    LOG("pageAddButton()")

    def textHelp =
        "Enter button number between 1 and 99."

    def pageProperties = [
        name:       "pageAddButton",
        title:      "Add Button",
        nextPage:   "pageEditButton",
    ]

    return dynamicPage(pageProperties) {
        section() {
            paragraph textHelp
            input "cfg_button", "number", title:"Which button?"
        }
    }
}

// Show "Configure Button" setup page.
private def pageEditButton(params) {
    LOG("pageEditButton(${params})")

    def button = params.button?.toInteger()
    if (!button) {
        button = settings.cfg_button?.toInteger()
    }

    if (!button || button < 1 || button > 99) {
        log.error "Invalid button number '${button}'"
        return pageSetup()
    }

    def textHelp =
        "You can configure remote control buttons to execute Routines, " +
        "change Modes or set Alarm System mode.\n\n" +
        "Some remote controls, for example Aeon Labs Minimote, can " +
        "recognize whether the button was pushed momentarily or held down. " +
        "You can configure Remote Controller to perform different actions " +
        "depending on the type of button action."

    def routines = getRoutineNames()
    def modes = getModeNames()
    def alarmModes = ["Away", "Stay", "Off"]

    def pageProperties = [
        name:       "pageEditButton",
        title:      "Configure Button ${button}",
        nextPage:   "pageSetup",
    ]

    return dynamicPage(pageProperties) {
        section() {
            paragraph textHelp
        }

        section("Configure 'Push' Button Actions") {
            input "push_${button}_routine", "enum", title:"Execute Routine",
                    options:routines, required:false
            input "push_${button}_mode", "enum", title:"Change Mode to...",
                    options:modes, required:false
            input "push_${button}_alarm", "enum", title:"Set Alarm System Mode",
                    options:alarmModes, required:false
        }

        section("Configure 'Hold' Button Actions") {
            input "hold_${button}_routine", "enum", title:"Execute Routine",
                    options:routines, required:false
            input "hold_${button}_mode", "enum", title:"Change Mode to...",
                    options:modes, required:false
            input "hold_${button}_alarm", "enum", title:"Set Alarm System Mode",
                    options:alarmModes, required:false
        }
    }
}

def installed() {
    state.installed = true
    initialize()
}

def updated() {
    unsubscribe()
    initialize()
}

def onButtonEvent(evt) {
    LOG("onButtonEvent(${evt.value})")

    if (!state.buttons || !evt.data) {
        return
    }

    def slurper = new JsonSlurper()
    def data = slurper.parseText(evt.data)
    def button = data?.buttonNumber?.toInteger()
    if (!button) {
        log.error "cannot parse button data: ${data}"
        return
    }

    LOG("Button '${button}' was ${evt.value}.")

    def item = state.buttons.find { it.button == button }
    if (!item) {
        LOG("Button '${button}' is not configured")
        return
    }

    def actions = item[evt.value]
    if (!actions) {
        LOG("Unknown button action '${evt.value}'")
        return
    }

    if (actions.routine) {
        executeRoutine(actions.routine)
    }

    if (actions.mode) {
        setMode(actions.mode)
    }

    if (actions.alarm) {
        setAlarmMode(actions.alarm)
    }
}

private def initialize() {
    log.info "Remote. Version ${getVersion()}. ${textCopyright()}"
    LOG("initialize with ${settings}")

    if (settings.remotes) {
        state.buttons = getButtons()
        subscribe(settings.remotes, "button", onButtonEvent)
    } else {
        state.buttons = []
    }

    LOG("state: ${state}")
}

private def getButtons() {
    LOG("getButtons()")

    def buttons = []
    (1..99).each() { btn ->
        def pushRoutine = settings["push_${btn}_routine"]
        def pushMode = settings["push_${btn}_mode"]
        def pushAlarm = settings["push_${btn}_alarm"]?.toLowerCase()
        def holdRoutine = settings["hold_${btn}_routine"]
        def holdMode = settings["hold_${btn}_mode"]
        def holdAlarm = settings["hold_${btn}_alarm"]?.toLowerCase()
        if (pushRoutine || pushMode || pushAlarm || holdRoutine || holdMode || holdAlarm) {
            def button = [
                button: btn,
                pushed: [routine:pushRoutine, mode:pushMode, alarm:pushAlarm],
                held:   [routine:holdRoutine, mode:holdMode, alarm:holdAlarm],
            ]

            buttons << button
        }
    }

    if (buttons.size() > 1) {
        buttons = buttons.sort() { it.button }
    }

    LOG("buttons: ${buttons}")
    return buttons
}

private def getRoutineNames() {
    def routines = location.helloHome?.getPhrases().collect() { it.label }
    return routines.sort()
}

private def getModeNames() {
    def modes = location.modes?.collect() { it.name }
    return modes.sort()
}

private def executeRoutine(name) {
    log.trace "Executing Routine \'${name}\'"
    location.helloHome.execute(name)
}

private def setMode(name) {
    log.trace "Setting location mode to \'${name}\'"
    setLocationMode(name)
}

private def setAlarmMode(name) {
    log.trace "Setting alarm system mode to \'${name}\'"

    def event = [
        name:           "alarmSystemStatus",
        value:          name,
        isStateChange:  true,
        displayed:      true,
        description:    "alarm system status is ${name}",
    ]

    sendLocationEvent(event)
}

private def getVersion() {
    return "1.0.0"
}

private def textCopyright() {
    return "Copyright © 2015 Statusbits.com"
}

private def textLicense() {
    return '''\
Licensed under the Apache License, Version 2.0 (the "License"); you may not \
use this file except in compliance with the License. You may obtain a copy \
of the License at:

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software \
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT \
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the \
License for the specific language governing permissions and limitations \
under the License.\
'''
}

private def LOG(message) {
    //log.trace message
}
