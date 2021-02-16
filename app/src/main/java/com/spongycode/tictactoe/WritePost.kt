package com.spongycode.tictactoe

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import com.aghajari.zoomhelper.ZoomHelper
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_write_post.*

class WritePost : AppCompatActivity() {


    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var firebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null

    private val pickImage = 100
    private var downloadUri: Uri? = null


    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_post)

        write_post_progressbar.isVisible = false
        firebaseStore = FirebaseStorage.getInstance()
        storageReference = FirebaseStorage.getInstance().reference
        auth = Firebase.auth
        val currentUser: FirebaseUser? = auth.currentUser
        val userid = currentUser?.uid
        firestore = FirebaseFirestore.getInstance()


        updateWriterImage(userid.toString())


        write_post_upd_img.setOnClickListener {
            val gallery =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, pickImage)
        }

        write_post_btn_post.setOnClickListener {
            postToFirestore()
        }


        write_post_btn_post.isEnabled = false
        write_post_btn_post.setAlpha(.5f);


        write_post_et_content.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val state = s.toString().trim { it <= ' ' }.isNotEmpty()
                write_post_btn_post.isEnabled = state // trim <initial blank spaces not allowed>
                if (state){
                    write_post_btn_post.setAlpha(1f)
                }else{
                    write_post_btn_post.setAlpha(.5f);

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

    private fun updateWriterImage(userid: String) {
        firestore.collection("users")
                .whereEqualTo("userid",userid)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (data in task.result!!) {
                            val imageurl = data.toObject(UserDataClass::class.java).imageurl
                            Glide.with(this).load(imageurl).into(write_post_profile_pic)
                        }
                    } else {
                        // to handle
                    }
                }
    }

    private fun postToFirestore() {
        firestore.collection("users")
                .whereEqualTo("userid", auth.currentUser?.uid.toString())
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (data in task.result!!) {
                            val fname = data.toObject(UserDataClass::class.java).fname
                            val lname = data.toObject(UserDataClass::class.java).lname
                            val eachBlog = EachBlog(write_post_et_content.text.toString(),
                                    downloadUri.toString(),
                                    "$fname $lname",
                                    auth.currentUser?.uid.toString())
                            firestore.collection("blogs")
                                    .add(eachBlog)
                                    .addOnSuccessListener {
                                        Toast.makeText(this, "post upload success", Toast.LENGTH_LONG).show()
                                        finish()

                                    }
                                    .addOnFailureListener { e -> Toast.makeText(this, "post upload failed", Toast.LENGTH_LONG).show() }
                        }
                    } else {
                        // to handle
                    }
                }


    }

    val REQUEST_CODE = 200


    @SuppressLint("ResourceAsColor")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == pickImage && data != null) {
            write_post_btn_post.isClickable=false
            write_post_progressbar.isVisible = true
            write_post_btn_post.setAlpha(.5f);
            write_post_tv_add_image.setText("Uploading...")
            write_post_upd_img.isClickable = false

            var imageUri: Uri = data.data!!
            //   iv_image.setImageURI(imageUri) Here you can assign the picked image uri to your imageview

            val ref = storageReference!!.child("pics/${System.currentTimeMillis()}")
            val uploadTask = ref?.putFile(imageUri)
            val urlTask =
                    uploadTask?.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                        if (!task.isSuccessful) {
                            task.exception?.let {
                                throw it
                            }
                        }
                        return@Continuation ref.downloadUrl
                    }).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            downloadUri = task.result
                            write_post_progressbar.isVisible = false
                            write_post_tv_add_image.setText("Uploaded!!")
                            write_post_btn_post.setAlpha(1f)
                            write_post_btn_post.isClickable=true
                        } else {
                            // Handle failures
                        }
                    }.addOnFailureListener {
                        // handle it
                    }
        }
    }


}
