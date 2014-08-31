/**
 *  X10 Bridge.
 *
 *  This SmartApp allows integration of X10 switches and dimmers with
 *  SmartThings. Please note that it requires a Linux host with Mochad server
 *  installed on the local network and accessible from the SmartThings hub.
 *  Mochad is a free, open-source X10 gateway software for Linux. Please
 *  visit https://github.com/statusbits/smartthings/blob/master/X10Bridge
 *  for more information.
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
 *  https://github.com/statusbits/smartthings/blob/master/X10Bridge/X10Bridge.app.groovy
 *
 *  Useful links:
 *  - X10 Bridge project page: https://github.com/statusbits/smartthings/blob/master/X10Bridge
 *  - Mochad project page: http://sourceforge.net/projects/mochad/
 *
 *  Revision History
 *  ----------------
 *  2014-08-18  V0.9.0  Initial check-in
 */

definition(
    name: "X10 Bridge",
    namespace: "statusbits",
    author: "geko@statusbits.com",
    description: "Connect X10 switches and dimmers to SmartThings. Requires Mochad server.",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    //oauth: true
)

preferences {
    page name:"setupInit"
    page name:"setupMenu"
    page name:"setupAddSwitch"
    page name:"setupActionAdd"
    page name:"setupRemoveDevices"
    page name:"setupActionRemove"
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
        "X10 Bridge allows you integrate X10 switches and dimmers into " +
        "SmartThings. Please note that it requires a Linux box running " +
        "Mochad server installed on the local network and accessible from " +
        "the SmartThings hub.\n\n" +
        "Mochad is a free, open-source X10 gateway software for Linux. " +
        "Please visit [insert link] for X10 Bridge setup instructions."

    def textPara2 = "${app.name}. ${textVersion()}\n${textCopyright()}"

    def textPara3 =
        "Please read the License Agreement below. By tapping the 'Next' " +
        "button at the top of the screen, you agree and accept the terms " +
        "and conditions of the License Agreement."

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

    state.setup.deviceType = null

    return dynamicPage(pageProperties) {
        section {
            paragraph textHelp
            href "setupAddSwitch", title:"Add X10 Switch", description:"Tap to open"
            if (state.setup.devices.size() > 0) {
                href "setupRemoveDevices", title:"Remove Devices", description:"Tap to open"
                href "setupListDevices", title:"List Installed Devices", description:"Tap to open"
            }
        }
        section("About") {
            paragraph "${app.name}. ${textVersion()}\n${textCopyright()}"
        }
    }
}

// Show "Add X10 Switch" setup page
private def setupAddSwitch() {
    TRACE("setupAddSwitch()")

    def textHelpName =
        "Give your X10 switch a descriptive name, for example 'Kitchen " +
        "Lights'."

    def textHelpAddr =
        "Each X10 device is assigned an address, consisting of two parts - " +
        "a House Code (letters A through P) and a Unit Code (numbers 1 " +
        "through 16), for example 'D12'. Please check your device and " +
        "enter its X10 device address below."

    def pageProperties = [
        name:       "setupAddSwitch",
        title:      "Add X10 Switch",
        nextPage:   "setupActionAdd",
        install:    false,
        uninstall:  state.setup.installed
    ]

    // Set new device type
    state.setup.deviceType = "switch"

    return dynamicPage(pageProperties) {
        section {
            paragraph textHelpName
            input "setupDevName", "string", title:"What is your switch name?",
                required:true, defaultValue:"X10 Switch"
        }
        section("X10 Address") {
            paragraph textHelpAddr
            input "setupHouseCode", "enum", title:"What is your switch House Code?",
                metadata:[values:x10HouseCodes()], required:true
            input "setupUnitCode", "enum", title:"What is your switch Unit Code?",
                metadata:[values:x10UnitCodes()], required:true
        }
    }
}

private def setupActionAdd() {
    TRACE("setupActionAdd()")

    String devAddr = settings.setupHouseCode + settings.setupUnitCode
    if (state.setup.devices.containsKey(devAddr)) {
        log.error "X10 address ${devAddr} is in use"
    } else {
        switch (state.setup.deviceType) {
        case "switch":
            addSwitch(devAddr)
            break;
        }
    }

    return setupMenu()
}

// Show "Remove Device" setup page
private def setupRemoveDevices() {
    TRACE("setupRemoveDevices()")

    def textHelp =
        "Select devices you wish to remove, then tap 'Next' to continue."

    def textNoDevices =
        "You have not configured any X10 devices yet. Tap 'Done' to continue."

    def pageProperties = [
        name:       "setupRemoveDevices",
        title:      "Remove Devices",
        nextPage:   "setupActionRemove",
        install:    false,
        uninstall:  state.setup.installed
    ]

    if (state.setup.devices.size() == 0) {
        return dynamicPage(pageProperties) {
            section {
                paragraph textNoDevices
            }
        }
    }

    def deviceList = []
    state.setup.devices.each { k,v ->
        deviceList << "${k} - ${v.name}"
    }

    return dynamicPage(pageProperties) {
        section {
            paragraph textHelp
            input "setupDevRemove", "enum", title:"Select Devices",
                metadata:[values:deviceList.sort()], required:false, multiple:true
        }
    }
}

private def setupActionRemove() {
    TRACE("setupActionRemove()")

    settings.setupDevRemove.each {
        def parts = it.tokenize()
        removeDevice(parts[0])
    }

    return setupMenu()
}

private def setupListDevices() {
    TRACE("setupListDevices()")

    def textNoDevices =
        "You have not configured any X10 devices yet. Tap 'Done' to continue."

    def pageProperties = [
        name:       "setupListDevices",
        title:      "Installed Devices",
        nextPage:   "setupMenu",
        install:    false,
        uninstall:  state.setup.installed
    ]

    if (state.setup.devices.size() == 0) {
        return dynamicPage(pageProperties) {
            section {
                paragraph textNoDevices
            }
        }
    }

    def switches = getDeviceListAsText('switch')
    return dynamicPage(pageProperties) {
        section {
            paragraph "Tap 'Done' to continue."
        }
        section("Switches") {
            paragraph switches
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

private def addSwitch(addr) {
    TRACE("addSwitch(${addr})")

	def dni = "X10:${addr}"
	if (getChildDevice(dni)) {
		log.error "Child device ${dni} already exist"
		return false
	}

	def devFile = "X10 Switch"
    def devParams = [
    	name  			: settings.setupDevName,
        label 			: settings.setupDevName,
        completedSetup 	: true
    ]

	log.debug "Creating Child device ${devParams}"
	//def dev = addChildDevice("statusbits", devFile, dni, null, devParams)
    //if (dev == null) {
	//    log.error "Cannot create child device \'${devFile}\'"
	//    return false
    //}

    def device = [
        'type'  : 'switch',
        'name'  : settings.setupDevName,
        'dni'   : dni
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

private def getDeviceMap() {
    def devices = [:]
    state.setup.devices.each { k,v ->
        if (!devices.containsKey(v.type)) {
            devices[v.type] = []
        }
        devices[v.type] << k
    }

    return devices
}

private def getDeviceListAsText(type) {
    String s = ""
    state.setup.devices.each { k,v ->
        if (v.type == type) {
            s += "${k} - ${v.name}\n"
        }
    }

    return s
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
