package com.example.voiceassistent

import java.text.SimpleDateFormat
import java.util.*

class Message() {
    lateinit var text: String
    lateinit var date: Date
    var isSend: Boolean = false


    constructor(text: String, isSend: Boolean) : this() {
        this.text = text
        this.isSend = isSend
        date = Date()
    }

    constructor(message: MessageEntity) : this() {
        text = message.text
        isSend = message.isSend

        val format = SimpleDateFormat()
        format.applyPattern("dd.mm.yyyy")
        date = format.parse(message.date)
    }


}