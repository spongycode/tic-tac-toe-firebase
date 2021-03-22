package com.spongycode.tictactoe.ui.drawer

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.spongycode.tictactoe.EachBlog
import com.spongycode.tictactoe.R
import com.spongycode.tictactoe.adapter.BlogRvAdapter
import com.spongycode.tictactoe.ui.PhotoViewerActivity
import com.spongycode.tictactoe.utils.Helper
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {


    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private var linearLayoutManager: LinearLayoutManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        this.title = "Profile"


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

    @SuppressLint("SetTextI18n")
    private fun updateWriterImage(userid: String) {
        Glide.with(this).load(Helper.userlogged.imageurl).into(imageViewAvatar)
        textViewName.text = Helper.userlogged.fname + " " + Helper.userlogged.lname
        imageViewAvatar.setOnClickListener {
            val intent = Intent(this, PhotoViewerActivity::class.java)
            intent.putExtra("IMAGE_URL", Helper.userlogged.imageurl)
            this.startActivity(intent)
        }
    }


}