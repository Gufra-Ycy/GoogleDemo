package com.example.gpdemo;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.gpdemo.firebase.MyFirebaseCallback;
import com.example.gpdemo.firebase.MyFirebaseManager;
import com.google.firebase.messaging.RemoteMessage;

public class MainActivity extends Activity{
    private static String TAG = "GP.Billing";
    private EditText txtGoods;
    private EditText txtAmount;
    private Button btnPay;

    private String productId = "test";

    private  GooglePlayBillingManager GPManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GPManager = GooglePlayBillingManager.getInstance(this);
        initView();

        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if (GPManager.setUpGP(MainActivity.this)){
                    GPManager.queryGoods(productId);
               }
            }
        });

        MyFirebaseManager.getInstance(this).setCallback(new MyFirebaseCallback() {
            @Override
            public void onGetToken(boolean isSuccess, String token) {
               if (isSuccess){
                   Log.d(TAG,"token"+token);
               }else{
                   Log.e(TAG,"获取token失败");
               }

            }

            @Override
            public void onNewToken(String token) {
                Log.d(TAG,"refreshToken"+token);
            }

            @Override
            public void onMessageReceived(RemoteMessage firebMessage) {
                Log.d(TAG,"msgBody:"+firebMessage.getNotification().getBody());
                sendNotification(firebMessage.getNotification().getBody());
            }

            @Override
            public void onMessageSent(String s) {

            }

            @Override
            public void onDeletedMessages() {

            }
        });
    }

    private void initView() {
        txtGoods = findViewById(R.id.txt_goods);
        txtAmount = findViewById(R.id.txt_amount);
        btnPay = (findViewById(R.id.btn_pay1));
        productId = String.valueOf(txtGoods.getText());

    }
    //发送消息栏推送
    private void sendNotification(String messageBody) {

        Intent intent = new Intent(this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = "0";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setContentTitle("firebase标题")
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

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
