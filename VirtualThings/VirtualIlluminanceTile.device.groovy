/**
 *  Virtual Illuminance Tile.
 *
 *  Version 1.2 (01/14/2015)
 *
 *  The latest version of this file can be found at:
 *  https://github.com/statusbits/smartthings/blob/master/VirtualThings/VirtualIlluminanceTile.device.groovy
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
    definition (name:"Virtual Illuminance Tile", namespace:"statusbits", author:"geko@statusbits.com") {
        capability "Illuminance Measurement"
        capability "Sensor"
        capability "Refresh"

        // custom commands
        command "parse", ["string"] // (string: "illuminance:<value>")
    }

    tiles {
        valueTile("illuminance", "device.illuminance", width:2, height:2) {
            state "luminosity", label:'${currentValue} ${unit}', unit:"lux",
                backgroundColors:[
                    [value:10,  color:"#999999"],
                    [value:400, color:"#fbd41b"]
                ]
        }

        standardTile("refresh", "device.status", inactiveLabel:false, decoration:"flat") {
            state "default", icon:"st.secondary.refresh", action:"refresh.refresh"
        }

        main(["illuminance"])
        details(["illuminance", "refresh"])
    }

    simulator {
        for (int i = 10; i < 100; i += 20) {
            status "Illuminance ${i} lux": "illuminance:${i}"
        }
        for (int i = 100; i < 1000; i += 200) {
            status "Illuminance ${i} lux": "illuminance:${i}"
        }
    }
}

def parse(String message) {
    TRACE("parse(${message})")

    Map msg = stringToMap(message)
    if (!msg.containsKey("illuminance")) {
        log.error "Invalid message: ${message}"
        return null
    }

    def event = [
        name:   "illuminance",
        value:  msg.illuminance,
        unit:   "lux"
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
    log.debug "illuminance: ${device.currentValue('illuminance')}"
}
