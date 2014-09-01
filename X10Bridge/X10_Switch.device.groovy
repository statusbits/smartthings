/**
 *  X10 Switch.
 *
 *  SmartDevice type for X10 switches and dimmers.
 *  Visit https://github.com/statusbits/smartthings/blob/master/X10Bridge for
 *  more information.
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
 *  https://github.com/statusbits/smartthings/blob/master/X10Bridge/X10_Switch.device.groovy
 *
 *  Revision History
 *  ----------------
 *  2014-08-30  V0.9.0  Initial check-in
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
		        nextState:"on", action:"switch.on"
		    state "on", label:'On', icon:"st.switches.switch.on", backgroundColor:"#79b821",
		        nextState:"off", action:"switch.off"
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
        parent.x10_on(this)
    	sendEvent(name:"switch", value:"on")
    }
}

// switch.off() command handler
def off() {
    TRACE("off()")

    if (parent) {
        parent.x10_off(this)
    	sendEvent(name:"switch", value:"off")
    }
}

// Custom dim() command handler
def dim() {
    TRACE("dim()")

    if (parent) {
        parent.x10_dim(this)
    }
}

// Custom bright() command handler
def bright() {
    TRACE("bright()")

    if (parent) {
        parent.x10_bright(this)
    }
}

// refresh.refresh() command handler
def refresh() {
    TRACE("refresh()")

    // update device network ID
    sendEvent(name: "networkId", value: device.deviceNetworkId)

    STATE()
}

private def TRACE(message) {
    log.debug message
}

private def STATE() {
    log.debug "switch is ${device.currentValue("switch")}"
    log.debug "deviceNetworkId: ${device.deviceNetworkId}"
}
