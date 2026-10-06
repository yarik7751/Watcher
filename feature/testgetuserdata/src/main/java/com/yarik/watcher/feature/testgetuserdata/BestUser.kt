package com.yarik.watcher.feature.testgetuserdata.datasource

data class BestUser(
    val id: Int,
    val name: String,
    val lastName: String,
    val age: Int,
    val phoneNumber: String
) {

    fun isEmpty(): Boolean = id == -1

    companion object {
        val EMPTY = BestUser(
            id = -1,
            name = "",
            lastName = "",
            age = 0,
            phoneNumber = ""
        )
    }
}