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
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_home.*


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

        if (user.imageurl!="") {

        Glide.with(context).load(user.imageurl).into(holder.imageurl)
        }

        val btn_battle = holder.itemView.findViewById<Button>(R.id.btn_battle)
        if (user.userid == auth.currentUser?.uid.toString()) {
            btn_battle.setText("Profile")
            btn_battle.setOnClickListener {
                val intent = Intent(context, ProfileActivity::class.java)
                context.startActivity(intent)
            }
        } else {

            btn_battle.setOnClickListener {
                firestore.collection("users")
                    .whereEqualTo("userid", auth.currentUser?.uid.toString())
                    .get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            for (data in task.result!!) {
                                val fname = data.toObject(UserDataClass::class.java).fname
                                val lname = data.toObject(UserDataClass::class.java).lname
                                val imageurl = data.toObject(UserDataClass::class.java).imageurl
                                firestore.collection("users")
                                    .document(user.userid)
                                    .collection("pendingrequest")
                                    .document(auth.currentUser?.uid.toString())

                                       //set function is being updated now, earlier it was "opponentid" <14-02> ,,,,now "userid"<15-02>
                                    .set(
                                            hashMapOf(
                                                    "fname" to fname,
                                                    "lname" to lname,
                                                    "imageurl" to imageurl,
                                                    "userid" to auth.currentUser?.uid.toString()

                                            )
                                    )
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                                context,
                                                "Challenge Sent to $fname $lname",
                                                Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(
                                                context,
                                                "Failed Sending Challenge",
                                                Toast.LENGTH_SHORT
                                        ).show()
                                    }


                            }
                        } else {

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
