/**
 *  Virtual Button Device Handler.
 *
 *  Version: 0.1  (01/17/2015)
 *
 *  The latest version of this file can be found at:
 *  https://github.com/statusbits/smartthings/blob/master/VirtualThings/VirtualButton.device.groovy
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
        capability "Refresh"

        // Custom commands
        command "parse", ["string"] // ("button:<number>, action:<pushed | held>")
    }

    tiles {
        standardTile("state", "device.state") {
            state "default", label:'', icon:"st.unknown.zwave.remote-controller", backgroundColor:"#ffffff"
        }

        standardTile("refresh", "device.button", inactiveLabel:false, decoration:"flat") {
            state "default", label:'', icon:"st.secondary.refresh", action:"refresh.refresh"
        }

        main("state")
        details(["state", "refresh"])
    }

    simulator {
        (1..10).each() {
            status "Button ${it} Pushed": "button:${it}, action:pushed"
            status "Button ${it} Held":   "button:${it}, action:held"
        }
    }
}

// Parse events into attributes
def parse(String message) {
    log.debug "parse(${message})"

    def m = stringToMap(message)
    if (m?.button == null || m?.action == null) {
        log.error "Invalid message: ${message}"
        return null
    }

    def button = m.button as Integer
    def action = m.action
    def evt = [
        name:               "button",
        value:              action,
        data:               [buttonNumber:button],
        descriptionText:    "${device.displayName} button ${button} was ${action}",
        isStateChange:      true
    ]

    def event = createEvent(evt)
    log.debug "event: ${event}"

    return event
}

def refresh() {
    STATE()
}

private def STATE() {
    log.debug "Display Name: ${device.displayName}"
    log.debug "Device Name: ${device.currentValue("deviceName")}"
    log.debug "Network ID: ${device.deviceNetworkId}"
}
