package com.spongycode.tictactoe.Notifications

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessagingService
import com.spongycode.tictactoe.utils.Helper.updateToken

class MyFirebaseInstanceId : FirebaseMessagingService()
{

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)

        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val refreshToken = FirebaseInstanceId.getInstance().token

        if (firebaseUser != null){
            updateToken(refreshToken)
        }

    }

}