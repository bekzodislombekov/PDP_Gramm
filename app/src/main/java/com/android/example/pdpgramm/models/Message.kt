package com.android.example.pdpgramm.models

class Message {
    var message: String? = null
    var fromUNumber: String? = null
    var toUNumber: String? = null
    var date: String? = null

    constructor(message: String?, fromUNumber: String?, toUNumber: String?, date: String?) {
        this.message = message
        this.fromUNumber = fromUNumber
        this.toUNumber = toUNumber
        this.date = date
    }

    constructor()
}