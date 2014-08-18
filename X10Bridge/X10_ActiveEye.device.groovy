/**
 *  X10 ActiveEye motion detector.
 *
 *  SmartDevice type for X10 ActiveEye (model MS16A) motion detector with
 *  light sensor.
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
 *  https://github.com/statusbits/smartthings/blob/master/X10Bridge/X10_ActiveEye.device.groovy
 *
 *  Useful links:
 *  - ActiveEye product page: http://www.x10.com/ms16a.html
 *  - ActiveEye setup instructions: http://kbase.x10.com/wiki/Active_Eye_Motion_Sensor_Setup
 *
 *  Version: 0.9.0
 *  Date: 2014-08-18
 */

// Device configuration
preferences {
    input("houseCode", "enum", title:"X10 House Code",
        required:true, displayDuringSetup: true,
        metadata:[values:["A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P"]],
        defaultValue: "A")
    input("unitCode", "enum", title:"X10 Unit Code",
        required:true, displayDuringSetup: true,
        metadata:[values:["1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16"]],
        defaultValue: "1")
}

// UI description
metadata {
    definition (name:"X10 ActiveEye", namespace:"statusbits", author:"geko@statusbits.com") {
        capability "Sensor"
        capability "Motion Sensor"
        capability "Illuminance Measurement"
        capability "Refresh"

        // custom commands
        command "setCurrentValue"
    }

    tiles {
        standardTile("motion", "device.motion", width:2, height:2, inactiveLabel:false) {
            state("unknown", label:'unknown', backgroundColor:"#ffffff")
            state("active", label:'motion', icon:"st.motion.motion.active", backgroundColor:"#53a7c0")
            state("inactive", label:'no motion', icon:"st.motion.motion.inactive", backgroundColor:"#ffffff")
        }

        valueTile("illuminance", "device.illuminance", width:1, height:1, inactiveLabel:false) {
            state("unknown", label:'unknown', backgroundColor:"#ffffff")
            state("illuminance", label:'${currentValue}', unit:"lux",
                backgroundColors:[
                    [value: 10,  color: "#767676"],
                    [value: 400, color: "#fbd41b"]
                ]
            )
        }

        standardTile("debug", "device.motion", inactiveLabel: false, decoration: "flat") {
            state "default", label:'', action:"refresh.refresh", icon:"st.secondary.refresh"
        }

        main(["motion", "illuminance"])
        details(["motion", "illuminance", "debug"])

        simulator {
            // status messages
            status "Motion Active":     "motion:active"
            status "Motion Inactive":   "motion:inactive"
            status "Light":             "illuminance:400"
            status "Dark":              "illuminance:10"
        }
    }
}

def parse(String message) {
    setCurrentValue(message)
    return null
}

def refresh() {
    TRACE("refresh()")
}

def setCurrentValue(String message) {
    TRACE("setCurrentValue(${message})")

    if (message == null) {
        return
    }

    def value = stringToMap(message)

    if (value.motion) {
        setMotion(value.motion)
    }

    if (value.illuminance) {
        setIlluminance(value.illuminance)
    }
}

private def setMotion(value) {
    TRACE("setMotion(${value})")

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

private def setIlluminance(value) {
    TRACE("setIlluminance(${value})")

    def event = [
        name  : "illuminance",
        value : value.toInteger(),
        unit  : "lux",
    ]

    sendEvent(event)
}

private def TRACE(message) {
    log.debug message
    log.debug "settings        : ${settings}"
    log.debug "deviceNetworkId : ${device.deviceNetworkId}"
    log.debug "motion          : ${device.currentValue("motion")}"
    log.debug "illuminance     : ${device.currentValue("illuminance")}"
}
