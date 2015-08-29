Pollster
========

**Please note that although Pollster has proven to be indispensable to
overcome SmartThings device polling issue, it was brought to my attention
that it does not meet SmartThings smart app approval criteria because polling
of the devices does not play nicely with SmartThings platform and in some
cases may lead to degraded system performance. Therefore, I do not recommend
using Pollster and no longer support it. Use it at your own risk.**

----

Many [SmartThings](http://www.smartthings.com) devices rely on frequent
polling to update their status periodically. These devices have 'polling'
capability and implement 'poll' command that the SmartThings polling engine
calls approximately every 10 minutes. The polling rate is not user-configurable
and may not be frequent enough for some devices. Also, the SmartThings polling
engine has been known to have bad days now and then, when it works
intermittently or stops working for hours or even days.

Here comes Pollster to the rescue! Pollster works behind the scenes and
periodically calls 'poll' or 'refresh' command for selected devices. Devices
can be arranged into three groups with independently configurable polling
intervals. The polling interval can be as short as one minute.

![Pollster Screenshot](http://statusbits.github.io/images/Pollster_1.2.jpg)


Installation
------------

Before you can install Pollster using your SmartThings smart phone app, you
have to self-publish it in the [SmartThings IDE](https://graph.api.smartthings.com).
Please refer to [SmartThings Developer Documentation](http://docs.smartthings.com/en/latest/index.html)
for more information.


Revision History
----------------

**Version 1.3. Released 8/29/2015**
* Added watchdog feature. Watchdog task runs every 15 minutes and checks to
  see that polling tasks are running. If watchdog detects that one of the
  polling tasks has stopped, it will send a notification message and attempt
  to restart Pollster. 
* The number of polling groups is reduced to three. The forth scheduling slot
  is reserved for the watchdog.
* Fixed a bug in "Poll Now" feature.
* Randomize seconds in the cron schedules.

**Version 1.2. Released 2/8/2015**
* Added ability to update devices that provide 'refresh' capability.
* Added "Poll Now" feature. You can now start the poll manually by touching
the app icon.

**Version 1.1. Released 8/23/2014**
* Devices can be arranged into 4 polling groups with different polling
interval.

**Version 1.0. Released 7/14/2014** 
* Published to SmartThings shared apps directory.


License
-------

Copyright (C) 2014 Statusbits.com

Licensed under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License. You may obtain a copy
of the License at:

<http://www.apache.org/licenses/LICENSE-2.0>

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
License  for the specific language governing permissions and limitations
under the License.
