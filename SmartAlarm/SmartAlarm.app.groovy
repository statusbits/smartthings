/**
 *  Smart Alarm.
 *
 *  Copyright (c) 2014 Statusbits.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use
 *  this file except in compliance with the License. You may obtain a copy of the
 *  License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed
 *  under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 *  CONDITIONS OF ANY KIND, either express or implied. See the License  for the
 *  specific language governing permissions and limitations under the License.
 *
 *  Version: 1.0.0
 *  Date: 2014-07-04
 */

definition(
    name: "Smart Alarm",
    namespace: "statusbits",
    author: "geko@statusbits.com",
    description: "Multi-Zone Virtual Alarm Panel",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience%402x.png"
)

preferences {
    page name:"setupInit"
    page name:"setupWelcome"
    page name:"setupConfigure"
    page name:"setupZones"
    page name:"setupControlPanel"
    page name:"setupPanelStatus"
    page name:"setupZoneBypass"
}

def setupInit() {
    TRACE("setupInit()")

    if (state.installed) {
        return setupControlPanel()
    } else {
        return setupWelcome()
    }
}

// Show setup welcome page
def setupWelcome() {
    TRACE("setupWelcome()")

    def textIntro =
        "Smart Alarm is a multi-zone virtual alarm panel. You can configure up to 16 " +
        "security zones and assign any number of sensors to each zone. " +
        "The alarm is armed and disarmed simply by setting home mode.\n\n" +
        "For more information, please visit [www.statusbits.com/p/smartalarm.html]."

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

    def textNext =
        "Please read the Licence Agreement below, then tap the 'Next' button at the " +
        "top of the page to continue."

    def pageProperties = [
        name:       "setupWelcome",
        title:      "Welcome!",
        nextPage:   "setupConfigure",
        uninstall:  false
    ]

    return dynamicPage(pageProperties) {
        section {
            paragraph "Smart Alarm ${textVersion()}\n${textCopyright()}"
        }
        section("Introduction") {
            paragraph textIntro
            paragraph textNext
        }
        section("License") {
            paragraph textLicense
        }
    }
}

// Show panel configuration page
def setupConfigure() {
    TRACE("setupConfigure()")

    def helpPage =
        "Tap the 'Next' button when done."

    def helpNumZones =
        "A security zone is an area of your home protected by one or more sensors, " +
        "for example a bedroom, a garage, or an entire floor in a multistory " +
        "building. You can configure up to 16 zones and assign any number of " +
        "sensors (contact, motion, smoke or moisture) to each zone."

    def helpArming =
        "Smart Alarm can be armed in one of two modes: Stay and Away. Interior " +
        "zones are not armed in Stay mode, alowing you to freely move inside your " +
        "home.\n\n" +
        "Smart Alarm is armed and disarmed simply by setting home mode. Just " +
        "specify in which mode(s) the alarm should be armed and as soon as one of " +
        "those modes becomes active, the alarm panel will arm itself. Switching " +
        "to any other mode will automatically disarm it."

    def helpExitDelay =
        "Exit delay allows you to exit premises within 45 seconds after arming the " +
        "alarm panel without setting of an alarm."

    def helpEntryDelay =
        "Entry delay allows you to enter premises when Smart Alarm is armed and " +
        "disarm it within 45 seconds without setting of an alarm."

    def helpAlarm =
        "Wnen an alarm is set off, Smart Alarm can turn on some sirens and/or lights."

    def helpSilent =
        "Enable Silent mode if you wish to temporarily disable sirens and switches. " +
        "You will still recieve push notifications and/or text messages, if configured."

    def helpNotify =
        "Smart Alarm can notify you via push messages and/or text messages whenever " +
        "it is armed, disarmed or when an alarm is set off."

    def inputNumZones = [
        name:           "numZones",
        type:           "enum",
        title:          "How many zones?",
        metadata:       [values:["4","8","12","16"]],
        defaultValue:   "4",
        required:       true
    ]

    def inputAwayModes = [
        name:           "awayModes",
        type:           "mode",
        title:          "Arm Away in these Modes",
        multiple:       true
    ]

    def inputStayModes = [
        name:           "stayModes",
        type:           "mode",
        title:          "Arm Stay in these Modes",
        multiple:       true,
        required:       false
    ]

    def inputExitDelay = [
        name:           "exitDelay",
        type:           "bool",
        title:          "Enable exit delay",
        defaultValue:   true,
        required:       true
    ]

    def inputEntryDelay = [
        name:           "entryDelay",
        type:           "bool",
        title:          "Enable entry delay",
        defaultValue:   true,
        required:       true
    ]

    def inputAlarms = [
        name:           "alarms",
        type:           "capability.alarm",
        title:          "Activate these alarms",
        multiple:       true,
        required:       false
    ]

    def inputSwitches = [
        name:           "switches",
        type:           "capability.switch",
        title:          "Turn on these switches",
        multiple:       true,
        required:       false
    ]

    def inputSilent = [
        name:           "silent",
        type:           "bool",
        title:          "Enable silent mode",
        defaultValue:   false
    ]

    def inputPushMessage = [
        name:           "pushMessage",
        type:           "bool",
        title:          "Send push notifications",
        defaultValue:   true
    ]

    def inputPhone1 = [
        name:           "phone1",
        type:           "phone",
        title:          "Primary phone number",
        required:       false
    ]

    def inputPhone2 = [
        name:           "phone2",
        type:           "phone",
        title:          "Secondary phone number",
        required:       false
    ]

    def pageProperties = [
        name:       "setupConfigure",
        title:      "Configure Smart Alarm",
        nextPage:   "setupZones",
        uninstall:  state.installed
    ]

    return dynamicPage(pageProperties) {
        section {
            paragraph helpPage
        }
        section("Number of Zones") {
            paragraph helpNumZones
            input inputNumZones
        }
        section("Arming Options") {
            paragraph helpArming
            input inputAwayModes
            input inputStayModes
            paragraph helpExitDelay
            input inputExitDelay
            paragraph helpEntryDelay
            input inputEntryDelay
        }
        section("Alarm Options") {
            paragraph helpAlarm
            input inputAlarms
            input inputSwitches
            paragraph helpSilent
            input inputSilent
        }
        section("Notification Options") {
            paragraph helpNotify
            input inputPushMessage
            input inputPhone1
            input inputPhone2
        }
    }
}

// Show zone configuration page
def setupZones() {
    TRACE("setupZones()")

    def numZones = settings.numZones.toInteger()
    assert numZones > 0

    def helpPage =
        "Tap on each section below to configure security zones, then tap the " +
        "'Done' button to complete setup."

    def helpName =
        "You can give each zone a descriptive name, for example a 'Master Bedroom' " +
        "or a 'Garage'."

    def helpType =
        "A zone can be one of three types - Exterior, Interior or Alert. An " +
        "Exterior zone is armed when the alarm panel is armed in either Away or " +
        "Stay mode. An interior zone is armed only in Away mode. An Alert zone " +
        "is always armed and is typically used for fire and flood alarms."

    def helpTriggers =
        "A zone can be assigned one or more triggers, i.e. sensors that set off an " +
        "alarm when activated."

    def helpCameras =
        "A zone can be assigned one or more security cameras. These cameras will " +
        "take a snapshot whenever a zone is breached."

    def helpBypass =
        "If you wish to temporarily disable a zone, then turn on zone bypass. A zone " +
        "in bypass mode shall never set off an alarm."

    def pageProperties = [
        name:       "setupZones",
        title:      "Configure Zones",
        install:    true,
        uninstall:  state.installed
    ]

    return dynamicPage(pageProperties) {
        section {
            paragraph helpPage
        }
        for (int n = 1; n <= numZones; n++) {
            section("Zone ${n}", hideable:true, hidden:true) {
                paragraph helpName
                input "z${n}_name", "string", title:"Zone name", defaultValue:"Zone ${n}"
                paragraph helpType
                input "z${n}_type", "enum", title:"Select zone type", metadata:[values:["Exterior","Interior","Alert"]], defaultValue:"Exterior"
                paragraph helpTriggers
                input "z${n}_contact", "capability.contactSensor", title:"Which contact sensors?", multiple:true, required:false
                input "z${n}_motion", "capability.motionSensor", title:"Which motion sensors?", multiple:true, required:false
                input "z${n}_smoke", "capability.smokeDetector", title:"Which smoke sensors?", multiple:true, required:false
                input "z${n}_water", "capability.waterSensor", title:"Which moisture sensors?", multiple:true, required:false
                paragraph helpCameras
                input "z${n}_camera", "capability.imageCapture", title:"Which cameras?", multiple:true, required:false
                paragraph helpBypass
                input "z${n}_bypass", "bool", title:"Enable zone bypass", defaultValue:false
            }
        }
    }
}

// Show control panel page
def setupControlPanel() {
    TRACE("setupControlPanel()")

    def pageProperties = [
        name:       "setupControlPanel",
        title:      "Control Panel",
        install:    true,
        uninstall:  state.installed
    ]

    return dynamicPage(pageProperties) {
        section {
            buttons name:"buttonReset", required:false,
                buttons:[
                    [label:"Reset", action:"panelReset"]
                ]
            buttons name:"buttonPanic", required:false,
                buttons:[
                    [label:"Panic", action:"panelPanic", backgroundColor:"red"]
                ]
        }
        section {
            href "setupPanelStatus", title:"Alarm Panel Status", description:"Tap to open"
            href "setupZoneBypass", title:"Quick Zone Bypass", description:"Tap to open"
            href "setupConfigure", title:"Configure Smart Alarm", description:"Tap to open"
        }
    }
}

// Show panel status page
def setupPanelStatus() {
    TRACE("setupPanelStatus()")

    def pageProperties = [
        name:       "setupPanelStatus",
        title:      "Alarm Panel Status",
        install:    false,
        uninstall:  false
    ]

    def statusArmed
    if (state.armed) {
        statusArmed = "Armed "
        statusArmed += state.stay ? "Stay" : "Away"
    } else {
        statusArmed = "Disarmed"
    }
    def statusExitDelay = settings.exitDelay ? "On" : "Off"
    def statusEntryDelay = settings.entryDelay ? "On" : "Off"
    def statusSilent = settings.silent ? "On" : "Off"
    def statusPushMsg = settings.pushMessage ? "On" : "Off"

    return dynamicPage(pageProperties) {
        section {
            paragraph "Alarm is now ${statusArmed}"
            paragraph "Exit delay: ${statusExitDelay}"
            paragraph "Entry delay: ${statusEntryDelay}"
            paragraph "Silent mode: ${statusSilent}"
            paragraph "Push messages: ${statusPushMsg}"
        }
        section("Zone Status") {
            for (zone in state.zones) {
                def zoneStatus = "${zone.name}: "
                if (zone.alert) {
                    zoneStatus += "alert"
                } else if (zone.interior) {
                    zoneStatus += "interior"
                } else {
                    zoneStatus += "exterior"
                }

                if (zone.bypass) {
                    zoneStatus += ", bypassed"
                } else if (zone.armed) {
                    zoneStatus += ", armed"
                } else {
                    zoneStatus += ", disarmed"
                }

                paragraph zoneStatus
            }
        }
    }
}

// Show zone bypass page
def setupZoneBypass() {
    TRACE("setupZoneBypass()")

    def pageProperties = [
        name:       "setupZoneBypass",
        title:      "Quick Zone Bypass",
        install:    true,
        uninstall:  false
    ]

    return dynamicPage(pageProperties) {
        section {
            for (int n = 0; n < state.numZones; n++) {
                def zone = state.zones[n]
                input "z${n + 1}_bypass", "bool", title:"${zone.name}", defaultValue:false
            }
        }
    }
}

def installed()
{
    state.installed = true
    initialize()
}

def updated()
{
    unschedule()
    unsubscribe()
    initialize()
}

private initialize()
{
    TRACE("initialize()")
    log.debug "settings: ${settings}"
    log.trace "${app.name}. ${textVersion()}. ${textCopyright()}"

    state.numZones = settings.numZones.toInteger()
    state.exitDelay = settings.exitDelay ? 45 : 0
    state.entryDelay = settings.entryDelay ? 45 : 0
    state.zones = []
    for (int n = 0; n < state.numZones; n++) {
        state.zones[n] = zoneInit(n)
        if (state.zones[n].alert) {
            zoneArm(n)
        }
    }

    if (settings.awayModes?.contains(location.mode)) {
        state.armed = true
        state.stay = false
    } else if (settings.stayModes?.contains(location.mode)) {
        state.armed = true
        state.stay = true
    } else {
        state.armed = false
        state.stay = false
    }

    panelReset()
    subscribe(location, onLocation)
}

def panelReset()
{
    TRACE("panelReset()")

    unschedule()
    alarms*.off()
    switches*.off()
    state.alarm = false
    for (int n = 0; n < state.numZones; n++) {
        zoneReset(n)
    }

    panelStatus()
}

private def panelDisarm()
{
    TRACE("panelDisarm()")

    unschedule()
    alarms*.off()
    switches*.off()
    state.armed = false
    state.alarm = false
    for (int n = 0; n < state.numZones; n++) {
        zoneReset(n)
    }

    panelStatus()
}

private def panelStatus()
{
    TRACE("panelStatus()")

    def msg = "${location.name} alarm "
    if (state.armed) {
        def mode = state.stay ? "Stay" : "Away"
        msg += "armed '${mode}'."
    } else {
        msg += "disarmed."
    }

    for (zone in state.zones) {
        msg += "\n${zone.name}: "
        if (zone.bypass) {
            msg += "bypass"
        } else if (zone.armed) {
            msg += "armed"
        } else {
            msg += "disarmed"
        }

    }

    notify(msg)
}

private def panelPanic()
{
    TRACE("panelPanic()")

    state.alarm = true;
    activateAlarm()
}

private def zoneInit(n)
{
    def z = n + 1
    def handlers = [
        onZone1, onZone2, onZone3, onZone4,
        onZone5, onZone6, onZone7, onZone8,
        onZone9, onZone10, onZone11, onZone12,
        onZone13, onZone14, onZone15, onZone16,
    ]

    def zone = [:]
    zone.name       = settings."z${z}_name"
    zone.alert      = settings."z${z}_type" == "Alert" ? true : false
    zone.interior   = settings."z${z}_type" == "Interior" ? true : false
    zone.bypass     = settings."z${z}_bypass"
    zone.evHandler  = handlers[n]
    zone.armed      = false
    zone.alarm      = null

    return zone
}

private def zoneReset(n)
{
    TRACE("zoneReset(${n})")

    def zone = state.zones[n]
    if (!zone.bypass && (zone.alert || (state.armed && !(state.stay && zone.interior)))) {
        if (!zone.armed) {
            zoneArm(n)
        }
    } else {
        if (zone.armed) {
            zoneDisarm(n)
        }
    }
}

private def zoneArm(n)
{
    def zone = state.zones[n]
    def devices = getZoneDevices(n)

    if (devices.contact) {
        subscribe(devices.contact, "contact.open", zone.evHandler)
    }

    if (devices.motion) {
        subscribe(devices.motion, "motion.active", zone.evHandler)
    }

    if (devices.smoke) {
        subscribe(devices.smoke, "smoke.detected", zone.evHandler)
        subscribe(devices.smoke, "smoke.tested", zone.evHandler)
        subscribe(devices.smoke, "carbonMonoxide.detected", zone.evHandler)
        subscribe(devices.smoke, "carbonMonoxide.tested", zone.evHandler)
    }

    if (devices.water) {
        subscribe(devices.water, "water.wet", zone.evHandler)
    }

    if (devices.camera) {
        subscribe(devices.camera, "image", onImageCapture)
    }

    state.zones[n].armed = true
    state.zones[n].alarm = null

    log.debug "Zone '${zone.name}' armed"
}

private def zoneDisarm(n)
{
    def zone = state.zones[n]
    def devices = getZoneDevices(n)

    if (devices.motion) unsubscribe(devices.motion)
    if (devices.contact) unsubscribe(devices.contact)
    if (devices.water) unsubscribe(devices.water)
    if (devices.smoke) unsubscribe(devices.smoke)
    if (devices.camera) unsubscribe(devices.camera)

    state.zones[n].armed = false
    state.zones[n].alarm = null

    log.debug "Zone '${zone.name}' disarmed"
}

private def getZoneDevices(n)
{
    if (n >= state.numZones)
        return null

    n++

    def devices = [:]
    devices.contact = settings."z${n}_contact"
    devices.motion  = settings."z${n}_motion"
    devices.smoke   = settings."z${n}_smoke"
    devices.water   = settings."z${n}_water"
    devices.camera  = settings."z${n}_camera"

    return devices
}

private def onAlarm(n, evt)
{
    TRACE("onAlarm(${n}, ${evt.displayName})")

    if (n >= state.numZones) {
        return
    }

    def zone = state.zones[n]
    if (!zone.armed) {
        TRACE("onAlarm: Hmm... False alarm?")
        return
    }

    // Set zone to alarm state
    state.zones[n].alarm = evt.displayName

    // Take security camera snapshot
    def devices = getZoneDevices(n)
    devices.camera*.take()

    // Activate alarm
    if (!state.alarm) {
        state.alarm = true
        if (zone.alert || !state.entryDelay) {
            activateAlarm()
        } else {
            runIn(state.entryDelay, activateAlarm)
        }
    }
}

// these must be public!
def onZone1(evt)  { onAlarm(0,  evt) }
def onZone2(evt)  { onAlarm(1,  evt) }
def onZone3(evt)  { onAlarm(2,  evt) }
def onZone4(evt)  { onAlarm(3,  evt) }
def onZone5(evt)  { onAlarm(4,  evt) }
def onZone6(evt)  { onAlarm(5,  evt) }
def onZone7(evt)  { onAlarm(6,  evt) }
def onZone8(evt)  { onAlarm(7,  evt) }
def onZone9(evt)  { onAlarm(8,  evt) }
def onZone10(evt) { onAlarm(9,  evt) }
def onZone11(evt) { onAlarm(10, evt) }
def onZone12(evt) { onAlarm(11, evt) }
def onZone13(evt) { onAlarm(12, evt) }
def onZone14(evt) { onAlarm(13, evt) }
def onZone15(evt) { onAlarm(14, evt) }
def onZone16(evt) { onAlarm(15, evt) }

def onLocation(evt)
{
    TRACE("onLocation(${evt})")

    def mode = evt.value
    def newArmed
    def newStay
    if (settings.awayModes?.contains(mode)) {
        newArmed = true
        newStay = false
    } else if (settings.stayModes?.contains(mode)) {
        newArmed = true
        newStay = true
    } else {
        newArmed = false
        newStay = false
    }

    if (state.armed == newArmed && state.stay == newStay) {
        return
    }

    if (state.armed) {
        panelDisarm()
    }

    state.armed = newArmed
    state.stay = newStay
    if (newArmed) {
        if (state.exitDelay) {
            runIn(state.exitDelay, panelReset)
        } else {
            panelReset()
        }
    }
}

def onImageCapture(evt)
{
    TRACE("onImageCapture(${evt})")
}

def activateAlarm()
{
    if (!state.alarm) {
        TRACE("activateAlarm: Hmm... False alarm?")
        return
    }

    // Activate alarms and switches
    if (!settings.silent) {
        alarms*.both()
        switches*.on()
    }

    // Send notifications
    def msg = "Alarm at location '${location.name}'!"
    for (zone in state.zones) {
        if (zone.alarm) {
            msg += "\n${zone.name}: ${zone.alarm}"
        }
    }
    notify(msg)

    // Reset panel in 3 minutes
    runIn(180, panelReset)
}

private def notify(msg)
{
    log.trace "[notify] ${msg}"

    if (settings.pushMessage) {
        sendPush(msg)
    }

    if (settings.phone1) {
        sendSms(phone1, msg)
    }

    if (settings.phone2) {
        sendSms(phone2, msg)
    }
}

private def textVersion() {
    def text = "Version 1.0.0"
}

private def textCopyright() {
    def text = "Copyright (c) 2014 Statusbits.com"
}

private def TRACE(message) {
    //log.debug message
    //log.debug "state: ${state}"
}
