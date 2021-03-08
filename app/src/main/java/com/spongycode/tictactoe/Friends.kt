package com.spongycode.tictactoe

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
import kotlinx.android.synthetic.main.fragment_friends.*

class Friends : Fragment() {

    val firestore = FirebaseFirestore.getInstance()
    private var linearLayoutManager: LinearLayoutManager? = null


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {


        loadAllFriends()

        firestore.collection("users")
                .addSnapshotListener { value, error ->
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
        return inflater.inflate(R.layout.fragment_friends, container, false)

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
                        rv_users?.setHasFixedSize(true);

                    } catch (ex: Exception) {
                        android.widget.Toast.makeText(
                                requireContext(),
                                ex.toString(),
                                android.widget.Toast.LENGTH_SHORT
                        )
                                .show()
                    }


                }
        // rv in tab fragment for all end    }
    }
}
