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
    page name:"setupMochad"
    page name:"setupAddSwitch"
    page name:"setupActionAdd"
    page name:"setupListDevices"
    page name:"setupTestConnection"
    page name:"setupActionTest"
}

private def setupInit() {
    TRACE("setupInit()")

    if (state.setup) {
        // already initialized, go to setup menu
        return setupMenu()
    }

    // initialize app state and show welcome page
    state.setup = [:]
    state.setup.installed = false
    state.devices = [:]
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
        name        : "setupInit",
        title       : "Welcome!",
        nextPage    : "setupMenu",
        install     : false,
        uninstall   : state.setup.installed
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

    // if Mochad is not configured, then do it now
    if (!settings.containsKey('mochadIpAddress')) {
        return setupMochad()
    }

    def pageProperties = [
        name        : "setupMenu",
        title       : "Setup Menu",
        nextPage    : null,
        install     : true,
        uninstall   : state.setup.installed
    ]

    state.setup.deviceType = null

    return dynamicPage(pageProperties) {
        section {
            href "setupMochad", title:"Configure Mochad Gateway", description:"Tap to open"
            href "setupAddSwitch", title:"Add X10 Switch", description:"Tap to open"
            if (state.devices.size() > 0) {
                href "setupListDevices", title:"List Installed Devices", description:"Tap to open"
            }
        }
        section("Utilities") {
            href "setupTestConnection", title:"Test Mochad Connection", description:"Tap to open"
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

// Show "Configure Mochad" setup page
private def setupMochad() {
    TRACE("setupMochad()")

    def textPara1 =
        "X10 Bridge communicates with X10 devices via Mochad TCP gateway " +
        "running on a Linux box. The Linux box must be connected to your " +
        "local network and assigned a static (or reserved) IP address, so " +
        "it does not change when the Linux box is rebooted.\n\n" +
        "Enter IP address and TCP port of your Mochad gateway, then tap " +
        "Done to continue."

    def textPara2 =
        "Mochad works with two types of X10 controllers - CM15A and CM19A. " +
        "CM15A can transmit X10 commands using both power line (PL) and " +
        "radio frequency (RF) protocols, while CM19A can only transmit X10 " +
        "commands using RF protocol and requires an RF-to-PL adapter, for " +
        "example TM751 or RR501." 

    def inputIpAddress = [
        name        : "mochadIpAddress",
        type        : "string",
        title       : "What is your gateway IP Address?"
    ]

    def inputTcpPort = [
        name        : "mochadTcpPort",
        type        : "number",
        title       : "What is your gateway TCP Port?",
        defaultValue: "1099"
    ]

    def inputProtocol = [
        name        : "mochadProtocol",
        type        : "enum",
        title       : "What X10 protocol do you use?",
        metadata    : [values:["PL", "RF"]],
        defaultValue: "PL"
    ]

    def pageProperties = [
        name        : "setupMochad",
        title       : "Configure Mochad Gateway",
        nextPage    : "setupMenu",
        install     : false,
        uninstall   : state.installed
    ]

    return dynamicPage(pageProperties) {
        section {
            paragraph textPara1
            input inputIpAddress
            input inputTcpPort
        }
        section("Choose X10 Protocol") {
            paragraph textPara2
            input inputProtocol
        }
    }
}

// Show "Mochad Connection Test" setup page
private def setupTestConnection() {
    TRACE("setupTestConnection()")

    def textHelp =
        "You can execute any Mochad command to verify that your hub can " +
        "communicate with the gateway. Tap Next to continue."

    def inputCommand = [
        name        : "mochadCommand",
        type        : "text",
        title       : "Enter Mochad command",
        autoCorrect : false
    ]

    def pageProperties = [
        name        : "setupTestConnection",
        title       : "Test Mochad Connection",
        nextPage    : "setupActionTest",
        install     : false,
        uninstall   : state.installed
    ]

    return dynamicPage(pageProperties) {
        section {
            paragraph textHelp
            input inputCommand
        }
    }
}

// Execute Mochad connection test
private def setupActionTest() {
    TRACE("setupActionTest()")

    def pageProperties = [
        name        : "setupActionTest",
        title       : "Mochad Connection Test",
        nextPage    : "setupMenu",
        install     : false,
        uninstall   : state.installed
    ]

    if (settings.mochadCommand) {
        def networkId = makeNetworkId(settings.mochadIpAddress, settings.mochadTcpPort)
        socketSend("${settings.mochadCommand}\r\n", networkId)
    }

    return dynamicPage(pageProperties) {
        section {
            paragraph "Executing Mochad command:\n  ${settings.mochadCommand}"
            paragraph "Tap Done to continue."
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

    def inputDeviceName = [
        name        : "setupDevName",
        type        : "string",
        title       : "What is your switch name?",
        required    : true,
        defaultValue:"X10 Switch"
    ]

    def inputHouseCode = [
        name        : "setupHouseCode",
        type        : "enum",
        title       : "What is your switch House Code?",
        metadata    : [values:x10HouseCodes()],
        required    : true
    ]

    def inputUnitCode = [
        name        : "setupUnitCode",
        type        : "enum",
        title       : "What is your switch Unit Code?",
        metadata    : [values:x10UnitCodes()],
        required    : true
    ]

    def pageProperties = [
        name        : "setupAddSwitch",
        title       : "Add X10 Switch",
        nextPage    : "setupActionAdd",
        install     : false,
        uninstall   : state.setup.installed
    ]

    // Set new device type
    state.setup.deviceType = "switch"

    return dynamicPage(pageProperties) {
        section {
            paragraph textHelpName
            input inputDeviceName
        }
        section("X10 Address") {
            paragraph textHelpAddr
            input inputHouseCode
            input inputUnitCode
        }
    }
}

private def setupActionAdd() {
    TRACE("setupActionAdd()")

    String devAddr = settings.setupHouseCode + settings.setupUnitCode
    if (state.devices.containsKey(devAddr)) {
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

private def setupListDevices() {
    TRACE("setupListDevices()")

    def textNoDevices =
        "You have not configured any X10 devices yet. Tap Done to continue."

    def pageProperties = [
        name        : "setupListDevices",
        title       : "Installed Devices",
        nextPage    : "setupMenu",
        install     : false,
        uninstall   : state.setup.installed
    ]

    if (state.devices.size() == 0) {
        return dynamicPage(pageProperties) {
            section {
                paragraph textNoDevices
            }
        }
    }

    def switches = getDeviceListAsText('switch')
    return dynamicPage(pageProperties) {
        section {
            paragraph "Tap Done to continue."
        }
        section("Switches") {
            paragraph switches
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

// Handle Location events
def onLocation(evt) {
    TRACE("onLocation(${evt})")

    if (evt.description == 'ping') {
        // ignore ping event
        return
    }

    if (evt.eventSource == 'HUB') {
        // Parse Hub event
        def hubEvent = stringToMap(evt.description)
        log.debug "hubEvent: ${hubEvent}"

        // Add Hub ID to the parsed event
        //hubEvent.hubId = evt.hubId
        //parseLanResponse(hubEvent)
    }
}

// Handle SmartApp touch event.
def onAppTouch(evt) {
    TRACE("onAppTouch(${evt})")
    STATE()

	// test
	x10_on(null)
}

// Excecute X10 'on' command on behalf of child device
def x10_on(nid) {
    TRACE("x10_on(${nid})")

    def s = nid?.tokenize(':')
    if (s.size < 2 || s[0].toUpperCase() != 'X10') {
        log.debug "Invalid device network ID ${nid}"
        return
    }

    socketSend("${settings.mochadProtocol} ${s[1]} on\r\n", state.networkId)
}

// Excecute X10 'off' command on behalf of child device
def x10_off(nid) {
	TRACE("x10_off(${nid})")

    def s = nid?.tokenize(':')
    if (s.size < 2 || s[0].toUpperCase() != 'X10') {
        log.debug "Invalid device network ID ${nid}"
        return
    }

    socketSend("${settings.mochadProtocol} ${s[1]} off\r\n", state.networkId)
}

// Excecute X10 'dim' command on behalf of child device
def x10_dim(nid) {
	TRACE("x10_dim(${nid})")

    def s = nid?.tokenize(':')
    if (s.size < 2 || s[0].toUpperCase() != 'X10') {
        log.debug "Invalid device network ID ${nid}"
        return
    }

    socketSend("${settings.mochadProtocol} ${s[1]} dim\r\n", state.networkId)
}

// Excecute X10 'bright' command on behalf of child device
def x10_bright(nid) {
	TRACE("x10_bright(${nid})")

    def s = nid?.tokenize(':')
    if (s.size < 2 || s[0].toUpperCase() != 'X10') {
        log.debug "Invalid device network ID ${nid}"
        return
    }

    socketSend("${settings.mochadProtocol} ${s[1]} bright\r\n", state.networkId)
}

private def initialize() {
    log.trace "${app.name}. ${textVersion()}. ${textCopyright()}"
    STATE()

    state.setup.installed = true
    state.networkId = makeNetworkId(settings.mochadIpAddress, settings.mochadTcpPort)
    updateDeviceList()

    // subscribe to attributes, devices, locations, etc.
    subscribe(app, onAppTouch)

    // Subscribe to location events with filter disabled
    subscribe(location, null, onLocation, [filterEvents:false])
}

private def addSwitch(addr) {
    TRACE("addSwitch(${addr})")

    def dni = "X10:${addr}".toUpperCase()
    if (getChildDevice(dni)) {
        log.error "Child device ${dni} already exist"
        return false
    }

    def devFile = "X10 Switch"
    def devParams = [
        name            : settings.setupDevName,
        label           : settings.setupDevName,
        completedSetup  : true
    ]

    log.trace "Creating child device ${devParams}"
    try {
        def dev = addChildDevice("statusbits", devFile, dni, null, devParams)
    } catch (e) {
        log.error "Cannot create child device. Error: ${e}"
        return false
    }

    // save device in the app state
    state.devices[addr] = [
        'dni'   : dni,
        'type'  : 'switch',
    ]

    STATE()
    return true
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

    // refresh all devices
    def devices = getChildDevices()
    devices?.each {
        it.refresh()
    }
}

private def getDeviceMap() {
    def devices = [:]
    state.devices.each { k,v ->
        if (!devices.containsKey(v.type)) {
            devices[v.type] = []
        }
        devices[v.type] << k
    }

    return devices
}

private def getDeviceListAsText(type) {
    String s = ""
    state.devices.each { k,v ->
        if (v.type == type) {
            def dev = getChildDevice(v.dni)
            if (dev) {
                s += "${k} - ${dev.displayName}\n"
            }
        }
    }

    return s
}

private def mochadComand() {

}

private def socketSend(message, networkId) {
    TRACE("socketSend(${message}, ${networkId})")

    def hubAction = new physicalgraph.device.HubAction(message,
            physicalgraph.device.Protocol.LAN, networkId)

    TRACE("hubAction:\n${hubAction.getProperties()}")
    sendHubCommand(hubAction)
}

// Returns device Network ID in 'AAAAAAAA:PPPP' format
private String makeNetworkId(ipaddr, port) {
    TRACE("createNetworkId(${ipaddr}, ${port})")

    String hexIp = ipaddr.tokenize('.').collect {
        String.format('%02X', it.toInteger())
    }.join()

    String hexPort = String.format('%04X', port)
    return "${hexIp}:${hexPort}"
}

private def x10HouseCodes() {
    return ["A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P"]
}

private def x10UnitCodes() {
    return ["1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16"]
}

private def textVersion() {
    def text = "Version 0.9.0"
}

private def textCopyright() {
    def text = "Copyright (c) 2014 Statusbits.com"
}

private def TRACE(message) {
    log.debug message
}

private def STATE() {
    log.debug "state: ${state}"
    log.debug "settings: ${settings}"
}
