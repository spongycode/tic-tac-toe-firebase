package com.spongycode.tictactoe.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.spongycode.tictactoe.R
import com.spongycode.tictactoe.adapter.UserAdapter
import com.spongycode.tictactoe.model.UserDataClass
import kotlinx.android.synthetic.main.fragment_friends.*

class Friends : Fragment() {

    val firestore = FirebaseFirestore.getInstance()
    private var linearLayoutManager: LinearLayoutManager? = null
    var viewLoad: View? = null


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        if (viewLoad == null) {

            viewLoad = inflater.inflate(R.layout.fragment_friends, container, false)

            loadAllFriends()
        }

        firestore.collection("users")
                .addSnapshotListener { snapshots, e ->
                    loadAllFriends()
                }

        firestore.collection("allgames")
                .whereEqualTo("gamestat", "start")
                .addSnapshotListener { snapshots, e ->
                    if (e != null) {
                        return@addSnapshotListener
                    }
                    for (dc in snapshots!!.documentChanges) {
                        when (dc.type) {
                            DocumentChange.Type.REMOVED -> {
                                loadAllFriends()
                            }
                        }
                    }
                }

        firestore.collection("allgames")
                .whereEqualTo("gamestat", "notstart")
                .addSnapshotListener { snapshots, e ->
                    if (e != null) {
                        return@addSnapshotListener
                    }
                    for (dc in snapshots!!.documentChanges) {
                        when (dc.type) {
                            DocumentChange.Type.REMOVED -> {
                                loadAllFriends()
                            }
                        }
                    }
                }


        // Inflating layout All Users Fragment

        return viewLoad

    }

    private fun loadAllFriends() {
        // rv in tab fragment for all users init
        firestore.collection("users")
                .orderBy("fname", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener { documents ->
                    try {
                        val userList = mutableListOf<UserDataClass>()
                        for (document in documents) {
                            val user = document.toObject(UserDataClass::class.java)
                            userList.add(user)
                        }
                        linearLayoutManager =
                                GridLayoutManager(getActivity(), 2, RecyclerView.VERTICAL, false)
                        rv_users?.layoutManager = linearLayoutManager
                        rv_users?.adapter =
                                UserAdapter(userList, requireContext(), firestore) // Your adapter
                        rv_users?.setHasFixedSize(true)
                        swiperefreshFriends.isRefreshing = false


                    } catch (ex: Exception) {
//                        android.widget.Toast.makeText(
//                                requireContext(),
//                                ex.toString(),
//                                android.widget.Toast.LENGTH_SHORT
//                        )
//                                .show()
                    }


                }
        // rv in tab fragment for all end    }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swiperefreshFriends.setOnRefreshListener {
            loadAllFriends()
        }
    }
}
