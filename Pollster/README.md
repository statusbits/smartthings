Pollster
========

Many [SmartThings](http://fbuy.me/bb9pe) devices rely on frequent polling to
update their status periodically. These devices have 'polling' capability and
implement poll() command that the SmartThings polling engine calls
approximately every 10 minutes. The polling rate is not user-configurable and
may not be frequent enough for some devices. Also, the SmartThings polling
engine has been known to have bad days now and then, when it works
intermittently or stops working for hours and even days.

Here comes Pollster to the rescue! Pollster works behind the scenes and
periodically calls poll() or refresh() command for selected devices. Devices
can be arranged into four groups with independently configurable polling
intervals. The polling interval can be as short as one minute.

![Pollster Screenshot](http://statusbits.github.io/images/Pollster_1.2.jpg)

Installation
------------

Pollster is available in the "Convenience" section of the Shared Smart Apps in
[SmartThings IDE](https://graph.api.smartthings.com).

1. Go to "My SmartApps" section and click on the "+ New SmartApp" button on
the right.

2. On the "New SmartApp" page, fill out mandatory "Name" and "Description"
fields (it does not matter what you put there), then click the "Create" button
at the bottom.

3. When a new app template opens in the IDE, click on the "Browse SmartApps"
drop-down list in the upper right corner and select "Browse Shared SmartApps".
A list of shared SmartApps will appear on the left side of the editor window.

4. Scroll down to "Convenience" section and click on it.

5. Select "Pollster" from the list and click the red "Overwrite" button
in the bottom right corner.

6. Click the blue "Save" button above the editor window.

7. Click the "Publish" button next to it and select "For Me". You have now
self-published your SmartApp.

8. Open SmartThings mobile app on iPhone or Android and go to the Dashboard.

9. Tap on the round "+" button and navigate to "My Apps" section by swiping the
menu ribbon all the way to the left.

10. "Pollster" app should be available in the list that appears below the
menu ribbon. Tap it and follow the setup instructions.


Revision History
----------------

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
