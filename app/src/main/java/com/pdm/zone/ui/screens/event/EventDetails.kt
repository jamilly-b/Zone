package com.pdm.zone.ui.screens.event

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.pdm.zone.data.model.Event
import com.pdm.zone.ui.theme.Primary
import com.pdm.zone.ui.theme.Secondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailsPage (
    eventId: Int,
    navController: NavHostController,
    event: Event? = null,
    onBackClick: () -> Unit = {},
    onConfirmPresence: (Event) -> Unit = {}
) {
    val displayEvent = event ?: Event(
        id = 1,
        title = "Lorem ipsum dolor",
        location = "Rua XXX, Várzea - Recife, PE",
        dateTime = "21 de jun | 19h - 23h",
        description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nulla nec augue a ligula iaculis aliquam in sit amet libero. Sed tempor odio eget elit pharetra, et eleifend lorem tempus. Pellentesque lobortis venenatis sapien at blandit. Quisque maximus urna bibendum efficitur lacinia. Nam accumsan mauris in ante tempus dictum.",
        imageRes = android.R.drawable.ic_menu_gallery,
        category = "Evento",
        attendees = listOf("João", "Maria", "Pedro", "Ana", "Carlos", "Lucia", "Roberto", "Fernanda"),
        confirmedCount = 30,
        interestedCount = 15,
        date = "21 de jun",
        startTime = "19h",
        endTime = "23h",
        creatorId = "Nome usuário"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header com imagem
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
        ) {
            // Imagem
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
                    .background(Color.Gray.copy(alpha = 0.3f))
            )

            // Barra de navegação
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = {navController.popBackStack()}) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }

        // Conteúdo principal
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Título do evento
            Text(
                text = displayEvent.title,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Primary,
                modifier = Modifier.padding(bottom = 1.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Confirmados e interessados
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        text = "${displayEvent.confirmedCount} Confirmados",
                        fontSize = 12.sp,
                        color = Secondary
                    )
                    Text(
                        text = "${displayEvent.interestedCount} Interessados",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                // Favoritar
                IconButton(onClick = { /* TO DO */ }) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Favoritar",
                        tint = Primary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            // Descrição
            Text(
                text = displayEvent.description,
                fontSize = 14.sp,
                color = Color.Black,
                lineHeight = 20.sp,
                modifier = Modifier.padding(vertical = 5.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Informações do evento
            EventInfoSection(event = displayEvent)

            Spacer(modifier = Modifier.height(16.dp))

            // Criado por
//            displayEvent.createdBy?.let { creator ->
//                Text(
//                    text = "Criado por *$creator*",
//                    fontSize = 12.sp,
//                    color = Color.Gray
//                )
//            }
        }
    }
}

@Composable
fun EventInfoSection(event: Event) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Localização
        EventInfoItem(
            icon = Icons.Default.LocationOn,
            text = event.location
        )

        // Horário
        val timeText = if (event.startTime != null && event.endTime != null) {
            "${event.startTime} - ${event.endTime}"
        } else {
            event.dateTime
        }

        // Data
        event.date?.let { date ->
            EventInfoItem(
                icon = Icons.Default.DateRange,
                text = date
            )
        }
    }
}

@Composable
fun EventInfoItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = Color(0xFF8B5A96),
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = text,
            fontSize = 14.sp,
            color = Color.Black
        )
    }
}