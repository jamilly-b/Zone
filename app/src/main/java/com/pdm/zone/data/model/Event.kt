package com.pdm.zone.data.model

data class Event(
    val id: Int,
    val title: String,
    val location: String,
    val dateTime: String,
    val description: String,
    val imageRes: Int,
    val category: String,
    val attendees: List<String> = emptyList(),
    val confirmedCount: Int = 0,
    val interestedCount: Int = 0,
    val price: String? = null,
    val distance: String? = null,
    val startTime: String? = null,
    val endTime: String? = null,
    val date: String? = null,
    val creatorId: String = "",
    val createdTime: String = ""
)