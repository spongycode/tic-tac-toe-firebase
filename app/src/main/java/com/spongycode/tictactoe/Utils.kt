package com.spongycode.tictactoe

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.PorterDuff
import android.view.MotionEvent
import android.view.View

object Utils {
    @SuppressLint("ClickableViewAccessibility")
    fun buttonEffect(button: View, color: String) {

        button.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
//                    val color: Int = Color.parseColor("#85ffdd")
                    val color: Int = Color.parseColor(color)
                    v.background.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
                    v.invalidate()
                }
                MotionEvent.ACTION_UP -> {
                    v.background.clearColorFilter()
                    v.invalidate()
                }
            }
            false
        }

    }

}