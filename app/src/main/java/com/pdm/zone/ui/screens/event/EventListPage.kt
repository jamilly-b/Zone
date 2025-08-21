package com.pdm.zone.ui.screens.event

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.pdm.zone.R
import com.pdm.zone.data.model.Event
import com.pdm.zone.ui.components.CompactEventCard
import com.pdm.zone.ui.theme.Primary
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ListPage(
    navController: NavHostController,
    viewModel: ListViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR")) }
    val timeFormatter = remember { SimpleDateFormat("HH:mm", Locale("pt", "BR")) }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Título da página
            Text(
                text = "Meus Eventos",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = Primary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
            )

            // Conteúdo dos eventos
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                when {
                    uiState.isLoading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                    uiState.error != null -> {
                        Text(
                            text = uiState.error!!,
                            modifier = Modifier.align(Alignment.Center),
                            color = Color.Red
                        )
                    }
                    else -> {
                        if (uiState.confirmedEvents.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Você não confirmou presença em nenhum evento próximo",
                                    color = Color.Gray,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(vertical = 8.dp)
                            ) {
                                items(uiState.confirmedEvents) { event ->
                                    CompactEventCard(
                                        event = event,
                                        onCardClick = {
                                            navController.navigate("eventDetails/${it.id}")
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}