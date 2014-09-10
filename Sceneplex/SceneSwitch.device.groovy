/**
 *  Scene Switch
 *
 *  This is a switch device tile used for activating 'Hello, Home' actions
 *  with Sceneplex smart app.
 *
 *  Copyright (c) 2014 geko@statusbits.com
 *
 *  The latest version of this file can be found at:
 *  https://github.com/statusbits/smartthings/blob/master/Sceneplex/SceneSwitch.device.groovy
 *
 *  Revision History
 *  ----------------
 *  2014-09-09  V1.0.0  Initial release.
 */

metadata {
    definition (name: "Scene Switch", namespace:"statusbits", author:"geko@statusbits.com") {
        capability "Actuator"
        capability "Switch"
    }

    tiles {
        standardTile("button", "device.switch", width:2, height:2, canChangeIcon:true) {
            state "off", label:'${name}', action:"switch.on", icon:"st.Entertainment.entertainment1", backgroundColor:"#ffffff"
            state "on", label:'${name}', action:"switch.off", icon:"st.Entertainment.entertainment1", backgroundColor:"#79b821"
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
        log.error "Cannot parse message '${message}'"
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
    //log.debug message
}

private def STATE() {
    //log.debug "deviceNetworkId : ${device.deviceNetworkId}"
    //log.debug "switch          : ${device.currentValue("switch")}"
}
