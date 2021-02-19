package com.spongycode.tictactoe

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_ttt_interface.*
import kotlinx.android.synthetic.main.fragment_live_games.*

class TttInterface : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ttt_interface)

        val gamemode = intent?.getStringExtra("gamemode")
        val opponentid = intent?.getStringExtra("opponentid")



        initButtonState()
        disableButtons(true)

        val tempArrayPlayers = arrayOf(auth.currentUser?.uid.toString(), opponentid)
        tempArrayPlayers.sort()
        val gameid = tempArrayPlayers[0] + "@" + tempArrayPlayers[1]

        // find state <dot cross> init
        var MY_STATE = ""
        firestore.collection("allgames")
            .whereEqualTo("gameid", gameid)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    if (document.toObject(LiveGameData::class.java).receiverid == auth.currentUser?.uid.toString()) {
                        MY_STATE = "X"
                    } else {
                        MY_STATE = "O"
                    }
                }
            }
        // find state <dot cross> end

        firestore.collection("allgames").document(gameid)
            .addSnapshotListener { sanpshot, e ->
                seekToEnableButtons(gameid)
                seekAllPostionMark(gameid)
                findWinner()
            }


        if (gamemode == "online") {
            updateImageNameBothPlayer(opponentid!!)
            seekToEnableButtons(gameid)
            findWinner()
        }


        btn_reset_game.setOnClickListener {


            initButtonState()
            disableButtons(false)
            win_message.visibility = GONE

            firestore.collection("allgames").document(gameid).update("pos1", "-1")
                .addOnCompleteListener { button1.setText("-1") }
            firestore.collection("allgames").document(gameid).update("pos2", "-1")
                .addOnCompleteListener { button2.setText("-1") }
            firestore.collection("allgames").document(gameid).update("pos3", "-1")
                .addOnCompleteListener { button3.setText("-1") }
            firestore.collection("allgames").document(gameid).update("pos4", "-1")
                .addOnCompleteListener { button4.setText("-1") }
            firestore.collection("allgames").document(gameid).update("pos5", "-1")
                .addOnCompleteListener { button5.setText("-1") }
            firestore.collection("allgames").document(gameid).update("pos6", "-1")
                .addOnCompleteListener { button6.setText("-1") }
            firestore.collection("allgames").document(gameid).update("pos7", "-1")
                .addOnCompleteListener { button7.setText("-1") }
            firestore.collection("allgames").document(gameid).update("pos8", "-1")
                .addOnCompleteListener { button8.setText("-1") }
            firestore.collection("allgames").document(gameid).update("pos9", "-1")
                .addOnCompleteListener { button9.setText("-1") }

        }

        win_message.visibility = GONE

        button1.setOnClickListener {
            tapButtonOnline(MY_STATE, findViewById(R.id.button1), 1, opponentid!!, gameid)
        }
        button2.setOnClickListener {
            tapButtonOnline(MY_STATE, findViewById(R.id.button2), 2, opponentid!!, gameid)
        }
        button3.setOnClickListener {
            tapButtonOnline(MY_STATE, findViewById(R.id.button3), 3, opponentid!!, gameid)
        }
        button4.setOnClickListener {
            tapButtonOnline(MY_STATE, findViewById(R.id.button4), 4, opponentid!!, gameid)
        }
        button5.setOnClickListener {
            tapButtonOnline(MY_STATE, findViewById(R.id.button5), 5, opponentid!!, gameid)
        }
        button6.setOnClickListener {
            tapButtonOnline(MY_STATE, findViewById(R.id.button6), 6, opponentid!!, gameid)
        }
        button7.setOnClickListener {
            tapButtonOnline(MY_STATE, findViewById(R.id.button7), 7, opponentid!!, gameid)
        }
        button8.setOnClickListener {
            tapButtonOnline(MY_STATE, findViewById(R.id.button8), 8, opponentid!!, gameid)
        }
        button9.setOnClickListener {
            tapButtonOnline(MY_STATE, findViewById(R.id.button9), 9, opponentid!!, gameid)
        }
    }

    private fun seekAllPostionMark(gameid: String) {
        firestore.collection("allgames")
            .whereEqualTo("gameid", gameid)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    tapDotCross(document.get("pos1").toString(), findViewById(R.id.button1))
                    tapDotCross(document.get("pos2").toString(), findViewById(R.id.button2))
                    tapDotCross(document.get("pos3").toString(), findViewById(R.id.button3))
                    tapDotCross(document.get("pos4").toString(), findViewById(R.id.button4))
                    tapDotCross(document.get("pos5").toString(), findViewById(R.id.button5))
                    tapDotCross(document.get("pos6").toString(), findViewById(R.id.button6))
                    tapDotCross(document.get("pos7").toString(), findViewById(R.id.button7))
                    tapDotCross(document.get("pos8").toString(), findViewById(R.id.button8))
                    tapDotCross(document.get("pos9").toString(), findViewById(R.id.button9))
                }
            }
    }

    private fun tapDotCross(dc: String, buttonid: Button) {
        val button: Button = buttonid
        if (dc == "X") {
            button.setText("X")
            button.setBackgroundResource(R.drawable.ic_cross)
            button.isClickable = false
        }
        if (dc == "O") {
            button.setText("O")
            button.setBackgroundResource(R.drawable.ic_dot)
            button.isClickable = false
        }
        if (dc == "-1") {
            button.setBackgroundResource(R.drawable.ic_btn_default_state)
        }
        findWinner()
    }

    private fun tapButtonOnline(
        myState: String,
        buttonid: Button,
        pos: Int,
        opponentid: String = "",
        gameid: String) {
        val button: Button = buttonid
        if (myState == "O") {
            button.setText("O")
            button.setBackgroundResource(R.drawable.ic_dot)
            button.isClickable = false
        } else {
            button.setText("X")
            button.setBackgroundResource(R.drawable.ic_cross)
            button.isClickable = false
        }
        disableButtons(true)
        firestore.collection("allgames").document(gameid)
            .update("canplay", opponentid)
            .addOnCompleteListener {
            }
        firestore.collection("allgames").document(gameid)
            .update("pos" + pos.toString(), myState)
            .addOnCompleteListener {

            }
        firestore.collection("allgames").document(gameid)
            .update("gamestat", "start")
            .addOnCompleteListener {
            }
        findWinner()


    }

    private fun seekToEnableButtons(gameid: String) {
        firestore.collection("allgames")
            .whereEqualTo("gameid", gameid)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val canplay = document.toObject(LiveGameData::class.java).canplay
                    if (canplay == auth.currentUser?.uid.toString()) {
                        disableButtons(false)
                        cl_player_bg.setBackgroundColor(
                            ContextCompat.getColor(
                                this,
                                R.color.canplay
                            )
                        )
                        cl_opponent_bg.setBackgroundColor(
                            ContextCompat.getColor(
                                this,
                                R.color.cantplay
                            )
                        )

                    } else {
                        disableButtons(true)
                        cl_player_bg.setBackgroundColor(
                            ContextCompat.getColor(
                                this,
                                R.color.cantplay
                            )
                        )
                        cl_opponent_bg.setBackgroundColor(
                            ContextCompat.getColor(
                                this,
                                R.color.canplay
                            )
                        )
                    }
                }
            }


    }

    private fun updateImageNameBothPlayer(opponentid: String) {
        firestore.collection("users").whereEqualTo("userid", auth.currentUser?.uid.toString())
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (data in task.result!!) {
                        val fname = data.toObject(UserDataClass::class.java).fname
                        val lname = data.toObject(UserDataClass::class.java).lname
                        val imageurl = data.toObject(UserDataClass::class.java).imageurl
                        fname_holder_player.setText(fname)
                        lname_holder_player.setText(lname)
                        Glide.with(this).load(imageurl)
                            .into(image_holder_player)
                    }
                } else {

                }
            }
        firestore.collection("users").whereEqualTo("userid", opponentid)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (data in task.result!!) {
                        val fname = data.toObject(UserDataClass::class.java).fname
                        val lname = data.toObject(UserDataClass::class.java).lname
                        val imageurl = data.toObject(UserDataClass::class.java).imageurl
                        fname_holder_opponent.setText(fname)
                        lname_holder_opponent.setText(lname)
                        Glide.with(applicationContext).load(imageurl)
                            .into(image_holder_opponent)
                    }
                } else {

                }
            }


    }

    private fun initButtonState() {
        button1.setBackgroundResource(R.drawable.ic_btn_default_state)
        button2.setBackgroundResource(R.drawable.ic_btn_default_state)
        button3.setBackgroundResource(R.drawable.ic_btn_default_state)
        button4.setBackgroundResource(R.drawable.ic_btn_default_state)
        button5.setBackgroundResource(R.drawable.ic_btn_default_state)
        button6.setBackgroundResource(R.drawable.ic_btn_default_state)
        button7.setBackgroundResource(R.drawable.ic_btn_default_state)
        button8.setBackgroundResource(R.drawable.ic_btn_default_state)
        button9.setBackgroundResource(R.drawable.ic_btn_default_state)

    }

    private fun disableButtons(state: Boolean) {
        button1.isClickable = !state
        button2.isClickable = !state
        button3.isClickable = !state
        button4.isClickable = !state
        button5.isClickable = !state
        button6.isClickable = !state
        button7.isClickable = !state
        button8.isClickable = !state
        button9.isClickable = !state
    }

    private fun findWinner() {

        var foundWinner = false
        //row1
        if (button1.text == "X" && button2.text == "X" && button3.text == "X") {
            win_message.setText("PLAYER 02 WON 😂")
            foundWinner = true
        }
        if (button1.text == "O" && button2.text == "O" && button3.text == "O") {
            win_message.setText("PLAYER 01 WON 😁")
            foundWinner = true
        }

        //row2
        if (button4.text == "X" && button5.text == "X" && button6.text == "X") {
            win_message.setText("PLAYER 02 WON 😂")
            foundWinner = true
        }
        if (button4.text == "O" && button5.text == "O" && button6.text == "O") {
            win_message.setText("PLAYER 01 WON 😁")
            foundWinner = true
        }

        //row3
        if (button7.text == "X" && button8.text == "X" && button9.text == "X") {
            win_message.setText("PLAYER 02 WON 😂")
            foundWinner = true


        }
        if (button7.text == "O" && button8.text == "O" && button9.text == "O") {
            win_message.setText("PLAYER 01 WON 😁")
            foundWinner = true

        }

        //col1
        if (button1.text == "X" && button4.text == "X" && button7.text == "X") {
            win_message.setText("PLAYER 02 WON 😂")
            foundWinner = true


        }
        if (button1.text == "O" && button4.text == "O" && button7.text == "O") {
            win_message.setText("PLAYER 01 WON 😁")
            foundWinner = true

        }

        //col2
        if (button2.text == "X" && button5.text == "X" && button8.text == "X") {
            win_message.setText("PLAYER 02 WON 😂")
            foundWinner = true


        }
        if (button2.text == "O" && button5.text == "O" && button8.text == "O") {
            win_message.setText("PLAYER 01 WON 😁")
            foundWinner = true

        }

        //col3
        if (button3.text == "X" && button6.text == "X" && button9.text == "X") {
            win_message.setText("PLAYER 02 WON 😂")
            foundWinner = true


        }
        if (button3.text == "O" && button6.text == "O" && button9.text == "O") {
            win_message.setText("PLAYER 01 WON 😁")
            foundWinner = true

        }

        //cross1
        if (button1.text == "X" && button5.text == "X" && button9.text == "X") {
            win_message.setText("PLAYER 02 WON 😂")
            foundWinner = true


        }
        if (button1.text == "O" && button5.text == "O" && button9.text == "O") {
            win_message.setText("PLAYER 01 WON 😁")
            foundWinner = true

        }

        //cross2
        if (button3.text == "X" && button5.text == "X" && button7.text == "X") {
            win_message.setText("PLAYER 02 WON 😂")

            foundWinner = true

        }
        if (button3.text == "O" && button5.text == "O" && button7.text == "O") {
            win_message.setText("PLAYER 01 WON 😁")
            foundWinner = true

        }

        if (foundWinner == true) {
            win_message.visibility = VISIBLE
            disableButtons(true)
        }
    }
}