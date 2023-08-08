package com.orot.menuboss_tv

import android.content.Context
import android.util.Log
//import com.amazon.A3L.messaging.A3LMessagingService
//import com.amazon.A3L.messaging.RemoteMessage
//
//class MyA3LMessagingService : A3LMessagingService() {
//    override fun onMessageReceived(context: Context, remoteMessage: RemoteMessage) {
//        try {
//            Log.w(TAG, "getNotification: " + remoteMessage.notification.title)
//        } catch (e: Exception) {
//            Log.w(TAG, "getNotification: " + e.message)
//        }
//        try {
//            Log.w(TAG, "getNotification: " + remoteMessage.notification.body)
//        } catch (e: Exception) {
//            Log.w(TAG, "getNotification: " + e.message)
//        }
//        try {
//            Log.w(TAG, "getData: " + remoteMessage.data)
//        } catch (e: Exception) {
//            Log.w(TAG, "getData: " + e.message)
//        }
//        try {
//            Log.w(TAG, "getRemoteMessageType: " + remoteMessage.remoteMessageType)
//        } catch (e: Exception) {
//            Log.w(TAG, "getRemoteMessageType: " + e.message)
//        }
//    }
//
//    override fun onNewToken(context: Context, token: String) {
//        Log.d(TAG, "In onNewDeviceId")
//        Log.d(TAG, "Device token: $token")
//    }
//
//    companion object {
//        private const val TAG = "MyA3LMessagingService"
//    }
//}