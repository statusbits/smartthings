GE 45600 Remote Control
=======================

[GE 45600](http://www.amazon.com/dp/B0013V6RW0)  is an inexpensive Z-Wave
handheld remote control with 9 buttons. It can be used as a secondary
controller with [SmartThings](http://fbuy.me/bb9pe) (or any other Z-Wave
home automation system) to control switches and light dimmers. However,
SmartThings generic device handler for Z-Wave remote controls does not
allow to send button presses to the smart apps. 

And now you can! This custom device handler provides `capability.button`
and sends `button` events for each of 9 remote control buttons.

Click here to watch the [demo video](http://youtu.be/6Uts5BLrYnw).

**Note:** *To send button commands to SmartThings, you must press the
"SCENE" button on the remote first (the third button in the top row). To
switch back to normal remote control mode, press the "LIGHT" button (the
first button in the top row).*


Installation
------------

**1. Install Custom GE45600 Device Handler**

1. Open [SmartThings IDE](https://graph.api.smartthings.com) in your web
browser and log into your account.
2. Click on the "My Device Types" section in the navigation bar.
3. Click on the green "+ New SmartDevice" button.
4. On the "New SmartDevice" page, click on the "From Code" tab.
5. In another browser window, go to the **GE45600**
[source code](https://github.com/statusbits/smartthings-x10/blob/master/GE45600/GE45600.device.groovy)
on GitHub.
6. Copy the **GE45600** source code from GitHub and paste it into the
IDE editor window (see Step 4).
7. Click the blue "Create" button underneath the editor window.
8. Click the "Publish" button next to it and select "For Me". You have now
self-published your smart device handler.

**2. Replace Generic Z-Wave Remote device handler with custom GE45600 device handler**

1. Click on "My Devices" section in the SmartThings IDE navigation bar.
2. Locate GE 45600 device instance in the installed devices list and click on
its name (in the "Display Name" column).
3. On the device properties page, click the "Edit" button at the bottom of the
page.
4. On the "Edit Device" page, scroll down to the "Type" section and select
"GE45600 Remote" from the drop-down list.
5. Click on the "Update" button at the bottom of the page.


License
-------

Copyright (C) 2014 Statusbits.com

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
