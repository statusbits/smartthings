/**
 *  X10 Switch.
 *
 *  SmartDevice type for X10 switches and dimmers.
 *  Visit https://github.com/statusbits/smartthings/blob/master/X10Bridge for
 *  more information.
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
 *  The latest version of this file can be found at:
 *  https://github.com/statusbits/smartthings/blob/master/X10Bridge/X10_Switch.device.groovy
 *
 *  Revision History
 *  ----------------
 *  2014-09-05  V1.0.0  Released into SmartThings community.
 *  2014-08-30  V0.9.0  Initial check-in.
 */

metadata {
    definition (name:"X10 Switch", namespace:"statusbits", author:"geko@statusbits.com") {
        capability "Actuator"
        capability "Switch"
        capability "Refresh"

        // custom attributes
        attribute "networkId", "string"

        // custom commands
        command "parse"     // (String "<attribute>:<value>[,<attribute>:<value>]")
        command "dim"       // Sends X10 Dim command
        command "bright"    // Sends X10 Bright command
    }

    tiles {
        standardTile("switch", "device.switch", width:2, height:2, canChangeIcon:true) {
		    state "off", label:'Off', icon:"st.switches.switch.off", backgroundColor:"#ffffff",
		        action:"switch.on" //, nextState:"on"
		    state "on", label:'On', icon:"st.switches.switch.on", backgroundColor:"#79b821",
		        action:"switch.off" //, nextState:"off"
        }

        standardTile("bright", "device.switch", inactiveLabel:false, decoration:"flat") {
            state "default", label:'Bright', icon:"st.custom.buttons.add-icon",
                action:"bright"
        }

        standardTile("dim", "device.switch", inactiveLabel:false, decoration:"flat") {
            state "default", label:'Dim', icon:"st.custom.buttons.subtract-icon",
                action:"dim"
        }

        valueTile("networkId", "device.networkId", decoration:"flat", inactiveLabel:false) {
            state "default", label:'${currentValue}', inactiveLabel:false
        }

        standardTile("debug", "device.motion", inactiveLabel: false, decoration: "flat") {
            state "default", label:'', action:"refresh.refresh", icon:"st.secondary.refresh"
        }

        main(["switch"])
        details(["switch", "bright", "dim", "networkId", "debug"])

        simulator {
            // status messages
            status "Switch On": "switch:1"
            status "Switch Off": "switch:0"
        }
    }
}

def parse(String message) {
    TRACE("parse(${message})")

    Map msg = stringToMap(message)
    if (msg?.size() == 0) {
        log.error "Invalid message: ${message}"
        return null
    }

    if (msg.containsKey("switch")) {
        def value = msg.switch.toInteger()
        switch (value) {
        case 0: off(); break
        case 1: on(); break
        }
    }

    STATE()
    return null
}

// switch.on() command handler
def on() {
    TRACE("on()")

    if (parent) {
        parent.x10_on(device.deviceNetworkId)
    	sendEvent(name:"switch", value:"on")
    }
}

// switch.off() command handler
def off() {
    TRACE("off()")

    if (parent) {
        parent.x10_off(device.deviceNetworkId)
    	sendEvent(name:"switch", value:"off")
    }
}

// Custom dim() command handler
def dim() {
    TRACE("dim()")

    if (parent) {
        parent.x10_dim(device.deviceNetworkId)
    }
}

// Custom bright() command handler
def bright() {
    TRACE("bright()")

    if (parent) {
        parent.x10_bright(device.deviceNetworkId)
    }
}

// refresh.refresh() command handler
def refresh() {
    TRACE("refresh()")

    // update device network ID
    sendEvent(name:"networkId", value:device.deviceNetworkId)

    STATE()
}

private def TRACE(message) {
    //log.debug message
}

private def STATE() {
    //log.debug "switch is ${device.currentValue("switch")}"
    //log.debug "deviceNetworkId: ${device.deviceNetworkId}"
}
