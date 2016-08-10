# data-push-android
=====

Data Push is an application to receive push notifications in Android using FCM.

Documentation
====

Features
------

- Receive data when application is running;

- Receive data when application is at foreground;

- Receive data when application is at background;

- Encrypt received push messages;

- Store encrypted push messages locally;

- Retrieve the last push message and decrypt it;

- Display Message ID (Unique Identifier);

Environment
------

First of all you'll need to include Postman to your Chrome browser in order to send the push notification.

You can get Postman at https://www.getpostman.com/

Then, download the collection: https://www.getpostman.com/collections/e34d476e5731c1f0eef9

It must look like a data-message:

````{ "to" : "<YOUR_APPLICATION_TOKEN>", "priority" : "high", "data" : { "body" : "Push Message Text" } } ````


Important Notes
------

Sometimes Firebase Messaging can be picky: you'll notice a delay when sending the push notification.

If you try to send the notification and it doesn't arrive, wait a few minutes and try again.

Thank you!
