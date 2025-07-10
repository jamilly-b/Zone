package com.pdm.zone.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.pdm.zone.data.model.Event
import com.pdm.zone.ui.components.EventCard

@Composable
fun HomePage(navController: NavHostController) {
    val events = listOf(
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
            interestedCount = 15,
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
            interestedCount = 20,
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
            interestedCount = 35,
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
            interestedCount = 40,
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
            interestedCount = 18,
            price = "R$ 10",
            distance = "2.0 km",
            date = "14/07",
            startTime = "20h"
        )
    )


    Scaffold { innerPadding ->
        LazyColumn(
            contentPadding = PaddingValues(
                top = innerPadding.calculateTopPadding() + 16.dp,
                bottom = 16.dp,
                start = 16.dp,
                end = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(events) { event ->
                EventCard(
                    event = event,
                    onCardClick = {
                        navController.navigate("eventDetails/${event.id}")
                    }
                )
            }
        }
    }
}