package com.android.example.pdpgramm.models

import java.io.Serializable

class Account : Serializable {
    var firstName: String? = null
    var lastName: String? = null
    var phoneNumber: String? = null
    var isOnline: Boolean = false
    var lastEnter: String? = null
    var accountPhoto: String? = null

    constructor()
    constructor(
        firstName: String?,
        lastName: String?,
        phoneNumber: String?,
        isOnline: Boolean,
        lastEnter: String?,
        accountPhoto: String?
    ) {
        this.firstName = firstName
        this.lastName = lastName
        this.phoneNumber = phoneNumber
        this.isOnline = isOnline
        this.lastEnter = lastEnter
        this.accountPhoto = accountPhoto
    }
}