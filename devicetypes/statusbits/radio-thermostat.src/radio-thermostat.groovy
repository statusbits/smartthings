/**
 *  Filtrete 3M-50 WiFi Thermostat.
 *
 *  For more information, please visit:
 *  <https://github.com/statusbits/smartthings/tree/master/RadioThermostat/>
 *
 *  --------------------------------------------------------------------------
 *
 *  Copyright © 2014 Statusbits.com
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
 *
 *  --------------------------------------------------------------------------
 *
 *  Version 1.1.0 (12/13/2016)
 */

import groovy.json.JsonSlurper

preferences {
    input("confIpAddr", "string", title:"Thermostat IP Address",
        required:true, displayDuringSetup:true)
    input("confTcpPort", "number", title:"Thermostat TCP Port",
        defaultValue:80, required:true, displayDuringSetup:true)
    input("pollingInterval", "number", title:"Polling interval in minutes (1 - 59)",
        defaultValue:5, required:true, displayDuringSetup:true)
}

metadata {
    definition (name:"Radio Thermostat", namespace:"statusbits", author:"geko@statusbits.com") {
        capability "Thermostat"
        capability "Temperature Measurement"
        capability "Sensor"
        capability "Refresh"
        capability "Polling"

        // Custom attributes
        attribute "fanState", "string"  // Fan operating state. Values: "on", "off"
        attribute "hold", "string"      // Target temperature Hold status. Values: "on", "off"

        // Custom commands
        command "temperatureUp"
        command "temperatureDown"
        command "holdOn"
        command "holdOff"
    }

    tiles(scale:2) {
        multiAttributeTile(name:"thermostat", type:"thermostat", width:6, height:4) {
		    tileAttribute("device.temperature", key:"PRIMARY_CONTROL") {
			    attributeState("default", label:'${currentValue}°', unit:"dF", defaultState:true,
                    backgroundColors:[
                        [value:10, color:"#153591"],
                        [value:15, color:"#1e9cbb"],
                        [value:18, color:"#90d2a7"],
                        [value:21, color:"#44b621"],
                        [value:24, color:"#f1d801"],
                        [value:27, color:"#d04e00"],
                        [value:30, color:"#bc2323"],
                        [value:31, color:"#153591"],
                        [value:44, color:"#1e9cbb"],
                        [value:59, color:"#90d2a7"],
                        [value:74, color:"#44b621"],
                        [value:84, color:"#f1d801"],
                        [value:95, color:"#d04e00"],
                        [value:96, color:"#bc2323"]
                    ]
                )
	        }
			tileAttribute("device.temperature", key:"VALUE_CONTROL") {
				attributeState("VALUE_UP", action:"temperatureUp")
				attributeState("VALUE_DOWN", action:"temperatureDown")
			}
			//tileAttribute("device.humidity", key:"SECONDARY_CONTROL") {
			//	attributeState("default", label:'${currentValue}%', unit:"%")
			//}
			tileAttribute("device.fanState", key:"SECONDARY_CONTROL") {
				attributeState("fanState", label:'Fan ${currentValue}', defaultState:true)
			}
			tileAttribute("device.thermostatOperatingState", key:"OPERATING_STATE") {
				attributeState("idle", backgroundColor:"#44b621", defaultState:true)
				attributeState("heating", backgroundColor:"#ea5462")
				attributeState("cooling", backgroundColor:"#269bd2")
			}
			tileAttribute("device.thermostatMode", key:"THERMOSTAT_MODE") {
				attributeState("off", label:'${name}', defaultState:true)
				attributeState("heat", label:'${name}')
				attributeState("cool", label:'${name}')
				attributeState("auto", label:'${name}')
			}
			tileAttribute("device.heatingSetpoint", key:"HEATING_SETPOINT") {
				attributeState("heatingSetpoint", label:'${currentValue}', unit:"dF", defaultState:true)
			}
			tileAttribute("device.coolingSetpoint", key:"COOLING_SETPOINT") {
				attributeState("heatingSetpoint", label:'${currentValue}', unit:"dF", defaultState:true)
			}
        }

        valueTile("temperature", "device.temperature", width:2, height:2) {
            state "temperature", label:'${currentValue}°', unit:"dF",
                backgroundColors:[
                    [value:10, color:"#153591"],
                    [value:15, color:"#1e9cbb"],
                    [value:18, color:"#90d2a7"],
                    [value:21, color:"#44b621"],
                    [value:24, color:"#f1d801"],
                    [value:27, color:"#d04e00"],
                    [value:30, color:"#bc2323"],
                    [value:31, color:"#153591"],
                    [value:44, color:"#1e9cbb"],
                    [value:59, color:"#90d2a7"],
                    [value:74, color:"#44b621"],
                    [value:84, color:"#f1d801"],
                    [value:95, color:"#d04e00"],
                    [value:96, color:"#bc2323"]
                ]
        }

        standardTile("modeHeat", "device.thermostatMode", width:2, height:2) {
            state "default", label:'', icon:"st.thermostat.heat", backgroundColor:"#FFFFFF", action:"thermostat.heat", defaultState:true
            state "heat", label:'', icon:"st.thermostat.heat", backgroundColor:"#FFCC99", action:"thermostat.off"
        }

        standardTile("modeCool", "device.thermostatMode", width:2, height:2) {
            state "default", label:'', icon:"st.thermostat.cool", backgroundColor:"#FFFFFF", action:"thermostat.cool", defaultState:true
            state "cool", label:'', icon:"st.thermostat.cool", backgroundColor:"#99CCFF", action:"thermostat.off"
        }

        standardTile("modeAuto", "device.thermostatMode", width:2, height:2) {
            state "default", label:'', icon:"st.thermostat.auto", backgroundColor:"#FFFFFF", action:"thermostat.auto", defaultState:true
            state "auto", label:'', icon:"st.thermostat.auto", backgroundColor:"#99FF99", action:"thermostat.off"
        }

        standardTile("mode", "device.thermostatMode", width:2, height:2) {
            state "default", label:'N/A', defaultState:true
            state "off", label:'', icon:"st.thermostat.heating-cooling-off", backgroundColor:"#FFFFFF", action:"thermostat.heat", nextState:"working"
            state "heat", label:'', icon:"st.thermostat.heat", backgroundColor:"#FFCC99", action:"thermostat.cool", nextState:"working"
            state "cool", label:'', icon:"st.thermostat.cool", backgroundColor:"#99CCFF", action:"thermostat.auto", nextState:"working"
            state "auto", label:'', icon:"st.thermostat.auto", backgroundColor:"#99FF99", action:"thermostat.off", nextState:"working"
            state "working", label:'Busy...', icon:"", backgroundColor:"#C0C0C0", action:"refresh.refresh"
        }

        standardTile("fanMode", "device.thermostatFanMode", width:2, height:2) {
            state "default", label:'N/A', defaultState:true
            state "auto", label:'', icon:"st.thermostat.fan-auto", backgroundColor:"#A4FCA6", action:"thermostat.fanOn", nextState:"working"
            state "on", label:'', icon:"st.thermostat.fan-on", backgroundColor:"#FAFCA4", action:"thermostat.fanAuto", nextState:"working"
            state "working", label:'', icon:"st.secondary.refresh", backgroundColor:"#A0A0A0", action:"refresh.refresh"
        }

        standardTile("hold", "device.hold", width:2, height:2) {
            state "default", label:'N/A', defaultState:true
            state "on", label:'Hold On', icon:"st.Weather.weather2", backgroundColor:"#FFDB94", action:"holdOff", nextState:"working"
            state "off", label:'Hold Off', icon:"st.Weather.weather2", backgroundColor:"#FFFFFF", action:"holdOn", nextState:"working"
            state "working", label:'', icon:"st.secondary.refresh", backgroundColor:"#A0A0A0", action:"refresh.refresh"
        }

        standardTile("refresh", "device.thermostatMode", width:2, height:2, decoration:"flat") {
            state "default", icon:"st.secondary.refresh", action:"refresh.refresh"
        }

        main("temperature")
        details(["thermostat",
            "modeHeat", "modeCool", "modeAuto",
            "mode", "fanMode", "hold", "refresh"])
    }

    simulator {
        status "Temperature 72.0":      "simulator:true, temp:72.00"
        status "Cooling Setpoint 76.0": "simulator:true, t_cool:76.00"
        status "Heating Setpoint 68.0": "simulator:true, t_cool:68.00"
        status "Thermostat Mode Off":   "simulator:true, tmode:0"
        status "Thermostat Mode Heat":  "simulator:true, tmode:1"
        status "Thermostat Mode Cool":  "simulator:true, tmode:2"
        status "Thermostat Mode Auto":  "simulator:true, tmode:3"
        status "Fan Mode Auto":         "simulator:true, fmode:0"
        status "Fan Mode Circulate":    "simulator:true, fmode:1"
        status "Fan Mode On":           "simulator:true, fmode:2"
        status "State Off":             "simulator:true, tstate:0"
        status "State Heat":            "simulator:true, tstate:1"
        status "State Cool":            "simulator:true, tstate:2"
        status "Fan State Off":         "simulator:true, fstate:0"
        status "Fan State On":          "simulator:true, fstate:1"
        status "Hold Disabled":         "simulator:true, hold:0"
        status "Hold Enabled":          "simulator:true, hold:1"
    }
}

def installed() {
    initialize()
}

def updated() {
    unschedule()
    initialize()
}

private def initialize() {
    log.info "Radio Thermostat. ${textVersion()}. ${textCopyright()}"
	DEBUG("initialize with settings: ${settings}")

    state.hostAddress = "${settings.confIpAddr}:${settings.confTcpPort}"
    state.dni = createDNI(settings.confIpAddr, settings.confTcpPort)
    state.updated = 0

    startPollingTask()
    STATE()
}

def pollingTask() {
    DEBUG("pollingTask()")
    sendHubCommand(apiGet("/tstat"))
    state.lastPoll = now()
}

def parse(String message) {
    DEBUG("parse(${message})")

    def msg = stringToMap(message)
    if (msg.headers) {
        // parse HTTP response headers
        def headers = new String(msg.headers.decodeBase64())
        def parsedHeaders = parseHttpHeaders(headers)
        DEBUG("parsedHeaders: ${parsedHeaders}")
        if (parsedHeaders.status != 200) {
            log.error "Server error: ${parsedHeaders.reason}"
            return null
        }

        // parse HTTP response body
        if (!msg.body) {
            log.error "HTTP response has no body"
            return null
        }

        def body = new String(msg.body.decodeBase64())
        def slurper = new JsonSlurper()
        def tstat = slurper.parseText(body)
        return parseTstatData(tstat)
    } else if (msg.containsKey("simulator")) {
        // simulator input
        return parseTstatData(msg)
    }

    return null
}

// thermostat.setThermostatMode
def setThermostatMode(mode) {
    DEBUG("setThermostatMode(${mode})")

    switch (mode) {
    case "off":             return off()
    case "heat":            return heat()
    case "cool":            return cool()
    case "auto":            return auto()
    case "emergency heat":  return emergencyHeat()
    }

    log.error "Invalid thermostat mode: \'${mode}\'"
}

// thermostat.off
def off() {
    DEBUG("off()")

    if (device.currentValue("thermostatMode") == "off") {
        return null
    }

    //sendEvent([name:"thermostatMode", value:"off"])
    return writeTstatValue('tmode', 0)
}

// thermostat.heat
def heat() {
    DEBUG("heat()")

    if (device.currentValue("thermostatMode") == "heat") {
        return null
    }

    //sendEvent([name:"thermostatMode", value:"heat"])
    return writeTstatValue('tmode', 1)
}

// thermostat.cool
def cool() {
    DEBUG("cool()")

    if (device.currentValue("thermostatMode") == "cool") {
        return null
    }

    //sendEvent([name:"thermostatMode", value:"cool"])
    return writeTstatValue('tmode', 2)
}

// thermostat.auto
def auto() {
    DEBUG("auto()")

    if (device.currentValue("thermostatMode") == "auto") {
        return null
    }

    //sendEvent([name:"thermostatMode", value:"auto"])
    return writeTstatValue('tmode', 3)
}

// thermostat.emergencyHeat
def emergencyHeat() {
    DEBUG("emergencyHeat()")
    log.warn "'emergency heat' mode is not supported"
    return null
}

// thermostat.setThermostatFanMode
def setThermostatFanMode(fanMode) {
    DEBUG("setThermostatFanMode(${fanMode})")

    switch (fanMode) {
    case "auto":        return fanAuto()
    case "circulate":   return fanCirculate()
    case "on":          return fanOn()
    }

    log.error "Invalid fan mode: \'${fanMode}\'"
}

// thermostat.fanAuto
def fanAuto() {
    DEBUG("fanAuto()")

    if (device.currentValue("thermostatFanMode") == "auto") {
        return null
    }

    //sendEvent([name:"thermostatFanMode", value:"auto"])
    return writeTstatValue('fmode', 0)
}

// thermostat.fanCirculate
def fanCirculate() {
    DEBUG("fanCirculate()")
    log.warn "Fan 'Circulate' mode is not supported"
    return null
}

// thermostat.fanOn
def fanOn() {
    DEBUG("fanOn()")

    if (device.currentValue("thermostatFanMode") == "on") {
        return null
    }

    //sendEvent([name:"thermostatFanMode", value:"on"])
    return writeTstatValue('fmode', 2)
}

// thermostat.setHeatingSetpoint
def setHeatingSetpoint(temp) {
    DEBUG("setHeatingSetpoint(${temp})")

    double minT = 36.0
    double maxT = 94.0
    def scale = getTemperatureScale()
    double t = (scale == "C") ? temperatureCtoF(temp) : temp

    t = t.round()
    if (t < minT) {
        log.warn "Cannot set heating target below ${minT} °F."
        return null
    } else if (t > maxT) {
        log.warn "Cannot set heating target above ${maxT} °F."
        return null
    }

    def ev = [
        name:   "heatingSetpoint",
        value:  (scale == "C") ? temperatureFtoC(t) : t,
        unit:   scale,
    ]

    sendEvent(ev)

    return writeTstatValue('it_heat', t)
}

// thermostat.setCoolingSetpoint
def setCoolingSetpoint(temp) {
    DEBUG("setCoolingSetpoint(${temp})")

    double minT = 36.0
    double maxT = 94.0
    def scale = getTemperatureScale()
    double t = (scale == "C") ? temperatureCtoF(temp) : temp

    t = t.round()
    if (t < minT) {
        log.warn "Cannot set cooling target below ${minT} °F."
        return null
    } else if (t > maxT) {
        log.warn "Cannot set cooling target above ${maxT} °F."
        return null
    }

    def ev = [
        name:   "coolingSetpoint",
        value:  (scale == "C") ? temperatureFtoC(t) : t,
        unit:   scale,
    ]

    sendEvent(ev)

    return writeTstatValue('it_cool', t)
}

def temperatureUp() {
    DEBUG("temperatureUp()")    

    def step = (getTemperatureScale() == "C") ? 0.5 : 1
    def mode = device.currentValue("thermostatMode")
    if (mode == "heat") {
        def t = device.currentValue("heatingSetpoint")?.toFloat()
        if (!t) {
            log.error "Cannot get current heating setpoint."
            return null
        }
        return setHeatingSetpoint(t + step)
    } else if (mode == "cool") {
        def t = device.currentValue("coolingSetpoint")?.toFloat()
        if (!t) {
            log.error "Cannot get current cooling setpoint."
            return null
        } 
        return setCoolingSetpoint(t + step)
    } else {
        log.warn "Cannot change temperature while in '${mode}' mode."
        return null
    }
}

def temperatureDown() {
    DEBUG("temperatureDown()")    

    def step = (getTemperatureScale() == "C") ? 0.5 : 1
    def mode = device.currentValue("thermostatMode")
    if (mode == "heat") {
        def t = device.currentValue("heatingSetpoint")?.toFloat()
        if (!t) {
            log.error "Cannot get current heating setpoint."
            return null
        }
 
        return setHeatingSetpoint(t - step)
    } else if (mode == "cool") {
        def t = device.currentValue("coolingSetpoint")?.toFloat()
        if (!t) {
            log.error "Cannot get current cooling setpoint."
            return null
        }
 
        return setCoolingSetpoint(t - step)
    } else {
        log.warn "Cannot change temperature while in '${mode}' mode."
        return null
    }
}

def holdOn() {
    DEBUG("holdOn()")

    if (device.currentValue("hold") == "on") {
        return null
    }

    sendEvent([name:"hold", value:"on"])
    writeTstatValue("hold", 1)
}

def holdOff() {
    DEBUG("holdOff()")

    if (device.currentValue("hold") == "off") {
        return null
    }

    sendEvent([name:"hold", value:"off"])
    writeTstatValue("hold", 0)
}

// polling.poll 
def poll() {
    DEBUG("poll()")
    return refresh()
}

// refresh.refresh
def refresh() {
    DEBUG("refresh()")
    STATE()

    def interval = getPollingInterval() * 60
    def elapsed =  (now() - state.lastPoll) / 1000
    if (elapsed > (interval + 300)) {
        log.warn "Restarting polling task..."
        unschedule()
        startPollingTask()
    }

    return apiGet("/tstat")
}

// Creates Device Network ID in 'AAAAAAAA:PPPP' format
private String createDNI(ipaddr, port) {
    DEBUG("createDNI(${ipaddr}, ${port})")

    def hexIp = ipaddr.tokenize('.').collect {
        String.format('%02X', it.toInteger())
    }.join()

    def hexPort = String.format('%04X', port.toInteger())

    return "${hexIp}:${hexPort}"
}

private updateDNI() {
    if (device.deviceNetworkId != state.dni) {
        device.deviceNetworkId = state.dni
    }
}

private getPollingInterval() {
    def minutes = settings.pollingInterval.toInteger()
    if (minutes < 1) {
        minutes = 1
    } else if (minutes > 59) {
        minutes = 59
    }

    return minutes
}

private startPollingTask() {
    pollingTask()

    Random rand = new Random(now())
    def seconds = rand.nextInt(60)
    def sched = "${seconds} 0/${getPollingInterval()} * * * ?"

    DEBUG("Scheduling polling task with \"${sched}\"")
    schedule(sched, pollingTask)
}

private apiGet(String path) {
    DEBUG("apiGet(${path})")

    def headers = [
        HOST:       state.hostAddress,
        Accept:     "*/*"
    ]

    def httpRequest = [
        method:     'GET',
        path:       path,
        headers:    headers
    ]

    updateDNI()

    return new physicalgraph.device.HubAction(httpRequest)
}

private apiPost(String path, data) {
    DEBUG("apiPost(${path}, ${data})")

    def headers = [
        HOST:       state.hostAddress,
        Accept:     "*/*"
    ]

    def httpRequest = [
        method:     'POST',
        path:       path,
        headers:    headers,
        body:       data
    ]

    updateDNI()

    return new physicalgraph.device.HubAction(httpRequest)
}

private def writeTstatValue(name, value) {
    DEBUG("writeTstatValue(${name}, ${value})")

    def json = "{\"${name}\": ${value}}"
    def hubActions = [
        apiPost("/tstat", json),
        delayHubAction(2000),
        apiGet("/tstat")
    ]

    return hubActions
}

private def delayHubAction(ms) {
    return new physicalgraph.device.HubAction("delay ${ms}")
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

private def parseTstatData(Map tstat) {
    DEBUG("parseTstatData(${tstat})")

    def events = []
    if (tstat.containsKey("error_msg")) {
        log.error "Thermostat error: ${tstat.error_msg}"
        return null
    }

    if (tstat.containsKey("success")) {
        // this is POST response - ignore
        return null
    }

    if (tstat.containsKey("temp")) {
        //Float temp = tstat.temp.toFloat()
        def ev = [
            name:   "temperature",
            value:  scaleTemperature(tstat.temp.toFloat()),
            unit:   getTemperatureScale(),
        ]

        events << createEvent(ev)
    }

    if (tstat.containsKey("t_cool")) {
        def ev = [
            name:   "coolingSetpoint",
            value:  scaleTemperature(tstat.t_cool.toFloat()),
            unit:   getTemperatureScale(),
        ]

        events << createEvent(ev)
    }

    if (tstat.containsKey("t_heat")) {
        def ev = [
            name:   "heatingSetpoint",
            value:  scaleTemperature(tstat.t_heat.toFloat()),
            unit:   getTemperatureScale(),
        ]

        events << createEvent(ev)
    }

    if (tstat.containsKey("tstate")) {
        def ev = [
            name:   "thermostatOperatingState",
            value:  parseThermostatState(tstat.tstate)
        ]

        events << createEvent(ev)
    }

    if (tstat.containsKey("fstate")) {
        def ev = [
            name:   "fanState",
            value:  parseFanState(tstat.fstate)
        ]

        events << createEvent(ev)
    }

    if (tstat.containsKey("tmode")) {
        def ev = [
            name:   "thermostatMode",
            value:  parseThermostatMode(tstat.tmode)
        ]

        events << createEvent(ev)
    }

    if (tstat.containsKey("fmode")) {
        def ev = [
            name:   "thermostatFanMode",
            value:  parseFanMode(tstat.fmode)
        ]

        events << createEvent(ev)
    }

    if (tstat.containsKey("hold")) {
        def ev = [
            name:   "hold",
            value:  parseThermostatHold(tstat.hold)
        ]

        events << createEvent(ev)
    }

    state.updated = now()

    DEBUG("events: ${events}")
    return events
}

private def parseThermostatState(val) {
    def values = [
        "idle",     // 0
        "heating",  // 1
        "cooling"   // 2
    ]

    return values[val.toInteger()]
}

private def parseFanState(val) {
    def values = [
        "off",      // 0
        "on"        // 1
    ]

    return values[val.toInteger()]
}

private def parseThermostatMode(val) {
    def values = [
        "off",      // 0
        "heat",     // 1
        "cool",     // 2
        "auto"      // 3
    ]

    return values[val.toInteger()]
}

private def parseFanMode(val) {
    def values = [
        "auto",     // 0
        "circulate",// 1 (not supported by CT30)
        "on"        // 2
    ]

    return values[val.toInteger()]
}

private def parseThermostatHold(val) {
    def values = [
        "off",      // 0
        "on"        // 1
    ]

    return values[val.toInteger()]
}

private def scaleTemperature(Double temp) {
    if (getTemperatureScale() == "C") {
        return temperatureFtoC(temp)
    }

    return temp.round(1)
}

private def temperatureCtoF(Double tempC) {
    Double t = (tempC * 1.8) + 32
    return t.round(1)
}

private def temperatureFtoC(Double tempF) {
    Double t = (tempF - 32) / 1.8
    return t.round(1)
}

private def textVersion() {
    return "Version 1.1.0 (05/22/2016)"
}

private def textCopyright() {
    return "Copyright © 2014 Statusbits.com"
}

private def DEBUG(message) {
    log.debug message
}

private def STATE() {
    log.trace "state: ${state}"
    log.trace "deviceNetworkId: ${device.deviceNetworkId}"
    log.trace "temperature: ${device.currentValue("temperature")}"
    log.trace "heatingSetpoint: ${device.currentValue("heatingSetpoint")}"
    log.trace "coolingSetpoint: ${device.currentValue("coolingSetpoint")}"
    log.trace "thermostatMode: ${device.currentValue("thermostatMode")}"
    log.trace "thermostatFanMode: ${device.currentValue("thermostatFanMode")}"
    log.trace "thermostatOperatingState: ${device.currentValue("thermostatOperatingState")}"
    log.trace "fanState: ${device.currentValue("fanState")}"
    log.trace "hold: ${device.currentValue("hold")}"
}
