/**
 *  Filtrete 3M-50 WiFi Thermostat
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
 *  https://github.com/statusbits/smartthings/blob/master/RadioThermostat/RadioThermostat.device.groovy
 *
 *  Version: 0.9.0
 *  Date: 2014-08-12
 */

preferences {
    input("confIpAddr", "string", title:"IP Address",
        required:true, displayDuringSetup: true)
    input("confTcpPort", "number", title:"TCP Port",
        defaultValue:80, required:true, displayDuringSetup:true)
}

metadata {
    definition (name:"Radio Thermostat", namespace:"statusbits", author:"geko@statusbits.com") {
        capability "Thermostat"
        capability "Temperature Measurement"
        capability "Sensor"
        capability "Refresh"
        capability "Polling"

		// Custom commands
		command "heatLevelUp"
		command "heatLevelDown"
		command "coolLevelUp"
		command "coolLevelDown"
		command "switchMode"
		command "switchFanMode"
    }

    tiles {
        valueTile("temperature", "device.temperature", width:2, height:2) {
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

        standardTile("mode", "device.thermostatMode", inactiveLabel:false, decoration:"flat") {
			state "auto", label:'', icon:"st.thermostat.auto", action:"setOperatingMode"
            state "cool", label:'', icon:"st.thermostat.cool", action:"setOperatingMode"
            state "heat", label:'', icon:"st.thermostat.heat", action:"setOperatingModee"
            state "off", label:'', icon:"st.thermostat.heating-cooling-off", action:"setOperatingMode"
        }

		standardTile("fanMode", "device.thermostatFanMode", inactiveLabel:false, decoration:"flat") {
			state "fanAuto", label:'', icon:"st.thermostat.fan-auto", action:"setFanMode"
			state "fanCirculate", label:'', icon:"st.thermostat.fan-circulate", action:"setFanMode"
			state "fanOn", label:'', icon:"st.thermostat.fan-on", action:"setFanMode"
		}

        main(["temperature"])
        details(["temperature", "mode", "fanMode"])
    }

    simulator {
    	status "Mode: Off":		"mode:off"
    	status "Mode: Auto":	"mode:auto"
    	status "Mode: Cool":	"mode:cool"
    	status "Mode: Heat":	"mode:heat"
    }
}

def parse(String message) {
    TRACE("parse(${message})")

    def msg = stringToMap(message)
    if (msg.mode) {
        setThermostatMode(msg.mode)
    }

    return null
}

// thermostat.setThermostatMode
def setThermostatMode(mode) {
	TRACE("setThermostatMode(${mode})")
}

// thermostat.auto
def auto() {
	TRACE("auto()")
}

// thermostat.cool
def cool() {
	TRACE("cool()")
}

// thermostat.heat
def heat() {
	TRACE("heat()")
}

// thermostat.off
def off() {
	TRACE("off()")
}

// thermostat.emergencyHeat
def emergencyHeat() {
	TRACE("emergencyHeat()")
}

// thermostat.setThermostatFanMode
def setThermostatFanMode(fanMode) {
	TRACE("setThermostatFanMode(${fanMode})")
}

// thermostat.fanAuto
def fanAuto() {
	TRACE("fanAuto()")
}

// thermostat.fanOn
def fanOn() {
	TRACE("fanOn()")
}

// thermostat.fanCirculate
def fanCirculate() {
	TRACE("fanCirculate()")
}

// thermostat.setHeatingSetpoint
def setHeatingSetpoint(tempHeat) {
	TRACE("setHeatingSetpoint(${tempHeat})")
}

// thermostat.setCoolingSetpoint
def setCoolingSetpoint(tempCool) {
	TRACE("setCoolingSetpoint(${tempCool})")
}

// polling.poll 
def poll()
{
    TRACE("poll()")
}

// refresh.refresh
def refresh()
{
    TRACE("refresh()")
}

def setCurrentValue(value) {
    TRACE("setCurrentValue(${value})")

    Float temp = value.toFloat()
    def tempScale = getTemperatureScale()
    if (tempScale == "C") {
        temp = (temp - 32) / 1.8
    }

    def event = [
        name  : "temperature",
        value : temp,
        unit  : tempScale,
    ]

    sendEvent(event)
}

private def TRACE(message) {
    log.debug message
}
