package com.spongycode.tictactoe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_signin.*
import kotlinx.android.synthetic.main.activity_signin.btn_sign_up
import kotlinx.android.synthetic.main.activity_signup.*

class SignupActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        auth = Firebase.auth

        btn_sign_up.setOnClickListener {
            signUpUser()
        }

    }

    private fun signUpUser() {
        if (tv_signup_fname.text.toString().isEmpty()) {
            tv_signup_fname.error = "Field Required"
            tv_signup_fname.requestFocus()
            return
        }
        if (tv_signup_lname.text.toString().isEmpty()) {
            tv_signup_lname.error = "Field Required"
            tv_signup_lname.requestFocus()
            return
        }



        if (tv_signup_email.text.toString().isEmpty()) {
            tv_signup_email.error = "Please enter Email"
            tv_signup_email.requestFocus()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(tv_signup_email.text.toString()).matches()) {
            tv_signup_email.error = "Please enter valid Email"
            tv_signup_email.requestFocus()
            return
        }

        if (tv_signup_password.text.toString().isEmpty()) {
            tv_signup_password.error = "Please enter Password"
            tv_signup_password.requestFocus()
            return
        }

        auth.createUserWithEmailAndPassword(tv_signup_email.text.toString(), tv_signup_password.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    // Accessing Firestore
                    val userid = auth.currentUser?.uid.toString()

                    firestore = FirebaseFirestore.getInstance()

                    firestore.collection("users").document(auth.currentUser?.uid.toString())
                        .set(UserDataClass(tv_signup_fname.text.toString(),tv_signup_lname.text.toString(),tv_signup_email.text.toString(),userid))
                        .addOnSuccessListener { Log.d("BlogActivity", "DocumentSnapshot successfully written!") }
                        .addOnFailureListener { e -> Log.w("BlogActivity", "Error writing document", e) }


                    startActivity(Intent(this, SigninActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }



    }
}