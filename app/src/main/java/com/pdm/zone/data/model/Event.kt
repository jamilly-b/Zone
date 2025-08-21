package com.pdm.zone.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName
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
    var interestedUsers: List<String> = emptyList(),

    // Adicionamos estas propriedades com PropertyName para que o Firestore as reconheça
    // mesmo sendo campos calculados no lado do cliente
    @get:PropertyName("confirmedCount")
    @PropertyName("confirmedCount")
    var _confirmedCount: Int = 0,

    @get:PropertyName("interestedCount")
    @PropertyName("interestedCount")
    var _interestedCount: Int = 0
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

    // Propriedades calculadas
    @get:Exclude  // Excluir do Firestore, pois usamos os campos _confirmedCount e _interestedCount para serialização
    val confirmedCount: Int
        get() = attendees.size

    @get:Exclude  // Excluir do Firestore, pois usamos os campos _interestedCount e _interestedCount para serialização
    val interestedCount: Int
        get() = interestedUsers.size
}