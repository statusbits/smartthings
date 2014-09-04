X10 Bridge
==========

Under construction ...

X10 Bridge is a SmartApp that allows integration of
[X10](http://www.x10.com/x10-basics.html) appliance modules, switches and
dimmers with [SmartThings](http://fbuy.me/bb9pe) home automation system.

Click here to watch [YouTube demo video](http://youtu.be/laCar-03Jq4).

Since SmartThings cannot communicate with X10 devices directly, a gateway is
necessary to connect these two systems. X10 Bridge requires open-source
[mochad](http://sourceforge.net/projects/mochad/) TCP Gateway running on a
Linux computer and either CM15A or CM19A X10 transceiver to communicate with
X10 devices.

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

Installation
------------

