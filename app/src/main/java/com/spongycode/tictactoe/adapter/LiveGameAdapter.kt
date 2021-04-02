package com.spongycode.tictactoe.adapter

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
import com.spongycode.tictactoe.LiveGameData
import com.spongycode.tictactoe.R
import com.spongycode.tictactoe.ui.TttInterfaceActivity
import com.spongycode.tictactoe.model.UserDataClass


class LiveGameAdapter(
        private val gameList: MutableList<LiveGameData>,
        private val context: Context)
    : RecyclerView.Adapter<LiveGameAdapter.ViewHolder>() {


    val auth = Firebase.auth

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.live_game_layout, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val game = gameList[position]

        val id = if (game.senderid != auth.currentUser?.uid.toString()) {
            game.senderid!!
        } else {
            game.receiverid!!
        }
        firestore.collection("users")
                .whereEqualTo("userid", id)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (data in task.result!!) {
                            holder.liveFname.text = data.toObject(UserDataClass::class.java).fname
                            holder.liveLname.text = data.toObject(UserDataClass::class.java).lname
                            val imageurl = data.toObject(UserDataClass::class.java).imageurl
                            Glide.with(holder.itemView.context.applicationContext).load(imageurl).into(holder.liveImageurl)
                        }
                    }
                }


        val tempArrayPlayers = arrayOf(auth.currentUser?.uid.toString(), id)
        tempArrayPlayers.sort()
        val gameid = tempArrayPlayers[0] + "@" + tempArrayPlayers[1]

        firestore.collection("allgames")
                .whereEqualTo("gameid", gameid)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        if (document.get("senderid") == id && document.get("gamestat") == "notstart") {
                            holder.liveGamestateBtn.text = "Accept"
                        }
                        if (document.get("receiverid") == id && document.get("gamestat") == "notstart") {
                            holder.liveGamestateBtn.isEnabled = false
                            holder.liveGamestateBtn.text = "Wait"
                            holder.liveGamestateBtn.alpha = 0.5f
                        }
                        if (document.get("gamestat") != "notstart") {
                            holder.liveGamestateBtn.text = "Continue"
                        }
                    }
                }

        holder.liveGamestateBtn.setOnClickListener {
            val intent = Intent(context, TttInterfaceActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra("opponentid", id)
            context.startActivity(intent)
        }


        holder.closeBtn.setOnClickListener {
            gameList.removeAt(position)
            notifyItemRemoved(position)
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
        internal var liveFname: TextView = view.findViewById(R.id.live_game_user_fname)
        internal var liveLname: TextView = view.findViewById(R.id.live_game_user_lname)
        internal var liveImageurl: ImageView = view.findViewById(R.id.live_game_user_imageurl)
        internal var liveGamestateBtn: Button = view.findViewById(R.id.live_game_btn_gamestate)
        internal var closeBtn: ImageButton = view.findViewById(R.id.btn_close)
    }
}
