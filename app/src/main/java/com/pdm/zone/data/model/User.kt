package com.pdm.zone.data.model

data class User(
    val id: Int,
    val name: String,
    val avatar: String? = null,
    val initials: String = name.take(2).uppercase()
)