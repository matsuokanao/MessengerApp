package com.example.messengerapp.ModelClasses

class Chatlist {

    private var id = ""

    constructor()

    constructor(id: String) {
        this.id = id
    }

    fun getId(): String? {
        return id
    }

    fun setId(id: String?){
        this.id = id!!
    }


}