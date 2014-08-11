/**
 *  Xively Bridge
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
 *  Version: 0.9.0
 *  Date: 2014-08-10
 */

definition(
    name: "Xively Bridge",
    namespace: "statusbits",
    author: "geko@statusbits.com",
    description: "This SmartApp acts as a bridge between SmartThings and Xively device cloud (xively.com).",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")

preferences {
    section("About") {
        paragraph "Xively (xively.com) is an Internet of Things device " +
            "cloud. This SmartApp maps Xively device data feeds to " +
            "SmartThings virtual devices."
        paragraph "Version 0.9.0.\nCopyright (c) 2014 Statusbits.com"
    }

    section("Xively Credentials") {
        input "xi_apikey", "text", title: "Xively API Key"
        input "xi_feed", "number", title: "Xively Feed ID"
    }

    section("Smart Things") {
        input "temperature", "capability.temperatureMeasurement", title: "Select temperature sensor"
    }

    section("Polling Interval") {
        input "interval", "number", title:"Set polling interval (in minutes)", defaultValue:5
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
    TRACE("initialize() with settings: ${settings}")

    if (settings.temperature) {
        log.debug "Supported commands: ${settings.temperature.supportedCommands}"
    }

    def minutes = settings.interval.toInteger()
    if (minutes > 0) {
        TRACE("Scheduling polling task to run every ${minutes} minutes.")
        schedule("0 0/${minutes} * * * ?", pollingTask)
    }
}

def pollingTask() {
    TRACE("pollingTask()")

    //if (!settings.devices?.size()) {
    //    TRACE("There's no devices to poll. Stopping daemon now.")
    //    unschedule()
    //    return
    //}

    readDatastream(xi_feed, "Temperature1")
}

private readDatastream(feed, channel) {
    TRACE("readDatastream(${feed}, ${channel})")

    def headers = [
        "X-ApiKey" : "${xi_apikey}"
    ]

    def params = [
        uri: "https://api.xively.com/v2/feeds/${feed}/datastreams/${channel}.json",
        headers: headers
    ]

    httpGet(params) {response -> parseReadResponse(response)}
}

private parseReadResponse(response) {
    TRACE("parseWriteResponse(status: ${response.status} data: ${response.data})")

    if (response.status != 200) {
        log.error "Datastream read failed. HTTP error ${response.status}. ${response.data}"
        return
    }

    if (response.data.current_value) {
        settings.temperature.setCurrentValue(response.data.current_value)
    }
}

private def writeDatastream(feed, channel, value) {
    TRACE("writeDatastream(${feed}, ${channel}, ${value})")

    def uri = "https://api.xively.com/v2/feeds/${feed}.json"
    def json = "{\"version\":\"1.0.0\",\"datastreams\":[{\"id\":\"${channel}\",\"current_value\":\"${value}\"}]}"

    def headers = [
        "X-ApiKey" : "${xi_apikey}"
    ]

    def params = [
        uri: uri,
        headers: headers,
        body: json
    ]

    httpPutJson(params) {response -> parseWriteResponse(response)}
}

private def parseWriteResponse(response) {
    TRACE("parseWriteResponse(status: ${response.status} data: ${response.data})")

    if (response.status != 200) {
        log.error "Datastream write failed. HTTP error ${response.status}. ${response.data}"
        return
    }
}

private def TRACE(message) {
    log.debug message
    log.debug "state: ${state}"
}
