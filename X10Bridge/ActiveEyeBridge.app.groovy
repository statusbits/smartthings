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
 *  Version: 0.9.0
 *  Date: 2014-08-18
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
    page name:"setupWizard", title:"Setup Wizard", nextPage:"setupWizard", install:false
    page name:"setupAddDevice", nextPage:"setupWizard", install:false
}

private def setupInit() {
    TRACE("setupInit()")
    log.debug "app: ${app.getProperties()}"

    if (!state.setup) {
        // Initialize setup state
        state.setup = [:]
        state.setup.menu = true
        state.setup.installed = false
        state.setup.devices = [:]
    } else {
        // Start in menu mode
        state.setup.menu = true

        if (state.setup.installed) {
            // Skip welcome page
            return setupWizard()
        }
    }

    return pageWelcome()
}

// Setup wizard state machine.
def setupWizard() {
    TRACE("setupWizard()")

    if (state.setup.devices.size() == 0) {
        // Jump to the "Add Device" page 
        return pageAddDevice()
    }

    if (state.setup.menu) {
        // Jump to the 'Main Menu' page
        return pageMainMenu()
    }

    // Jump to appropriate menu action page
    def nextPage
    switch (settings.setupAction) {
        case "Add Sensors":
            nextPage = pageAddDevice()
            break

        case "Remove Sensors":
            nextPage = setupRemoveDevice()
            break

        case "List Sensors":
            nextPage = setupListDevices()
            break

        case "Exit Setup":
            nextPage = setupFinish()
            break

        default:
            nextPage = pageMainMenu()
    }

    state.setup.menu = true
    return nextPage
}

// Generates "Welcome" page
private def pageWelcome() {
    TRACE("pageWelcome()")

    def textWelcome =
        "ActiveEye Bridge integrates X10 ActiveEye (Model MS16A) and/or " +
        "EagleEye (Model MS14A) motion sensors into SmartThings.\n\n" +
        "ActiveEye Bridge does not rely on the SmartThings Hub to receive " +
        "signals from the motion sensors. Instead, it requires an X10 " +
        "serial Firecracker receiver (Model MR26A) and an ActiveEye Gateway " +
        "software installed on a PC. Please visit [www.statusbits.com/p/activeeye] " +
        "for more information.\n\n" +
        "Please read the Licence Agreement below, then tap the 'Next' button " +
        "at the top of the screen to accept the terms and conditions of the " +
        "License Agreement and continue."

    def textLicense =
        "Licensed under the Apache License, Version 2.0 (the \"License\"); " +
        "you may not use this software except in compliance with the License. " +
        "You may obtain a copy of the License at:\n\n" +
        "http://www.apache.org/licenses/LICENSE-2.0\n\n" +
        "Unless required by applicable law or agreed to in writing, software " +
        "distributed under the License is distributed on an \"AS IS\" BASIS, " +
        "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or " +
        "implied. See the License for the specific language governing " +
        "permissions and limitations under the License."

    def pageProperties = [
        name:       "setupInit",
        title:      "Welcome!",
        nextPage:   "setupWizard",
        uninstall:  false
    ]

    return dynamicPage(pageProperties) {
        section {
            paragraph textWelcome
        }
        section("License") {
            paragraph textLicense
        }
        section("Copyright (c) 2014 Statusbits.com") {}
    }
}

// Generates "Main Menu" page
private def pageMainMenu() {
    TRACE("pageMainMenu()")

    // Setup action menu
    def actions = [
        "Add Sensors",
        "Remove Sensors",
        "List Sensors",
        "Exit Setup"
    ]

    def text =
        "Tap the Main Menu below and select one of the options, then tap the " +
        "'Next' button at the top of the screen to continue."

    state.setup.menu = false
    return dynamicPage(name:"setupWizard", uninstall:state.setup.installed) {
        section("Main Menu") {
            paragraph text
            input name: "setupAction", title:"Main Menu", type: "enum", required:true,
                description: "Tap to select option", metadata:[values:actions]
        }
    }
}

// Generates "Add Device" page
private def pageAddDevice() {
    TRACE("pageAddDevice()")

    def houseCodes = ["A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P"]
    def unitCodes = ["1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16"]
    def helpName =
        "Give the motion sensor a descriptive name, for example 'Hallway Motion'."
    def helpAddress =
        "Each ActiveEye motion sensor is assigned a unique X10 address, " +
        "consisting of the 'House Code' (letters A through P) and the 'Unit Code' " +
        "(numbers 1 through 16). Please enter the sensor's X10 address below."
    def helpContinue =
        "Select 'Yes' if you wish to add more sensors, then tap the 'Next' button " +
        "at the top of the screen to continue."

    return dynamicPage(name:"setupWizard", title:"Add Motion Sensor", nextPage:"setupAddDevice", uninstall:state.setup.installed) {
        section("Sensor Name") {
            input "setupDevName", "string", title: "What is your sensor name?", required:true
            paragraph helpName
        }
        section("X10 Address") {
            input "setupHouseCode", "enum", title: "What is your House Code?",
                metadata:[values:houseCodes], required:true
            input "setupUnitCode", "enum", title: "What is your Unit Code?",
                metadata:[values:unitCodes], required:true
            paragraph helpAddress
        }
        section("Continue") {
            input "setupAddMore", "enum", title: "Add more sensors?",
                metadata:[values:["Yes","No"]], required:true
            paragraph helpContinue
        }
    }
}

private def setupAddDevice() {
    TRACE("setupAddDevice()")

    def devAddr = settings.setupHouseCode + settings.setupUnitCode
    addDevice(devAddr, settings.setupDevName)

    if (settings.setupAddMore == 'Yes') {
        return pageAddDevice()
    } else {
        return pageMainMenu()
    }
}

private def setupRemoveDevice() {
    TRACE("setupRemoveDevice()")

    return dynamicPage(name:"setupWizard", uninstall:state.setup.installed) {
        section("Operation Not Implemented") {
            paragraph "Tap 'Next' to continue."
        }
    }
}

private def setupListDevices() {
    TRACE("setupListDevices()")

    def text = ""

    if (state.setup.devices.size() == 0) {
        text = "You have not configured any devices yet."
    } else {
        text =
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

    return dynamicPage(name:"setupWizard", uninstall:state.setup.installed) {
        section("Device List") {
            paragraph text
        }
        section("Tap 'Next' to continue.") {}
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

private def setupFinish() {
    TRACE("setupFinish()")

    def message =
        "You have successfully conifigured your devices. Tap the 'Next' " +
        "button at the top of the screen one more time to complete installation."

    state.setup.installed = true
    state.setup.action = "finish"

    // override install attribute to complete installation
    return dynamicPage(name:"setupWizard", nextPage:null, uninstall:state.setup.installed, install:true) {
        section("Congratulations!") {
            paragraph message
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

def initialize() {
    TRACE("initialize()")
    log.trace "${app.name}. Version 0.1.0. Copyright (c) 2014, Statusbits.com"

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

// Handle SmartApp touch event.
def onAppTouch(evt) {
    TRACE("onAppTouch(${evt})")
}

private def textAbout() {
    def text =
        "Version 0.1.0\n" +
        "Copyright (c) 2014 Statusbits.com\n\n" +
        "Visit [www.statusbits.com/p/activeeye] for more information."
}

private def TRACE(message) {
    log.debug message
    log.debug "settings: ${settings}"
    log.debug "state: ${state}"
}
