package com.spongycode.tictactoe.ui.welcome

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.spongycode.tictactoe.R
import com.spongycode.tictactoe.model.UserDataClass
import com.spongycode.tictactoe.utils.Helper
import kotlinx.android.synthetic.main.activity_signin.btn_sign_up
import kotlinx.android.synthetic.main.activity_signup.*

@Suppress("DEPRECATION")
class SignupActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        auth = Firebase.auth
        this.supportActionBar?.hide()

        val profileImages = listOf("cat", "croc","dino","mong", "wolf")
        var imageUrlProfile = ""

        Helper.buttonEffect(btn_sign_up, "#C665F37D")

        when(profileImages.random()){
            "cat" -> {
                imageUrlProfile = "https://firebasestorage.googleapis.com/v0/b/tic-tac-toe-fb8df.appspot.com/o/profilepics%2Fcat.png?alt=media&token=894a2674-ee8d-449d-a8d1-8867a3ab1057"
                signup_profile_iv.setImageResource(R.drawable.cat)
            }
            "croc" -> {
                imageUrlProfile = "https://firebasestorage.googleapis.com/v0/b/tic-tac-toe-fb8df.appspot.com/o/profilepics%2Fcroc.png?alt=media&token=2f1d77a3-4c1e-4ef8-8363-bafdd5600724"
                signup_profile_iv.setImageResource(R.drawable.croc)

            }
            "dino" -> {
                imageUrlProfile = "https://firebasestorage.googleapis.com/v0/b/tic-tac-toe-fb8df.appspot.com/o/profilepics%2Fdino.png?alt=media&token=10520084-fded-453f-b7e7-bf4c3f7b84ce"
                signup_profile_iv.setImageResource(R.drawable.dino)

            }
            "mong" -> {
                imageUrlProfile = "https://firebasestorage.googleapis.com/v0/b/tic-tac-toe-fb8df.appspot.com/o/profilepics%2Fmong.png?alt=media&token=e590ed0d-ab8c-46da-86ec-6f527daabd97"
                signup_profile_iv.setImageResource(R.drawable.mong)

            }
            "wolf" -> {
                imageUrlProfile = "https://firebasestorage.googleapis.com/v0/b/tic-tac-toe-fb8df.appspot.com/o/profilepics%2Fwolf.png?alt=media&token=7890f965-2fc4-4419-8c02-79c3c66f8e0c"
                signup_profile_iv.setImageResource(R.drawable.wolf)

            }
        }


        btn_sign_up.setOnClickListener {
            signUpUser(imageUrlProfile)
        }

    }

    private fun signUpUser(imageurl : String) {
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


        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Registering ...")
        progressDialog.setMessage("Working on it, Please Wait")
        progressDialog.show()

        auth.createUserWithEmailAndPassword(tv_signup_email.text.toString(), tv_signup_password.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    // Accessing Firestore
                    val userid = auth.currentUser?.uid.toString()
                    firestore = FirebaseFirestore.getInstance()
                    firestore.collection("users").document(auth.currentUser?.uid.toString())
                        .set(UserDataClass(tv_signup_fname.text.toString(),tv_signup_lname.text.toString(),tv_signup_email.text.toString(),userid,imageurl))
                        .addOnSuccessListener { Log.d("BlogActivity", "DocumentSnapshot successfully written!") }
                        .addOnFailureListener { e -> Log.w("BlogActivity", "Error writing document", e) }
                    startActivity(Intent(this, StarterActivity::class.java))
                    finish()
                } else {
                    progressDialog.hide()
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }

    }
}