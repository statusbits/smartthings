/**
 *  Virtual Motion Tile
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
 *  https://github.com/statusbits/smartthings/blob/master/VirtualThings/VirtualMotionTile.device.groovy
 *
 *  Version: 1.0.0
 *  Date: 2014-08-10
 */

metadata {
    definition (name:"Virtual Motion Tile", namespace:"statusbits", author:"geko@statusbits.com") {
        capability "Motion Sensor"
        capability "Sensor"

        // custom commands
        command "setCurrentValue"
    }

    tiles {
	    standardTile("motion", "device.motion", width: 2, height: 2) {
		    state("active", label:'motion', icon:"st.motion.motion.active", backgroundColor:"#53a7c0")
		    state("inactive", label:'no motion', icon:"st.motion.motion.inactive", backgroundColor:"#ffffff")
	    }

	    main "motion"
	    details(["motion"])
    }

    simulator {
        status "Active"         : "current_value:active"
        status "Inactive"       : "current_value:inactive"
        status "Invalid value"  : "current_value:foobar"
        status "Invalid format" : "foobar"
    }
}

def parse(String message) {
    TRACE("parse(${message})")

    def msg = stringToMap(message)
    if (msg.current_value) {
        setCurrentValue(msg.current_value)
    } else {
        log.error "Invalid message: ${message}"
    }

    return null
}

def setCurrentValue(value) {
    TRACE("setCurrentValue(${value})")

    def values = ["active", "inactive"]
    if (null == values.find {it == value }) {
        log.error "Invalid value: ${value}"
        return null
    }

    def event = [
        name  : "motion",
        value : value,
    ]

    sendEvent(event)
}

private def TRACE(message) {
    //log.debug message
}
