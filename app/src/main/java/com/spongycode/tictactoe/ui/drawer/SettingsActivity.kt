package com.spongycode.tictactoe.ui.drawer

import android.app.ProgressDialog
import android.content.Context
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
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.spongycode.tictactoe.R
import com.spongycode.tictactoe.model.UserDataClass
import com.spongycode.tictactoe.ui.PhotoViewerActivity
import com.spongycode.tictactoe.utils.BitmapScaler
import com.spongycode.tictactoe.utils.Helper
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.activity_write_post.*
import java.io.ByteArrayOutputStream
import java.util.*

class SettingsActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private val pickImage = 100
    private var storageReference: StorageReference? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        this.title = "Settings"
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor(Helper.tablayout_color)))



        storageReference = FirebaseStorage.getInstance().reference

        firestore = FirebaseFirestore.getInstance()


        profile_progress_bar.visibility = GONE

        cl_name_change_data_entry.visibility = GONE




        profile_profile_pic_btn_change.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, pickImage)
        }

        cl_name_change_btn_abs.setOnClickListener { v ->
            if (profile_name_hint_cmd.hint.toString() == "(Tap to change)") {
                profile_name_hint_cmd.hint = "(Tap to cancel)"
                cl_name_change_data_entry.visibility = VISIBLE
            } else {
                getProfileNameAndPic("lname", "fname", "no")
                cl_name_change_data_entry.visibility = GONE
                profile_name_hint_cmd.hint = "(Tap to change)"
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.hideSoftInputFromWindow(v.windowToken, 0)
                profile_et_fname.setText("")
                profile_et_lname.setText("")

            }
        }

        profile_btn_rename.setOnClickListener { v ->
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(v.windowToken, 0)

            profile_btn_rename.visibility = GONE
            profile_progress_bar.visibility = VISIBLE
            changeName(profile_et_fname.text.toString(), profile_et_lname.text.toString())
        }

        profile_btn_rename.alpha = .5f
        profile_btn_rename.isEnabled = false

        profile_et_fname.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val state = s.toString().trim { it <= ' ' }.isNotEmpty()
                if (state) {
                    if (profile_et_lname.text.toString() != "") {
                        profile_btn_rename.alpha = 1f
                        profile_btn_rename.isEnabled = state
                    }
                } else {
                    getProfileNameAndPic("fname", "no", "no")
                    profile_btn_rename.alpha = .5f
                    profile_btn_rename.isEnabled = state
                }
                profile_tv_fname.text = profile_et_fname.text.toString()
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // no use for now
            }

            override fun afterTextChanged(s: Editable) {
                // no use for now
            }
        })

        profile_et_lname.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val state = s.toString().trim { it <= ' ' }.isNotEmpty()
                // trim <initial blank spaces not allowed>
                if (state) {
                    if (profile_et_fname.text.toString() != "") {
                        profile_btn_rename.alpha = 1f
                        profile_btn_rename.isEnabled = state
                    }
                } else {
                    getProfileNameAndPic("no", "lname", "no")
                    profile_btn_rename.alpha = .5f
                    profile_btn_rename.isEnabled = state
                }
                profile_tv_lname.text = profile_et_lname.text.toString()

            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // no use for now
            }

            override fun afterTextChanged(s: Editable) {
                // no use for now
            }
        })

        getProfileNameAndPic("fname", "lname", "imageurl")

    }

    private fun changeName(fname: String, lname: String) {
        val userRef = firestore.collection("users").document(Helper.userlogged.uid)
        Helper.userlogged.fname = fname
        Helper.userlogged.lname = lname
        userRef.update("fname", fname)
                .addOnCompleteListener {
                }
        userRef.update("lname", lname)
                .addOnCompleteListener {
                    getProfileNameAndPic("fname", "lname", "no")
                    profile_et_fname.setText("")
                    profile_et_lname.setText("")
                    profile_progress_bar.visibility = GONE
                    profile_btn_rename.visibility = VISIBLE
                    cl_name_change_data_entry.visibility = GONE
                    profile_name_hint_cmd.hint = "(Tap to change)"
                    Toast.makeText(this, "Name Updated", Toast.LENGTH_SHORT).show()
                }
    }


    private fun getProfileNameAndPic(fname: String, lname: String, imageurl: String) {
        firestore.collection("users")
                .whereEqualTo("userid", Helper.userlogged.uid)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (data in task.result!!) {
                            if (fname == "fname") {

                                profile_tv_fname.text = data.toObject(UserDataClass::class.java).fname
                            }

                            if (lname == "lname") {

                                profile_tv_lname.text = data.toObject(UserDataClass::class.java).lname
                            }

                            if (imageurl == "imageurl") {
                                Glide.with(this)
                                        .load(data.toObject(UserDataClass::class.java).imageurl)
                                        .into(findViewById(R.id.profile_profile_pic))
                            }

                            profile_profile_pic.setOnClickListener {
                                val intent = Intent(this, PhotoViewerActivity::class.java)
                                intent.putExtra(
                                        "IMAGE_URL",
                                        data.toObject(UserDataClass::class.java).imageurl
                                )
                                this.startActivity(intent)
                            }
                        }

                    }
                }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val progressDialog = ProgressDialog(this)
        if (resultCode == RESULT_OK && requestCode == pickImage && data != null && data.data != null) {
            val imageUri: Uri = data.data!!
            val extension: String =
                    imageUri.toString().substring(imageUri.toString().lastIndexOf("."))
            val ref = storageReference!!.child("profilepics/${System.currentTimeMillis()}")
            val uploadTask: UploadTask
            uploadTask = if (extension.toLowerCase(Locale.ROOT).trim() != ".gif") {
                progressDialog.setTitle("Uploading...")
                progressDialog.setMessage("Changing Profile Pic, Please Wait")
                progressDialog.show()

                val imageByteArray = getImageByteArray(imageUri)
                ref.putBytes(imageByteArray as ByteArray)
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
                    val downloadUri = task.result
                    Helper.userlogged.imageurl = downloadUri.toString()
                    firestore.collection("users").document(Helper.userlogged.uid)
                            .update("imageurl", downloadUri.toString())
                            .addOnCompleteListener {
                                progressDialog.hide()
                                Toast.makeText(this, "Profile Pic Updated", Toast.LENGTH_SHORT)
                                        .show()
                                profile_profile_pic.setImageURI(imageUri)

                            }
                }
            }
        }
    }

    private fun getImageByteArray(imageUri: Uri): Any {
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

}

