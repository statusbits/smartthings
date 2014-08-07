Virtual Things
==============

Virtual "things" are SmartThings devices that are not tied to the actual
hardware. They do however appear as real devices and can be interacted with
using "things" tiles in the SmartThings UI and can be used in SmartApps just
like any other device.

An example of virtual device is SmartThings' "On/Off Button Tile" that
implements "Switch" device
[capability](https://graph.api.smartthings.com/ide/doc/capabilities) and can
fire "on" and "off" events when a user touches the switch tile.

Virtual 8-button Remote Control
-------------------------------

This virtual device implements SmartThings "Button" device capability and
emulates an 8-button remote control. It fires "pushed" messages whenever one
of its eight button tiles is touched.

Installation
------------

Currently, the only way to install user-defined devices is using
[SmartThings IDE](https://graph.api.smartthings.com).

TBD
