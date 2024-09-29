package com.app.pojo

class Obj(val userID: Int, val id: Int, val title: String, val completed: Boolean) {
    override fun toString(): String {
        return "Obj(userID=$userID, id=$id, title='$title', completed=$completed)"
    }
}