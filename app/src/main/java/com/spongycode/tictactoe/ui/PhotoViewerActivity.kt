package com.spongycode.tictactoe.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.spongycode.tictactoe.R
import kotlinx.android.synthetic.main.activity_photo_viewer.*

class PhotoViewerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_viewer)
        this.supportActionBar?.hide()

        val imageUrl = intent.getStringExtra("IMAGE_URL")
        Glide.with(applicationContext).load(imageUrl).into(fullscreen_content)
    }
}