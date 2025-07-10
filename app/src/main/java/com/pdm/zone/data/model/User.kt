package com.pdm.zone.data.model

data class User(
    val uid: String = "",
    val name: String = "",
    val username: String = "",
    val photoUrl: String? = null,
    val biography: String? = null,
    val birthday: String? = null,
    val createdTime: String = "",
    val followers: List<String> = emptyList(),
    val following: List<String> = emptyList(),
    val createdEvents: List<String> = emptyList(),
    val favoriteEvents: List<String> = emptyList()
)