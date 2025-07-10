package com.pdm.zone.ui.screens.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.pdm.zone.data.model.Event
import com.pdm.zone.ui.components.EventCard
import com.pdm.zone.ui.screens.event.FavoritesManager
import com.pdm.zone.ui.theme.OnPrimary
import com.pdm.zone.ui.theme.Primary
import com.pdm.zone.ui.theme.ZoneTheme

class EventsList : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ZoneTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ListPage()
                }
            }
        }
    }
}

@Composable
fun ListPage(navController: NavHostController? = null) {
    // Lista de todos os eventos (mesmo dados da HomePage)
    val allEvents = listOf(
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
    
    // Estado para forçar recomposição quando favoritos mudarem
    var refreshKey by remember { mutableStateOf(0) }
    
    // Filtrar eventos favoritos
    val favoriteEvents = remember(refreshKey) {
        allEvents.filter { event ->
            FavoritesManager.isFavorite(event.id)
        }
    }
    
    // Recompor a cada 1 segundo para capturar mudanças nos favoritos
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(1000)
            refreshKey++
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Cabeçalho
        Text(
            text = "Eventos Favoritos",
            fontWeight = FontWeight.Bold,
            color = Primary,
            fontSize = 24.sp,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        
        if (favoriteEvents.isEmpty()) {
            // Mensagem quando não há favoritos
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center)
            ) {
                Text(
                    text = "Nenhum evento favoritado ainda",
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Clique no coração ❤️ nos detalhes dos eventos para adicioná-los aqui",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )
            }
        } else {
            // Lista de eventos favoritos
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(favoriteEvents) { event ->
                    EventCard(
                        event = event,
                        onCardClick = {
                            navController?.navigate("eventDetails/${event.id}")
                        }
                    )
                }
            }
        }
    }
}