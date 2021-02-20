package com.spongycode.tictactoe

import java.sql.Timestamp

class EachBlog {

    var content: String? = null
    var image: String? = null
    var userid: String? = null
    var timestamp: com.google.firebase.Timestamp? = null
    var sysmillis: Long? = null

    constructor() {}

    constructor(content: String, image: String, userid: String,timestamp: com.google.firebase.Timestamp,sysmillis:Long) {
        this.content = content
        this.image = image
        this.userid = userid
        this.timestamp = timestamp
        this.sysmillis = sysmillis
    }

    constructor(content: String ,userid: String,timestamp: com.google.firebase.Timestamp,sysmillis:Long) {
        this.content = content
        this.userid = userid
        this.timestamp = timestamp
        this.sysmillis = sysmillis

    }

    fun toMap(): Map<String, Any> {

        val result = HashMap<String, Any>()
        result.put("content", content!!)
        result.put("image", image!!)
        result.put("userid", userid!!)
        result.put("timestamp", timestamp!!)
        result.put("sysmillis", sysmillis!!)

        return result
    }
}