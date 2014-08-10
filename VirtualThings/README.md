Virtual Things
==============

Virtual "things" are SmartThings devices that are not tied to any actual
hardware. They do however appear as real devices in the SmartThings UI and
can be interacted with using "things" tiles or used in the SmartApps just
like any other device.

An example of virtual device is SmartThings' "On/Off Button Tile" that
implements "Switch" device
[capability](https://graph.api.smartthings.com/ide/doc/capabilities) and can
fire "on" and "off" events when a user touches the switch tile.


### Virtual 8-button Remote Control (VirtualRemoteControl.device.groovy)

![Screenshot](http://statusbits.github.io/images/VirtualRemoteControl.jpg)

This virtual device implements SmartThings "Button" device capability and
emulates an 8-button remote control. It fires "pushed" messages whenever one
of its eight button tiles is touched.

Virtual Remote Control device can be used with the "Button Controller"
SmartApp to turn switches on and off, open and close door locks, change
home "modes", trigger "Hello Home" actions, etc. Note, that Button
Controller app supports only four buttons though.


### Virtual Temperature Tile (VirtualTemperatureTile.device.groovy)

This virtual device implements SmartThings "Temperature" device capability
and can be used to display temperature data from external source. The device
current temperature value can be set by calling its 'parse' method with a
string argument in the "current_value:<value>" format. For example:
    
    parse("current_value:72.5")


Installation
------------

Currently, the only way to install user-defined devices is using
[SmartThings IDE](https://graph.api.smartthings.com).

TBD
