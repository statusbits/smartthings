[Back to SmartThings Projects](https://github.com/statusbits/smartthings)

Radio Thermostat
----------------

The **RadioThermostat** Smart Device handler allows you to connect
[Filtrete 3M-50](http://www.radiothermostat.com/filtrete/products/3M-50/)
WiFi thermostat to the [SmartThings](http://www.smartthings.com) home
automation system.

![Filtrete 3M-50](http://statusbits.github.io/images/Filtrete_3M50.jpg)

Here's a screenshot of the **RadioThermostat** device running inside the
SmartThings iPhone mobile app.

![RadioThermostat Screenshot](http://statusbits.github.io/images/RadioThermostat.jpg)

---

[![PayPal](https://www.paypalobjects.com/en_US/i/btn/btn_donate_LG.gif)](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=ATTTMV7JV2W9W)

*If you enjoy using this software, please show your appreciation by making a small
donation. Thank you for your support!*

---

### Requirements

1. Your Wi-Fi thermostat must be connected to the same local network (LAN) as
your SmartThings hub.
2. It is important that the thermostat's IP address remain unchanged even if
your router is rebooted. This can be accomplished by using your router's
"reserved IP address" feature. Please consult your web router manual on how to
reserve an IP address.
3. Obviously, you need a functioning Internet connection for your hub to
communicate with the SmartThings cloud.


### Installation

**Note:** RadioThermostat is a SmartThings *device handler*. Installing custom
device is a two-step process -- first, you need to install and self-publish
the SmartDevice *Type*, then you need to create an instance of the new
SmartDevice. Both steps must be performed in the SmartThings Web-based
[IDE](https://graph.api.smartthings.com).

**1. Installing RadioThermostat SmartDevice Type**

1. Open [SmartThings IDE](https://graph.api.smartthings.com) in your web
browser and log into your account.
2. Click on the "My Device Types" section in the navigation bar (or open the
[SmartDevices](https://graph.api.smartthings.com/ide/devices) page).
3. On the "\<Your Name\>'s Device Types" page, click on the "+ New SmartDevice"
button on the right.
4. On the "New SmartDevice" page, fill out the mandatory "Name" field. It does
not matter what you type in the name field since it will be overwritten in the
next step anyway. Click the blue "Create" button at the bottom of the page. An
IDE editor window containing device handler template should now open.
5. Copy the **RadioThermostat**
[source code](https://raw.githubusercontent.com/statusbits/smartthings/master/devicetypes/statusbits/radio-thermostat.src/radio-thermostat.groovy)
from GitHub and paste it into the IDE editor window (see previous step). Make
sure you *completely overwrite* contents of the editor window with the source
code copied from the GitHub.
6. Click the blue "Save" button above the editor window.
7. Click the "Publish" button next to it and select "For Me". You have now
self-published your smart device handler.

**2. Installing RadioThermostat SmartDevice**

1. In the [SmartThings IDE](https://graph.api.smartthings.com), click on the
"My Devices" section (or open the [Device List](https://graph.api.smartthings.com/device/list)
page).
2. On the "Device List" page, click on the "+ New Device" button on the right.
3. On the "Create Device" page, enter device name in the mandatory "Name"
field. You can use any name here, for example "Radio Thermostat".
4. Optionally, enter device label in the "Label" field. This is the actual
label that will displayed in the SmartThings mobile app.
5. Fill in the mandatory "Device Network Id" filed. You can enter any string
or number here, since it will be overwritten by the device handler anyway.
6. In the mandatory "Type" field, select "Radio Thermostat" from the drop-down
list.
7. In the mandatory "Version" field, select "Published" from the drop-down
list.
8. In the "Location" field, select location where you want to install new
device.
9. In the "Hub" filed, select the name of your SmartThings hub.
10. Click the blue "Create" button at the bottom of the page.

**3. Refresh Device List in SmartThings Mobile App**

1. Open the SmartThings app on your mobile device, go to the Dashboard and
*log out* of your SmartThings account.
2. Exit and *restart* the SmartThings mobile app.
3. Log back in into your SmartThings account. A new RadioThermostat device
should now appear on the "Things" page.


### Known Issues

None. Please note that as of version 2.0.0, the RadioThermostat device handler
performs polling automatcally and does not require Pollster. :=) 


### Revision History

**Version 2.0.0. Released 12/17/2016**
* Completely redesigned UI using new multi-attribute tile.
* Implemented automatic polling. Pollster is no longer reuired.
* Check for upper and lower temperature limits when setting heating and
cooling setpoints.


**Version 1.0.3. Released 08/25/2015**
* Fixed an issue with DNI not being updated.


### License

Copyright © 2014 Statusbits.com

This program is free software: you can redistribute it and/or modify it
under the terms of the GNU General Public License as published by the Free
Software Foundation, either version 3 of the License, or (at your option)
any later version.

This program is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License along
with this program.  If not, see <http://www.gnu.org/licenses/>.
