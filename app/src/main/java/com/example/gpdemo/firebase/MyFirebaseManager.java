package com.example.gpdemo.firebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.gpdemo.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class MyFirebaseManager {

    private static MyFirebaseManager instance = null;

    private FirebaseAnalytics firebaseAnalytics = null;

    private static String TAG = "MyFirebaseManager";

    private String token = "";

    private MyFirebaseCallback callback = null;

    private Context mContext = null;

    private String packName = "";

    public MyFirebaseManager(Context context) {
        this.mContext = context;
    }
    public static MyFirebaseManager getInstance(Context context){
        if (instance == null){
            instance = new MyFirebaseManager(context);
        }
        return instance;
    }

    public MyFirebaseCallback getCallback() {
        return callback;
    }

    public void setCallback( MyFirebaseCallback callback) {
        this.callback = callback;
        getFirebaseToken();
    }

    //是否开启数据收集
    public void setAnalyticsEnable(boolean enable){
        if (firebaseAnalytics == null){
            firebaseAnalytics = FirebaseAnalytics.getInstance(mContext);
        }
        firebaseAnalytics.setAnalyticsCollectionEnabled(enable);
    }
    //获取token令牌
    private void  getFirebaseToken(){
        Log.d(TAG,"getFirebaseToken");
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (!task.isSuccessful()){
                    Log.e(TAG,"获取firebase令牌失败");
                    token = "";
                    callback.onGetToken(false,"");
                }
                // Get new Instance ID token
                else {
                    token = task.getResult().getToken();
                    callback.onGetToken(true,token);
                    Log.e(TAG,"firebase令牌："+token);
                }
            }
        });
    }

    //自定义事件记录接口
    public void logCustomEvent(String eventName, Bundle bundle){
        if (firebaseAnalytics == null){
            firebaseAnalytics = FirebaseAnalytics.getInstance(mContext);
        }
        firebaseAnalytics.logEvent(eventName,bundle);
    }

    //预定义事件记录
    //注册事件：sign_up
    public void logSignUpEvent(String method){
        if (firebaseAnalytics == null){
            firebaseAnalytics = FirebaseAnalytics.getInstance(mContext);
        }
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.METHOD, method);
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SIGN_UP, bundle);
    }

    //登录事件：login
    public void logLoginEvent(String method){
        if (firebaseAnalytics == null){
            firebaseAnalytics = FirebaseAnalytics.getInstance(mContext);
        }
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.METHOD, method);
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle);
    }

    //分享事件：share
    public void logShareEvent(String contentType, String itemId){
        if (firebaseAnalytics == null){
            firebaseAnalytics = FirebaseAnalytics.getInstance(mContext);
        }
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, contentType);
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, itemId);
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SHARE, bundle);
    }

    //开始教程事件：tutorial_begin
    public void logBeginTutorialEvent(){
        if (firebaseAnalytics == null){
            firebaseAnalytics = FirebaseAnalytics.getInstance(mContext);
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.TUTORIAL_BEGIN, null);
    }

    //完成教程事件：tutorial_complete
    public void logCompleteTutorialEvent(){
        if (firebaseAnalytics == null){
            firebaseAnalytics = FirebaseAnalytics.getInstance(mContext);
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.TUTORIAL_COMPLETE, null);
    }

    //加入群组事件：join_group
    public void logJoinGroupEvent(String groupId){
        if (firebaseAnalytics == null){
            firebaseAnalytics = FirebaseAnalytics.getInstance(mContext);
        }
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.GROUP_ID, groupId);
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.JOIN_GROUP, bundle);
    }

    //支出游戏币事件：spend_virtual_currency
    public void logSpendEvent(String itemName, double value, String virtualCurrencyName){
        if (firebaseAnalytics == null){
            firebaseAnalytics = FirebaseAnalytics.getInstance(mContext);
        }
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, itemName);
        bundle.putDouble(FirebaseAnalytics.Param.VALUE, value);
        bundle.putString(FirebaseAnalytics.Param.VIRTUAL_CURRENCY_NAME, virtualCurrencyName);
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SPEND_VIRTUAL_CURRENCY, bundle);
    }

    //获得游戏币事件：earn_virtual_currency
    public void logEarnEvent(double value, String virtualCurrencyName){
        if (firebaseAnalytics == null){
            firebaseAnalytics = FirebaseAnalytics.getInstance(mContext);
        }
        Bundle bundle = new Bundle();
        bundle.putDouble(FirebaseAnalytics.Param.VALUE,value);
        bundle.putString(FirebaseAnalytics.Param.VIRTUAL_CURRENCY_NAME,virtualCurrencyName);
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.EARN_VIRTUAL_CURRENCY,bundle);
    }

    //升级事件：level_up
    public void logLevelEvent(String character ,long level){
        if (firebaseAnalytics == null){
            firebaseAnalytics = FirebaseAnalytics.getInstance(mContext);
        }
        Bundle bundle = new Bundle();
        bundle.putLong(FirebaseAnalytics.Param.LEVEL,level);
        bundle.putString(FirebaseAnalytics.Param.CHARACTER,character);
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.EARN_VIRTUAL_CURRENCY,bundle);
    }

    //解锁成就事件：unlock_achievement
    public void logUnlockAchievement(String achievementID){
        if (firebaseAnalytics == null){
            firebaseAnalytics = FirebaseAnalytics.getInstance(mContext);
        }
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ACHIEVEMENT_ID,achievementID);
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.UNLOCK_ACHIEVEMENT,bundle);
    }

    //发送消息栏推送
    private void sendNotification(Context context, String messageBody) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = "0";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, channelId)
//                        .setSmallIcon(ResourceId.drawable.hw_btn_claim_selector)
                        .setContentTitle("firebase标题")
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
