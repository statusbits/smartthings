/**
 *  Virtual Humidity Tile
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
 *  https://github.com/statusbits/smartthings/blob/master/VirtualThings/VirtualHumidityTile.device.groovy
 *
 *  Revision History
 *  ----------------
 *  2014-08-28  V1.1.0  parse takes 'humidity:<value>' as an argument
 *  2014-08-11  V1.0.0  Initial release
 */

metadata {
    definition (name:"Virtual Humidity Tile", namespace:"statusbits", author:"geko@statusbits.com") {
        capability "Relative Humidity Measurement"
        capability "Sensor"

        // custom commands
        command "parse"     // (String "humidity:<value>")
    }

    tiles {
        valueTile("humidity", "device.humidity", inactiveLabel: false, decoration: "flat") {
            state "humidity", label:'${currentValue}%', unit:"Humidity"
        }

        main(["humidity"])
        details(["humidity"])
    }

    simulator {
        for (int i = 0; i <= 100; i += 20) {
            status "Humidity ${i}%": "humidity:${i}"
        }
    }
}

def parse(String message) {
    TRACE("parse(${message})")

    Map msg = stringToMap(message)
    if (!msg.containsKey("humidity")) {
        log.error "Invalid message: ${message}"
        return null
    }

    Float val = msg.humidity.toFloat()
    def event = [
        name  : "humidity",
        value : val.round(1),
        unit  : "%",
    ]

    TRACE("event: (${event})")
    sendEvent(event)
}

private def TRACE(message) {
    //log.debug message
}