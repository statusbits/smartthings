X10 Bridge
==========

Under construction ...

X10 Bridge is a SmartApp that allows integration of
[X10](http://www.x10.com/x10-basics.html) appliance modules, lamp modules,
switches and dimmers with the [SmartThings](http://fbuy.me/bb9pe) home
automation system.

Click here to watch [YouTube demo video](http://youtu.be/laCar-03Jq4).

![Screenshot](http://statusbits.github.io/images/X10Bridge-01.jpg)

Requirements
------------

X10 uses power lines to send commands to appliance modules, light switches,
dimmers, etc. Because SmartThings cannot communicate with the X10 devices
directly, a gateway is required to connect these two systems. X10 Bridge uses
an open-source [mochad](http://sourceforge.net/projects/mochad/) TCP Gateway
running on a Linux computer and either [CM15A](http://www.x10.com/cm15a-module.html)
or [CM19A](http://www.x10.com/cm19a.html) X10 interface module to communicate
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

Mochad TCP gateway supports two types of X10 interfaces:
the [CM15A](http://www.x10.com/cm15a-module.html) and the
[CM19A](http://www.x10.com/cm19a.html).

The CM15A interface can send both power line (PL) and radio frequency (RF)
commands, whereas CM19A can only send RF commands. Therefore, when using
CM19A interface, you'll also need one of the X10 transceiver modules - either
[TM751](http://www.x10.com/tm751.html) or [RR501](http://www.x10.com/rr501.html).
Each of these modules can receive RF commands and forward them to other X10
modules over power lines. They also function as an appliance module, i.e.


Installation
------------

TBD


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
