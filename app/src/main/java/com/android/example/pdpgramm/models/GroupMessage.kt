package com.android.example.pdpgramm.models

class GroupMessage {
    var message: String? = null
    var fromUNumber: Account? = null
    var date: String? = null

    constructor(message: String?, fromUNumber: Account?, date: String?) {
        this.message = message
        this.fromUNumber = fromUNumber
        this.date = date
    }

    constructor()
}