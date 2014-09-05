X10 Bridge
==========

**X10 Bridge** is a smart app that allows integration of
[X10](http://www.x10.com/x10-basics.html) appliance modules, lamp modules,
switches and dimmers with the [SmartThings](http://fbuy.me/bb9pe) home
automation system.

Click here to watch the [demo video](http://youtu.be/laCar-03Jq4).

![Screenshot](http://statusbits.github.io/images/X10Bridge-01.jpg)

Requirements
------------

X10 uses power lines to send commands to appliance modules, light switches,
dimmers, etc. Because SmartThings cannot communicate with the X10 devices
directly, a gateway is required to connect these two systems. **X10 Bridge**
uses an open-source [mochad](http://sourceforge.net/projects/mochad/) TCP
Gateway running on a Linux computer and either
[CM15A](http://www.x10.com/cm15a-module.html) or
[CM19A](http://www.x10.com/cm19a.html) X10 interface module to communicate
with the X10 devices.

Mochad can be installed on any computer capable of running Linux. The only
hardware requirement is that your computer must have a USB port and be
connected to the same local area network (LAN) as your SmartThings hub. You
can use an old laptop or desktop computer or even a
[Raspberry Pi](http://x10linux.blogspot.com/2012/08/installing-mochad-on-raspberry-pi.html)
as your X10 gateway.

Note, that in order for the SmartThings hub to communicate with the gateway
computer, the latter must be assigned either a *static* or a *reserved* IP
address.

Mochad TCP gateway supports two types of X10 interfaces: the
[CM15A](http://www.x10.com/cm15a-module.html) and the
[CM19A](http://www.x10.com/cm19a.html).

The CM15A interface can send both power line (PL) and radio frequency (RF)
commands, whereas CM19A can only send RF commands. Therefore, when using
CM19A interface, you'll also need one of the X10 transceiver modules - either
[TM751](http://www.x10.com/tm751.html) or [RR501](http://www.x10.com/rr501.html).
Each of these modules can receive RF commands and forward them to other X10
modules over power line. They also function as an appliance module.


Installation
------------

**1. Installing mochad TCP gateway**

Mochad is distributed as a source code package, meaning that you'll have to
build it from source. The process is not that difficult though.

Note that libusb-1.0 development files are required to build mochad. If you're
using Ubuntu Linux, you can install it with the following command:

    sudo apt-get install libusb-1.0-0-dev

Now, download the
[mochad source tar ball](http://sourceforge.net/projects/mochad/files/latest/download?source=files)
and unpack in into a local directory.

    tar xzvf mochad-0.1.16.tar.gz

Next, change your current directory to 'mochad-0.1.16' and run the following
commands:

    ./configure
    make
    sudo make install

Now you can plug either CM15A or CM19A X10 interface into a USB port and
mochad will launch automatically.

For more information, please refer to mochad
[README](http://sourceforge.net/p/mochad/code/ci/master/tree/).

**2. Installing X10 Switch Device Type Handler**

Before you install the **X10 Bridge** smart app, you'll need to install the
**X10 Switch** device type handler.

1. Open [SmartThings IDE](https://graph.api.smartthings.com) in your web
browser and log into your account.
2. Click on the "My Device Types" section in the navigation bar (or open
[device page](https://graph.api.smartthings.com/ide/devices) directly).
3. On the "\<Your Name\>'s Device Types" page, click on the "+ New SmartDevice"
button on the right.
4. On the "New SmartDevice" page, fill out the mandatory "Name" field. It does
not matter what you type in the name field since it will be overwritten in the
next step anyway. Click the blue "Create" button at the bottom of the page. An
IDE editor window containing device handler template should now open.
5. In another browser window, go to the **X10 Switch**
[source code](https://github.com/statusbits/smartthings/blob/master/X10Bridge/X10_Switch.device.groovy)
on GitHub.
6. Copy the **X10 Switch** source code from GitHub and paste it into the
IDE editor window (see Step 4). Make sure you completely *overwrite*
content of the editor window with the new content copied from the GitHub.
7. Click the blue "Save" button above the editor window.
8. Click the "Publish" button next to it and select "For Me". You have now
self-published your smart device handler.


**3. Installing X10 Bridge SmartApp**

**X10 Bridge** smart app is available in the "My Apps" section of the Shared
Smart Apps in [SmartThings IDE](https://graph.api.smartthings.com).

1. Go to "My SmartApps" section and click on the "+ New SmartApp" button on
the right.
2. On the "New SmartApp" page, fill out mandatory "Name" and "Description"
fields (it does not matter what you put there), then click the blue "Create"
button at the bottom.
3. When a new smart app template opens in the IDE editor, click on the "Browse
SmartApps" drop-down list in the upper right corner and select "Browse Shared
SmartApps". A list of shared SmartApps will appear on the left side of the
editor window.
4. Scroll down to "My Apps" section and click on it.
5. Select "X10 Bridge" from the list and click the red "Overwrite" button in
the bottom right corner.
6. Click the blue "Save" button above the editor window.
7. Click the "Publish" button next to it and select "For Me". You have now
self-published your smart app.
8. Open SmartThings mobile app on iPhone or Android and go to the Dashboard.
9. Tap on the round "+" button and navigate to "My Apps" section by swiping
the menu ribbon all the way to the left. 
10. "X10 Bridge" app should be available in the list that appears below the
menu ribbon. Tap it and follow setup instructions. Please note, that if the
"X10 Bridge" app does not appear in the list, you may have to *log out* of
your SmartThings account and then restart your SmartThings mobile app.


Known Issues
------------

1. Currently, **X10 Bridge** allows *only* transmitting X10 commands.
Therefore, it has no way of knowing the actual state of the X10 devices.
For example, if the X10 switch, appliance module or a lamp module is turned on
or off either manually or by other means (e.g. using remote control), the
device state in the SmartThings will not be updated.

2. **X10 Bridge** uses *raw TCP socket* connection to communicate with mochad
TCP gateway. Raw TCP sockets are not officially supported by SmartThings
and therefore may not work reliably. Therefore, this smart app should be
considered *experimental* and should not be used for any critical tasks.
You have been warned.


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
