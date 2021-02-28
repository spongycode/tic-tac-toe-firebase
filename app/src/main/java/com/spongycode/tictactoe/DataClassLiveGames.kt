package com.spongycode.tictactoe


class LiveGameData {

    var senderid: String? = null
    var receiverid: String? = null
    var gamestat: String? = null
    var canplay: String? = null
    var gameid: String? = null
    var rematchto: String? = null


    constructor() {}

    constructor(senderid: String,receiverid:String,gamestat: String, canplay:String, rematchto : String) {
        this.senderid = senderid
        this.receiverid = receiverid
        this.gamestat = gamestat
        this.canplay = canplay
        this.gameid = gameid
        this.rematchto = rematchto
    }
}