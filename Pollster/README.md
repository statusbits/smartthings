Pollster
========

Many [SmartThings](http://fbuy.me/bb9pe) devices rely on polling to update
their status periodically. These devices have 'polling' capability and provide
poll() function that the SmartThings polling engine calls approximately every
10 minutes. Unfortunately, the SmartThings polling engine proved to be
unreliable and sometimes it stops working for hours and even days.

Here comes Pollster to the rescue! Pollster is a polling daemon that calls
poll() function periodically for selected devices. Devices can be arranged
into four groups with independently configurable polling intervals. The
polling interval can be as short as one minute.

![](https://sites.google.com/site/statusbits/pictures/Pollster.jpg)

Installation
------------

Pollster app is available in the "Convenience" section of the Shared Smart
Apps in [SmartThings IDE](https://graph.api.smartthings.com).

1. Go to "My SmartApps" section and click on the "+ New SmartApp" button on the right.
2. On the "New SmartApp" page, fill out mandatory "Name" and "Description" fields (it does not matter what you put there), then click the "Create" button at the bottom.
3. When a new app template opens in the IDE, click on the "Browse SmartApps" drop-down list in the upper right corner and select "Browse Shared SmartApps". A list of shared SmartApps will appear on the left side of the editor window.
4. Scroll down to "Convenience" section and click on it.
5. Select "Pollster" from the list and click the red "Overwrite" button in the bottom right corner.
6. Click the blue "Save" button above the editor window.
7. Click the "Publish" button next to it and select "For Me". You have now self-published your SmartApp.
8. Open SmartThings mobile app on iPhone or Android and go to the Dashboard.
9. Tap on the round "+" button and navigate to "My Apps" section by swiping the menu ribbon all the way to the left.
10. "Pollster" app should be available in the list that appears below the menu ribbon. Tap it and follow setup instructions.
