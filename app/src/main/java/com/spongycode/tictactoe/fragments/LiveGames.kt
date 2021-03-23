package com.spongycode.tictactoe.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.spongycode.tictactoe.LiveGameData
import com.spongycode.tictactoe.R
import com.spongycode.tictactoe.adapter.LiveGameAdapter
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
        return inflater.inflate(R.layout.fragment_live_games, container, false)
    }

    private fun updateAll() {

        val gameList = mutableListOf<LiveGameData>()
        firestore.collection("allgames")
                .whereEqualTo("senderid", auth.currentUser?.uid.toString())
                .get()
                .addOnSuccessListener { documents ->
                    no_game_text?.visibility = GONE
                    no_game_image?.visibility = GONE


                    for (document in documents) {
                        val game = document.toObject(LiveGameData::class.java)
                        gameList.add(game)
                    }
                    if (gameList.isEmpty()){
                        no_game_text?.visibility = VISIBLE
                        no_game_image?.visibility = VISIBLE
                    }
                    linearLayoutManager = LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false)
                    rv_live_games?.layoutManager = linearLayoutManager
                    rv_live_games?.adapter = LiveGameAdapter(gameList, requireContext(), firestore) // Your adapter
                    rv_live_games?.setHasFixedSize(true)
                }



        firestore.collection("allgames")
                .whereEqualTo("receiverid", auth.currentUser?.uid.toString())
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {

                        val game = document.toObject(LiveGameData::class.java)
                        gameList.add(game)
                        linearLayoutManager = LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false)
                        rv_live_games?.layoutManager = linearLayoutManager
                        rv_live_games?.adapter = LiveGameAdapter(gameList, requireContext(), firestore) // Your adapter
                        rv_live_games?.setHasFixedSize(true)

                    }

                    if (gameList.isEmpty()){
                        no_game_text?.visibility = VISIBLE
                        no_game_image?.visibility = VISIBLE
                    }else{
                        no_game_text?.visibility = GONE
                        no_game_image?.visibility = GONE
                    }
                }


    }
}
