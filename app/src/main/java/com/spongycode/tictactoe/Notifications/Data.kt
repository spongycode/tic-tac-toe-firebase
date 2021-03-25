package com.spongycode.tictactoe.Notifications

class Data {
    private var senderId: String = ""
    private var icon = 0
    private var body: String = ""
    private var title: String = ""
    private var receiverId: String = ""

    constructor(){}

    constructor(senderId: String, icon: Int, body: String, title: String, receiverId: String) {
        this.senderId = senderId
        this.icon = icon
        this.body = body
        this.title = title
        this.receiverId = receiverId
    }


}