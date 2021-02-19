package com.spongycode.tictactoe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_friends.*
import kotlinx.android.synthetic.main.fragment_live_games.*


class LiveGames : Fragment() {

    val firestore = FirebaseFirestore.getInstance()
    private var linearLayoutManager: LinearLayoutManager? = null
    private lateinit var auth: FirebaseAuth


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        auth = Firebase.auth
        updateAll()


        firestore.collection("allgames")
                .addSnapshotListener { snapshot, e ->
                    if (snapshot != null) {
                        updateAll()
                    }
                }


        // Inflating layout live matches (ongoing games) Fragment
        return inflater.inflate(R.layout.fragment_live_games, container, false)
    }

    private fun updateAll() {
        // rv in live games fragment for all current games init
        val gameList = mutableListOf<LiveGameData>()
        firestore.collection("allgames")
                .whereEqualTo("senderid", auth.currentUser?.uid.toString())
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
//                        Log.d(TAG, "${document.id} => ${document.data}")

                        val game = document.toObject(LiveGameData::class.java)
                        gameList.add(game)
                        linearLayoutManager = LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false)
                        rv_live_games?.layoutManager = linearLayoutManager
                        rv_live_games?.adapter = LiveGameAdapter(gameList, requireContext(), firestore) // Your adapter
                        rv_live_games?.setHasFixedSize(true);
                    }
                }
                .addOnFailureListener { exception ->
//                    Log.w(TAG, "Error getting documents: ", exception)
                }



        firestore.collection("allgames")
                .whereEqualTo("receiverid", auth.currentUser?.uid.toString())
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
//                        Log.d(TAG, "${document.id} => ${document.data}")

                        val game = document.toObject(LiveGameData::class.java)
                        gameList.add(game)
                        linearLayoutManager = LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false)
                        rv_live_games?.layoutManager = linearLayoutManager
                        rv_live_games?.adapter = LiveGameAdapter(gameList, requireContext(), firestore) // Your adapter
                        rv_live_games?.setHasFixedSize(true);
                    }
                }
                .addOnFailureListener { exception ->
//                    Log.w(TAG, "Error getting documents: ", exception)
                }


    }
}
