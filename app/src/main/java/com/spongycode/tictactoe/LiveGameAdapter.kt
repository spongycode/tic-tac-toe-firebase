package com.spongycode.tictactoe

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase


class LiveGameAdapter(
        private val gameList: MutableList<LiveGameData>,
        private val context: Context,
        private val firestoreDB: FirebaseFirestore)
    : RecyclerView.Adapter<LiveGameAdapter.ViewHolder>() {


    val auth = Firebase.auth

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.live_game_layout, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val game = gameList[position]
        // getting profile image and name of opponent init
        var id = ""

        if (game.senderid != auth.currentUser?.uid.toString()) {
            id = game.senderid!!
        } else {
            id = game.receiverid!!
        }
        firestore.collection("users")
                .whereEqualTo("userid", id)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (data in task.result!!) {
                            holder.live_fname.text = data.toObject(UserDataClass::class.java).fname
                            holder.live_lname.text = data.toObject(UserDataClass::class.java).lname
                            val imageurl = data.toObject(UserDataClass::class.java).imageurl
                            Glide.with(context).load(imageurl).into(holder.live_imageurl)
                        }
                    } else {
                        // to handle
                    }
                }


        val tempArrayPlayers = arrayOf(com.spongycode.tictactoe.auth.currentUser?.uid.toString(), id)
        tempArrayPlayers.sort()
        val gameid = tempArrayPlayers[0] + "@" + tempArrayPlayers[1]


        // getting profile image of opponent end


        // assign state of button <continue, accept, wait> on button init
        firestore.collection("allgames")
                .whereEqualTo("gameid", gameid)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        if (document.get("senderid") == id && document.get("gamestat") == "notstart") {
                            holder.live_gamestate_btn.setText("Accept")
                        }
                        if (document.get("receiverid") == id && document.get("gamestat") == "notstart") {
                            holder.live_gamestate_btn.isEnabled = false
                            holder.live_gamestate_btn.setText("Wait")
                            holder.live_gamestate_btn.alpha = 0.5f
                        }
                        if (document.get("gamestat") != "notstart") {
                            holder.live_gamestate_btn.setText("Continue")
                        }
                    }
                }


        // assign state of button <continue, accept, wait> on button end


        holder.live_gamestate_btn.setOnClickListener {
            val intent = Intent(context, TttInterface::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("opponentid", id)
            intent.putExtra("gamemode", "online")
            context.startActivity(intent)
        }


//        holder.close_btn.setOnClickListener {
//            firestore.collection("allgames").document(gameid)
//                    .delete()
//                    .addOnSuccessListener {
//                    }
//        }


        holder.close_btn.setOnClickListener {
            val theRemovedItem = gameList.get(position)
            // remove your item from data base
            gameList.removeAt(position) // remove the item from list
            notifyItemRemoved(position) // notify the adapter about the removed item
            firestore.collection("allgames").document(gameid)
                    .delete()
                    .addOnSuccessListener {
                    }
        }


    }

    override fun getItemCount(): Int {
        return gameList.size
    }

    inner class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        internal var live_fname: TextView = view.findViewById(R.id.live_game_user_fname)
        internal var live_lname: TextView = view.findViewById(R.id.live_game_user_lname)
        internal var live_imageurl: ImageView = view.findViewById(R.id.live_game_user_imageurl)
        internal var live_gamestate_btn: Button = view.findViewById(R.id.live_game_btn_gamestate)
        internal var close_btn: ImageButton = view.findViewById(R.id.btn_close)
    }
}
