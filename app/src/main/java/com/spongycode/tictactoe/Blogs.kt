package com.spongycode.tictactoe
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class Blogs : Fragment() {
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflating layout Blogs Fragment
        return inflater.inflate(R.layout.fragment_blogs, container, false)
    }
}