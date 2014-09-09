/**
 *  Scene Switch
 *
 *  This is a switch device tile used for activating 'Hello, Home' actions
 *  with Sceneplex smart app.
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
 *  https://github.com/statusbits/smartthings/blob/master/Sceneplex/SceneSwitch.device.groovy
 *
 *  Revision History
 *  ----------------
 *  2014-09-00  V0.9.0  Initial check-in.
 */

metadata {
    definition (name: "Scene Switch", namespace:"statusbits", author:"geko@statusbits.com") {
        capability "Actuator"
        capability "Switch"
    }

    tiles {
        standardTile("button", "device.switch", width:2, height:2, canChangeIcon:true) {
            state "off", label:'${name}', action:"switch.on", icon:"st.switches.switch.off", backgroundColor:"#ffffff"
            state "on", label:'${name}', action:"switch.off", icon:"st.switches.switch.on", backgroundColor:"#79b821"
        }

        main "button"
        details(["button"])
    }

    simulator {
        // status messages
        status "On": "on"
        status "Off": "off"
    }
}

// parse events into attributes
def parse(String message) {
    TRACE("parse(${message})")

    switch (message) {
    case "on":
        on()
        break;

    case "off":
        off()
        break;

    default:
        log.error "Invalid message: '${message}'"
    }

    STATE()
}

// handle 'on' command
def on() {
    TRACE("on()")
    sendEvent([name:"switch", value:"on"])
}

// handle 'off' command
def off() {
    TRACE("off()")
    sendEvent([name:"switch", value:"off"])
}

private def TRACE(message) {
    log.debug message
}

private def STATE() {
    log.debug "deviceNetworkId : ${device.deviceNetworkId}"
    log.debug "switch          : ${device.currentValue("switch")}"
}
