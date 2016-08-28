//package com.fries.edoo.firebase;
//
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.content.Context;
//import android.content.Intent;
//import android.media.RingtoneManager;
//import android.net.Uri;
//import android.os.Bundle;
//import android.support.v4.app.NotificationCompat;
//import android.util.Log;
//
//import com.fries.edoo.R;
//import com.fries.edoo.activities.MainActivity;
//import com.fries.edoo.models.ItemLop;
//import com.google.firebase.messaging.FirebaseMessagingService;
//import com.google.firebase.messaging.RemoteMessage;
//
//import java.util.HashMap;
//import java.util.Map;
//
//public class MyFirebaseMessagingService extends FirebaseMessagingService {
//
//    private static final String TAG = "MyFirebaseMsgService";
//
//    /**
//     * Called when message is received.
//     *
//     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
//     */
//    @Override
//    public void onMessageReceived(RemoteMessage remoteMessage) {
//        sendNotification(remoteMessage.getData());
//        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
////        Log.d(TAG, "From: " + remoteMessage.getFrom());
//
//        // Check if message contains a data payload.
//        if (remoteMessage.getData().size() > 0) {
//            Log.d(TAG, "Message data payload: " + remoteMessage.getData().toString());
//        }
//
//        // Check if message contains a notification payload.
////        if (remoteMessage.getNotification() != null) {
////            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
////        }
//
//    }
//
//    /**
//     * Create and show a simple notification containing the received FCM message.
//     *
//     * @param msgData FCM message body received.
//     */
//    private void sendNotification(Map<String, String> msgData) {
//        String classId = msgData.get("class_id]");
//        String className = msgData.get("class_name]");
//        String teacherName = msgData.get("teacher_name]");
//        String classTitle = msgData.get("title]");
//
//        ItemLop itemLop = new ItemLop(className, classId, "", teacherName, 0);
//        Bundle b = new Bundle();
//        b.putSerializable("item_class", itemLop);
//
//        Intent intent = new Intent(this, MainActivity.class);
//        intent.putExtras(b);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
//                PendingIntent.FLAG_ONE_SHOT);
//
//        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
//                .setSmallIcon(R.mipmap.ic_noti)
//                .setContentTitle("Thông báo mới từ lớp " + className)
//                .setContentText("Gv " + teacherName + ": " + classTitle)
//                .setAutoCancel(true)
//                .setSound(defaultSoundUri)
//                .setContentIntent(pendingIntent);
//
//        NotificationManager notificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        notificationManager.notify((int) System.currentTimeMillis(), notificationBuilder.build());
//    }
//}
