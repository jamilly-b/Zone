package com.pdm.zone.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.pdm.zone.data.model.Event
import com.pdm.zone.data.model.User
import androidx.compose.runtime.State


class EventViewModel : ViewModel() {
    private val _confirmedEvents = mutableStateOf<List<Event>>(emptyList())
    val confirmedEvents: List<Event> get() = _confirmedEvents.value

    private val _currentUser = mutableStateOf(
        User(
            uid = "1",
            name = "Nome Sobrenome",
            username = "Username",
            photoUrl = null,
            biography = "Exemplo de biografia",
            birthday = "1990-01-01",
            createdTime = "2023-01-01",
            favoriteEvents = listOf(),
            followers = listOf("1", "2", "3", "4"),
            following = listOf("1", "3", "4", "5", "6", "7"),
            createdEvents = listOf("1", "3")
        )
    )

    val currentUser: State<User> get() = _currentUser

    fun confirmEventPresence(event: Event) {
        val isAlreadyConfirmed = _currentUser.value.favoriteEvents.contains(event.id.toString())

        if (!isAlreadyConfirmed) {
            _currentUser.value = _currentUser.value.copy(
                favoriteEvents = _currentUser.value.favoriteEvents + event.id.toString()
            )

            _confirmedEvents.value = _confirmedEvents.value + event
        }
    }

    fun unconfirmEventPresence(event: Event) {
        val isConfirmed = _currentUser.value.favoriteEvents.contains(event.id.toString())

        if (isConfirmed) {
            _currentUser.value = _currentUser.value.copy(
                favoriteEvents = _currentUser.value.favoriteEvents - event.id.toString()
            )

            _confirmedEvents.value = _confirmedEvents.value.filter { it.id != event.id }
        }
    }

    fun getUpcomingEvents(): List<Event> {
        return _confirmedEvents.value
    }

    fun getPastEvents(): List<Event> {
        return emptyList()
    }
}