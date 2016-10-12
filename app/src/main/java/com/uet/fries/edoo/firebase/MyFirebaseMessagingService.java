package com.uet.fries.edoo.firebase;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.uet.fries.edoo.R;
import com.uet.fries.edoo.activities.MainActivity;
import com.uet.fries.edoo.models.ItemLop;
import com.uet.fries.edoo.models.ItemTimeLine;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        sendNotification(remoteMessage.getData());
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
//        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData().toString());
        }

        // Check if message contains a notification payload.
//        if (remoteMessage.getNotification() != null) {
//            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
//        }

    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param msgData FCM message body received.
     */
    private void sendNotification(Map<String, String> msgData) {
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);

        String notiType = msgData.get("type]");

        if (notiType != null){
            if (notiType.equalsIgnoreCase("teacher_post")){
                String classId = msgData.get("class_id]");
                String className = msgData.get("class_name]");
                String teacherName = msgData.get("teacher_name]");
                String classTitle = msgData.get("title]");

                ItemLop itemLop = new ItemLop(className, classId, "", teacherName, 0);
                Bundle b = new Bundle();
                b.putSerializable("item_class", itemLop);
                b.putString("type", "teacher_post");

                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtras(b);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                        PendingIntent.FLAG_ONE_SHOT);
                notificationBuilder.setContentTitle("Thông báo mới từ lớp " + className)
                        .setContentText("Gv " + teacherName + ": " + classTitle)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

            } else if (notiType.equalsIgnoreCase("comment")){
                String postId = msgData.get("post_id]");
                boolean isIncognito = Boolean.parseBoolean(msgData.get("is_incognito]"));
                String content = msgData.get("content]");
                String name = "Ẩn danh";

                if (!isIncognito){
                    name = msgData.get("name]");
                }

                ItemTimeLine itemTimeLine = new ItemTimeLine();
                itemTimeLine.setIdPost(postId);
                Bundle b = new Bundle();
                b.putSerializable("item_timeline", itemTimeLine);
                b.putString("type", "comment");

                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtras(b);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                        PendingIntent.FLAG_ONE_SHOT);
                notificationBuilder.setContentTitle("Bạn nhận được câu trả lời mới!")
                        .setContentText(name + ": " + content)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);
            }
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
            notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
        } else {
            notificationBuilder.setSmallIcon(R.mipmap.ic_stat_ic_noti)
                    .setColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }

        PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(
                PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP,
                TAG);

        wl.acquire();
        wl.release();

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
//            if (!pm.isInteractive()){
//                wl.acquire();
//                wl.release();
//            }
//        } else {
//            if (!pm.isScreenOn()){
//                wl.acquire();
//                wl.release();
//            }
//        }

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify((int) System.currentTimeMillis(), notificationBuilder.build());
    }
}
