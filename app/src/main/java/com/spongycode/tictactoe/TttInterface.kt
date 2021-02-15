package com.spongycode.tictactoe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_ttt_interface.*

class TttInterface : AppCompatActivity() {

    var dot_player_can_move = true
    var cross_player_can_move = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ttt_interface)

        initButtonState()
        var list_of_buttons = mutableListOf<Button>(
                findViewById(R.id.button1), findViewById(R.id.button2), findViewById(R.id.button3),
                findViewById(R.id.button4), findViewById(R.id.button5), findViewById(R.id.button6),
                findViewById(R.id.button7), findViewById(R.id.button8), findViewById(R.id.button9)
        )
        val opponentid = "tempNULLvalue" //offline (to be changed with firestore capture, in online mode)

        btn_reset_game.setOnClickListener {

            list_of_buttons = mutableListOf<Button>(
                    findViewById(R.id.button1), findViewById(R.id.button2), findViewById(R.id.button3),
                    findViewById(R.id.button4), findViewById(R.id.button5), findViewById(R.id.button6),
                    findViewById(R.id.button7), findViewById(R.id.button8), findViewById(R.id.button9)
            )

            dot_player_can_move = true
            cross_player_can_move = false
            initButtonState()
            disableButtons(false)
            win_message.visibility = GONE


            button1.setText("-1")
            button2.setText("-1")
            button3.setText("-1")
            button4.setText("-1")
            button5.setText("-1")
            button6.setText("-1")
            button7.setText("-1")
            button8.setText("-1")
            button9.setText("-1")
        }

        win_message.visibility = GONE

        button1.setOnClickListener {
            list_of_buttons.remove(findViewById(R.id.button1))
            tapButton(opponentid.toString(), findViewById(R.id.button1))
            findWinner()

            if (list_of_buttons.isNotEmpty()) {
                val random = list_of_buttons.random()
                list_of_buttons.remove(random)
                Toast.makeText(this, random.text.toString(), Toast.LENGTH_SHORT).show()
                tapButton("compID", random)
                findWinner()

            }

        }
        button2.setOnClickListener {
            list_of_buttons.remove(findViewById(R.id.button2))
            tapButton(opponentid.toString(), findViewById(R.id.button2))
            findWinner()

            if (list_of_buttons.isNotEmpty()) {
                val random = list_of_buttons.random()
                list_of_buttons.remove(random)
                Toast.makeText(this, random.text.toString(), Toast.LENGTH_SHORT).show()
                tapButton("compID", random)
                findWinner()

            }
        }
        button3.setOnClickListener {
            list_of_buttons.remove(findViewById(R.id.button3))
            tapButton(opponentid.toString(), findViewById(R.id.button3))
            findWinner()

            if (list_of_buttons.isNotEmpty()) {
                val random = list_of_buttons.random()
                list_of_buttons.remove(random)
                Toast.makeText(this, random.text.toString(), Toast.LENGTH_SHORT).show()
                tapButton("compID", random)
                findWinner()

            }
        }
        button4.setOnClickListener {
            list_of_buttons.remove(findViewById(R.id.button4))
            tapButton(opponentid.toString(), findViewById(R.id.button4))
            findWinner()

            if (list_of_buttons.isNotEmpty()) {
                val random = list_of_buttons.random()
                list_of_buttons.remove(random)
                Toast.makeText(this, random.text.toString(), Toast.LENGTH_SHORT).show()
                tapButton("compID", random)
                findWinner()

            }
        }
        button5.setOnClickListener {
            list_of_buttons.remove(findViewById(R.id.button5))
            tapButton(opponentid.toString(), findViewById(R.id.button5))
            findWinner()

            if (list_of_buttons.isNotEmpty()) {
                val random = list_of_buttons.random()
                list_of_buttons.remove(random)
                Toast.makeText(this, random.text.toString(), Toast.LENGTH_SHORT).show()
                tapButton("compID", random)
                findWinner()

            }
        }
        button6.setOnClickListener {
            list_of_buttons.remove(findViewById(R.id.button6))
            tapButton(opponentid.toString(), findViewById(R.id.button6))
            findWinner()

            if (list_of_buttons.isNotEmpty()) {
                val random = list_of_buttons.random()
                list_of_buttons.remove(random)
                Toast.makeText(this, random.text.toString(), Toast.LENGTH_SHORT).show()
                tapButton("compID", random)
                findWinner()

            }
        }
        button7.setOnClickListener {
            list_of_buttons.remove(findViewById(R.id.button7))
            tapButton(opponentid.toString(), findViewById(R.id.button7))
            findWinner()

            if (list_of_buttons.isNotEmpty()) {
                val random = list_of_buttons.random()
                list_of_buttons.remove(random)
                Toast.makeText(this, random.text.toString(), Toast.LENGTH_SHORT).show()
                tapButton("compID", random)
                findWinner()

            }
        }
        button8.setOnClickListener {
            list_of_buttons.remove(findViewById(R.id.button8))
            tapButton(opponentid.toString(), findViewById(R.id.button8))
            findWinner()

            if (list_of_buttons.isNotEmpty()) {
                val random = list_of_buttons.random()
                list_of_buttons.remove(random)
                Toast.makeText(this, random.text.toString(), Toast.LENGTH_SHORT).show()
                tapButton("compID", random)
                findWinner()

            }
        }
        button9.setOnClickListener {
            list_of_buttons.remove(findViewById(R.id.button9))
            tapButton(opponentid.toString(), findViewById(R.id.button9))
            findWinner()

            if (list_of_buttons.isNotEmpty()) {
                val random = list_of_buttons.random()
                list_of_buttons.remove(random)
                Toast.makeText(this, random.text.toString(), Toast.LENGTH_SHORT).show()
                tapButton("compID", random)
                findWinner()

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


    fun tapButton(opponentid: String, buttonid: Button) {
        // opponent id is not used, used in online mode

        val button: Button = buttonid
        if (dot_player_can_move == true) {
            button.setText("O")
            button.setBackgroundResource(R.drawable.ic_dot)
            button.isClickable = false
            cross_player_can_move = true
            dot_player_can_move = false

        } else {
            button.setText("X")
            button.setBackgroundResource(R.drawable.ic_cross)
            button.isClickable = false
            cross_player_can_move = false
            dot_player_can_move = true
        }
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