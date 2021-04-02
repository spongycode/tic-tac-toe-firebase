package com.spongycode.tictactoe.ui

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import com.bumptech.glide.Glide
import com.spongycode.tictactoe.LiveGameData
import com.spongycode.tictactoe.R
import com.spongycode.tictactoe.adapter.auth
import com.spongycode.tictactoe.adapter.firestore
import com.spongycode.tictactoe.model.UserDataClass
import com.spongycode.tictactoe.utils.Helper
import kotlinx.android.synthetic.main.activity_ttt_interface.*

class TttInterfaceActivity : AppCompatActivity() {
    private var MY_STATE = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ttt_interface)

        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor(Helper.tablayout_color)))

        Helper.buttonEffect(btn_exit, "#FFED4F4F")
        Helper.buttonEffect(btn_rematch, "#FF68C84C")
        Helper.buttonEffect(ib_accept_rematch, "#FFFFFFFF")
        Helper.buttonEffect(ib_exit_rematch, "#FFFFFFFF")

        val opponentid = intent?.getStringExtra("opponentid")

        initButtonState()
        disableButtons(true)

        btn_exit.visibility = GONE
        btn_rematch.visibility = GONE
        ib_accept_rematch.visibility = GONE
        ib_exit_rematch.visibility = GONE
        message_placeholder.visibility = GONE

        val tempArrayPlayers = arrayOf(auth.currentUser?.uid.toString(), opponentid)
        tempArrayPlayers.sort()
        val gameid = tempArrayPlayers[0] + "@" + tempArrayPlayers[1]

        updateImageNameBothPlayer(opponentid!!)

        checkmystate(gameid)

        firestore.collection("allgames").document(gameid)
                .addSnapshotListener { snapshot, e ->
                    seekToEnableButtons(gameid)
                    seekAllPostionMark(gameid, opponentid)
                    findWinner(MY_STATE, gameid, opponentid)
                    updateScore(gameid, opponentid)
                    checkrematch(gameid)
                }

        ib_accept_rematch.setOnClickListener {
            settextm1()
            ib_accept_rematch.visibility = GONE
            ib_exit_rematch.visibility = GONE
            message_placeholder.visibility = GONE


            firestore.collection("allgames").document(gameid)
                    .update("rematchto", "none")
                    .addOnCompleteListener {
                        seekToEnableButtons(gameid)
                        seekAllPostionMark(gameid, opponentid)
                        Toast.makeText(this, "Your Chance, Make your Move", Toast.LENGTH_LONG).show()
                    }

        }

        btn_exit.setOnClickListener {
            val progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Exiting...")
            progressDialog.setMessage("Destroying Room")
            progressDialog.show()
            firestore.collection("allgames").document(gameid)
                    .delete()
                    .addOnSuccessListener {
                        progressDialog.hide()
                        finish()
                    }
        }

        ib_exit_rematch.setOnClickListener {
            val progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Exiting...")
            progressDialog.setMessage("Destroying Room")
            progressDialog.show()
            firestore.collection("allgames").document(gameid)
                    .delete()
                    .addOnSuccessListener {
                        progressDialog.hide()
                        finish()
                    }
        }

        btn_rematch.setOnClickListener {

            val progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Rematch")
            progressDialog.setMessage("Sending Rematch ..")
            progressDialog.show()

            firestore.collection("allgames")
                    .whereEqualTo("gameid", gameid)
                    .get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            if (document.get("rematchto").toString() == "none") {

                                lateinit var myscore: String
                                lateinit var opponentscore: String

                                firestore.collection("allgames")
                                        .whereEqualTo("gameid", gameid)
                                        .get()
                                        .addOnSuccessListener { documents ->
                                            for (document in documents) {
                                                myscore = document.get(auth.currentUser?.uid.toString())
                                                        .toString()
                                                opponentscore = document.get(opponentid).toString()

                                                firestore.collection("allgames").document(gameid)
                                                        .delete()
                                                        .addOnCompleteListener {
                                                            progressDialog.hide()
                                                            Toast.makeText(this, "Rematch offer Sent, Wait!!", Toast.LENGTH_SHORT).show()

                                                            offWinningLines()

                                                            message_placeholder.visibility = GONE
                                                            btn_exit.visibility = GONE
                                                            btn_rematch.visibility = GONE

                                                            initButtonState()

                                                            addnewrematch(gameid, opponentid, myscore, opponentscore)
                                                        }
                                            }
                                        }
                            }
                        }
                    }




            disableButtons(true)
            message_placeholder.visibility = GONE

            settextm1()
        }


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


    private fun updateScore(gameid: String, opponentid: String) {


        firestore.collection("allgames")
                .whereEqualTo("gameid", gameid)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val myscore = document.get(auth.currentUser?.uid.toString()).toString()
                        val opponentscore = document.get(opponentid).toString()
                        if (player_score.text.toString() != myscore) {
                            player_score.text = myscore
                            btn_exit.visibility = VISIBLE
                            btn_rematch.visibility = VISIBLE
                        }

                        if (opponent_score.text.toString() != opponentscore) {
                            opponent_score.text = opponentscore
                            btn_exit.visibility = VISIBLE
                            btn_rematch.visibility = VISIBLE
                        }
                    }
                }


    }

    private fun settextm1() {
        button1.text = "-1"
        button2.text = "-1"
        button3.text = "-1"
        button4.text = "-1"
        button5.text = "-1"
        button6.text = "-1"
        button7.text = "-1"
        button8.text = "-1"
        button9.text = "-1"
    }

    private fun checkmystate(gameid: String) {
        firestore.collection("allgames")
                .whereEqualTo("gameid", gameid)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        if (document.toObject(LiveGameData::class.java).receiverid == auth.currentUser?.uid.toString()) {
                            MY_STATE = "X"
                            Glide.with(this).load(R.drawable.ic_cross).into(image_holder_player_state)
                            Glide.with(this).load(R.drawable.ic_dot).into(image_holder_opponent_state)

                        } else {
                            MY_STATE = "O"
                            Glide.with(this).load(R.drawable.ic_dot).into(image_holder_player_state)
                            Glide.with(this).load(R.drawable.ic_cross).into(image_holder_opponent_state)
                        }
                    }
                }
    }

    private fun addnewrematch(
            gameid: String,
            opponentid: String,
            myscore: String,
            opponentscore: String
    ) {
        val docref = firestore.collection("allgames").document(gameid)
        docref.set(
                hashMapOf(
                        "receiverid" to opponentid,
                        "senderid" to auth.currentUser?.uid.toString(),
                        "canplay" to opponentid,
                        "gamestat" to "notstart",
                        "gameid" to gameid,
                        "rematchto" to opponentid,
                        "winnerfound" to "no",
                        opponentid to opponentscore,
                        auth.currentUser?.uid.toString() to myscore
                )
        )


    }

    @SuppressLint("SetTextI18n")
    private fun checkrematch(gameid: String) {
        firestore.collection("allgames")
                .whereEqualTo("gameid", gameid)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        if (document.get("rematchto").toString() == auth.currentUser?.uid.toString()) {
                            initButtonState()
                            offWinningLines()
                            btn_exit.visibility = GONE
                            btn_rematch.visibility = GONE
                            ib_accept_rematch.visibility = VISIBLE
                            ib_exit_rematch.visibility = VISIBLE
                            message_placeholder.text = "Opponent offers Rematch"
                            message_placeholder.visibility = VISIBLE

                        }
                    }
                }
    }

    private fun seekAllPostionMark(gameid: String, opponentid: String) {
        firestore.collection("allgames")
                .whereEqualTo("gameid", gameid)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        tapDotCross(
                                document.get("pos1").toString(),
                                findViewById(R.id.button1),
                                gameid,
                                opponentid
                        )
                        tapDotCross(
                                document.get("pos2").toString(),
                                findViewById(R.id.button2),
                                gameid,
                                opponentid
                        )
                        tapDotCross(
                                document.get("pos3").toString(),
                                findViewById(R.id.button3),
                                gameid,
                                opponentid
                        )
                        tapDotCross(
                                document.get("pos4").toString(),
                                findViewById(R.id.button4),
                                gameid,
                                opponentid
                        )
                        tapDotCross(
                                document.get("pos5").toString(),
                                findViewById(R.id.button5),
                                gameid,
                                opponentid
                        )
                        tapDotCross(
                                document.get("pos6").toString(),
                                findViewById(R.id.button6),
                                gameid,
                                opponentid
                        )
                        tapDotCross(
                                document.get("pos7").toString(),
                                findViewById(R.id.button7),
                                gameid,
                                opponentid
                        )
                        tapDotCross(
                                document.get("pos8").toString(),
                                findViewById(R.id.button8),
                                gameid,
                                opponentid
                        )
                        tapDotCross(
                                document.get("pos9").toString(),
                                findViewById(R.id.button9),
                                gameid,
                                opponentid
                        )
                    }
                }
    }

    private fun tapDotCross(dc: String, buttonid: Button, gameid: String, opponentid: String) {
        val button: Button = buttonid
        if (dc == "X") {
            button.text = "X"
            button.setBackgroundResource(R.drawable.ic_cross)
            button.isClickable = false
        }
        if (dc == "O") {
            button.text = "O"
            button.setBackgroundResource(R.drawable.ic_dot)
            button.isClickable = false
        }
        if (dc == "-1") {
            button.setBackgroundResource(R.drawable.ic_btn_default_state)
        }
        findWinner(MY_STATE, gameid, opponentid)
    }

    private fun tapButtonOnline(
            myState: String,
            buttonid: Button,
            pos: Int,
            opponentid: String = "",
            gameid: String
    ) {

        val vibrate = applicationContext.getSystemService<Vibrator>()

        vibrate?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrate.vibrate(
                        VibrationEffect.createOneShot(
                                100,
                                VibrationEffect.DEFAULT_AMPLITUDE
                        )
                )
            } else {
                //deprecated in API 26
                vibrate.vibrate(100)
            }
        }


        val button: Button = buttonid
        if (myState == "O") {
            button.text = "O"
            button.setBackgroundResource(R.drawable.ic_dot)
            button.isClickable = false
        } else {
            button.text = "X"
            button.setBackgroundResource(R.drawable.ic_cross)
            button.isClickable = false
        }
        disableButtons(true)
        firestore.collection("allgames").document(gameid)
                .update("canplay", opponentid)
                .addOnCompleteListener {
                }
        firestore.collection("allgames").document(gameid)
                .update("pos$pos", myState)
                .addOnCompleteListener {

                }
        firestore.collection("allgames").document(gameid)
                .update("gamestat", "start")
                .addOnCompleteListener {
                }
    }

    private fun seekToEnableButtons(gameid: String) {
        firestore.collection("allgames")
                .whereEqualTo("gameid", gameid)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val canplay = document.toObject(LiveGameData::class.java).canplay
                        if (canplay == auth.currentUser?.uid.toString() &&
                                document.toObject(LiveGameData::class.java).rematchto != auth.currentUser?.uid.toString() &&
                                document.toObject(LiveGameData::class.java).winnerfound != "yes"
                        ) {
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
                            fname_holder_player.text = fname
                            lname_holder_player.text = lname
                            Glide.with(this).load(imageurl)
                                    .into(image_holder_player)
                        }
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
                            fname_holder_opponent. text = fname
                            lname_holder_opponent.text = lname
                            Glide.with(applicationContext).load(imageurl)
                                    .into(image_holder_opponent)
                        }
                    }
                }


    }

    @SuppressLint("SetTextI18n")
    private fun findWinner(myState: String, gameid: String, opponentid: String) {

        var foundWinner = false
        var winnerstate = "-1"
        //row1
        if (button1.text == "X" && button2.text == "X" && button3.text == "X") {
            foundWinner = true
            winnerstate = "X"
            winning_line_1.visibility = VISIBLE
        }
        if (button1.text == "O" && button2.text == "O" && button3.text == "O") {
            foundWinner = true
            winnerstate = "O"
            winning_line_1.visibility = VISIBLE
        }

        //row2
        if (button4.text == "X" && button5.text == "X" && button6.text == "X") {
            foundWinner = true
            winnerstate = "X"
            winning_line_2.visibility = VISIBLE
        }
        if (button4.text == "O" && button5.text == "O" && button6.text == "O") {
            foundWinner = true
            winnerstate = "O"
            winning_line_2.visibility = VISIBLE
        }

        //row3
        if (button7.text == "X" && button8.text == "X" && button9.text == "X") {
            foundWinner = true
            winnerstate = "X"
            winning_line_3.visibility = VISIBLE
        }
        if (button7.text == "O" && button8.text == "O" && button9.text == "O") {
            foundWinner = true
            winnerstate = "O"
            winning_line_3.visibility = VISIBLE
        }

        //col1
        if (button1.text == "X" && button4.text == "X" && button7.text == "X") {
            foundWinner = true
            winnerstate = "X"
            winning_line_4.visibility = VISIBLE
        }
        if (button1.text == "O" && button4.text == "O" && button7.text == "O") {
            foundWinner = true
            winnerstate = "O"
            winning_line_4.visibility = VISIBLE
        }

        //col2
        if (button2.text == "X" && button5.text == "X" && button8.text == "X") {
            foundWinner = true
            winnerstate = "X"
            winning_line_5.visibility = VISIBLE
        }
        if (button2.text == "O" && button5.text == "O" && button8.text == "O") {
            foundWinner = true
            winnerstate = "O"
            winning_line_5.visibility = VISIBLE
        }

        //col3
        if (button3.text == "X" && button6.text == "X" && button9.text == "X") {
            foundWinner = true
            winnerstate = "X"
            winning_line_6.visibility = VISIBLE
        }
        if (button3.text == "O" && button6.text == "O" && button9.text == "O") {
            foundWinner = true
            winnerstate = "O"
            winning_line_6.visibility = VISIBLE
        }

        //cross1
        if (button1.text == "X" && button5.text == "X" && button9.text == "X") {
            foundWinner = true
            winnerstate = "X"
            winning_line_7.visibility = VISIBLE
        }
        if (button1.text == "O" && button5.text == "O" && button9.text == "O") {
            foundWinner = true
            winnerstate = "O"
            winning_line_7.visibility = VISIBLE
        }

        //cross2
        if (button3.text == "X" && button5.text == "X" && button7.text == "X") {
            foundWinner = true
            winnerstate = "X"
            winning_line_8.visibility = VISIBLE
        }
        if (button3.text == "O" && button5.text == "O" && button7.text == "O") {
            foundWinner = true
            winnerstate = "O"
            winning_line_8.visibility = VISIBLE
        }

        if (foundWinner) {

            settextm1()
            disableButtons(true)

            if (winnerstate == myState) {
                message_placeholder.text = "You Win!!"
                firestore.collection("allgames")
                        .whereEqualTo("gameid", gameid)
                        .get()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                for (data in task.result!!) {
                                    var receiveridtbs: String
                                    var senderidtbs: String

                                    val receiverid = data.get("receiverid").toString()

                                    if (receiverid == auth.currentUser?.uid.toString()) {
                                        receiveridtbs = auth.currentUser?.uid.toString()
                                        senderidtbs = data.get("senderid").toString()
                                    } else {
                                        receiveridtbs = data.get("senderid").toString()
                                        senderidtbs = auth.currentUser?.uid.toString()
                                    }
                                    val currscoremine =
                                            data.get(auth.currentUser?.uid.toString()).toString()
                                    val currscoreopp = data.get(opponentid).toString()
                                    val canplay = data.get("canplay").toString()

                                    firestore.collection("allgames").document(gameid)
                                            .delete()
                                            .addOnCompleteListener {
                                                val docref =
                                                        firestore.collection("allgames").document(gameid)
                                                docref.set(
                                                        hashMapOf(
                                                                "receiverid" to receiveridtbs,
                                                                "senderid" to senderidtbs,
                                                                "canplay" to canplay,
                                                                "gamestat" to "notstart",
                                                                "gameid" to gameid,
                                                                "rematchto" to "none",
                                                                "winnerfound" to "yes",
                                                                opponentid to currscoreopp,
                                                                auth.currentUser?.uid.toString() to currscoremine.toInt() + 1
                                                        )
                                                )
                                            }
                                }
                            }
                        }

            } else {
                message_placeholder.text = "You Lose!!"
            }

            message_placeholder.visibility = VISIBLE

            disableButtons(true)
        }



        if (button1.text != "-1" && button2.text != "-1" && button3.text != "-1" &&
                button4.text != "-1" && button5.text != "-1" && button6.text != "-1" &&
                button7.text != "-1" && button8.text != "-1" && button9.text != "-1" && !foundWinner
        ) {

            settextm1()
            disableButtons(true)
            message_placeholder.text = "Draw"
            message_placeholder.visibility = VISIBLE
            btn_exit.visibility = VISIBLE
            btn_rematch.visibility = VISIBLE

        }


    }

    private fun offWinningLines() {
        winning_line_1.visibility = GONE
        winning_line_2.visibility = GONE
        winning_line_3.visibility = GONE
        winning_line_4.visibility = GONE
        winning_line_5.visibility = GONE
        winning_line_6.visibility = GONE
        winning_line_7.visibility = GONE
        winning_line_8.visibility = GONE
    }

    private fun initButtonState() {
        button1.setBackgroundResource(R.drawable.ic_baseline_circle_24)
        button2.setBackgroundResource(R.drawable.ic_baseline_circle_24)
        button3.setBackgroundResource(R.drawable.ic_baseline_circle_24)
        button4.setBackgroundResource(R.drawable.ic_baseline_circle_24)
        button5.setBackgroundResource(R.drawable.ic_baseline_circle_24)
        button6.setBackgroundResource(R.drawable.ic_baseline_circle_24)
        button7.setBackgroundResource(R.drawable.ic_baseline_circle_24)
        button8.setBackgroundResource(R.drawable.ic_baseline_circle_24)
        button9.setBackgroundResource(R.drawable.ic_baseline_circle_24)

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

}