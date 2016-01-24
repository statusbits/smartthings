[Back to SmartThings Projects](https://github.com/statusbits/smartthings)

Pollster
--------

**Please note that frequent polling of some devices (particularly Zigbee and
Z-Wave) may cause network congestion and lead to degraded system performance.
Therefore, polling rate faster than 5 minutes is not recommended. The author
of this software bears no responsibility for your system performance. Use it
at your own risk.**

----

Many [SmartThings](http://www.smartthings.com) devices rely on frequent
polling to update their status periodically. These devices have 'polling'
capability and implement 'poll' command that the SmartThings polling engine
calls approximately every 10 minutes. The polling rate is not user-configurable
and may not be frequent enough for some devices. Also, the SmartThings polling
engine has been known to have bad days now and then, when it works
intermittently or stops working for hours or even days.

Here comes Pollster to the rescue! Pollster works behind the scenes and
periodically calls 'poll' or 'refresh' command for selected devices. Devices
can be arranged into four groups with independently configurable polling
intervals. The polling interval can be as short as one minute.

![Pollster Screenshot](http://statusbits.github.io/images/Pollster_1.2.jpg)

---

*If you like this app, please consider supporting its development by making a
donation via PayPal.*

[![PayPal](https://www.paypalobjects.com/en_US/i/btn/btn_donate_LG.gif)](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=ATTTMV7JV2W9W)


### Installation

1. Self-publish Pollster SmartApp by creating a new SmartApp in the
[SmartThings IDE](https://graph.api.smartthings.com) and pasting the
[source code](https://raw.githubusercontent.com/statusbits/smartthings/master/smartapps/statusbits/pollster.src/pollster.groovy)
in the "From Code" tab. Please refer to the
[SmartThings Developer Documentation](http://docs.smartthings.com/en/latest/index.html)
for more information.

2. Open SmartThings mobile app and locate "Pollster" SmartApp in the "My Apps"
section of the Marketplace.


### Revision History

**Version 1.4.2. Released 1/23/2016**

* Work around UndeclaredThrowableException thrown by unschedule().

**Version 1.4.1. Released 1/19/2016**

* Four polling groups.
* Execute watchdog process upon app touch and location events (sunrise, sunset
  or mode change).
* Watchdog process will restart Pollster if any of the polling schedules is
  late by 10 minutes or more.
* Don't use "hideable" section attribute due to UI rendering bug.

**Version 1.3.1. Released 10/4/2015**

* Disabled push notification on restart.
* Made 'About' section hidden by default.

**Version 1.3. Released 8/29/2015**

* Added watchdog feature. Watchdog task runs every 15 minutes and checks to
  see that the polling tasks are running. If watchdog detects that one of the
  polling tasks has stopped, it will send a notification message and attempt
  to restart Pollster. 
* The number of polling groups is reduced to three. The fourth scheduling slot
  is reserved for the watchdog.
* Fixed a bug in the "Poll Now" feature.
* Randomized seconds in the cron schedules.

**Version 1.2. Released 2/8/2015**

* Added ability to update devices that provide 'refresh' capability.
* Added "Poll Now" feature. You can now start the poll manually by touching
the app icon.

**Version 1.1. Released 8/23/2014**

* Devices can be arranged into 4 polling groups with different polling
interval.

**Version 1.0. Released 7/14/2014** 

* Published to SmartThings shared apps directory.


### License

Copyright © 2014 Statusbits.com

Licensed under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License. You may obtain a copy
of the License at:

<http://www.apache.org/licenses/LICENSE-2.0>

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
License  for the specific language governing permissions and limitations
under the License.
