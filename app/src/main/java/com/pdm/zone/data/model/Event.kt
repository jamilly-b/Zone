package com.pdm.zone.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Event(
    @DocumentId
    var id: String = "",
    var title: String = "",
    var description: String = "",
    var location: String = "",
    var category: EventCategory = EventCategory.FESTAS,
    var imageUrl: String? = null,
    var creatorId: String = "",
    var creatorUsername: String = "",

    // Campos de data e hora
    @ServerTimestamp
    var eventDate: Date? = null,
    var startTime: String? = null,
    var endTime: String? = null,

    var attendees: List<String> = emptyList(),
    var interestedUsers: List<String> = emptyList()
) {
    constructor() : this(
        id = "",
        title = "",
        description = "",
        location = "",
        category = EventCategory.FESTAS,
        imageUrl = null,
        creatorId = "",
        creatorUsername = "",
        eventDate = null,
        startTime = null,
        endTime = null,
        attendees = emptyList(),
        interestedUsers = emptyList()
    )

    val confirmedCount: Int
        get() = attendees.size

    val interestedCount: Int
        get() = interestedUsers.size
}