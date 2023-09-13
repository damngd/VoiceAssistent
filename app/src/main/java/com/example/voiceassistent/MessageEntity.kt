package com.example.voiceassistent

import java.text.SimpleDateFormat

class MessageEntity {
    var text: String = ""
    var date: String = ""
    var isSend:Boolean = false

    constructor(text: String, date: String, isSend:Boolean){
        this.text = text
        this.date = date
        this.isSend = isSend
    }

    constructor(message: Message) {
        text = message.text
        isSend = message.isSend

        val format = SimpleDateFormat()
        format.applyPattern("dd.mm.yyyy")
        date = format.format(message.date)
    }

}