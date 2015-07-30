VLC Thing
=========

**VLC Thing** is a [SmartThings](http://www.smartthings.com) device handler
for controlling [VLC Media Player](http://www.videolan.org) remotely.

VLC is a free and open source cross-platform multimedia player and framework
that plays most multimedia files as well as DVDs, Audio CDs, VCDs, and various
streaming protocols. VLC can be installed on virtually any Linux, Mac or
Windows laptop or desktop computer.

The primary goal of this project is to enable audio and voice notifications
for SmartThings that are currently available only using expensive
[Sonos](http://www.sonos.com) wireless audio system. The idea is to use an
old laptop computer running VLC as an alternative to Sonos.

**VLC Thing** implements some of the extended Sonos commands and thus can be
used with existing Smart Apps written specifically for Sonos, for example
"Sonos Notify with Sound" and "Sonos Weather Forecast".

Naturally, VLC *is not* a Sonos substitute. If you're interested in advanced
Sonos features like multi-room audio, you should probably go ahead and buy
Sonos. However, if your needs are limited to single-room voice notifications,
**VLC Thing** may just fit the bill.

Here's a screenshot of the **VLC Thing** device running inside the SmartThings
iPhone mobile app.

![VLC Thing Screenshot](http://statusbits.github.io/images/VLCThing-01.jpg)

You can also click here to watch the
[YouTube demo video](http://youtu.be/laCar-03Jq4).


Requirements
------------

1. Laptop or desktop computer. VLC Media Player can be installed on virtually
any computer running Linux, Windows or Mac OS.
2. The computer must be connected to the same local area network (LAN) as the
SmartThings hub. It is important that the computer's IP address remain
unchanged even if your computer or the router is rebooted. This can be
accomplished by using either "static" or "reserved" IP address.
3. Obviously, you need a functioning Internet connection for your hub to
communicate with the SmartThings cloud.


Installing and Configuring VLC Media Player
-------------------------------------------

1. Download the latest version of VLC Media player from
[videolan.org](http://www.videolan.org)
2. Follow [installation instructions](https://wiki.videolan.org/Documentation:Installing_VLC/)
to install VLC Media Player.
3. **VLC Thing** uses HTTP interface to communicate with the VLC Media Player.
The HTTP interface is disabled by default. You will have to enable the HTTP
interface by following the instructions available here:
 - for [Linux](http://hobbyistsoftware.com/VLCSetup-linux)
 - for [Windows](http://hobbyistsoftware.com/vlcsetup-win-manual)
 - for [Mac OS](http://hobbyistsoftware.com/vlcsetup-mac-manual)


Installing and Configuring VLC Thing Device Handler
---------------------------------------------------

**Note:** VLC Thing is a SmartThings *device handler*. Installing SmartThings
custom device is a somewhat complicated process, so please read these
instructions carefully. First, you will need to install and self-publish the
SmartDevice *Type*, then you will need to create an instance of the new
SmartDevice. Both steps must be performed in the SmartThings Web-based
[IDE](https://graph.api.smartthings.com).

**1. Installing VLC Thing SmartDevice Type**

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
5. Copy the **VLC Thing**
[source code](https://github.com/statusbits/smartthings/blob/master/vlc_thing/vlc_thing.groovy)
from GitHub and paste it into the IDE editor window (see previous step). Make
sure you *completely overwrite* contents of the editor window with the source
code copied from the GitHub.
6. Click the blue "Save" button above the editor window.
7. Click the "Publish" button next to it and select "For Me". You have now
self-published your smart device handler.

**2. Installing VLC Thing SmartDevice**

1. In the [SmartThings IDE](https://graph.api.smartthings.com), click on the
"My Devices" section (or open the [Device List](https://graph.api.smartthings.com/device/list)
page).
2. On the "Device List" page, click on the "+ New Device" button on the right.
3. On the "Create Device" page, enter device name in the mandatory "Name"
field. You can use any name here, for example "VLC Thing".
4. Optionally, enter device label in the "Label" field. This is the actual
label that will displayed in the SmartThings mobile app.
5. Fill in the mandatory "Device Network Id" filed. You can enter any string
or number here, since it will be overwritten by the device handler anyway.
6. In the mandatory "Type" field, select "VLC Thing" from the drop-down
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

**4. Configure VLC Thing device in SmartThings Mobile App**

1. Tap on the gear icon in the upper right corner of the **VLC Thing** tile to
open device details page.
2. Tap on the "Preferences" tile at the bottom of the page to open the device
preferences page.
3. In the **VLC Thing** preferences page, enter IP address and TCP port of
the computer with installed VLC Media Player and a password you have
configured during VLC Media Player configuration.
4. Tap the "Done" button in the upper right corner of the preferences page.
5. To verify that the **VLC Thing** and the VLC player are configured
correctly, tap the "Test" tile below the volume control. You should hear a
phrase "VLC for SmartThings is brought to you by Statusbits.com."


Revision History
----------------

**Version 1.2.1. Released 07/29/2015**
* Fixed an issue with truncated sounds when played using Sonos extensions
(i.e. playTrackAndRestore, playTrackAndResume, etc.)

**Version 1.2.0. Released 06/28/2015**
* Fixed Issue #4 (Unable to play text-to-speech on 64-bit Windows).
* Minor performance improvements.

**Version 1.1.0. Released 11/03/2014**
* Implemented new 'Speech Synthesis' capability for text-to-speech
applications.

**Version 1.0.1. Released 10/05/2014**
* Fixed Issue #1 - Exception thrown when calling device.playTrackAndRestore().

**Version 1.0.0. Released 10/04/2014**
* First public release.


License
-------

Copyright (C) 2014 geko@statusbits.com

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
