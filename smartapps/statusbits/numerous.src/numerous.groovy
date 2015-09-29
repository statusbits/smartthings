/**
 *  Numerous.
 *
 *  View your SmartThings data in Numerous (www.numerousapp.com).
 *
 *  --------------------------------------------------------------------------
 *
 *  Copyright © 2015 Statusbits.com
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
 *  Version 1.1.0 (09/28/2015)
 */

import groovy.json.JsonSlurper

definition(
    name: "Numerous",
    namespace: "statusbits",
    author: "geko@statusbits.com",
    description: "View your SmartThings data in Numerous (www.numerousapp.com)",
    category: "My Apps",
    iconUrl: "http://statusbits.github.io/icons/numerous-32x32.png",
    iconX2Url: "http://statusbits.github.io/icons/numerous-160x160.png",
    oauth: false
)

preferences {
    page(name:"pageSetup")
}

// Show "Setup Menu" page
private def pageSetup() {
    LOG("pageSetup()")

    if (state.version != getVersion()) {
        setupInit()
    }

    def textApiKey =
        "You can find your API key in the Numerous mobile apps under " +
        "Settings > Developer Info. The easiest way to enter your API " +
        "key is to copy and paste it."

    def textDevices =
        "For each selected device, a new Numerous data channel (known as a " +
        "'metric') will be created. All metrics are created private by " +
        "default. You can change the metric properties in the Numerous " +
        "mobile app."

    def textAbout =
        "Version ${getVersion()}\n${textCopyright()}\n\n" +
        "You can contribute to the development of this app by making a " +
        "PayPal donation to geko@statusbits.com. We appreciate your support."

    def inputApiKey = [
        name:       "apiKey",
        type:       "string",
        title:      "Your API Key",
        required:   true
    ]
    def inputTemperature = [
        name:       "devTemperature",
        type:       "capability.temperatureMeasurement",
        title:      "Temperature Sensors",
        multiple:   true,
        required:   false
    ]

    def inputHumidity = [
        name:       "devHumidity",
        type:       "capability.relativeHumidityMeasurement",
        title:      "Humidity Sensors",
        multiple:   true,
        required:   false
    ]

    def inputIlluminance = [
        name:       "devIlluminance",
        type:       "capability.illuminanceMeasurement",
        title:      "Illuminance Sensors",
        multiple:   true,
        required:   false
    ]

    def inputPower = [
        name:       "devPower",
        type:       "capability.powerMeter",
        title:      "Power Meters",
        multiple:   true,
        required:   false
    ]

    def inputEnergy = [
        name:       "devEnergy",
        type:       "capability.energyMeter",
        title:      "Energy Meters",
        multiple:   true,
        required:   false
    ]

    def inputBattery = [
        name:       "devBattery",
        type:       "capability.battery",
        title:      "Batteries",
        multiple:   true,
        required:   false
    ]

    def hrefAbout = [
        url:        "http://statusbits.github.io/smartthings/",
        style:      "embedded",
        title:      "Tap for more information...",
        description:"http://statusbits.github.io/smartthings/",
        required:   false
    ]

    def pageProperties = [
        name:       "pageSetup",
        nextPage:   null,
        install:    true,
        uninstall:  state.installed
    ]

    return dynamicPage(pageProperties) {
        section("API Key") {
            paragraph textApiKey
            input inputApiKey
        }

        section("Devices") {
            paragraph textDevices
            input inputTemperature
            input inputHumidity
            input inputIlluminance
            input inputPower
            input inputEnergy
            input inputBattery
        }

        section("About") {
            paragraph textAbout
            href hrefAbout
        }

        section([title:"Options", mobileOnly:true]) {
            label title:"Assign a name", required:false
        }
    }
}

def installed() {
    LOG("Installed with settings: ${settings}")
    state.installed = true
    initialize()
}

def updated() {
    LOG("Updated with settings: ${settings}")
    unsubscribe()
    initialize()
}

def onTemperature(evt) {
    LOG("onTemperature($evt.value)")

    def device = evt.device
    def metricId = getMetricId(device.id, "temperature")
    if (!metricId) {
        createTemperatureMetric(device)
    } else {
        def data = [
            value: evt.value
        ]

        apiUpdateValue(metricId, data)
    }
}

def onHumidity(evt) {
    LOG("onHumidity($evt.value)")

    def device = evt.device
    def metricId = getMetricId(device.id, "humidity")
    if (!metricId) {
        createHumidityMetric(device)
    } else {
        def data = [
            value: evt.integerValue / 100
        ]

        apiUpdateValue(metricId, data)
    }
}

def onIlluminance(evt) {
    LOG("onIlluminance($evt.value)")

    def device = evt.device
    def metricId = getMetricId(device.id, "illuminance")
    if (!metricId) {
        createIlluminanceMetric(device)
    } else {
        def data = [
            value: evt.value
        ]

        apiUpdateValue(metricId, data)
    }
}

def onPower(evt) {
    LOG("onPower($evt.value)")

    def device = evt.device
    def metricId = getMetricId(device.id, "power")
    if (!metricId) {
        createPowerMetric(device)
    } else {
        def data = [
            value: evt.value
        ]

        apiUpdateValue(metricId, data)
    }
}

def onEnergy(evt) {
    LOG("onEnergy($evt.value)")

    def device = evt.device
    def metricId = getMetricId(device.id, "energy")
    if (!metricId) {
        createEnergyMetric(device)
    } else {
        def data = [
            value: evt.value
        ]

        apiUpdateValue(metricId, data)
    }
}

def onBattery(evt) {
    LOG("onBattery($evt.value)")

    def device = evt.device
    def metricId = getMetricId(device.id, "battery")
    if (!metricId) {
        createBatteryMetric(device)
    } else {
        def data = [
            value: evt.integerValue / 100
        ]

        apiUpdateValue(metricId, data)
    }
}

private def setupInit() {
    LOG("setupInit()")

    if (state.installed == null) {
        state.installed = false
        state.metrics = [:]
    }

    state.version = getVersion()
    return true
}

private def initialize() {
    log.info "Numerous. Version ${getVersion()}. ${textCopyright()}"

    if (!settings.apiKey) {
        log.error "Missing API key!"
        state.apiAuth = null
    } else {
        String auth = "${settings.apiKey}:".bytes.encodeBase64()
        state.apiAuth = auth

        createMetrics()

        subscribe(settings.devTemperature, "temperature", onTemperature)
        subscribe(settings.devHumidity, "humidity", onHumidity)
        subscribe(settings.devIlluminance, "illuminance", onIlluminance)
        subscribe(settings.devPower, "power", onPower)
        subscribe(settings.devEnergy, "energy", onEnergy)
        subscribe(settings.devBattery, "battery", onBattery)
    }

    STATE()
}

private def createMetrics() {
    LOG("createMetrics()")

    settings.devTemperature?.each() {
        def metricId = getMetricId(it.id, "temperature")
        if (!metricId) {
            createTemperatureMetric(it)
        }
    }

    settings.devHumidity?.each() {
        def metricId = getMetricId(it.id, "humidity")
        if (!metricId) {
            createHumidityMetric(it)
        }
    }

    settings.devIlluminance?.each() {
        def metricId = getMetricId(it.id, "illuminance")
        if (!metricId) {
            createIlluminanceMetric(it)
        }
    }

    settings.devPower?.each() {
        def metricId = getMetricId(it.id, "power")
        if (!metricId) {
            createPowerMetric(it)
        }
    }

    settings.devEnergy?.each() {
        def metricId = getMetricId(it.id, "energy")
        if (!metricId) {
            createEnergyMetric(it)
        }
    }

    settings.devBattery?.each() {
        def metricId = getMetricId(it.id, "battery")
        if (!metricId) {
            createBatteryMetric(it)
        }
    }
}

private def createTemperatureMetric(device) {
    LOG("createTemperatureMetric(${device})")

    def data = [
        label:          device.displayName,
        description:    "Temperature of ${device.displayName} at ${location.name}",
        kind:           "temperature",
        precision:      1,
        unit:           "°F",
        units:          "°F",
        visibility:     "private",
        value:          device.currentTemperature,
    ]

    apiCreateMetric(device.id, "temperature", data)
}

private def createHumidityMetric(device) {
    LOG("createHumidityMetric(${device})")

    def data = [
        label:          device.displayName,
        description:    "Relative Humidity of ${device.displayName} at ${location.name}",
        kind:           "percent",
        visibility:     "private",
        value:          device.currentHumidity.toInteger() / 100,
    ]

    apiCreateMetric(device.id, "humidity", data)
}

private def createIlluminanceMetric(device) {
    LOG("createIlluminanceMetric(${device})")

    def data = [
        label:          device.displayName,
        description:    "Illuminance of ${device.displayName} at ${location.name}",
        kind:           "number",
        unit:           "Lux",
        units:          "Lux",
        visibility:     "private",
        value:          device.currentIlluminance,
    ]

    apiCreateMetric(device.id, "illuminance", data)
}

private def createPowerMetric(device) {
    LOG("createPowerMetric(${device})")

    def data = [
        label:          device.displayName,
        description:    "Power of ${device.displayName} at ${location.name}",
        kind:           "number",
        precision:      1,
        unit:           "W",
        units:          "W",
        visibility:     "private",
        value:          device.currentPower,
    ]

    apiCreateMetric(device.id, "power", data)
}

private def createEnergyMetric(device) {
    LOG("createEnergyMetric(${device})")

    def data = [
        label:          device.displayName,
        description:    "Energy of ${device.displayName} at ${location.name}",
        kind:           "number",
        precision:      1,
        unit:           "kWh",
        units:          "kWh",
        visibility:     "private",
        value:          device.currentEnergy,
    ]

    apiCreateMetric(device.id, "energy", data)
}

private def createBatteryMetric(device) {
    LOG("createBatteryMetric(${device})")

    def data = [
        label:          device.displayName,
        description:    "Battery Level of ${device.displayName} at ${location.name}",
        kind:           "percent",
        visibility:     "private",
        value:          device.currentBattery.toInteger() / 100,
    ]

    apiCreateMetric(device.id, "battery", data)
}

private def apiCreateMetric(deviceId, deviceType, Map data) {
    LOG("apiCreateMetric(${deviceId}, ${deviceType}, ${data})")

    def headers = [
        "Authorization": "Basic ${state.apiAuth}",
    ]

    def params = [
        uri:        "https://api.numerousapp.com/v2/metrics",
        headers:    headers,
        body:       toJson(data)
    ]

    //log.debug "params: ${params}"

    try {
        httpPostJson(params) { response ->
            def status = response.getStatus()
            if (status != 201) {
                log.error "Server error: ${response.getStatusLine()}"
                return
            }

            def metric = response.getData()
            LOG("metric: ${metric}")
            def key = "${deviceType}_${deviceId}"
            state.metrics[key] = metric.id
        }
    } catch (e) {
        log.error "${e}"
    }
}

private def apiUpdateMetric(metricId, Map data) {
    LOG("apiUpdateMetric(${metricId}, ${data})")

    def headers = [
        "Authorization": "Basic ${state.apiAuth}",
    ]

    def params = [
        uri:        "https://api.numerousapp.com/v2/metrics/${metricId}",
        headers:    headers,
        body:       toJson(data)
    ]

    //log.debug "params: ${params}"

    try {
        httpPutJson(params) { response ->
            def status = response.getStatus()
            if (status != 200) {
                log.error "Server error: ${response.getStatusLine()}"
                return
            }

            LOG("data: ${response.getData()}")
        }
    } catch (e) {
        log.error "${e}"
    }
}

private def apiDeleteMetric(metricId) {
    LOG("apiDeleteMetric(${metricId})")

    def headers = [
        "Authorization": "Basic ${state.apiAuth}",
    ]

    def params = [
        uri:        "https://api.numerousapp.com/v2/metrics/${metricId}",
        headers:    headers,
    ]

    //log.debug "params: ${params}"

    try {
        httpDelete(params) { response ->
            def status = response.getStatus()
            if (status != 204) {
                log.error "Server error: ${response.getStatusLine()}"
                return false
            }
        }
    } catch (e) {
        log.error "${e}"
        return false
    }

    return true
}

private def apiReadMetric(metricId) {
    LOG("apiReadMetric(${metricId})")

    def headers = [
        "Authorization": "Basic ${state.apiAuth}",
    ]

    def params = [
        uri:        "https://api.numerousapp.com/v2/metrics/${metricId}",
        headers:    headers,
    ]

    //log.debug "params: ${params}"

    try {
        httpGet(params) { response ->
            def status = response.getStatus()
            if (status != 200) {
                log.error "Server error: ${response.getStatusLine()}"
                return null
            }

            return response.getData()
        }
    } catch (e) {
        log.error "${e}"
    }

    return null
}

private def apiUpdateValue(metricId, Map data) {
    LOG("apiUpdateValue(${metricId}, ${data})")

    def headers = [
        "Authorization": "Basic ${state.apiAuth}",
    ]

    def params = [
        uri:        "https://api.numerousapp.com/v2/metrics/${metricId}/events",
        headers:    headers,
        body:       toJson(data)
    ]

    //log.debug "params: ${params}"

    try {
        httpPostJson(params) { response ->
            def status = response.getStatus()
            if (status != 200) {
                log.error "Server error: ${response.getStatusLine()}"
                return
            }

            LOG("data: ${response.getData()}")
        }
    } catch (e) {
        log.error "${e}"
    }
}

private def getMetricId(deviceId, deviceType) {
    def key = "${deviceType}_${deviceId}"
    return state.metrics[key]
}

private def toJson(Map m) {
	return new org.codehaus.groovy.grails.web.json.JSONObject(m).toString()
}

private def getVersion() {
    return "1.1.0"
}

private def textCopyright() {
    return "Copyright © 2015 Statusbits.com"
}

private def LOG(message) {
    //log.trace message
}

private def STATE() {
    //log.trace "state: ${state}"
}
