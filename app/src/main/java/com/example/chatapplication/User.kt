package com.example.chatapplication

class User {
    var name:String? = null
    var email: String? = null
    var uid:String? = null
    var FCMToken:String? = null

    constructor() {}

    constructor(name: String?, email: String?, uid: String?, FCMToken: String?) {
        this.name = name
        this.email = email
        this.uid = uid
        this.FCMToken = FCMToken
    }
}