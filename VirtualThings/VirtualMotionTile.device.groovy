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
 *  Revision History
 *  ----------------
 *  2014-08-28  V1.1.0  parse takes 'motion:<value>' as an argument
 *  2014-08-10  V1.0.0  Initial release
 */

metadata {
    definition (name:"Virtual Motion Tile", namespace:"statusbits", author:"geko@statusbits.com") {
        capability "Motion Sensor"
        capability "Sensor"

        // custom commands
        command "parse"     // (String "motion:<active|inactive>")
    }

    tiles {
	    standardTile("motion", "device.motion", width: 2, height: 2) {
            state "default", label:'[Motion]', backgroundColor:"#C0C0C0"
		    state("active", label:'motion', icon:"st.motion.motion.active", backgroundColor:"#53a7c0")
		    state("inactive", label:'no motion', icon:"st.motion.motion.inactive", backgroundColor:"#ffffff")
	    }

	    main "motion"
	    details(["motion"])
    }

    simulator {
        status "Active"         : "motion:active"
        status "Inactive"       : "motion:inactive"
        status "Invalid value"  : "motion:foobar"
        status "Invalid format" : "foobar"
    }
}

def parse(String message) {
    TRACE("parse(${message})")

    Map msg = stringToMap(message)
    if (!msg.containsKey("motion")) {
        log.error "Invalid message: ${message}"
        return null
    }

    def value = msg.motion
    def values = ["active", "inactive"]
    if (null == values.find {it == value }) {
        log.error "Invalid value: ${value}"
        return null
    }

    def event = [
        name  : "motion",
        value : value,
    ]

    TRACE("event: (${event})")
    sendEvent(event)
}

private def TRACE(message) {
    //log.debug message
}
