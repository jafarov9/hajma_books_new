package com.hajma.apps.hajmabooks.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.hajma.apps.hajmabooks.R;
import com.hajma.apps.hajmabooks.activity.HomeActivity;

import java.util.Random;

public class MyFirabaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.e("qwewqe", remoteMessage.getData().toString());

        showNotification(remoteMessage);
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.e("qwewqe", "Message");
    }

    private void showNotification(RemoteMessage remoteMessage) {
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "com.hajma.apps.hajmabooks";

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Notification",
                    NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.setDescription("DEV");
            notificationChannel.enableLights(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder notiBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);

        if(remoteMessage.getData() != null) {

            if(remoteMessage.getData().size() > 0) {

                String title = remoteMessage.getData().get("title");
                String message = remoteMessage.getData().get("message");
                String image_url = remoteMessage.getData().get("img_url");

                int bookId = Integer.parseInt(remoteMessage.getData().get("book_id"));

                Log.e("hghgh", remoteMessage.getData().toString());



                notiBuilder
                        .setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setSmallIcon(R.drawable.logo)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setContentIntent(getPendingIntent(bookId));

                ImageRequest imageRequest = new ImageRequest(image_url, new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        Log.e("qwewqe", "Onresponse");
                        notiBuilder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(response));
                        notiBuilder.setContentIntent(getPendingIntent(bookId));
                        notificationManager.notify(new Random().nextInt(), notiBuilder.build());
                    }
                }, 0, 0, null, Bitmap.Config.RGB_565, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("qwewqe", error.getMessage());
                    }
                });

                RequestQueue requestQueue = Volley.newRequestQueue(this);
                requestQueue.add(imageRequest);


                //notificationManager.notify(new Random().nextInt(), notiBuilder.build());

            }
        }

    }

    private PendingIntent getPendingIntent(int bookId) {
        // Create an Intent for the activity you want to start
        Intent resultIntent = new Intent(this, HomeActivity.class);
        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        //TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        //stackBuilder.addNextIntentWithParentStack(resultIntent);
        // Get the PendingIntent containing the entire back stack
        //resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        resultIntent.putExtra("bookId", bookId);
        resultIntent.putExtra("key", "dtFrag");
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 100, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        //PendingIntent pendingIntent = stackBuilder.getPendingIntent(100, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;

    }

}
