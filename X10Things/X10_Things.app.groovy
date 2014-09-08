/**
 *  X10 Things.
 *
 *  This smart app allows connecting X10 remote controls and ActiveEye motion
 *  sensors to SmartThings. Please note that you don't need SmartThings hub to
 *  connect X10 devices. Instead, you will need an X10 receiver (for example
 *  MR26A) and an X10 Gateway to send X10 commands directly to SmartThings
 *  over the Internet.
 *
 *  Please visit <https://github.com/statusbits/smartthings/blob/master/X10Things>
 *  for more information.
 *
 *  --------------------------------------------------------------------------
 *
 *  Copyright (c) 2014 geko@statusbits.com
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
 *  The latest version of this file can be found at:
 *  https://github.com/statusbits/smartthings/blob/master/X10Things/X10_Things.app.groovy
 *
 *  Revision History
 *  ----------------
 *  2014-09-07  V0.9.0  Initial check-in.
 */

definition(
    name: "X10 Things",
    namespace: "statusbits",
    author: "geko@statusbits.com",
    description: "Connect X10 remote controls and ActiveEye motion sensors to SmartThings.",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    oauth: true
)

preferences {
    page name:"setupInit"
    page name:"setupMenu"
    page name:"setupRemoteControl"
    page name:"setupActiveEye"
    page name:"setupAddDevice"
    page name:"setupShowDevices"
    page name:"setupRestEndpoint"
}

mappings {
    path("/devices") {
        action: [
            GET: "restListDevices"
        ]
    }

    path("/device/:unit/:command") {
        action: [
            GET: "restCommand"
        ]
    }
}

private def setupInit() {
    TRACE("setupInit()")

    if (state.installed) {
        // already initialized, go to setup menu
        return setupMenu()
    }

    // initialize app state and show welcome page
    state.installed = false
    state.devices = [:]
    return setupWelcome()
}

// Show setup welcome page
private def setupWelcome() {
    TRACE("setupWelcome()")

    def textPara1 =
        "This smart app allows connecting X10 remote controls and " +
        "ActiveEye motion sensors to SmartThings. Please note that you " +
        "don't need SmartThings hub to connect X10 devices. Instead, you " +
        "will need an X10 receiver (for example MR26A) and an X10 Gateway " +
        "to send X10 commands directly to SmartThings over the Internet."

    def textPara2 = "${app.name}. ${textVersion()}\n${textCopyright()}"

    def textPara3 =
        "Please read the License Agreement below. By tapping the 'Next' " +
        "button at the top of the screen, you agree and accept the terms " +
        "and conditions of the License Agreement."

    def textLicense =
        "This program is free software: you can redistribute it and/or " +
        "modify it under the terms of the GNU General Public License as " +
        "published by the Free Software Foundation, either version 3 of " +
        "the License, or (at your option) any later version.\n\n" +
        "This program is distributed in the hope that it will be useful, " +
        "but WITHOUT ANY WARRANTY; without even the implied warranty of " +
        "MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU " +
        "General Public License for more details.\n\n" +
        "You should have received a copy of the GNU General Public License " +
        "along with this program. If not, see <http://www.gnu.org/licenses/>."

    def pageProperties = [
        name        : "setupInit",
        title       : "Welcome!",
        nextPage    : "setupMenu",
        install     : false,
        uninstall   : state.installed
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

    def pageProperties = [
        name        : "setupMenu",
        title       : "Setup Menu",
        nextPage    : null,
        install     : true,
        uninstall   : state.installed
    ]

    return dynamicPage(pageProperties) {
        section {
            href "setupRemoteControl", title:"Configure Remote Control", description:"Tap to open"
            href "setupActiveEye", title:"Add ActiveEye Motion Sensor", description:"Tap to open"
            if (state.devices?.size()) {
                href "setupShowDevices", title:"Show Installed Devices", description:"Tap to open"
            }
        }
        section("Utilities") {
            href "setupRestEndpoint", title:"Show REST API Endpoint", description:"Tap to open"
        }
        section([title:"Options", mobileOnly:true]) {
            label title:"Assign a name", required:false
            //mode title:"Set for specific mode(s)", required:false
        }
        section("About") {
            paragraph "${app.name}. ${textVersion()}\n${textCopyright()}"
        }
    }
}

// Show "Configure Remote Control" setup page
private def setupRemoteControl() {
    TRACE("setupRemoteControl()")

    def textAbout =
        "You can use X10 remote control to turn SmartThings switches on " +
        "and off and/or to activate 'Hello, Home' actions. Two different " +
        "actions can be assigned to each pair of the 'On' and 'Off' buttons."

    def textHouseCode =
        "X10 remote control transmits commands on one of the 16 channels, " +
        "knows as 'House Codes' (letters 'A' through 'P'). Please select " +
        "House Code assigned to your remote control below."

    def inputHouseCode = [
        name        : "remoteHouseCode",
        type        : "enum",
        title       : "What is your House Code?",
        metadata    : [values:x10HouseCodes()],
        required    : false
    ]

    def pageProperties = [
        name        : "setupRemoteControl",
        title       : "Configure Remote Control",
        nextPage    : "setupMenu",
        install     : false,
        uninstall   : state.installed
    ]

    def hhActions = getHHActions()

    return dynamicPage(pageProperties) {
        section {
            paragraph textAbout
            paragraph textHouseCode
            input inputHouseCode
        }
        for (int n = 1; n <= 16; n++) {
            section("Button ${n}", hideable:true, hidden:true) {
                input "switches_${n}", "capability.switch", title:"Which switches?", multiple:true, required:false
                input "actionOn_${n}", "enum", title:"Which action for 'On' button?", metadata:[values:hhActions], required:false
                input "actionOff_${n}", "enum", title:"Which action for 'Off' button?", metadata:[values:hhActions], required:false
            }
        }
    }
}

// Show "Add ActiveEye Motion Sensor" setup page
private def setupActiveEye() {
    TRACE("setupActiveEye()")

    def textAddress =
        "Each ActiveEye motion sensor is assigned an address, consisting " +
        "of a House Code (letters 'A' - 'P') and a Unit Code (numbers 1 - " +
        "16), for example 'D12'. Please select the X10 address assigned " +
        "to your motion sensor below."

    def inputDeviceName = [
        name        : "motionDeviceName",
        type        : "string",
        title       : "What is your sensor name?",
        defaultValue: "ActiveEye Motion Sensor",
        required    : true
    ]

    def inputHouseCode = [
        name        : "motionHouseCode",
        type        : "enum",
        title       : "What is your House Code?",
        metadata    : [values:x10HouseCodes()],
        required    : true
    ]

    def inputUnitCode = [
        name        : "motionUnitCode",
        type        : "enum",
        title       : "What is your Unit Code?",
        metadata    : [values:x10UnitCodes()],
        required    : true
    ]

    def pageProperties = [
        name:       "setupActiveEye",
        title:      "Add ActiveEye Motion Sensor",
        nextPage:   "setupAddDevice",
        install:    false,
        uninstall:  state.installed
    ]

    return dynamicPage(pageProperties) {
        section {
            input inputDeviceName
            paragraph textAddress
            input inputHouseCode
            input inputUnitCode
        }
    }
}

private def setupAddDevice() {
    TRACE("setupAddDevice()")

    addActiveEye(settings.motionHouseCode, settings.motionUnitCode)
    return setupMenu()
}

// Show "Show Installed Devices" setup page
private def setupShowDevices() {
    TRACE("setupShowDevices()")

    def textNoDevices =
        "You have not configured any X10 devices. Tap Done to continue."

    def pageProperties = [
        name        : "setupShowDevices",
        title       : "Installed Devices",
        nextPage    : "setupMenu",
        install     : false,
        uninstall   : state.installed
    ]

    def devices = getDeviceMap()
    if (devices.size() == 0) {
        return dynamicPage(pageProperties) {
            section {
                paragraph textNoDevices
            }
        }
    }

    devices = devices.sort({ k1, k2 -> k1 <=> k2 } as Comparator)
    return dynamicPage(pageProperties) {
        section {
            paragraph "Tap Done to continue."
        }
        section("ActiveEye Motion Sensors") {
            devices.each { k,v ->
                paragraph "${k} - ${v}"
            }
        }
    }
}

// Show "REST API Endpoint" setup page
private setupRestEndpoint() {
    TRACE("setupRestEndpoint()")

    def accessToken = getAccessToken()
    TRACE("URI: https://graph.api.smartthings.com/api/token/${accessToken}/smartapps/installations/${app.id}/")

    def textHelp =
        "The X10 gateway can send X10 commands to the X10 Things using " +
        "REST API. Please save the REST API endpoint URL and the access " +
        "token shown below for your reference.\n\n"
        "Tap Done to continue."

    def pageProperties = [
        name        : "setupRestEndpoint",
        title       : "Application REST API Endpoint",
        nextPage    : "setupMenu",
        install     : false,
        uninstall   : state.installed
    ]

    return dynamicPage(pageProperties) {
        section {
            paragraph textHelp
        }
        section("REST API Endpoint") {
            paragraph getAppUri()
        }
        section("REST API Access Token") {
            paragraph accessToken
        }
    }
}

def installed() {
    TRACE("installed()")

    initialize()
}

def updated() {
    TRACE("updated()")

    unsubscribe()
    initialize()
}

def uninstalled() {
    TRACE("uninstalled()")

    // delete all child devices
    def devices = getChildDevices()
    devices?.each {
        try {
            deleteChildDevice(it.deviceNetworkId)
        } catch (e) {
            log.error "Cannot delete device ${it.deviceNetworkId}. Error: ${e}"
        }
    }
}

// Handle SmartApp touch event.
def onAppTouch(evt) {
    STATE()
}

// '.../devices' REST endpoint handler
def restListDevices() {
    TRACE("restListDevices()\n params: ${params}")
}

// '.../device/:unit/:command' REST endpoint handler
def restCommand() {
    TRACE("restCommand()\n params: ${params}")
}

// Excecute X10 'on' command on behalf of child device
def x10_on(nid) {
    TRACE("x10_on(${nid})")

    def s = nid?.tokenize(':')
    if (s.size() < 2 || s[0].toUpperCase() != 'X10') {
        log.debug "Invalid device network ID ${nid}"
        return
    }
}

// Excecute X10 'off' command on behalf of child device
def x10_off(nid) {
    TRACE("x10_off(${nid})")

    def s = nid?.tokenize(':')
    if (s.size() < 2 || s[0].toUpperCase() != 'X10') {
        log.debug "Invalid device network ID ${nid}"
        return
    }
}

// Excecute X10 'dim' command on behalf of child device
def x10_dim(nid) {
    TRACE("x10_dim(${nid})")

    def s = nid?.tokenize(':')
    if (s.size() < 2 || s[0].toUpperCase() != 'X10') {
        log.debug "Invalid device network ID ${nid}"
        return
    }
}

// Excecute X10 'bright' command on behalf of child device
def x10_bright(nid) {
    TRACE("x10_bright(${nid})")

    def s = nid?.tokenize(':')
    if (s.size() < 2 || s[0].toUpperCase() != 'X10') {
        log.debug "Invalid device network ID ${nid}"
        return
    }
}

private def initialize() {
    log.trace "${app.name}. ${textVersion()}. ${textCopyright()}"
    STATE()

    state.installed = true
    getAccessToken()
    updateDeviceList()

    // for debugging
    subscribe(app, onAppTouch)
}

private def addActiveEye(houseCode, unitCode) {
    TRACE("addActiveEye(${houseCode}, ${unitCode})")

    def addr = "${houseCode}${unitCode}".toUpperCase()
    if (getChildDevice(addr)) {
        log.error "Child device ${addr} already exist"
        return false
    }

    def devFile = "X10 ActiveEye"
    def devParams = [
        name            : settings.motionDeviceName,
        label           : settings.motionDeviceName,
        completedSetup  : true
    ]

    log.trace "Creating child device ${devParams}"
    try {
        def dev = addChildDevice("statusbits", devFile, addr, null, devParams)
        dev.refresh()
    } catch (e) {
        log.error "Cannot create child device. Error: ${e}"
        return false
    }

    // save device in the app state
    state.devices[addr] = [
        'dni'   : addr,
        'type'  : 'motion',
    ]

    // create device for the ActiveEye light sensor
    def unitCode2 = unitCode.toInteger() + 1
    if (unitCode2 <= 16) {
        def addr2 = "${houseCode}${unitCode2}".toUpperCase()
        if (state.devices.containsKey(addr2)) {
            log.error "X10 address ${addr2} is in use"
        } else {
            state.devices[addr2] = [
                'dni'   : addr,
                'type'  : 'light',
            ]
        }
    }

    STATE()
    return true
}

private def getHHActions() {
    def scenes = []
    location.helloHome?.getPhrases().each {
        scenes << "${it.label}"
    }

    return scenes
}

// Purge devices that were removed manually
private def updateDeviceList() {
    TRACE("updateDeviceList()")

    state.devices.each { k,v ->
        if (!getChildDevice(v.dni)) {
            log.trace "Removing deleted device ${v.dni}"
            state.devices.remove(k)
        }
    }

    // refresh all child devices
    def devices = getChildDevices()
    devices?.each {
        it.refresh()
    }
}

private def getDeviceMap() {
    def map = [:]
    def devices = getChildDevices()
    devices?.each {
        map[it.deviceNetworkId] = it.displayName
    }

    return map
}

private def getAccessToken() {
    if (atomicState.accessToken) {
        return atomicState.accessToken
    }

    def token = createAccessToken()
    TRACE("Created new access token: ${token})")

    return token
}

private def getAppUri() {
    return "https://graph.api.smartthings.com/api/smartapps/installations/${app.id}/"
}

private def x10HouseCodes() {
    return ["A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P"]
}

private def x10UnitCodes() {
    return ["1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16"]
}

private def textVersion() {
    return "Version 0.9.0"
}

private def textCopyright() {
    return "Copyright (c) 2014 Statusbits.com"
}

private def TRACE(message) {
    log.debug message
    log.debug "state: ${state}"
}

private def STATE() {
    log.debug "settings: ${settings}"
    log.debug "state: ${state}"
}
