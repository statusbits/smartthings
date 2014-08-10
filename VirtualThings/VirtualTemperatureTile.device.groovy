/**
 *  Virtual Temperature Tile
 *
 *  Copyright (c) 2014 Statusbits.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use
 *  this file except in compliance with the License. You may obtain a copy of the
 *  License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed
 *  under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 *  CONDITIONS OF ANY KIND, either express or implied. See the License  for the
 *  specific language governing permissions and limitations under the License.
 *
 *  The latest version of this file can be found at:
 *  https://github.com/statusbits/smartthings/blob/master/VirtualThings/VirtualTemperatureSensor.device.groovy
 *
 *  Version: 1.0.0
 *  Date: 2014-08-10
 */

metadata {
    definition (name:"Virtual Temperature Tile", namespace:"statusbits", author:"geko@statusbits.com") {
        capability "Temperature Measurement"
        capability "Sensor"

        // custom commands
        command "parse"
    }

    tiles {
        valueTile("temperature", "device.temperature", width: 2, height: 2) {
            state("temperature", label:'${currentValue}°', unit:"F",
                backgroundColors:[
                    [value: 31, color: "#153591"],
                    [value: 44, color: "#1e9cbb"],
                    [value: 59, color: "#90d2a7"],
                    [value: 74, color: "#44b621"],
                    [value: 84, color: "#f1d801"],
                    [value: 95, color: "#d04e00"],
                    [value: 96, color: "#bc2323"]
                ]
            )
        }

        main(["temperature"])
        details(["temperature"])
    }

    simulator {
        status "Temperature 32.0": "current_value:32.0"
        status "Temperature 58.5": "current_value:58.5"
        status "Temperature 72.0": "current_value:72.0"
        status "Temperature 82.5": "current_value:82.5"
        status "Temperature 94.5": "current_value:94.5"
        status "Temperature 96.0": "current_value:96.0"
        status "Invalid message" : "foobar:100.0"
    }
}

def parse(String message) {
    TRACE("parse(${message})")

    def msg = stringToMap(message)
    if (msg.current_value == null) {
        log.error "Invalid message: ${message}"
        return null
    }

    def temp = msg.current_value.toFloat()

    def event = [
        name  : "temperature",
        value : temp,
        unit  : "F",
    ]

    return createEvent(event)
}

private def TRACE(message) {
    //log.debug message
}
