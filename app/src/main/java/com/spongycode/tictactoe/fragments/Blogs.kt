package com.spongycode.tictactoe.fragments


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.spongycode.tictactoe.EachBlog
import com.spongycode.tictactoe.R
import com.spongycode.tictactoe.adapter.BlogRvAdapter
import com.spongycode.tictactoe.ui.blog.WriteBlogActivity
import kotlinx.android.synthetic.main.fragment_blogs.*

class Blogs : Fragment() {

    private lateinit var firestore: FirebaseFirestore
    private var linearLayoutManager: LinearLayoutManager? = null
    private var firestoreListener: ListenerRegistration? = null

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        firestore = FirebaseFirestore.getInstance()
        val view = inflater.inflate(R.layout.fragment_blogs, container, false);
        val button = view.findViewById<FloatingActionButton>(R.id.blogs_floating_button)
        button.setOnClickListener {
            startActivity(Intent(context, WriteBlogActivity::class.java))
        }

        // rv in tab fragment for all blogs init
        loadAllBlogs()
        firestore.collection("users")
                .addSnapshotListener { snapshot, e ->
                    loadAllBlogs()
                }
        // rv in tab fragment for all blogs end

        firestore.collection("blogs")
                .addSnapshotListener { snapshot, e ->
                    loadAllBlogs()
                }
        // Inflating layout Blogs Fragment
        return view
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
                            blog.id = document.id
                            blogList.add(blog)
                        }

                        linearLayoutManager = LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false)
                        rv_blogs?.layoutManager = linearLayoutManager
                        rv_blogs?.adapter = BlogRvAdapter(blogList, requireContext(), "blogs", firestore) // Your adapter
                        val adapter = rv_blogs?.adapter
                        adapter?.notifyDataSetChanged()
                        rv_blogs?.setHasFixedSize(true)
                        rv_blogs?.attachFab(blogs_floating_button, activity as AppCompatActivity)

                    } catch (ex: Exception) {
                        Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
                    }
                }

    }

    private fun RecyclerView.attachFab(fab: FloatingActionButton, activity: AppCompatActivity) {
        this.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    fab.hide()

                } else if (dy < 0){
                    fab.show()
                    activity.supportActionBar!!.show()

                }
            }
        })
    }


}