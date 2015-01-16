/**
 *  GE 45600 Remote Control.
 *
 *  This devices handler makes it possible to use GE 45600/45601 Z-Wave
 *  handheld remote controls as a SmartThings scene controller similar to Aeon
 *  Labs' Minimote. All 9 buttons (1..9) generate "button" events that can be
 *  subscribed to by smart apps (for example "Button Controller") and used to
 *  perform any desired action.
 *
 *  Version: 1.0  (2015-01-16)
 *
 *  The latest version of this file can be found at:
 *  https://github.com/statusbits/smartthings/blob/master/GE45600/GE45600.device.groovy
 *
 *  --------------------------------------------------------------------------
 *
 *  Copyright (c) 2014 Statusbits.com
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
    definition (name:"GE45600 Remote", namespace:"statusbits", author:"geko@statusbits.com") {
        capability "Button"
        fingerprint deviceId: "0x01"
    }

    simulator {
        (1..9).each() {
            status "Button ${it}": "command: 2B01, payload: 0${it} FF"
        }
    }

    tiles {
        standardTile("state", "device.state", width: 2, height: 2) {
            state "connected", label:"", icon:"st.unknown.zwave.remote-controller", backgroundColor:"#ffffff"
        }

        main("state")
        details("state")
    }
}

def parse(String description) {
    //log.debug "parse(${description})"

    def cmd = zwave.parse(description)
    if (cmd == null) {
        log.error "Cannot parse '${description}'"
        return null
    }

    return createEvent(zwaveEvent(cmd))
}

// Handles Z-Wave SceneActivationSet command
def zwaveEvent(physicalgraph.zwave.commands.sceneactivationv1.SceneActivationSet cmd) {
    //log.debug "SceneActivationSet command: ${cmd}"

    def button = cmd.sceneId as Integer
    def event = [
        name:               "button",
        value:              "pushed",
        data:               [buttonNumber:button],
        descriptionText:    "${device.displayName} button ${button} was pushed",
        isStateChange:      true
    ]

    return event
}

// Handles all Z-Wave commands we aren't interested in
def zwaveEvent(physicalgraph.zwave.Command cmd) {
    //log.debug "Z-Wave command: ${cmd}"
    return [:]
}
