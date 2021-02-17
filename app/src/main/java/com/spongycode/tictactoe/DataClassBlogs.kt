package com.spongycode.tictactoe

class EachBlog {

    var content: String? = null
    var image: String? = null
    var userid: String? = null

    constructor() {}

    constructor(content: String, image: String, userid: String) {
        this.content = content
        this.image = image
        this.userid = userid

    }

    constructor(content: String ,userid: String) {
        this.content = content
        this.userid = userid
    }

    fun toMap(): Map<String, Any> {

        val result = HashMap<String, Any>()
        result.put("content", content!!)
        result.put("image", image!!)
        result.put("userid", userid!!)

        return result
    }
}