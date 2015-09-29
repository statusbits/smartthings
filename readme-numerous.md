[Back to SmartThings Projects](https://github.com/statusbits/smartthings)

Numerous
--------

The **Numerous** SmartApp allows you to display numeric values from your
[SmartThings](http://www.smartthings.com) sensors in the
[Numerous](http://numerousapp.com) mobile app for iPhone, iPad or Android.

[Numerous](http://numerousapp.com) is a free mobile app that displays numeric
values (known as "metrics") from various sources in real time. The metrics are
displayed as tiles on a configurable dashboard. The app allows you to:

1. Re-arrange tiles on the dashboard and group them into pages.
2. Edit a description string for each metric.
3. Set a background image for each tile.
4. View historic data for each metric.
5. View historic data as interactive graphs by switching to landscape mode.
6. Send push notifications for the value changes.
7. Trigger IFTTT rules on the value changes.
8. Display selected metrics in the "Today View" widget in iOS.
8. Display selected metrics on the [Apple Watch](http://numerousapp.com/watch/).
9. Share your metrics with the world, your friends, or keep them private.

Currently, you can connect the following SmartThings devices to Numerous:

* Temperature sensors
* Relative humidity sensors
* Illuminance sensors
* Power meters
* Energy meters
* Battery levels

---

*If you like this app, please consider supporting its development by making a
donation via PayPal.*

[![PayPal](https://www.paypalobjects.com/en_US/i/btn/btn_donate_LG.gif)](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=ATTTMV7JV2W9W)


### Screenshots

![Screenshot](http://statusbits.github.io/images/Numerous-01.jpg)


### Installation

1. Self-publish Numerous SmartApp by creating a new SmartApp in the
[SmartThings IDE](https://graph.api.smartthings.com/ide/apps) and pasting the
[source code](https://raw.githubusercontent.com/statusbits/smartthings/master/smartapps/statusbits/numerous.src/numerous.groovy)
in the "From Code" tab. Please refer to the
[SmartThings Developer Documentation](http://docs.smartthings.com/en/latest/index.html)
for more information.

2. If you have not done so, install
[Numerous mobile app](http://numerousapp.com/download/) on your iOS or Android
device and sign in.

3. Open the Numerous mobile app and copy the API Key found under Settings >
Developer Info.

4. Open SmartThings mobile app and locate Numerous SmartApp in the "My Apps"
section of the Marketplace.

5. Paste the Numerous API Key (see Step 3) into the "Your API Key" field.

6. Select devices that you'd like to view on the Numerous dashboard and tap
"Done".


### Revision History

**Version 1.1.0. Released 9/28/2015**

* Added battery level metrics.
* Fixed an issue with percents not displaying correctly.

**Version 1.0.0. Released 9/27/2015**

* Initial public release.


### License

Copyright © 2015 Statusbits.com

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
