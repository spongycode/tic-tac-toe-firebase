package com.spongycode.tictactoe

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_signin.*

class SigninActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        auth = Firebase.auth


        btn_log_in.setOnClickListener {
            loginUser()
        }

        btn_sign_up.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }

    private fun loginUser() {
        if (tv_signin_email.text.toString().isEmpty()) {
            tv_signin_email.error = "Please enter Email"
            tv_signin_email.requestFocus()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(tv_signin_email.text.toString()).matches()) {
            tv_signin_email.error = "Please enter valid Email"
            tv_signin_email.requestFocus()
            return
        }

        if (tv_signin_password.text.toString().isEmpty()) {
            tv_signin_password.error = "Please enter Password"
            tv_signin_password.requestFocus()
            return
        }

        auth.signInWithEmailAndPassword(tv_signin_email.text.toString(), tv_signin_password.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        startActivity(Intent(this, HomeActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(
                                baseContext, "Authentication failed.",
                                Toast.LENGTH_SHORT
                        ).show()
                    }
                }
    }


    public override fun onStart() {
        super.onStart()
        val currentUser: FirebaseUser? = auth.currentUser
        if (currentUser != null) {
            LoggingYou(currentUser)
        }


    }

    fun LoggingYou(currentUser: FirebaseUser?) {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()


    }

}