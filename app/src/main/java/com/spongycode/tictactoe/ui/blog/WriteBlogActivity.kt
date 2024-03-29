package com.spongycode.tictactoe.ui.blog

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ImageDecoder
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.spongycode.tictactoe.EachBlog
import com.spongycode.tictactoe.R
import com.spongycode.tictactoe.utils.BitmapScaler
import com.spongycode.tictactoe.utils.Helper
import kotlinx.android.synthetic.main.activity_edit_blog.*
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_signin.*
import kotlinx.android.synthetic.main.activity_write_post.*
import java.io.ByteArrayOutputStream
import java.util.*


@Suppress("DEPRECATION")
class WriteBlogActivity : AppCompatActivity() {


    private lateinit var firestore: FirebaseFirestore
    private var storageReference: StorageReference? = null

    private val pickImage = 100
    private var downloadUri: Uri? = null
    private var imageRouteClear = true


    @RequiresApi(Build.VERSION_CODES.Q)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_post)


        this.title = "Blog"
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor(Helper.tablayout_color)))

        write_post_progressbar.isVisible = false
        storageReference = FirebaseStorage.getInstance().reference
        firestore = FirebaseFirestore.getInstance()


        Helper.buttonEffect(write_post_btn_post, "#C665F37D")



        Glide.with(this).load(Helper.userlogged.imageurl).into(write_post_profile_pic)


        write_post_upd_img.setOnClickListener {
            val gallery =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, pickImage)
        }

        write_post_btn_post.setOnClickListener {
            postToFirestore()
        }






        write_post_btn_post.isEnabled = false
        write_post_btn_post.alpha = .5f


        write_post_et_content.addTextChangedListener(object : TextWatcher {
            @SuppressLint("SetTextI18n")
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                val text = s.toString()
                val len = text.trimStart(' ').length
                var lenH = len
                if (len > 1024) {
                    lenH = 1024
                }
                val height: Int = (lenH * 200 / 1024)
                counter_text_size.text = "$len/1024"
                setDimensions(counter_st_live, height)
                val state = len in 1..1024 && imageRouteClear
                write_post_btn_post.isEnabled = state // trim <initial blank spaces not allowed>
                if (state) {
                    write_post_btn_post.alpha = 1f
                } else {
                    write_post_btn_post.alpha = .5f
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


    private fun postToFirestore() {
        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Uploading...")
        progressDialog.setMessage("Working on it, Please Wait")
        progressDialog.show()

        val millis = System.currentTimeMillis()

        val eachBlog = EachBlog(
            millis.toString(),
            write_post_et_content.text.toString().trim(),
            downloadUri.toString(),
            Helper.userlogged.uid,
            Timestamp.now(),
            millis
        )
        firestore.collection("blogs")
            .add(eachBlog)
            .addOnCompleteListener {
                findAndModify(millis)
            }

            .addOnFailureListener {
                progressDialog.hide()
                Toast.makeText(this, "post upload failed", Toast.LENGTH_LONG).show()
            }

    }


    // just to bypass docType.ADDED -> called everytime

    private fun findAndModify(millis: Long) {
        firestore.collection("blogs")
            .whereEqualTo("id", millis.toString())
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (sd in task.result!!) {
                        firestore.collection("blogs").document(sd.id)
                            .update("sysmillis", millis + 1)
                            .addOnCompleteListener {
                                Toast.makeText(this, "post upload success", Toast.LENGTH_LONG)
                                    .show()
                                finish()
                            }
                    }
                }
            }
    }

    @SuppressLint("ResourceAsColor")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == pickImage && data != null) {
            imageRouteClear = false
            write_post_btn_post.isEnabled= false
            write_post_progressbar.isVisible = true
            write_post_btn_post.alpha = .5f
            write_post_tv_add_image.text = "Uploading..."
            write_post_upd_img.isClickable = false

            val imageUri: Uri = data.data!!

            val ref = storageReference!!.child("pics/${System.currentTimeMillis()}")

            val extension: String =
                imageUri.toString().substring(imageUri.toString().lastIndexOf("."))

            val uploadTask: UploadTask
            uploadTask = if (extension.toLowerCase(Locale.ROOT).trim() != ".gif") {
                val  imageByteArray = getImageByteArray(imageUri)
                ref.putBytes(imageByteArray)
            } else {
                ref.putFile(imageUri)
            }

            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
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
                    write_post_tv_add_image.text = "Uploaded!!"

                    val text = write_post_et_content.text.toString()
                    val len = text.trimStart(' ').length

                    if (len in 1..1024) {
                        write_post_btn_post.alpha = 1f
                        write_post_btn_post.isEnabled = true

                    }

                    imageRouteClear = true
                }
            }
        }
    }

    private fun getImageByteArray(imageUri: Uri): ByteArray {
        val originalBitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(contentResolver, imageUri)
            ImageDecoder.decodeBitmap(source)
        } else {
            MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
        }
        val scaledBitmap = BitmapScaler.scaleToFitHeight(originalBitmap, 1000)
        val byteOutputStream = ByteArrayOutputStream()
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 25, byteOutputStream)
        return byteOutputStream.toByteArray()
    }


    private fun setDimensions(view: View, height: Int) {
        val params: ViewGroup.LayoutParams = view.layoutParams
        params.height = height
        view.layoutParams = params
    }

}
