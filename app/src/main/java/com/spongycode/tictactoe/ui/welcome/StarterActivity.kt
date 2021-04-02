package com.spongycode.tictactoe.ui.welcome

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.spongycode.tictactoe.R
import com.spongycode.tictactoe.model.DataClassUserLogged
import com.spongycode.tictactoe.model.UserDataClass
import com.spongycode.tictactoe.utils.Helper


class StarterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_starter)
        this.supportActionBar?.hide()
        auth = Firebase.auth

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        firestore = FirebaseFirestore.getInstance()
        val currentUser: FirebaseUser? = auth.currentUser
        if (currentUser != null) {
            val uid = auth.currentUser?.uid.toString()
            Helper.userlogged = DataClassUserLogged(uid)
            firestore.collection("users")
                    .whereEqualTo("userid", uid)
                    .get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            for (data in task.result!!) {
                                Helper.userlogged.fname = data.toObject(UserDataClass::class.java).fname
                                Helper.userlogged.lname = data.toObject(UserDataClass::class.java).lname
                                Helper.userlogged.imageurl = data.toObject(UserDataClass::class.java).imageurl
                                val intent = Intent(this, HomeActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                startActivity(intent)
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

