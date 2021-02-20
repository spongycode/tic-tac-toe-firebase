package com.spongycode.tictactoe

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_home.*
import java.text.SimpleDateFormat
import java.util.*


class BlogRvAdapter(
        private val blogList: MutableList<EachBlog>,
        private val context: Context,
        private val firestoreDB: FirebaseFirestore)

    : RecyclerView.Adapter<BlogRvAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.blog_layout, parent, false)

        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val blog = blogList[position]

        holder.content.text = blog.content

        Glide.with(context).load(blog.image).into(holder.realimage)


        holder.realimage.setOnClickListener{
            val intent = Intent(context, PhotoViewerActivity::class.java)
            intent.putExtra("IMAGE_URL",blog.image)
            context.startActivity(intent)
        }

        val listDate = blog.timestamp!!.toDate().toString().split(":| ".toRegex()).map { it.trim() }
        val dateFinal = listDate[3] + ":" + listDate[4] + " on " + listDate[1] + " " + listDate[2]
        holder.postedtime.setText(dateFinal)

        // getting profile image and name (since name can be changed now real time) <author field to be removed now> for each blog init
        firestore.collection("users")
                .whereEqualTo("userid", blog.userid)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (data in task.result!!) {
                            val fname = data.toObject(UserDataClass::class.java).fname
                            val lname = data.toObject(UserDataClass::class.java).lname
                            holder.author.text = "$fname $lname"
                            val imageurl = data.toObject(UserDataClass::class.java).imageurl
                            Glide.with(context).load(imageurl).into(holder.profileimage)

                            holder.profileimage.setOnClickListener {
                                val intent = Intent(context, PhotoViewerActivity::class.java)
                                intent.putExtra("IMAGE_URL",imageurl)
                                context.startActivity(intent)
                            }
                        }
                    } else {
                        // to handle
                    }
                }
        // getting profile image for each blog end
    }

    override fun getItemCount(): Int {
        return blogList.size
    }

    inner class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        internal var author: TextView = view.findViewById(R.id.tvAuthor)
        internal var content: TextView = view.findViewById(R.id.tvContent)
        internal var realimage: ImageView = view.findViewById(R.id.blog_image_holder)
        internal var profileimage: ImageView = view.findViewById(R.id.blog_profile_image)
        internal var postedtime: TextView = view.findViewById(R.id.tvDate)
    }
}