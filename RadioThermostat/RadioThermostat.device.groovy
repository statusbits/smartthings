/**
 *  Filtrete 3M-50 WiFi Thermostat
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
 *  https://github.com/statusbits/smartthings/blob/master/RadioThermostat/RadioThermostat.device.groovy
 *
 *  Revision History
 *  ----------------
 *  2014-08-12: Version: 0.9.0
 */

import groovy.json.JsonSlurper

preferences {
    input("confIpAddr", "string", title:"Thermostat IP Address",
        required:true, displayDuringSetup: true)
    input("confTcpPort", "number", title:"Thermostat TCP Port",
        defaultValue:"80", required:true, displayDuringSetup:true)
}

metadata {
    definition (name:"Radio Thermostat", namespace:"statusbits", author:"geko@statusbits.com") {
        capability "Thermostat"
        capability "Temperature Measurement"
        capability "Sensor"
        capability "Refresh"
        capability "Polling"

        // Custom attributes
        attribute "hold", "string"      // Target temperature Hold status. Values: "on", "off"
        attribute "fanState", "string"  // Fan operating state. Values: "on", "off"

        // Custom commands
        command "heatLevelUp"
        command "heatLevelDown"
        command "coolLevelUp"
        command "coolLevelDown"
        command "holdOn"
        command "holdOff"
    }

    tiles {
        valueTile("temperature", "device.temperature") {
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

        standardTile("operatingState", "device.thermostatOperatingState", inactiveLabel:false, decoration:"flat") {
            state "default", label:'Unknown State'
            state "idle", label:'Idle'
            state "heating", label:'', icon:"st.thermostat.heating"
            state "cooling", label:'', icon:"st.thermostat.cooling"
        }

        standardTile("fanState", "device.fanState", inactiveLabel:false, decoration:"flat") {
            state "default", label:'Unknown Fan State'
            state "on", label:'', icon:"st.thermostat.fan-on"
            state "off", label:'', icon:"st.thermostat.fan-off"
        }

        //standardTile("mode", "device.thermostatMode", inactiveLabel:false, decoration:"flat") {
        standardTile("mode", "device.thermostatMode", inactiveLabel:false) {
            state "default", label:'Unknown Mode'
            state "heat", label:'', icon:"st.thermostat.heat", action:"thermostat.cool"
            state "cool", label:'', icon:"st.thermostat.cool", action:"thermostat.auto"
            state "auto", label:'', icon:"st.thermostat.auto", action:"thermostat.off"
            state "off", label:'', icon:"st.thermostat.heating-cooling-off", action:"thermostat.heat"
        }

        //standardTile("fanMode", "device.thermostatFanMode", inactiveLabel:false, decoration:"flat") {
        standardTile("fanMode", "device.thermostatFanMode", inactiveLabel:false) {
            state "default", label:'Unknown Fan Mode'
            state "auto", label:'', icon:"st.thermostat.fan-auto", action:"thermostat.fanAuto"
            state "on", label:'', icon:"st.thermostat.fan-on", action:"thermostat.fanCirculate"
            state "circulate", label:'', icon:"st.thermostat.fan-circulate", action:"thermostat.fanAuto"
        }

        valueTile("heatingSetpoint", "device.heatingSetpoint", inactiveLabel:false) {
            state "default", label:'${currentValue}° heat', unit:"F"
        }

        valueTile("coolingSetpoint", "device.coolingSetpoint", inactiveLabel:false) {
            state "default", label:'${currentValue}° cool', unit:"F"
        }

        standardTile("heatLevelUp", "device.heatingSetpoint", canChangeIcon: false, inactiveLabel: false, decoration: "flat") {
            state "default", label:'Heating', icon:"st.custom.buttons.add-icon", action:"heatLevelUp"
        }

        standardTile("heatLevelDown", "device.heatingSetpoint", canChangeIcon: false, inactiveLabel: false, decoration: "flat") {
            state "default", label:'Heating', icon:"st.custom.buttons.subtract-icon", action:"heatLevelDown"
        }

        standardTile("coolLevelUp", "device.coolingSetpoint", canChangeIcon: false, inactiveLabel: false, decoration: "flat") {
            state "default", label:'Cooling', icon:"st.custom.buttons.add-icon", action:"coolLevelUp"
        }

        standardTile("coolLevelDown", "device.coolingSetpoint", canChangeIcon: false, inactiveLabel: false, decoration: "flat") {
            state "default", label:'Cooling', icon:"st.custom.buttons.subtract-icon", action:"coolLevelDown"
        }

        standardTile("hold", "device.hold", inactiveLabel:false, decoration:"flat") {
            state "default", label:'Hold ${currentValue}'
            //state "on", label:'Hold On', action:"holdOff"
            //state "off", label:'Hold Off', action:"holdOn"
        }

        standardTile("refresh", "device.thermostatMode", inactiveLabel:false, decoration:"flat") {
            state "default", action:"refresh.refresh", icon:"st.secondary.refresh"
        }

        main(["temperature"])

        details(["temperature", "operatingState", "fanState",
            "heatingSetpoint", "heatLevelDown", "heatLevelUp",
            "coolingSetpoint", "coolLevelDown", "coolLevelUp",
            "mode", "fanMode", "hold",
            "refresh"])
    }

    simulator {
        status "Mode: Off":         "mode:off"
        status "Mode: Auto":        "mode:auto"
        status "Mode: Cool":        "mode:cool"
        status "Mode: Heat":        "mode:heat"
        status "Fan: Auto":         "fan:auto"
        status "Fan: On":           "fan:on"
        status "Fan: Circulate":    "fan:circulate"
    }
}

def parse(String message) {
    TRACE("parse(${message})")

    def msg = stringToMap(message)

    if (msg.headers) {
        def headers = new String(msg.headers.decodeBase64())
        def body = null
        if (msg.body) {
            body = new String(msg.body.decodeBase64())
        }

        return parseHttpResponse(headers, body)
    }

    //if (msg.mode) {
    //    setThermostatMode(msg.mode)
    //}

    return null
}

// thermostat.setThermostatMode
def setThermostatMode(mode) {
    TRACE("setThermostatMode(${mode})")

    switch (mode) {
    case "off":             return off()
    case "auto":            return auto()
    case "cool":            return cool()
    case "heat":            return heat()
    case "emergency heat":  return emergencyHeat()
    }

    log.error "Invalid thermostat mode: \'${mode}\'"
}

// thermostat.auto
def auto() {
    TRACE("auto()")
    writeTstatValue("tmode", 3)
}

// thermostat.cool
def cool() {
    TRACE("cool()")
    writeTstatValue("tmode", 2)
}

// thermostat.heat
def heat() {
    TRACE("heat()")
    writeTstatValue("tmode", 1)
}

// thermostat.off
def off() {
    TRACE("off()")
    writeTstatValue("tmode", 0)
}

// thermostat.emergencyHeat
def emergencyHeat() {
    TRACE("emergencyHeat()")
    log.warn "'emergency heat' mode is not supported"
}

// thermostat.setThermostatFanMode
def setThermostatFanMode(fanMode) {
    TRACE("setThermostatFanMode(${fanMode})")

    switch (fanMode) {
    case "auto":        return fanAuto()
    case "on":          return fanOn()
    case "circulate":   return fanCirculate()
    }

    log.error "Invalid fan mode: \'${fanMode}\'"
}

// thermostat.fanAuto
def fanAuto() {
    TRACE("fanAuto()")
    writeTstatValue("fmode", 0)
}

// thermostat.fanOn
def fanOn() {
    TRACE("fanOn()")
    writeTstatValue("fmode", 2)
}

// thermostat.fanCirculate
def fanCirculate() {
    TRACE("fanCirculate()")
    writeTstatValue("fmode", 1)
}

// thermostat.setHeatingSetpoint
def setHeatingSetpoint(tempHeat) {
    TRACE("setHeatingSetpoint(${tempHeat})")

    //sendEvent(name:'heatingSetpoint', value:tempHeat)
}

// thermostat.setCoolingSetpoint
def setCoolingSetpoint(tempCool) {
    TRACE("setCoolingSetpoint(${tempCool})")

    //sendEvent(name:'coolingSetpoint', value:tempCool)
}

def heatLevelDown() {
    TRACE("heatLevelDown()")

    def currentT = device.currentValue("coolingSetpoint")?.toInteger()
    if (currentT && currentT > 40) {
        setHeatingSetpoint(currentT - 1)
    }
}

def heatLevelUp() {
    TRACE("heatLevelUp()")

    def currentT = device.currentValue("coolingSetpoint")?.toInteger()
    if (currentT && currentT < 99) {
        setHeatingSetpoint(currentT + 1)
    }
}

def coolLevelDown() {
    TRACE("coolLevelDown()")

    def currentT = device.currentValue("coolingSetpoint")?.toInteger()
    if (currentT && currentT > 40) {
        setCoolingSetpoint(currentT - 1)
    }
}

def coolLevelUp() {
    TRACE("coolLevelUp()")

    def currentT = device.currentValue("coolingSetpoint")?.toInteger()
    if (currentT && currentT < 99) {
        setCoolingSetpoint(currentT + 1)
    }
}

def holdOn() {
    TRACE("holdOn()")
    writeTstatValue("hold", 1)
}

def holdOff() {
    TRACE("holdOff()")
    writeTstatValue("hold", 0)
}

// polling.poll 
def poll() {
    TRACE("poll()")
    refresh()
}

// refresh.refresh
def refresh() {
    TRACE("refresh()")

    setNetworkId(confIpAddr, confTcpPort)
    apiGet("/tstat")
}

// Sets device Network ID in 'AAAAAAAA:PPPP' format
private String setNetworkId(ipaddr, port) { 
    TRACE("setNetworkId(${ipaddr}, ${port})")

    def hexIp = ipaddr.tokenize('.').collect {
        String.format('%02X', it.toInteger())
    }.join()

    def hexPort = String.format('%04X', port.toInteger())
    device.deviceNetworkId = "${hexIp}:${hexPort}"
    log.debug "device.deviceNetworkId = ${device.deviceNetworkId}"
}

private apiGet(String path) {
    TRACE("apiGet(${path})")

    def headers = [
        HOST:       "${confIpAddr}:${confTcpPort}",
        Accept:     "*/*"
    ]

    def httpRequest = [
        method:     'GET',
        path:       path,
        headers:    headers
    ]

    return new physicalgraph.device.HubAction(httpRequest)
}

private apiPost(String path, data) {
    TRACE("apiPost(${path}, ${data})")

    def headers = [
        HOST:       "${confIpAddr}:${confTcpPort}",
        Accept:     "*/*"
    ]

    def httpRequest = [
        method:     'POST',
        path:       path,
        headers:    headers,
        body:       data
    ]

    return new physicalgraph.device.HubAction(httpRequest)
}

private def writeTstatValue(name, value) {
    TRACE("writeTstatValue(${name}, ${value})")

    def json = "{\"${name}\": ${value}}"
    def hubActions = [
        apiPost("/tstat", json),
        getDelayHubAction(2000),
        refresh()
    ]

    //TRACE("hubActions: ${hubActions}")
    return hubActions
}

private def getDelayHubAction(ms) {
    return new physicalgraph.device.HubAction("delay ${ms}")
}

private parseHttpResponse(String headers, String body) {
    TRACE("headers: ${headers}")
    TRACE("body: ${body}")

    // parse headers
    def parsedHeaders = parseHttpHeaders(headers)
    TRACE("parsedHeaders: ${parsedHeaders}")
    if (parsedHeaders.status != 200) {
        log.error "Server error: ${parsedHeaders.reason}"
        return null
    }

    // parse body
    if (body == null) {
        return null
    }

    def slurper = new JsonSlurper()
    def tstat = slurper.parseText(body)
    TRACE("tstat: ${tstat}")

    def events = []
    if (tstat.containsKey("error_msg")) {
        log.error "Thermostat error: ${tstat.error_msg}"
        return null
    }

    if (tstat.containsKey("temp")) {
        Float temp = tstat.temp.toFloat()
        def ev = [
            name:   "temperature",
            value:  scaleTemperature(temp),
            unit:   getTemperatureScale(),
        ]

        events << createEvent(ev)
    }

    if (tstat.containsKey("tstate")) {
        def tstate = parseThermostatState(tstat.tstate)
        TRACE("tstate: ${tstate}")
        if (device.currentState("thermostatOperatingState")?.value != tstate) {
            def ev = [
                name:   "thermostatOperatingState",
                value:  tstate
            ]

            events << createEvent(ev)
        }
    }

    if (tstat.containsKey("fstate")) {
        def fstate = parseFanState(tstat.fstate)
        TRACE("fstate: ${fstate}")
        if (device.currentState("fanState")?.value != fstate) {
            def ev = [
                name:   "fanState",
                value:  fstate
            ]

            events << createEvent(ev)
        }
    }

    if (tstat.containsKey("tmode")) {
        def tmode = parseThermostatMode(tstat.tmode)
        TRACE("tmode: ${tmode}")
        if (device.currentState("thermostatMode")?.value != tmode) {
            def ev = [
                name:   "thermostatMode",
                value:  tmode
            ]

            events << createEvent(ev)
        }
    }

    if (tstat.containsKey("fmode")) {
        def fmode = parseFanMode(tstat.fmode)
        TRACE("fmode: ${fmode}")
        if (device.currentState("thermostatFanMode")?.value != fmode) {
            def ev = [
                name:   "thermostatFanMode",
                value:  fmode
            ]

            events << createEvent(ev)
        }
    }

    if (tstat.containsKey("hold")) {
        def hold = parseThermostatHold(tstat.hold)
        TRACE("hold: ${hold}")
        if (device.currentState("hold")?.value != hold) {
            def ev = [
                name:   "thermostatFanMode",
                value:  hold
            ]

            events << createEvent(ev)
        }
    }

    TRACE("events: ${events}")
    return events
}

private parseHttpHeaders(String headers) {
    def lines = headers.readLines()
    def status = lines.remove(0).split()

    def result = [
        protocol:   status[0],
        status:     status[1].toInteger(),
        reason:     status[2]
    ]

    return result
}

private def parseThermostatState(val) {
    def values = [
        0: "idle",
        1: "heating",
        2: "cooling",
    ]

    return values[val]
}

private def parseFanState(val) {
    def values = [
        0: "off",
        1: "on",
    ]

    return values[val]
}

private def parseThermostatMode(val) {
    def values = [
        0: "off",
        1: "heat",
        2: "cool",
        3: "auto"
    ]

    return values[val]
}

private def parseFanMode(val) {
    def values = [
        0: "auto",
        1: "circulate",
        2: "on"
    ]

    return values[val]
}

private def parseThermostatHold(val) {
    def values = [
        0: "off",
        1: "on",
    ]

    return values[val]
}

private def setTemperature(value) {
    TRACE("setTemperature(${value})")

    Float temp = value.toFloat()
    def tempScale = getTemperatureScale()
    if (tempScale == "C") {
        temp = (temp - 32) / 1.8
    }

    def event = [
        name  : "temperature",
        value : temp,
        unit  : tempScale
    ]

    sendEvent(event)
}

private def scaleTemperature(temp) {
    if (getTemperatureScale() == "C") {
        temp = ((temp - 32) / 1.8).round(1)
    }

    return temp
}

private def TRACE(message) {
    log.debug message
}
