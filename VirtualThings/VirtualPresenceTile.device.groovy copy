/**
 *  Virtual Presence Tile
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
 *  https://github.com/statusbits/smartthings/blob/master/VirtualThings/VirtualPresenceTile.device.groovy
 *
 *  Revision History
 *  ----------------
 *  2014-08-27  V1.0.0  Initial release
 */

metadata {
    definition (name:"Virtual Presence Tile", namespace:"statusbits", author:"geko@statusbits.com") {
        capability "Presence Sensor"
        capability "Sensor"

        // custom commands
        command "parse"     // (String "presence:<present|not present>")
    }

    tiles {
        standardTile("presence", "device.presence", width:2, height:2, canChangeBackground:true) {
            state "default", label:'[Presence]', backgroundColor:"#C0C0C0"
            state "present", labelIcon:"st.presence.tile.present", backgroundColor:"#53a7c0"
            state "not present", labelIcon:"st.presence.tile.not-present", backgroundColor:"#ffffff"
        }

        main "presence"
        details(["presence"])
    }

    simulator {
        status "Present"        : "presence:present"
        status "Not present"    : "presence:not present"
        status "Invalid value"  : "presence:foobar"
        status "Invalid format" : "foobar"
    }
}

def parse(String message) {
    TRACE("parse(${message})")

    Map msg = stringToMap(message)
    if (!msg.containsKey("presence")) {
        log.error "Invalid message: ${message}"
        return null
    }

    def value = msg.presence
    def values = ["present", "not present"]
    if (null == values.find {it == value }) {
        log.error "Invalid value: ${value}"
        return null
    }

    def event = [
        name  : "presence",
        value : value,
    ]

    TRACE("event: (${event})")
    sendEvent(event)
}

private def TRACE(message) {
    //log.debug message
}
