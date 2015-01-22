/**
 *  Virtual Button Device Handler.
 *
 *  Version: 1.0  (01/22/2015)
 *
 *  The latest version of this file can be found at:
 *  https://github.com/statusbits/smartthings/blob/master/VirtualThings/VirtualButton.groovy
 *
 *  --------------------------------------------------------------------------
 *
 *  Copyright (c) 2015 Statusbits.com
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
 */

metadata {
    definition (name:"Virtual Button", namespace:"statusbits", author:"geko@statusbits.com") {
        capability "Button"

        // Custom commands
        command "parse", ["string"]             // parse("button:<number>, action:<pushed | held>")
        command "button", ["number", "string"]  // button(number, action)
        command "actionMainTile"                // main tile action handler
    }

    tiles {
        standardTile("state", "device.state", decoration:"flat") {
            state "default", label:'', icon:"st.unknown.zwave.remote-controller",
                backgroundColor:"#ffffff", action:"actionMainTile"
        }

        main("state")
        details(["state"])
    }

    simulator {
        (1..4).each() {
            status "Button ${it} Pushed":    "button:${it}, action:pushed"
            status "Button ${it} Held":      "button:${it}, action:held"
        }
    }
}

preferences {
    input("tileAction", "enum", title:"Tile action", required:true,
            metadata:[values:["None","Push","Hold"]], defaultValue:"None",
            displayDuringSetup:true)

    input("tileButton", "number", title:"Button number", required:true,
            defaultValue:"1", displayDuringSetup:true)
}

def updated() {
    log.info "Virtual Button. Version 1.0. Copyright © 2015 Statusbits.com"
    LOG("updated with ${settings}")

    def actions = [
        "Push" : "pushed",
        "Hold" : "held"
    ]

    // parse settings
    state.tileAction = settings.tileAction ? actions[settings.tileAction] : null
    state.tileButton = settings.tileButton?.toInteger() ?: 0
    LOG("state: ${state}")
}

// Parse events
def parse(String message) {
    LOG("parse(${message})")

    def map = stringToMap(message)
    if (map?.button == null) {
        log.warn "Cannot parse '${message}'"
    } else {
        if (map.action == null) {
            button(map.button)
        } else {
            button(map.button, map.action)
        }
    }

    return null
}

// 'button' command handler
def button(def button, String action = "pushed") {
    LOG("button(${button}, ${action})")

    int btnNumber = 0
    try {
        btnNumber = button as Integer
    }
    catch (e) {
        //log.error e
        log.error "Invalid argument: '${button}'"
        return
    }

    if (btnNumber < 1) {
        log.error "Invalid button number: '${btnNumber}'"
        return
    }

    def event = [
        name:               "button",
        value:              action,
        data:               [buttonNumber:btnNumber],
        descriptionText:    "${device.displayName} button ${btnNumber} was ${action}",
        isStateChange:      true
    ]

    LOG("sending event ${event}")
    sendEvent(event)
}

// Main tile action handler
def actionMainTile() {
    LOG("actionMainTile()")

    if (state.tileAction) {
        button(state.tileButton, state.tileAction)
    }
}

private def LOG(message) {
    //log.debug message
}
