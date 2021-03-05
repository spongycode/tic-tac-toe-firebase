package com.spongycode.tictactoe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_write_post.*
import kotlinx.android.synthetic.main.fragment_blogs.*

class ProfileActivity : AppCompatActivity() {


    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private var linearLayoutManager: LinearLayoutManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val userid = auth.currentUser?.uid.toString()

        updateWriterImage(userid)

        loadAllBlogs()



    }


    private fun loadAllBlogs() {
        firestore.collection("blogs")
                .orderBy("sysmillis", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { documents ->
                    try {
                        val blogList = mutableListOf<EachBlog>()
                        for (document in documents) {
                            val blog = document.toObject(EachBlog::class.java)
                            if (blog.userid == auth.currentUser?.uid.toString()) {
                                blogList.add(blog)
                            }

                        }
                        tv_myblogs.text = "My Blogs (${blogList.size})"

                        linearLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
                        recyclerViewStatus?.layoutManager = linearLayoutManager
                        recyclerViewStatus?.adapter = BlogRvAdapter(blogList, this,"profile", firestore) // Your adapter
                        val adapter = recyclerViewStatus?.adapter
                        adapter?.notifyDataSetChanged()
                        recyclerViewStatus?.setHasFixedSize(true);
                    } catch (ex: Exception) {
                        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                    }

                }
                .addOnFailureListener { exception ->
                    // error handle
                }
    }

    private fun updateWriterImage(userid: String) {
        firestore.collection("users")
                .whereEqualTo("userid", userid)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (data in task.result!!) {
                            val imageurl = data.toObject(UserDataClass::class.java).imageurl
                            Glide.with(this).load(imageurl).into(imageViewAvatar)
                            val name =
                                    data.toObject(UserDataClass::class.java).fname + " " + data.toObject(
                                            UserDataClass::class.java
                                    ).lname
                            textViewName.text = name

                            imageViewAvatar.setOnClickListener {
                                val intent = Intent(this, PhotoViewerActivity::class.java)
                                intent.putExtra("IMAGE_URL", imageurl)
                                this.startActivity(intent)
                            }
                        }


                    } else {
                        // to handle
                    }
                }
    }


}