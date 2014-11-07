/**
 *  Virtual Remote Control
 *
 *  --------------------------------------------------------------------------
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
 *  --------------------------------------------------------------------------
 *
 *  The latest version of this file can be found at:
 *  https://github.com/statusbits/smartthings/blob/master/VirtualThings/VirtualRemoteControl.device.groovy
 *
 *  Version: 1.1.0  (2014-11-07)
 */

metadata {
    definition (name:"Virtual Remote Control", namespace:"statusbits", author:"geko@statusbits.com") {
        capability "Button"
        capability "Refresh"

        // Custom commands
        command "parse"     // (String "button:<number>, action:<pushed | held>")
        command "button1"
        command "button2"
        command "button3"
        command "button4"
        command "button5"
        command "button6"
        command "button7"
        command "button8"

        // Custom attributes
        attribute "deviceName", "string"
        attribute "label1", "string"
        attribute "label2", "string"
        attribute "label3", "string"
        attribute "label4", "string"
        attribute "label5", "string"
        attribute "label6", "string"
        attribute "label7", "string"
        attribute "label8", "string"
    }

    preferences {
        input("label1", "string", title:"Button 1 Label", defaultValue:"Button 1", required:false, displayDuringSetup:false)
        input("label2", "string", title:"Button 2 Label", defaultValue:"Button 2", required:false, displayDuringSetup:false)
        input("label3", "string", title:"Button 3 Label", defaultValue:"Button 3", required:false, displayDuringSetup:false)
        input("label4", "string", title:"Button 4 Label", defaultValue:"Button 4", required:false, displayDuringSetup:false)
        input("label5", "string", title:"Button 5 Label", defaultValue:"Button 5", required:false, displayDuringSetup:false)
        input("label6", "string", title:"Button 6 Label", defaultValue:"Button 6", required:false, displayDuringSetup:false)
        input("label7", "string", title:"Button 7 Label", defaultValue:"Button 7", required:false, displayDuringSetup:false)
        input("label8", "string", title:"Button 8 Label", defaultValue:"Button 8", required:false, displayDuringSetup:false)
    }

    tiles {
        //standardTile("remote", "device.deviceName", canChangeIcon:true, canChangeBackground:true) {
        standardTile("remote", "device.deviceName") {
            state "default", label:'${currentValue}', icon:"st.unknown.zwave.remote-controller"
        }

        standardTile("button1", "device.label1", inactiveLabel:false, decoration:"flat") {
            state "default", label:'${currentValue}', icon:"st.unknown.thing.thing-circle", action:"button1"
        }

        standardTile("button2", "device.label2", inactiveLabel:false, decoration:"flat") {
            state "default", label:'${currentValue}', icon:"st.unknown.thing.thing-circle", action:"button2"
        }

        standardTile("button3", "device.label3", inactiveLabel:false, decoration:"flat") {
            state "default", label:'${currentValue}', icon:"st.unknown.thing.thing-circle", action:"button3"
        }

        standardTile("button4", "device.label4", inactiveLabel:false, decoration:"flat") {
            state "default", label:'${currentValue}', icon:"st.unknown.thing.thing-circle", action:"button4"
        }

        standardTile("button5", "device.label5", inactiveLabel:false, decoration:"flat") {
            state "default", label:'${currentValue}', icon:"st.unknown.thing.thing-circle", action:"button5"
        }

        standardTile("button6", "device.label6", inactiveLabel:false, decoration:"flat") {
            state "default", label:'${currentValue}', icon:"st.unknown.thing.thing-circle", action:"button6"
        }

        standardTile("button7", "device.label7", inactiveLabel:false, decoration:"flat") {
            state "default", label:'${currentValue}', icon:"st.unknown.thing.thing-circle", action:"button7"
        }

        standardTile("button8", "device.label8", inactiveLabel:false, decoration:"flat") {
            state "default", label:'${currentValue}', icon:"st.unknown.thing.thing-circle", action:"button8"
        }

        standardTile("refresh", "device.button", inactiveLabel:false, decoration:"flat") {
            state "default", label:'', icon:"st.secondary.refresh", action:"refresh.refresh"
        }

        main(["remote", "button1", "button2", "button3", "button4", "button5", "button6",
                "button7", "button8"])

        details(["remote", "button1", "button2", "button3", "button4", "button5", "button6",
                "button7", "button8", "refresh"])
    }

    simulator {
        for (int n = 1; n <= 8; n++) {
            status "Button ${n}": "button:${n}, action:pushed"
        }
    }
}

// Parse events into attributes
def parse(String message) {
    TRACE("parse(${message})")

    def m = stringToMap(message)
    if (m?.button == null || m?.action == null) {
        log.error "Invalid message: ${message}"
        return null
    }

    return buttonEvent(m.button, m.action)
}

def refresh() {
    TRACE("refresh()")

    sendEvent(name: "deviceName", value: device.displayName)
    sendEvent(name: "label1", value: settings.label1)
    sendEvent(name: "label2", value: settings.label2)
    sendEvent(name: "label3", value: settings.label3)
    sendEvent(name: "label4", value: settings.label4)
    sendEvent(name: "label5", value: settings.label5)
    sendEvent(name: "label6", value: settings.label6)
    sendEvent(name: "label7", value: settings.label7)
    sendEvent(name: "label8", value: settings.label8)

    STATE()
}

def button1()   { buttonPush(1) }
def button2()   { buttonPush(2) }
def button3()   { buttonPush(3) }
def button4()   { buttonPush(4) }
def button5()   { buttonPush(5) }
def button6()   { buttonPush(6) }
def button7()   { buttonPush(7) }
def button8()   { buttonPush(8) }

private def buttonPush(btn) {
    TRACE("buttonPush(${btn})")
    sendEvent(buttonEvent(btn, "pushed"))
}

private def buttonEvent(button, action) {
    TRACE("buttonEvent(${button}, ${action})")

    button = button as Integer

    def event = [
        name:               "button",
        value:              action,
        data:               [buttonNumber:button],
        descriptionText:    "${device.displayName} button ${button} was ${action}",
        isStateChange:      true
    ]

    createEvent(event)
}

private def TRACE(message) {
    log.debug message
}

private def STATE() {
    log.debug "settings: ${settings}"
    log.debug "Display Name: ${device.displayName}"
    log.debug "Device Name: ${device.currentValue("deviceName")}"
    log.debug "Label 1: ${device.currentValue("label1")}"
    log.debug "Label 2: ${device.currentValue("label2")}"
    log.debug "Label 3: ${device.currentValue("label3")}"
    log.debug "Label 4: ${device.currentValue("label4")}"
    log.debug "Label 5: ${device.currentValue("label5")}"
    log.debug "Label 6: ${device.currentValue("label6")}"
    log.debug "Label 7: ${device.currentValue("label7")}"
    log.debug "Label 8: ${device.currentValue("label8")}"
}
