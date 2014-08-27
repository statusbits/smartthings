/**
 *  Virtual Contact Tile
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
 *  https://github.com/statusbits/smartthings/blob/master/VirtualThings/VirtualContactTile.device.groovy
 *
 *  Revision History
 *  ----------------
 *  2014-08-28  V1.1.0  parse takes 'contact:<value>' as an argument
 *  2014-08-10  V1.0.0  Initial release
 */

metadata {
    definition (name:"Virtual Contact Tile", namespace:"statusbits", author:"geko@statusbits.com") {
        capability "Contact Sensor"
        capability "Sensor"

        // custom commands
        command "parse"     // (String "contact:<open|closed>")
    }

    tiles {
        standardTile("contact", "device.contact", width: 2, height: 2) {
            state "default", label:'[Contact]', backgroundColor:"#C0C0C0"
            state "open", label: '${name}', icon: "st.contact.contact.open", backgroundColor: "#ffa81e"
            state "closed", label: '${name}', icon: "st.contact.contact.closed", backgroundColor: "#79b821"
        }

        main(["contact"])
        details(["contact"])
    }

    simulator {
        status "open"           : "contact:open"
        status "closed"         : "contact:closed"
        status "Invalid value"  : "contact:foobar"
        status "Invalid format" : "foobar"
    }
}

def parse(String message) {
    TRACE("parse(${message})")

    Map msg = stringToMap(message)
    if (!msg.containsKey("contact")) {
        log.error "Invalid message: ${message}"
        return null
    }

    def value = msg.contact
    def values = ["open", "closed"]
    if (null == values.find {it == value }) {
        log.error "Invalid value: ${value}"
        return null
    }

    def event = [
        name  : "contact",
        value : value,
    ]

    TRACE("event: (${event})")
    sendEvent(event)
}

private def TRACE(message) {
    //log.debug message
}
