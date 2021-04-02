package com.spongycode.tictactoe.ui.blog

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.spongycode.tictactoe.R
import com.spongycode.tictactoe.model.UserDataClass
import com.spongycode.tictactoe.utils.Helper
import kotlinx.android.synthetic.main.activity_edit_blog.*
import kotlinx.android.synthetic.main.activity_write_post.*

class EditBlogActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_blog)

        this.title = "Blog"
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor(Helper.tablayout_color)))

        Helper.buttonEffect(edit_post_btn_post, "#C665F37D")


        auth = Firebase.auth
        firestore = FirebaseFirestore.getInstance()
        val currentText = intent?.getStringExtra("currentText")
        val docId = intent?.getStringExtra("docId")


        Glide.with(this).load(Helper.userlogged.imageurl).into(edit_post_profile_pic)
        edit_post_et_content.setText(currentText)

        edit_post_btn_post.setOnClickListener {
            val progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Uploading...")
            progressDialog.setMessage("Working on it, Please Wait")
            progressDialog.show()

            firestore.collection("blogs").document(docId!!)
                .update("content", edit_post_et_content.text.toString())
                .addOnCompleteListener {
                    Toast.makeText(this, "Blog Updated", Toast.LENGTH_SHORT).show()
                    finish()
                }
        }

        edit_post_et_content.addTextChangedListener(object : TextWatcher {
            @SuppressLint("SetTextI18n")
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val text = s.toString()
                val len = text.trimStart(' ').length
                var lenH = len
                if (len > 1024){
                    lenH = 1024
                }
                val height: Int = (lenH*200/1024)
                edit_blog_counter_text_size.text = "$len/1024"
                setDimensions(edit_blog_counter_st_live, height)
                val state = len in 1..1024
                edit_post_btn_post.isEnabled = state // trim <initial blank spaces not allowed>
                if (state) {
                    edit_post_btn_post.alpha = 1f
                } else {
                    edit_post_btn_post.alpha = .5f
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

    private fun setDimensions(view: View, height: Int) {
        val params: ViewGroup.LayoutParams = view.layoutParams
        params.height = height
        view.layoutParams = params
    }
}