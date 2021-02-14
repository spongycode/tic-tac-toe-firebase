package com.spongycode.tictactoe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class LiveGames: Fragment() {
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflating layout live matches (ongoing games) Fragment
        return inflater.inflate(R.layout.fragment_live_games, container, false)
    }
}
