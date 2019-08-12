package com.example.gpdemo.firebase;

import com.google.firebase.messaging.RemoteMessage;

public interface MyFirebaseCallback {
    void onGetToken(boolean isSuccess, String token);
    void onNewToken(String token);
    void onMessageReceived(RemoteMessage firebMessage);
    void onMessageSent(String s);
    void onDeletedMessages();
}
