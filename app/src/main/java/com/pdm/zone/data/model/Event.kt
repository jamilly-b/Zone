package com.pdm.zone.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Event(
    @DocumentId
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val location: String = "",
    val category: String = "",
    val imageUrl: String? = null,
    val creatorId: String = "",
    val creatorUsername: String = "",

    // Campos de data e hora
    @ServerTimestamp
    val eventDate: Date? = null,
    val startTime: String? = null,
    val endTime: String? = null,

    val attendees: List<String> = emptyList(),
    val interestedUsers: List<String> = emptyList()
) {

    val confirmedCount: Int
        get() = attendees.size

    val interestedCount: Int
        get() = interestedUsers.size
}