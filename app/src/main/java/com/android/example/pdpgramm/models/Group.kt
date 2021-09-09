package com.android.example.pdpgramm.models

class Group {
    var name: String? = null
    var groupPhoto: String? = null
    var accounts: ArrayList<Account>? = null
    var createdAccount: String? = null
    var createdDate: String? = null
    var uid: String? = null

    constructor()
    constructor(
        name: String?,
        groupPhoto: String?,
        accounts: ArrayList<Account>?,
        createdAccount: String?,
        createdDate: String?,
        uid: String?
    ) {
        this.name = name
        this.groupPhoto = groupPhoto
        this.accounts = accounts
        this.createdAccount = createdAccount
        this.createdDate = createdDate
        this.uid = uid
    }

}