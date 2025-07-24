package com.pdm.zone.data.model

data class User(
    val uid: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val username: String = "",
    val profilePic: String? = null,
    val biography: String? = null,
    val dateOfBirth: String? = null,
    val createdTime: String = "",
    val followers: List<String> = emptyList(),
    val following: List<String> = emptyList(),
    val createdEvents: List<String> = emptyList(),
    val favoriteEvents: List<String> = emptyList()
)