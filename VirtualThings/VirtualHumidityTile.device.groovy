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
 *  Version: 1.0.0
 *  Date: 2014-08-11
 */

metadata {
    definition (name:"Virtual Humidity Tile", namespace:"statusbits", author:"geko@statusbits.com") {
        capability "Relative Humidity Measurement"
        capability "Sensor"

        // custom commands
        command "setCurrentValue"
    }

    tiles {
        valueTile("humidity", "device.humidity", inactiveLabel: false) {
            state "humidity", label:'${currentValue}% humidity', unit:""
        }

        main(["humidity"])
        details(["humidity"])
    }

    simulator {
        for (int i = 0; i <= 100; i += 20) {
            status "Humidity ${i}%": "current_value:${i}"
        }
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

    def event = [
        name  : "humidity",
        value : value,
        unit  : "%",
    ]

    sendEvent(event)
}

private def TRACE(message) {
    //log.debug message
}
