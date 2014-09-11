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
    page name:"pageSetup"
    page name:"pageAbout"
    page name:"setupRemoteControl"
    page name:"setupActiveEye"
    page name:"setupAddDevice"
    page name:"setupShowDevices"
    page name:"setupRestEndpoint"
    page name:"actionRestEndpoint"
}

mappings {
    path("/devices") {
        action: [
            GET: "apiListDevices"
        ]
    }

    path("/command/:address/:command") {
        action: [
            GET: "apiCommand"
        ]
    }
}

// Show "Setup Menu" page
private def pageSetup() {
    TRACE("pageSetup()")

    if (state.devices == null) {
        // First run - initialize state
        state.devices = [:]
        state.installed = false
        return pageAbout()
    }

    updateDeviceList()

    def pageProperties = [
        name        : "pageSetup",
        title       : "Setup Menu",
        nextPage    : null,
        install     : true,
        uninstall   : state.installed
    ]

    return dynamicPage(pageProperties) {
        section {
            href "pageAbout", title:"About", description:"Tap to open"
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
    }
}

// Show "About" page
private def pageAbout() {
    TRACE("pageAbout()")

    def textAbout =
        "X10 Things allows you to connect X10 remote controls and " +
        "ActiveEye motion sensors to SmartThings. Please note that you " +
        "don't need SmartThings hub to connect X10 devices. Instead, you " +
        "will need an X10 receiver (for example MR26A) and an X10 Gateway " +
        "to send X10 commands directly to SmartThings over the Internet."

    def pageProperties = [
        name        : "pageAbout",
        title       : "About",
        nextPage    : "pageSetup",
        install     : false,
        uninstall   : state.installed
    ]

    return dynamicPage(pageProperties) {
        section {
            paragraph textAbout
            paragraph "${textVersion()}\n${textCopyright()}"
        }
        section("License") {
            paragraph textLicense()
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
        nextPage    : "pageSetup",
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

    def textLightSensor =
        "The ActiveEye has a built-in light sensor and can be optionally " +
        "configured to transmit the light sensor status. If you have " +
        "enabled this function, you should enable the light sensor here as " +
        "well to avoid unexpected behavior."

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

    def inputLightSensor = [
        name        : "enableLightSensor",
        type        : "bool",
        title       : "Light Sensor Enabled",
        defaultValue: false,
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
        section("Options") {
            paragraph textLightSensor
            input inputLightSensor
        }
    }
}

private def setupAddDevice() {
    TRACE("setupAddDevice()")

    addActiveEye(settings.motionHouseCode, settings.motionUnitCode, settings.enableLightSensor)
    return pageSetup()
}

// Show "Show Installed Devices" setup page
private def setupShowDevices() {
    TRACE("setupShowDevices()")

    def textNoDevices =
        "You have not configured any X10 devices. Tap Done to continue."

    def pageProperties = [
        name        : "setupShowDevices",
        title       : "Installed Devices",
        nextPage    : "pageSetup",
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

    def textAbout =
        "The X10 Gateway sends commands to the X10 Things using REST API. " +
        "Please note the API endpoint URL and the access token shown below " +
        "for your reference."

    def textSendSms =
        "You can send the URL and the access token to yourself via a text " +
        "message."

    def inputPhone = [
        name        : "setupPhoneNumber",
        type        : "phone",
        title       : "What is your phone number?",
        required    : false
    ]

    def pageProperties = [
        name        : "setupRestEndpoint",
        title       : "Application REST API",
        nextPage    : "actionRestEndpoint",
        install     : false,
        uninstall   : state.installed
    ]

    return dynamicPage(pageProperties) {
        section {
            paragraph textAbout
        }
        section("REST API Endpoint URL") {
            paragraph getAppUri()
        }
        section("REST API Access Token") {
            paragraph accessToken
        }
        section("Send it by SMS") {
            paragraph textSendSms
            input inputPhone
        }
    }
}

private def actionRestEndpoint() {
    TRACE("actionRestEndpoint()")

    if (settings.setupPhoneNumber) {
        def msg = "X10 Things API\n{\"appId\":\"${app.id}\",\"token\":\"${getAccessToken()}\"}"
        TRACE("Sending SMS to ${settings.setupPhoneNumber}: ${msg}")
        sendSms(settings.setupPhoneNumber, msg)
    }

    return pageSetup()
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

// Handle '.../devices' REST endpoint
def apiListDevices() {
    TRACE("apiListDevices()")

    return getDeviceMap()
}

// Handle '.../command/:address/:command' REST endpoint
def apiCommand() {
    TRACE("apiCommand()")

    def addr = params.address?.toUpperCase()
    def cmd = params.command?.toLowerCase()
    TRACE("addr:${addr}, cmd:${cmd}")

    if (!addr || !cmd) {
        log.error "Invalid request: ${params}"
        httpError(500, "Invalid request")
        return
    }

    if (state.devices.containsKey(addr)) {
        def dni = state.devices[addr].dni
        def device = getChildDevice(dni)
        if (!device) {
            log.error "Child device \'${dni}\' not found!"
            state.devices.remove(addr)
            httpError(500, "Device \'${dni}\' not found!")
            return
        }

        def attr = state.devices[addr].type
        def value = null
        switch (attr) {
        case "motion":
            def motionValues = ['off':0, 'on':1]
            value = motionValues[cmd]
            break

        case "light":
            def lightValues = ['off':1, 'on':0]
            value = lightValues[cmd]
            break
        }

        if (value == null) {
            log.debug "Command \'${cmd}\' ignored for device \'${dni}\'."
            return
        }

        device.parse("${attr}:${value}")
        return [device:device.displayName, attribute:attr, value:value]
    }

    def x10addr = parseX10Address(addr)
    if (x10addr.houseCode != settings.remoteHouseCode || !x10addr.unitCode) {
        // house code does not match - ignore
        return [error:false]
    }

    def switches = settings."switches_${x10addr.unitCode}"
    def action = null
    switch (cmd) {
    case 'on':
        action = settings."actionOn_${x10addr.unitCode}"
        switches*.on()
        break

    case 'off':
        action = settings."actionOff_${x10addr.unitCode}"
        switches*.off()
        break
    
    case 'dim':
        break

    case 'bright':
        break
    }

    if (action) {
        log.trace "Executing HelloHome action \'${action}\'"
        location.helloHome.execute(action)
    }

    return [error:false]
}

private def parseX10Address(addr) {
    def hc = addr[0]
    def uc = 0

    if (addr.length() > 1) {
        // Parse unit code portion of the address
        def s = addr[1..-1]
        if (s.isInteger()) {
            uc = s.toInteger()
            if (uc < 1 || uc > 16) {
                log.error "Invalid X10 address '${addr}'"
                uc = 0
            }
        }
    }

    return [houseCode: hc, unitCode: uc]
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

private def addActiveEye(houseCode, unitCode, enableLightSensor) {
    TRACE("addActiveEye(${houseCode}, ${unitCode}, ${enableLightSensor})")

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
        dev.parse("motion:0, light:0")
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

    if (enableLightSensor) {
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

private def textLicense() {
    def text =
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

    return text
}

private def TRACE(message) {
    log.debug message
    log.debug "state: ${state}"
}

private def STATE() {
    log.debug "settings: ${settings}"
    log.debug "state: ${state}"
}
