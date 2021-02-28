package com.spongycode.tictactoe

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


val firestore = FirebaseFirestore.getInstance()
val auth = FirebaseAuth.getInstance()


class UserAdapter(
        private val userList: MutableList<UserDataClass>,
        private val context: Context,
        private val firestoreDB: FirebaseFirestore
) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = userList[position]
        holder.fname.text = user.fname
        holder.lname.text = user.lname


        if (user.imageurl != "") {

            Glide.with(context).load(user.imageurl).into(holder.imageurl)
        }

        val btn_battle = holder.itemView.findViewById<Button>(R.id.btn_battle)
        if (user.userid == auth.currentUser?.uid.toString()) {
            btn_battle.setText("Profile")
            btn_battle.setOnClickListener {
                val intent = Intent(context, SettingsActivity::class.java)
                context.startActivity(intent)
            }
        } else {


            val tempArrayPlayers = arrayOf(auth.currentUser?.uid.toString(), user.userid)
            tempArrayPlayers.sort()
            val gameid = tempArrayPlayers[0] + "@" + tempArrayPlayers[1]





            firestore.collection("allgames")
                    .whereEqualTo("gameid", gameid)
                    .get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            for (data in task.result!!) {
                                btn_battle.isEnabled = false
                                btn_battle.alpha = 0.5f
                            }
                        } else {
                            // to handle
                        }
                    }



            btn_battle.setOnClickListener {
                val docref = firestore.collection("allgames").document(gameid)
                if (btn_battle.alpha != 0.5f) {
                    docref.set(
                            hashMapOf(
                                    "receiverid" to user.userid,
                                    "senderid" to auth.currentUser?.uid.toString(),
                                    "canplay" to user.userid,
                                    "gamestat" to "notstart",
                                    "gameid" to gameid,
                                    "rematchto" to "none",
                                    user.userid to "0",
                                    auth.currentUser?.uid.toString() to "0"
                            )
                    )
                            .addOnSuccessListener {
                                Toast.makeText(context, "Challenge Sent!!", Toast.LENGTH_SHORT).show()
                                btn_battle.isEnabled = false
                                btn_battle.alpha = 0.5f

                            }
                }


            }


        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    inner class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        internal var fname: TextView = view.findViewById(R.id.user_fname)
        internal var lname: TextView = view.findViewById(R.id.user_lname)
        internal var imageurl: ImageView = view.findViewById(R.id.user_imageurl)
    }
}
