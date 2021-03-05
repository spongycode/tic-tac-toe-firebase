package com.spongycode.tictactoe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_edit_blog.*
import kotlinx.android.synthetic.main.activity_write_post.*

class EditBlog : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_blog)

        auth = Firebase.auth
        firestore = FirebaseFirestore.getInstance()
        val currentText = intent?.getStringExtra("currentText")
        val docId = intent?.getStringExtra("docId")

        firestore.collection("users")
            .whereEqualTo("userid", auth.currentUser?.uid.toString())
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (data in task.result!!) {
                        val imageurl = data.toObject(UserDataClass::class.java).imageurl
                        Glide.with(this).load(imageurl).into(edit_post_profile_pic)
                        edit_post_et_content.setText(currentText)
                    }
                }
            }



        edit_post_btn_post.setOnClickListener {
            firestore.collection("blogs").document(docId!!)
                .update("content", edit_post_et_content.text.toString())
                .addOnCompleteListener {
                    Toast.makeText(this, "Blog Updated", Toast.LENGTH_SHORT).show()
                    finish()
                }
        }


        edit_post_et_content.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val state = s.toString().trim { it <= ' ' }.isNotEmpty()
                edit_post_btn_post.isEnabled = state // trim <initial blank spaces not allowed>
                if (state) {
                    edit_post_btn_post.setAlpha(1f)
                } else {
                    edit_post_btn_post.setAlpha(.5f);
                }

            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // no use for now
            }
            override fun afterTextChanged(s: Editable) {
                // no use for now
            }
        })




    }
}