package com.example.gpdemo.firebase;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static String TAG = "MyFirebaseMessagingService";
    private static String messageBody;
    private MyFirebaseCallback callback;

    @Override
    public void onCreate() {
        super.onCreate();
        this.callback = MyFirebaseManager.getInstance(getApplicationContext()).getCallback();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getNotification() != null){
            messageBody=remoteMessage.getNotification().getBody();
            Log.e(TAG,"消息from:"+remoteMessage.getFrom());
            Log.e(TAG,"消息data:"+remoteMessage.getData());
            Log.e(TAG,"消息Title:" +remoteMessage.getNotification().getTitle());
            Log.e(TAG,"消息messageBody:"+messageBody);
            Log.e(TAG,"消息clickAction:"+remoteMessage.getNotification().getClickAction());

            if (callback != null){
                callback.onMessageReceived(remoteMessage);
            }
        }
    }

    @Override
    public void onNewToken(String token) {
        Log.e(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        if (callback != null){
            callback.onNewToken(token);
        }

    }

    @Override
    public void onDeletedMessages() {
        Log.d(TAG,"onDeletedMessages");
        if (callback != null){
            callback.onDeletedMessages();
        }

    }

    @Override
    public void onMessageSent(String s) {
        Log.d(TAG,"onMessageSent:"+s);
        if (callback != null){
            callback.onMessageSent(s);
        }

    }



    public static String getMetaData(Context context, String dataName) {
        try {
            Log.d("QGSdkUtils","getMetaData");
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            if (info.metaData != null) {
                if(info.metaData.containsKey(dataName))
                {
                    String metaData = info.metaData.getString(dataName);
                    Log.d("QGSdkUtils", "metaData is:" + metaData);
                    return metaData;
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG,e.getMessage());
        }
        return "unknown";
    }

}
