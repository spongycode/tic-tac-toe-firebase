package com.spongycode.tictactoe

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.PorterDuff
import android.view.MotionEvent
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase


object Utils {


    var userlogged: UserLogged =  UserLogged("dummyID")


    @SuppressLint("ClickableViewAccessibility")
    fun buttonEffect(button: View, color: String) {

        button.setOnTouchListener { v, event ->


            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    val color: Int = Color.parseColor(color)
                    v.background.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
                    v.invalidate()

                    val scaleDownX = ObjectAnimator.ofFloat(
                        button,
                        "scaleX", 0.95f
                    )
                    val scaleDownY = ObjectAnimator.ofFloat(
                        button,
                        "scaleY", 0.95f
                    )
                    scaleDownX.duration = 100
                    scaleDownY.duration = 100

                    val scaleDown = AnimatorSet()
                    scaleDown.play(scaleDownX).with(scaleDownY)

                    scaleDown.start()


                }
                MotionEvent.ACTION_UP -> {
                    v.background.clearColorFilter()
                    v.invalidate()


                    val scaleDownX2 = ObjectAnimator.ofFloat(
                        button, "scaleX", 1f
                    )
                    val scaleDownY2 = ObjectAnimator.ofFloat(
                        button, "scaleY", 1f
                    )
                    scaleDownX2.duration = 100
                    scaleDownY2.duration = 100

                    val scaleDown2 = AnimatorSet()
                    scaleDown2.play(scaleDownX2).with(scaleDownY2)

                    scaleDown2.start()


                }
            }
            false
        }

    }


}











