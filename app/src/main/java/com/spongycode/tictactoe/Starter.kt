package com.spongycode.tictactoe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase


class Starter : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_starter)
        this.supportActionBar?.hide()
        auth = Firebase.auth
        firestore = FirebaseFirestore.getInstance()
        val currentUser: FirebaseUser? = auth.currentUser
        if (currentUser != null) {
            val uid = auth.currentUser?.uid.toString()
            Utils.userlogged = UserLogged(uid)
            firestore.collection("users")
                    .whereEqualTo("userid", uid)
                    .get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            for (data in task.result!!) {
                                Utils.userlogged.fname = data.toObject(UserDataClass::class.java).fname
                                Utils.userlogged.lname = data.toObject(UserDataClass::class.java).lname
                                Utils.userlogged.imageurl = data.toObject(UserDataClass::class.java).imageurl
                                startActivity(Intent(this, HomeActivity::class.java))
                                finish()
                            }
                        }
                    }
        } else {
            startActivity(Intent(this, SigninActivity::class.java))
            finish()
        }
    }
}

