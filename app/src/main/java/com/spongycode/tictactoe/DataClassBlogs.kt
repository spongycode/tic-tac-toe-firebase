package com.spongycode.tictactoe

class EachBlog {

    var content: String? = null
    var image: String? = null
    var author: String? = null
    var userid: String? = null

    constructor() {}

    constructor(content: String, image: String, author: String, userid: String) {
        this.content = content
        this.image = image
        this.author = author
        this.userid = userid

    }

    constructor(content: String, author: String,userid: String) {
        this.content = content
        this.author = author
        this.userid = userid

    }

    fun toMap(): Map<String, Any> {

        val result = HashMap<String, Any>()
        result.put("content", content!!)
        result.put("image", image!!)
        result.put("author", author!!)
        result.put("userid", userid!!)

        return result
    }
}