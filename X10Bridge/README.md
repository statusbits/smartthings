X10 Bridge
==========

Under construction ...

X10 Bridge is a SmartApp that allows integration of
[X10](http://www.x10.com/x10-basics.html) appliance modules, switches and
dimmers with the [SmartThings](http://fbuy.me/bb9pe) home automation system.

Click here to watch [YouTube demo video](http://youtu.be/laCar-03Jq4).

X10 uses power lines to send commands to appliance modules, light switches,
dimmers, etc. Since SmartThings cannot communicate with the X10 devices
directly, a gateway is necessary to connect these two systems. X10 Bridge
requires an open-source [mochad](http://sourceforge.net/projects/mochad/) TCP
Gateway running on a Linux computer and either CM15A or CM19A X10 interface
module to communicate with the X10 devices.

![Screenshot](http://statusbits.github.io/images/X10Bridge-01.jpg)

Hardware Requirements
---------------------

1. Computer with available USB port running Linux OS and connected to your
local area network (LAN).

This is necessary to run mochad TCP gateway. Mochad does not have any special
hardware requirements (other than presence of a USB port) and can run on
practically any flavor of Linux. You can install your favorite Linux
distribution on an old laptop or desktop computer or on a
[Raspberry Pi](http://x10linux.blogspot.com/2012/08/installing-mochad-on-raspberry-pi.html). 

The gateway computer and the SmartThings hub must be connected to the same
local network. Also, in order for the SmartThings hub to communicate with
the gateway computer, the latter must be assigned either a static or a
reserved IP address.

2. X10 Interface Module.

X10 uses power lines (PL) to send commands to the X10 devices such as
appliance modules, light switches, dimmers, etc. Some X10 devices (for example
remote controls, motion sensors, door/window sensors, etc.) use radio
frequency (RF) to send and receive X10 commands.

An X10 interface module connects to a computer's USB
port and allows sending X10 commands from the computer to the X10 devices.

Mochad TCP gateway supports two types of X10 interfaces:
[CM15A](http://www.x10.com/cm15a-module.html) and
[CM19A](http://www.x10.com/cm19a.html).



Installation
------------

