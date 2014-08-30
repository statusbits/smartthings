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
    page name:"setupSceneController"
    page name:"setupMotionSensor"
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

    def textHelp =
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
            paragraph textHelp
            href "setupSceneController", title:"Add Scene Controller", description:"Tap to open"
            href "setupMotionSensor", title:"Add Motion Sensor", description:"Tap to open"
            if (state.setup.devices.size() > 0) {
                href "setupRemoveDevices", title:"Remove Motion Sensors", description:"Tap to open"
                href "setupListDevices", title:"List Motion Sensors", description:"Tap to open"
            }
        }
    }
}

// Show "Add Scene Controller" setup page
private def setupSceneController() {
    TRACE("setupSceneController()")

    def textHelp =
        "You can use X10 remote control to activate \"Hello, Home\" " +
        "actions (also known as \"scenes\"). Each pair of the remote " +
        "control On and Off buttons transmits commands on one of the 256 " +
        "X10 channels, identified by the House Code (letters A through P) " +
        "and the Unit Code (numbers 1 through 16). You can assign " +
        "different scenes to On and Off buttons."

    def pageProperties = [
        name:       "setupSceneController",
        title:      "Add Scene Controller",
        nextPage:   "actionAddDevice",
        install:    false,
        uninstall:  state.setup.installed
    ]

    // Set new device type
    state.setup.newDeviceType = "scene"

    return dynamicPage(pageProperties) {
        section {
            paragraph textHelp
        }
        section("Select X10 Channel") {
            input "setupHouseCode", "enum", title:"House Code",
                metadata:[values:x10HouseCodes()], required:true
            input "setupUnitCode", "enum", title:"Unit Code",
                metadata:[values:x10UnitCodes()], required:true
        }
        section("Run this Scene ...") {
            input "setupSceneOn", "enum", title:"When On button is pushed",
                metadata:[values:getScenes()], required:true
            input "setupSceneOff", "enum", title:"When Off button is pushed",
                metadata:[values:["None"] + getScenes()], required:false
        }
    }
}

// Show "Add Motion Sensor" setup page
private def setupMotionSensor() {
    TRACE("setupMotionSensor()")

    def helpName =
        "Give the motion sensor a descriptive name, for example 'Hallway Motion'."

    def helpChannel =
        "Each ActiveEye motion sensor transmits commands on one of the 256 " +
        "channels, identified by \"House Code\" (letters A through P) and " +
        "\"Unit Code\" (numbers 1 through 16). Please select the sensor's " +
        "X10 channel below."

    def pageProperties = [
        name:       "setupMotionSensor",
        title:      "Add Motion Sensor",
        nextPage:   "actionAddDevice",
        install:    false,
        uninstall:  state.setup.installed
    ]

    // Set new device type
    state.setup.newDeviceType = "motion"

    return dynamicPage(pageProperties) {
        section {
            paragraph helpName
            input "setupDevName", "string", title:"What is your sensor name?",
                required:true, defaultValue:"X10 ActiveEye"
        }
        section("X10 Channel") {
            paragraph helpChannel
            input "setupHouseCode", "enum", title:"Select Motion Sensor House Code",
                metadata:[values:x10HouseCodes()], required:true
            input "setupUnitCode", "enum", title:"Select Motion Sensor Unit Code",
                metadata:[values:x10UnitCodes()], required:true
        }
        section("Options") {
            input "setupLightSensor", "bool", title:"Enable Light Sensor",
                defaultValue:true, required:true
        }
    }
}

private def actionAddDevice() {
    TRACE("actionAddDevice()")

    String devAddr = settings.setupHouseCode + settings.setupUnitCode
    if (state.setup.devices.containsKey(devAddr)) {
        log.error "X10 address ${devAddr} is in use"
    } else {
        switch (state.setup.newDeviceType) {
        case "scene":
            addSceneController(devAddr)
            break;

        case "motion":
            addMotionSensor(devAddr)
            break;
        }
    }

    state.setup.newDeviceType = null
    return setupMenu()
}

// Show "Remove Device" setup page
private def setupRemoveDevices() {
    TRACE("setupRemoveDevices()")

    def pageProperties = [
        name:       "setupRemoveDevices",
        title:      "Remove Motion Sensors",
        nextPage:   "actionRemoveDevices",
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

    def deviceList = []
    state.setup.devices.each() { k,v ->
        // enumerate only motion sensors
        if (v.type == "motion") {
            deviceList << "${k} - ${v.name}"
        }
    }

    return dynamicPage(pageProperties) {
        section {
            paragraph "Select device you wish to remove, then tap 'Next' to continue."
            input "setupDevRemove", "enum", title:"Select Devices",
                metadata:[values:deviceList], required:false, multiple:true
        }
    }
}

private def actionRemoveDevices() {
    TRACE("actionRemoveDevices()")

    settings.setupDevRemove.each() {
        def parts = it.tokenize()
        removeDevice(parts[0])
    }

    return setupMenu()
}

private def setupListDevices() {
    TRACE("setupListDevices()")

    def pageProperties = [
        name:       "setupListDevices",
        title:      "X10 Device List",
        nextPage:   "setupMenu",
        install:    false,
        uninstall:  state.setup.installed
    ]

    if (state.setup.devices.size() == 0) {
        return dynamicPage(pageProperties) {
            section {
                paragraph "You have not configured any X10 devices yet. Tap 'Done' to continue."
            }
        }
    }

    def motionSensors = ""
    state.setup.devices.each() { k,v ->
        // enumerate only motion sensors
        if (v.type == "motion") {
            motionSensors += "${k} - ${v.name}\n"
        }
    }

    return dynamicPage(pageProperties) {
        section {
            paragraph "Tap 'Done' to continue."
        }
        section("Motion Sensors") {
            paragraph motionSensors
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

private def addSceneController(addr) {
    TRACE("addMotionSensor(${addr})")

    def device = [
        'type'      : 'scene',
        'sceneOn'   : settings.setupSceneOn,
        'sceneOff'  : settings.setupSceneOff
    ]

    log.debug "device = ${device}"

    state.setup.devices[addr] = device

    log.debug "state = ${state}"
    return true
}

private def addMotionSensor(addr) {
    TRACE("addMotionSensor(${addr})")

	def devId = "X10:${addr}"
	if (getChildDevice(devId)) {
		log.error "Child device ${devId} already exist"
		return false
	}

	def devFile = "X10 ActiveEye"
    def devParams = [
    	name  			: settings.setupDevName,
        label 			: settings.setupDevName,
        completedSetup 	: true
    ]

	log.debug "Creating Child device ${devParams}"
	//def dev = addChildDevice("statusbits", devFile, devId, null, devParams)
    //if (dev == null) {
	//    log.error "Cannot create child device \'${devFile}\'"
	//    return false
    //}

    def device = [
        'type' : 'motion',
        'name' : settings.setupDevName
    ]

    log.debug "device = ${device}"

    state.setup.devices[addr] = device

    log.debug "state = ${state}"
    return true
}

private def removeDevice(addr) {
    TRACE("removeDevice(${addr})")
    state.setup.devices.remove(addr)
}

private def getScenes() {
    def scenes = []
    def actions = location.helloHome?.getPhrases()
    actions.each() {
        scenes << "${it.label}"
    }

    return scenes
}

private def x10HouseCodes() {
    def houseCodes = ["A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P"]
}

private def x10UnitCodes() {
    def unitCodes = ["1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16"]
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
