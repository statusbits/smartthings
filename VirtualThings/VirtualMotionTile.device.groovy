/**
 *  Virtual Motion Tile.
 *
 *  Version 1.2 (01/14/2015)
 *
 *  The latest version of this file can be found at:
 *  https://github.com/statusbits/smartthings/blob/master/VirtualThings/VirtualMotionTile.device.groovy
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
    definition (name:"Virtual Motion Tile", namespace:"statusbits", author:"geko@statusbits.com") {
        capability "Motion Sensor"
        capability "Sensor"
        capability "Refresh"

        // custom commands
        command "parse", ["string"] // (string: "motion:<active|inactive>")
    }

    tiles {
	    standardTile("motion", "device.motion", width: 2, height: 2) {
            state("default", label:'', backgroundColor:"#999999")
		    state("active", label:'motion', icon:"st.motion.motion.active", backgroundColor:"#53a7c0")
		    state("inactive", label:'no motion', icon:"st.motion.motion.inactive", backgroundColor:"#ffffff")
	    }

        standardTile("refresh", "device.status", inactiveLabel:false, decoration:"flat") {
            state "default", icon:"st.secondary.refresh", action:"refresh.refresh"
        }

	    main "motion"
	    details(["motion", "refresh"])
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
        name:   "motion",
        value:  value,
    ]

    TRACE("event: (${event})")
    sendEvent(event)
}

// refresh.refresh
def refresh() {
    TRACE("refresh()")
    STATE()
}

private def TRACE(message) {
    //log.debug message
}

private def STATE() {
    log.debug "deviceNetworkId: ${device.deviceNetworkId}"
    log.debug "motion: ${device.currentValue('motion')}"
}
