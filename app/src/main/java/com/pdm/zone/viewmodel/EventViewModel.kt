package com.pdm.zone.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.pdm.zone.data.model.Event
import com.pdm.zone.data.model.User
import androidx.compose.runtime.State


class EventViewModel : ViewModel() {
    private val _confirmedEvents = mutableStateOf<List<Event>>(emptyList())
    val confirmedEvents: List<Event> get() = _confirmedEvents.value

    // Lista de todos os eventos disponíveis
    private val _allEvents = mutableStateOf<List<Event>>(
        listOf(
            Event(
                id = 1,
                title = "Noite do Karaokê",
                location = "Estelita, Cabanga",
                dateTime = "01/07 | 19h",
                description = "Prepare-se para uma noite de karaokê emocionante e cheia de energia! Venha soltar a voz e mostrar seu talento musical na nossa casa! Noite do Karaokê. #Karaoke #NoiteDoKaraoke",
                imageRes = android.R.drawable.ic_menu_gallery,
                category = "Música",
                attendees = listOf("João", "Maria", "Pedro", "Ana", "Carlos"),
                confirmedCount = 30,
                price = "$",
                distance = "8.4 km",
                date = "01/07",
                startTime = "19h"
            ),
            Event(
                id = 2,
                title = "O Futuro nos Conecta",
                location = "Recife, PE",
                dateTime = "05/07 | 18h",
                description = "Evento sobre tecnologia e inovação que vai mudar o futuro.",
                imageRes = android.R.drawable.ic_menu_gallery,
                category = "Tecnologia",
                attendees = listOf("Tech", "Innovation", "Future"),
                confirmedCount = 45,
                price = "$",
                distance = "2.5 km",
                date = "05/07",
                startTime = "18h"
            ),
            Event(
                id = 3,
                title = "Festival de Arte Urbana",
                location = "Praça do Arsenal, Recife",
                dateTime = "10/07 | 14h",
                description = "Um festival ao ar livre com grafite, música e performance de artistas urbanos. Celebre a cultura de rua com arte e criatividade.",
                imageRes = android.R.drawable.ic_menu_gallery,
                category = "Arte",
                attendees = listOf("Lucas", "Fernanda", "Aline"),
                confirmedCount = 60,
                price = "Gratuito",
                distance = "1.1 km",
                date = "10/07",
                startTime = "14h"
            ),
            Event(
                id = 4,
                title = "Feira Vegana",
                location = "Parque da Jaqueira, Recife",
                dateTime = "12/07 | 10h",
                description = "Uma feira com comidas veganas, cosméticos naturais e sustentabilidade. Perfeita para quem busca uma vida mais consciente.",
                imageRes = android.R.drawable.ic_menu_gallery,
                category = "Cultura",
                attendees = listOf("Rafaela", "Caio", "Bianca", "Thiago"),
                confirmedCount = 80,
                price = "Gratuito",
                distance = "3.2 km",
                date = "12/07",
                startTime = "10h"
            ),
            Event(
                id = 5,
                title = "Sarau de Poesia e Café",
                location = "Café Literário, Boa Vista",
                dateTime = "14/07 | 20h",
                description = "Noite de poesias, declamações e um ambiente acolhedor com café e boas conversas. Traga seus versos ou apenas aprecie.",
                imageRes = android.R.drawable.ic_menu_gallery,
                category = "Literatura",
                attendees = listOf("Júlia", "Enzo", "Sofia"),
                confirmedCount = 25,
                price = "R$ 10",
                distance = "2.0 km",
                date = "14/07",
                startTime = "20h"
            )
        )
    )
    
    val allEvents: List<Event> get() = _allEvents.value

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

    // Buscar evento por ID
    fun getEventById(eventId: Int): Event? {
        return _allEvents.value.find { it.id == eventId }
    }

    fun confirmEventPresence(event: Event) {
        val isAlreadyConfirmed = _currentUser.value.favoriteEvents.contains(event.id.toString())

        if (!isAlreadyConfirmed) {
            // Atualizar lista de eventos favoritos do usuário
            _currentUser.value = _currentUser.value.copy(
                favoriteEvents = _currentUser.value.favoriteEvents + event.id.toString()
            )

            // Atualizar contador do evento na lista principal
            _allEvents.value = _allEvents.value.map { 
                if (it.id == event.id) {
                    it.copy(confirmedCount = it.confirmedCount + 1)
                } else {
                    it
                }
            }

            // Adicionar evento à lista de confirmados
            _confirmedEvents.value = _confirmedEvents.value + event.copy(confirmedCount = event.confirmedCount + 1)
        }
    }

    fun unconfirmEventPresence(event: Event) {
        val isConfirmed = _currentUser.value.favoriteEvents.contains(event.id.toString())

        if (isConfirmed) {
            // Remover da lista de eventos favoritos do usuário
            _currentUser.value = _currentUser.value.copy(
                favoriteEvents = _currentUser.value.favoriteEvents - event.id.toString()
            )

            // Atualizar contador do evento na lista principal
            _allEvents.value = _allEvents.value.map { 
                if (it.id == event.id) {
                    it.copy(confirmedCount = maxOf(0, it.confirmedCount - 1))
                } else {
                    it
                }
            }

            // Remover evento da lista de confirmados
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