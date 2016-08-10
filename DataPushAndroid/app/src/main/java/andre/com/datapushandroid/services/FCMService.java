/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package andre.com.datapushandroid.services;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import andre.com.datapushandroid.ApplicationState;
import andre.com.datapushandroid.MainActivity;
import andre.com.datapushandroid.R;
import andre.com.datapushandroid.interfaces.EncryptionResponseInterface;
import andre.com.datapushandroid.tasks.EncryptionOperation;

import static andre.com.datapushandroid.MainActivity.MY_PREFS_NAME;


public class FCMService extends FirebaseMessagingService implements EncryptionResponseInterface {


    private static final String TAG = "FCMService";
    public final static String MY_ACTION = "SAVE_PUSH_NOTIFICATION";

    private String push_id = "";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.

        // Getting MessageId will return null for a majority of devices (not all of them).
        // This is a bug in Firebase and it will be fixed on 9.4+
        // You can check it at
        //
        // http://stackoverflow.com/questions/38007894/fcm-android-null-message-id
        //

        // push_id = remoteMessage.getMessageId();

        // I'm now changing the id to the sender of this message which also has an unique ID.

        push_id = remoteMessage.getFrom();
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        //Log.d(TAG, "Id: " + remoteMessage.getMessageId());

        Intent intent = new Intent();
        intent.setAction(MY_ACTION);
        intent.putExtra("MessageId", push_id);

        // Check if message contains a data payload.
        if (remoteMessage.getData() != null) {

            String body = remoteMessage.getData().get("body");

            intent.putExtra("Body", body);

            EncryptionOperation task = new EncryptionOperation();
            task.HashString(body, this);

            // Check if application is not visible to the user
            if (!ApplicationState.isActivityVisible()) {

                sendNotification(remoteMessage.getData().get("body"));

            }else
            {
                sendBroadcast(intent);
            }


        }

    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("FCM Message")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    @Override
    public void encrypted_push(String responseStr) {

        Log.i("Push Criptografado: ", responseStr);

        // Store Last Message Id and Body
        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();

        editor.putString("MessageId", push_id);
        editor.putString("Body", responseStr);
        editor.apply();

    }

    @Override
    public void decrypted_push(String responseStr) {

        Log.i("Push Descriptografado: ", responseStr);

    }
}
