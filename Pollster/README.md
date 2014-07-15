Pollster
========

Many SmartThings devices rely on polling to periodically update their state. These devices have 'polling'
capability and provide poll() function that the SmartThings polling engine calls approximately every 10
minutes. Unfortunately, SmartThings polling engine proved to be unreliable and sometimes it stops working
for hours and even days.

Here comes Pollster to the rescue! Pollster is a polling daemon that calls poll() function periodically
for selected devices that provide 'polling' capability. Polling interval can be set by user, as fast as
1 minute.
