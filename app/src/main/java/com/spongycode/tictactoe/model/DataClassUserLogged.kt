package com.spongycode.tictactoe.model

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class DataClassUserLogged(uid: String) {
    var fname: String = ""
    var lname: String = ""
    var imageurl: String = ""
}


