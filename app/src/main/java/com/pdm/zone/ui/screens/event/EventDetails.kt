package com.pdm.zone.ui.screens.event

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.pdm.zone.data.model.Event
import com.pdm.zone.ui.theme.Primary
import com.pdm.zone.ui.theme.Secondary
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailsPage (
    eventId: String,
    navController: NavHostController,
    viewModel: EventDetailsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(eventId) {
        viewModel.loadEvent(eventId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
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
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
        ) {
            when {
                // Estado de carregamento
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                // Estado de erro
                uiState.error != null -> {
                    Text(
                        text = uiState.error!!,
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.Red
                    )
                }
                // Estado de sucesso
                uiState.event != null -> {
                    EventDetailsContent(event = uiState.event!!)
                }
            }
        }
    }
}

@Composable
private fun EventDetailsContent(event: Event) {
    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
        ) {
            // TODO: Carregar a imagem da event.imageUrl usando uma biblioteca como Coil
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
                    .background(Color.Gray.copy(alpha = 0.3f))
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
                text = event.title,
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
                        text = "${event.confirmedCount} Confirmados",
                        fontSize = 12.sp,
                        color = Secondary
                    )
                    Text(
                        text = "${event.interestedCount} Interessados",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                // Favoritar
                IconButton(onClick = { /* TODO: Implementar lógica de favoritar */ }) {
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
                text = event.description,
                fontSize = 14.sp,
                color = Color.Black,
                lineHeight = 20.sp,
                modifier = Modifier.padding(vertical = 5.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Informações do evento
            EventInfoSection(event = event)

            Spacer(modifier = Modifier.height(16.dp))

            // Criado por
            Text(
                text = "Criado por @${event.creatorUsername}",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}


@Composable
fun EventInfoSection(event: Event) {
    val dateFormatter = remember { SimpleDateFormat("dd 'de' MMMM", Locale("pt", "BR")) }

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Localização
        EventInfoItem(
            icon = Icons.Default.LocationOn,
            text = event.location
        )

        // Data e Horário
        event.eventDate?.let { date ->
            val formattedDate = dateFormatter.format(date).replaceFirstChar { it.uppercase() }
            val timeText = if (event.startTime != null && event.endTime != null) {
                "$formattedDate | ${event.startTime} - ${event.endTime}"
            } else {
                formattedDate
            }
            EventInfoItem(
                icon = Icons.Default.DateRange,
                text = timeText
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