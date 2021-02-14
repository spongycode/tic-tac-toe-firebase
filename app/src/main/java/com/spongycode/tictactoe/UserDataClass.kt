package com.spongycode.tictactoe

class UserDataClass {

    val imageurl: String = ""
    var fname: String = ""
    var lname: String = ""
    var email: String = ""
    var userid: String = ""

    constructor() {}

    constructor(fname: String, lname: String, email: String, userid: String) {
        this.fname = fname
        this.lname = lname
        this.email = email
        this.userid = userid
    }

    fun toMap(): Map<String, Any> {

        val result = HashMap<String, Any>()
        result.put("fname", fname)
        result.put("lname", lname)
        result.put("email", email)
        result.put("userid", userid)

        return result
    }
}