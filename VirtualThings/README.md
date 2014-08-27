Virtual Things
==============

Virtual "things" are [SmartThings](http://fbuy.me/bb9pe) devices that are not
tied to any actual hardware. They do however appear as real devices in the
SmartThings UI and can be interacted with using "things" tiles or used in the
SmartApps just like any other device.

An example of virtual device is SmartThings' "On/Off Button Tile" that
implements "Switch" device
[capability](https://graph.api.smartthings.com/ide/doc/capabilities) and can
fire "on" and "off" events when a user touches the switch tile.


### 8-button Virtual Remote Control

    File: VirtualRemoteControl.device.groovy

![Screenshot](http://statusbits.github.io/images/VirtualRemoteControl.jpg)

This virtual device implements SmartThings "Button" device capability and
emulates an 8-button remote control. It fires "pushed" messages whenever one
of its eight button tiles is touched.

Virtual Remote Control device can be used with the "Button Controller"
SmartApp to turn switches on and off, open and close door locks, change
home "modes", trigger "Hello Home" actions, etc. Note, that Button
Controller app supports only four buttons though.


### Virtual Contact Tile

    File: VirtualContactTile.device.groovy

This virtual device implements SmartThings "Contact Sensor" device capability
and can be used to display status of an open/closed contact from external
source. The device current status can be set by calling its 'parse' command
with "contact:<value>" string as an argument, where <value> is either "open"
or "closed":

    parse("contact:<open | closed>")


### Virtual Humidity Tile

    File: VirtualHumidityTile.device.groovy

This virtual device implements SmartThings "Relative Humidity Measurement"
device capability and can be used to display relative humidity data from
external source. The device current humidity value can be set by calling its
'parse' command with "humidity:<value>" string as an argument, where <value>
is relative humidity in percents:

    parse("humidity:<value>")


### Virtual Illuminance Tile

    File: VirtualIlluminanceTile.device.groovy

This virtual device implements SmartThings "Illuminance Measurement" device
capability and can be used to display illuminance data from external source.
The device current illuminance value can be set by calling its 'parse' command
with "illuminance:<value>" string as an argument, where <value> is illuminance
value in lux:

    parse("illuminance:<value>")


### Virtual Motion Tile

    File: VirtualMotionTile.device.groovy

This virtual device implements SmartThings "Motion Sensor" device capability
and can be used to display active/inactive motion sensor status from external
source. The device current status can be set by calling its 'parse' command
with "motion:<value>"  string as an argument, where <value> is either "active"
or "inactive":

    parse("contact:<active | inactive>")


### Virtual Temperature Tile

    File: VirtualTemperatureTile.device.groovy

This virtual device implements SmartThings "Temperature Measurement" device
capability and can be used to display temperature data from external source.
The device current temperature value can be set by calling its 'parse' command
with "temperature:<value>" string as an argument, where <value> is temperature
in degrees of Fahrenheit:

    parse("temperature:<value>")


Installation
------------

Currently, the only way to install user-defined devices is using
[SmartThings IDE](https://graph.api.smartthings.com).

TBD
