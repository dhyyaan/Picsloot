package com.think360.picsloot.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.think360.picsloot.R;
import com.think360.picsloot.activities.PicsLootActivity;
import com.think360.picsloot.api.AppController;
import com.think360.picsloot.api.EventToRefresh;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by Sukhjot on 6/14/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d(TAG, "FROM:" + remoteMessage.getFrom());

        //Check if the message contains data
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data: " + remoteMessage.getData());

            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                sendNotification(json);
            } catch (JSONException e) {
              //  Toast.makeText(getApplicationContext(),"noti1 "+e, Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

        }

        //Check if the message contains com.think360.cmg.notification
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Mesage body:" + remoteMessage.getNotification().getBody());
            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                sendNotification(json);


            } catch (JSONException e) {
                e.printStackTrace();
               // Toast.makeText(getApplicationContext(),"noti2 "+e, Toast.LENGTH_SHORT).show();
            }

        }
    }

    /**
     * Dispay the com.think360.cmg.notification
     *
     * @param body
     */
    private void sendNotification(JSONObject body) {

        String msg = "";
        try {

            msg = body.getString("message");
            ((AppController) getApplication()).bus().send(new EventToRefresh(1001));
            PicsLootActivity.Companion.getPicsLootActivity().updateNotiCount(Integer.parseInt(body.getString("count")));

        } catch (JSONException e) {
            e.printStackTrace();
           // Toast.makeText(getApplicationContext(),"noti3 "+e, Toast.LENGTH_SHORT).show();
        }
        Intent intent = new Intent(this, PicsLootActivity.class);
        intent.putExtra("TO_OPEN", R.id.ivNotification);

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0/*Request code*/, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //Set sound of com.think360.cmg.notification
        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notifiBuilder = null;


            notifiBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Picsloot")
                    .setContentText(msg)
                    .setAutoCancel(true)
                    .setSound(notificationSound)
                    .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0 /*ID of com.think360.cmg.notification*/, notifiBuilder.build());


    }
}
