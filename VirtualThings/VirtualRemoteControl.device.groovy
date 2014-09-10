/**
 *  Virtual Remote Control
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
 *  https://github.com/statusbits/smartthings/blob/master/VirtualThings/VirtualRemoteControl.device.groovy
 *
 *  Version: 1.0.0
 *  Date: 2014-08-07
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
        //attribute "label1", "string"
    }

    preferences {
        //input("label_1", "string", title:"Button 1 Label", required:false, displayDuringSetup:false)
    }

    tiles {
        standardTile("remote", "device.button", canChangeIcon:true, canChangeBackground:true) {
            state "default", label:"", icon:"st.unknown.zwave.remote-controller"
        }

        standardTile("button1", "device.button", inactiveLabel:false, decoration:"flat") {
            state "default", label:"Button 1", icon:"st.unknown.thing.thing-circle", action:"button1"
        }

        standardTile("button2", "device.button", inactiveLabel:false, decoration:"flat") {
            state "default", label:"Button 2", icon:"st.unknown.thing.thing-circle", action:"button2"
        }

        standardTile("button3", "device.button", inactiveLabel:false, decoration:"flat") {
            state "default", label:"Button 3", icon:"st.unknown.thing.thing-circle", action:"button3"
        }

        standardTile("button4", "device.button", inactiveLabel:false, decoration:"flat") {
            state "default", label:"Button 4", icon:"st.unknown.thing.thing-circle", action:"button4"
        }

        standardTile("button5", "device.button", inactiveLabel:false, decoration:"flat") {
            state "default", label:"Button 5", icon:"st.unknown.thing.thing-circle", action:"button5"
        }

        standardTile("button6", "device.button", inactiveLabel:false, decoration:"flat") {
            state "default", label:"Button 6", icon:"st.unknown.thing.thing-circle", action:"button6"
        }

        standardTile("button7", "device.button", inactiveLabel:false, decoration:"flat") {
            state "default", label:"Button 7", icon:"st.unknown.thing.thing-circle", action:"button7"
        }

        standardTile("button8", "device.button", inactiveLabel:false, decoration:"flat") {
            state "default", label:"Button 8", icon:"st.unknown.thing.thing-circle", action:"button8"
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
        status "Button 1": "button:1, action:pushed"
        status "Button 2": "button:2, action:pushed"
        status "Button 3": "button:3, action:pushed"
        status "Button 4": "button:4, action:pushed"
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
    //log.debug message
}
