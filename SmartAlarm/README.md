## Smart Alarm

Smart Alarm is a multi-zone virtual alarm panel for the [SmartThings](http://fbuy.me/bb9pe)
home automation system.

### Features

* Up to 16 independent security zones
* Unlimited number of sensors (contact, motion, fire, moisture) per zone
* Two arming modes - Away and Stay
* Optional entry and exit delays
* Zones can be designated as Interior (armed in Away mode only) Exterior (armed in both Away and Stay modes) and Alert (always armed)
* Zone bypass allows quickly exclude select zones
* Setting of an alarm can activate sirens, turn on switches, send push notifications and text messages
* Silent mode disables sirens and switches, but leaves push notifications and text messages on
* Panic button

Smart Alarm is armed and disarmed simply by setting SmartThings location 'mode'.
For example, you can set it up to arm in Away mode when the location mode is
set to 'Away' and to arm in Stay mode when the location mode is set to 'Night'.
Setting location to any other mode, for example 'Home', will automatically
disarm Smart Alarm.

This allows arming and disarming SmartAlarm using 'Hello Home' actions. For
example, 'Good Night!' action activates the 'Night' mode, thus automatically
arming Smart Alarm in Stay mode. Location modes can aslo be changed based on
presence or other criteria, giving you many different ways to arm and disarm
Smart Alarm.

### Screenshots

![](https://sites.google.com/site/statusbits/pictures/SmartAlarm1.jpg)

![](https://sites.google.com/site/statusbits/pictures/SmartAlarm2.jpg)

### Installation

Smart Alarm app is available in the "Safety & Security" section of the Shared
Smart Apps in [SmartThings IDE](https://graph.api.smartthings.com).

1. Go to "My SmartApps" section and click on the "+ New SmartApp" button on the right.
2. On the "New SmartApp" page, fill out mandatory "Name" and "Description" fields (it does not matter what you put there), then click the "Create" button at the bottom.
3. When a new app template opens in the IDE, click on the "Browse SmartApps" drop-down list in the upper right corner and select "Browse Shared SmartApps". A list of shared SmartApps will appear on the left side of the editor window.
4. Scroll down to "Safety & Security" section and click on it.
5. Select "Smart Alarm" app from the list and click the red "Overwrite" button in the bottom right corner.
6. Click the blue "Save" button above the editor window.
7. Click the "Publish" button next to it and select "For Me". You have now self-published your SmartApp.
8. Open SmartThings mobile app on iPhone or Android and go to the Dashboard.
9. Tap on the round "+" button and navigate to "My Apps" section by swiping the menu ribbon all the way to the left.
10. "Smart Alarm" app should be available in the list of SmartApps that appears below the menu ribbon. Tap it and follow setup instructions.
