/**
 *  X10 ActiveEye Bridge.
 *
 *  Copyright (c) 2014 Statusbits.com
 *
 *  SmartApp for interfacing SmartThings with X10 ActiveEye (model MS16A)
 *  or EagleEye (model MS14A) motion detectors.
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
 *  https://github.com/statusbits/smartthings/blob/master/X10Bridge/ActiveEyeBridge.app.groovy
 *
 *  Useful links:
 *  - ActiveEye product page: http://www.x10.com/ms16a.html
 *  - ActiveEye setup instructions: http://kbase.x10.com/wiki/Active_Eye_Motion_Sensor_Setup
 *
 *  Revision History
 *  ----------------
 *  2014-08-18  V0.9.0  Initial check-in
 */

definition(
    name: "ActiveEye Bridge",
    namespace: "statusbits",
    author: "geko@statusbits.com",
    description: "Connect X10 ActiveEye motion sensors to SmartThings",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    oauth: true
)

preferences {
    page name:"setupInit"
    page name:"setupMenu"
    page name:"setupAddDevice"
    page name:"actionAddDevice"
    page name:"setupRemoveDevices"
    page name:"actionRemoveDevices"
    page name:"setupListDevices"
}

private def setupInit() {
    TRACE("setupInit()")

    if (state.setup) {
        // already initialized, go to setup menu
        return setupMenu()
    }

    // initialize setup state and show welcome page
    state.setup = [:]
    state.setup.installed = false
    state.setup.devices = [:]
    return setupWelcome()
}

// Show setup welcome page
private def setupWelcome() {
    TRACE("setupWelcome()")

    def textPara1 =
        "ActiveEye Bridge integrates X10 ActiveEye (model MS16A) and/or " +
        "EagleEye (model MS14A) motion sensors into SmartThings.\n\n" +
        "ActiveEye Bridge does not rely on the SmartThings Hub to receive " +
        "signals from the motion sensors. Instead, it requires an X10 " +
        "serial Firecracker receiver (model MR26A) and an X10 Gateway " +
        "software installed on a PC. Please visit " +
        "[www.statusbits.com/p/activeeye] for more information."

    def textPara2 = "${app.name}. ${textVersion()}\n${textCopyright()}"

    def textPara3 =
        "Please read the License Agreement below, then tap the 'Next' " +
        "button at the top of the screen to accept the terms and " +
        "conditions of the License Agreement and continue."

    def textLicense =
        "Licensed under the Apache License, Version 2.0 (the \"License\"); " +
        "you may not use this software except in compliance with the " +
        "License. You may obtain a copy of the License at:\n\n" +
        "http://www.apache.org/licenses/LICENSE-2.0\n\n" +
        "Unless required by applicable law or agreed to in writing, " +
        "software distributed under the License is distributed on an " +
        "\"AS IS\" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, " +
        "either express or implied. See the License for the specific " +
        "language governing permissions and limitations under the License."

    def pageProperties = [
        name:       "setupInit",
        title:      "Welcome!",
        nextPage:   "setupMenu",
        install:    false,
        uninstall:  state.setup.installed
    ]

    return dynamicPage(pageProperties) {
        section {
            paragraph textPara1
            paragraph textPara2
            paragraph textPara3
        }
        section("License") {
            paragraph textLicense
        }
    }
}

// Show "Main Menu" page
private def setupMenu() {
    TRACE("setupMenu()")

    if (state.setup.devices.size() == 0) {
        // Jump to the "Add Device" page 
        //return setupAddDevice()
    }

    def text =
        "Select one of the actions below, then tap the 'Done' button at " +
        "the top of the screen to complete setup."

    def pageProperties = [
        name:       "setupMenu",
        title:      "Setup Menu",
        nextPage:   null,
        install:    true,
        uninstall:  state.setup.installed
    ]

    state.setup.menu = false
    return dynamicPage(pageProperties) {
        section {
            paragraph text
            href "setupAddDevice", title:"Add Motion Sensor", description:"Tap to open"
            href "setupRemoveDevices", title:"Remove Motion Sensors", description:"Tap to open"
            href "setupListDevices", title:"List Motion Sensors", description:"Tap to open"
        }
    }
}

// Show "Add Device" setup page
private def setupAddDevice() {
    TRACE("setupAddDevice()")

    def helpName =
        "Give the motion sensor a descriptive name, for example 'Hallway Motion'."

    def helpAddress =
        "Each ActiveEye motion sensor is assigned a unique X10 address, " +
        "consisting of the 'House Code' (letters A through P) and the " +
        "'Unit Code' (numbers 1 through 16). Please enter the sensor's " +
        "X10 address below."

    def helpContinue =
        "Select 'Yes' if you wish to add more sensors, then tap the 'Next' " +
        "button at the top of the screen to continue."

    def houseCodes = ["A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P"]
    def unitCodes = ["1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16"]

    def pageProperties = [
        name:       "setupAddDevice",
        title:      "Add Motion Sensor",
        nextPage:   "actionAddDevice",
        install:    false,
        uninstall:  state.setup.installed
    ]

    return dynamicPage(pageProperties) {
        section("Sensor Name") {
            paragraph helpName
            input "setupDevName", "string", title: "What is your sensor name?", required:true
        }
        section("X10 Address") {
            paragraph helpAddress
            input "setupHouseCode", "enum", title: "What is your House Code?",
                metadata:[values:houseCodes], required:true
            input "setupUnitCode", "enum", title: "What is your Unit Code?",
                metadata:[values:unitCodes], required:true
        }
    }
}

private def actionAddDevice() {
    TRACE("actionAddDevice()")

    def devAddr = settings.setupHouseCode + settings.setupUnitCode
    addDevice(devAddr, settings.setupDevName)
    return setupMenu()
}

// Show "Remove Device" setup page
private def setupRemoveDevices() {
    TRACE("setupRemoveDevices()")

    def pageProperties = [
        name:       "setupRemoveDevices",
        title:      "Remove Motion Sensors",
        nextPage:   "setupMenu",
        install:    false,
        uninstall:  state.setup.installed
    ]

    if (state.setup.devices.size() == 0) {
        return dynamicPage(pageProperties) {
            section {
                paragraph "You have not configured any motion sensors yet."
                paragraph "Tap 'Next' to continue."
            }
        }
    }

    return dynamicPage(pageProperties) {
        section {
            paragraph "Not Implemented."
            paragraph "Tap 'Next' to continue."
        }
    }
}

private def actionRemoveDevices() {
    TRACE("actionRemoveDevices()")

    //removeDevice(devAddr, settings.setupDevName)
    return setupMenu()
}

private def setupListDevices() {
    TRACE("setupListDevices()")

    def deviceList = ""

    if (state.setup.devices.size() == 0) {
        deviceList = "You have not configured any devices yet."
    } else {
        deviceList =
            "Switches:\n" +
            "  A1 - Kitchen Lights\n" +
            "  A2 - Hallway Lights\n" +
            "Dimmers:\n" +
            "  B1 - Family Room Lights\n" +
            "  B2 - Master Bedoom Lights\n" +
            "Motion Sensors:\n" +
            "  C1 - Family Motion\n" +
            "  C2 - Hallway Motion\n" +
            "Remote Control:\n" +
            "  D1 - Garage Lights\n" +
            "  D2 - Coffee Maker"
    }

    def pageProperties = [
        name:       "setupListDevices",
        title:      "Configured Motion Sensors",
        nextPage:   "setupMenu",
        install:    false,
        uninstall:  state.setup.installed
    ]

    return dynamicPage(pageProperties) {
        section {
            paragraph "Tap 'Next' to continue."
            paragraph deviceList
        }
    }
}

private def setupConfigureDevice() {
    TRACE("setupConfigureDevice()")

    return dynamicPage(name:"setupWizard", uninstall:state.setup.installed) {
        section("Title") {
            input "deviceName", "string", title: "Device name"

        }
    }
}

def installed() {
    initialize()
}

def updated() {
    unsubscribe()
    initialize()
}

// Handle SmartApp touch event.
def onAppTouch(evt) {
    TRACE("onAppTouch(${evt})")
}

private def initialize() {
    TRACE("initialize()")
    log.trace "${app.name}. ${textVersion()}. ${textCopyright()}"

    // subscribe to attributes, devices, locations, etc.
    subscribe(app, onAppTouch)
}

private def addDevice(addr, name) {
    def device = [
        'name' : name,
        'type' : "motion",
    ]

    log.debug "device = ${device}"

    state.setup.devices[addr] = device

    log.debug "state = ${state}"
}

private def textVersion() {
    def text = "Version 0.9.0"
}

private def textCopyright() {
    def text = "Copyright (c) 2014 Statusbits.com"
}

private def TRACE(message) {
    log.debug message
    log.debug "state: ${state}"
    log.debug "settings: ${settings}"
}
