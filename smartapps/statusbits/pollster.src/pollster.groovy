/**
 *  Pollster - The SmartThings Polling Daemon.
 *
 *  Pollster works behind the scenes and periodically calls 'poll' or
 *  'refresh' commands for selected devices. Devices can be arranged into
 *  three polling groups with configurable polling intervals down to 1 minute.
 *
 *  Please visit [https://github.com/statusbits/smartthings] for more
 *  information. 
 *
 *  --------------------------------------------------------------------------
 *
 *  Copyright © 2014 Statusbits.com
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
 *  Version 1.3.1 (10/04/2015)
 */

definition(
    name: "Pollster",
    namespace: "statusbits",
    author: "geko@statusbits.com",
    description: "Poll or refresh device status periodically.",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")

preferences {
    section("About", hideable:true, hidden:true) {
        def hrefAbout = [
            url:        "http://statusbits.github.io/smartthings/",
            style:      "embedded",
            title:      "Tap for more information...",
            description:"http://statusbits.github.io/smartthings/",
            required:   false
        ]

        paragraph about()
        href hrefAbout
    }

    (1..3).each() { n ->
        section("Polling Group ${n}") {
            input "group_${n}", "capability.polling", title:"Select devices to be polled", multiple:true, required:false
            input "refresh_${n}", "capability.refresh", title:"Select devices to be refreshed", multiple:true, required:false
            input "interval_${n}", "number", title:"Set polling interval (in minutes)", defaultValue:5
        }
    }
}

def installed() {
    initialize()
}

def updated() {
    unschedule()
    initialize()
}

def onAppTouch(event) {
    LOG("onAppTouch(${event.value})")

    def devPoll = []
    def devRefresh = []

    (1..3).each() { n ->
        if (settings["group_${n}"]) {
            devPoll.addAll(settings["group_${n}"])
        }

        if (settings["refresh_${n}"]) {
            devRefresh.addAll(settings["refresh_${n}"])
        }
    }

    log.debug "devPoll: ${devPoll}"
    log.debug "devRefresh: ${devRefresh}"

    devPoll*.poll()
    devRefresh*.refresh()
}

def pollingTask1() {
    LOG("pollingTask1()")

    state.trun1 = now()

    if (settings.group_1) {
        settings.group_1*.poll()
    }

    if (settings.refresh_1) {
        settings.refresh_1*.refresh()
    }
}

def pollingTask2() {
    LOG("pollingTask2()")

    state.trun2 = now()

    if (settings.group_2) {
        settings.group_2*.poll()
    }

    if (settings.refresh_2) {
        settings.refresh_2*.refresh()
    }
}

def pollingTask3() {
    LOG("pollingTask3()")

    state.trun3 = now()

    if (settings.group_3) {
        settings.group_3*.poll()
    }

    if (settings.refresh_3) {
        settings.refresh_3*.refresh()
    }
}

def watchdogTask() {
    LOG("watchdogTask()")

    if (settings.interval_1 && state.trun1) {
        def t = now() - state.trun1
        if (t > (settings.interval_1 * 120000)) {
            log.warn "Polling task #1 is toast. Restarting..."
            restart()
            return
        }
    }

    if (settings.interval_2 && state.trun2) {
        def t = now() - state.trun2
        if (t > (settings.interval_2 * 120000)) {
            log.warn "Polling task #2 is toast. Restarting..."
            restart()
            return
        }    
    }

    if (settings.interval_3 && state.trun3) {
        def t = now() - state.trun3
        if (t > (settings.interval_3 * 120000)) {
            log.warn "Polling task #3 is toast. Restarting..."
            restart()
            return
        }
    }
}

private def initialize() {
    log.info "Pollster. Version ${version()}. ${copyright()}"
    LOG("initialize() with settings: ${settings}")

    state.trun1 = 0
    state.trun2 = 0
    state.trun3 = 0

    Random rand = new Random(now())
    def numTasks = 0
    (1..3).each() { n ->
        def minutes = settings."interval_${n}".toInteger()
        def seconds = rand.nextInt(60)
        def size1 = settings["group_${n}"]?.size() ?: 0
        def size2 = settings["refresh_${n}"]?.size() ?: 0

        if (minutes > 0 && (size1 + size2) > 0) {
            LOG("Scheduling polling task ${n} to run every ${minutes} minutes.")
            def sched = "${seconds} 0/${minutes} * * * ?"
            schedule(sched, "pollingTask${n}")
            numTasks++
        }
    }

    if (numTasks) {
        def seconds = rand.nextInt(60)
        schedule("${seconds} 1/15 * * * ?", watchdogTask)
    }

    subscribe(app, onAppTouch)

    LOG("state: ${state}")
}

private def restart() {
    //sendNotification("Pollster is toast. Restarting...")
    updated()
}

private def about() {
    def text =
        "Pollster works behind the scenes and periodically calls 'poll' or " +
        "'refresh' commands for selected devices. Devices can be arranged " +
        "into three polling groups with configurable polling intervals " +
        "down to 1 minute.\n\n" +
        "Version ${version()}\n${copyright()}\n\n" +
        "You can contribute to the development of this app by making a " +
        "PayPal donation to geko@statusbits.com. We appreciate your support."
}

private def version() {
    return "Version 1.3.1"
}

private def copyright() {
    return "Copyright © 2014 Statusbits.com"
}

private def LOG(message) {
    //log.trace message
}
