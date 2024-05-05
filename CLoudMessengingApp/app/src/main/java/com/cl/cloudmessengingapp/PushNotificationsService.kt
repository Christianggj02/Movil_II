package com.cl.cloudmessengingapp

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService

class PushNotificationsService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("TOKEN", token)
    }
}