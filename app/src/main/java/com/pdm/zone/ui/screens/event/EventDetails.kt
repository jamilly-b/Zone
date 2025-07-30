package com.pdm.zone.ui.screens.event

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.pdm.zone.R
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
                title = {
                    Text(
                        text = uiState.event?.title ?: "",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
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
                uiState.event != null -> {
                    EventDetailsContent(
                        event = uiState.event!!,
                        isConfirmed = uiState.isCurrentUserConfirmed,
                        isInterested = uiState.isCurrentUserInterested,
                        onConfirmClick = { viewModel.togglePresenceConfirmation() },
                        onInterestClick = { viewModel.toggleInterest() }
                    )
                }
            }
        }
    }
}

@Composable
private fun EventDetailsContent(
    event: Event,
    isConfirmed: Boolean,
    isInterested: Boolean,
    onConfirmClick: () -> Unit,
    onInterestClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(event.imageUrl)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.placeholder_event),
                contentDescription = event.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                contentScale = ContentScale.Crop
            )
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.headlineLarge,
                    color = Primary,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text(
                            text = "${event.confirmedCount} Confirmados",
                            style = MaterialTheme.typography.bodySmall,
                            color = Secondary
                        )
                        Text(
                            text = "${event.interestedCount} Interessados",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }

                    // ------------------ Ícones provisórios ------------------
                    Row {
                        IconButton(onClick = onConfirmClick) {
                            Icon(
                                imageVector = if (isConfirmed) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = if (isConfirmed) "Remover confirmação" else "Confirmar Presença",
                                tint = Primary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        IconButton(onClick = onInterestClick) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Tenho Interesse",
                                tint = if (isInterested) Primary else Color.Gray,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = event.description,
                    style = MaterialTheme.typography.bodyLarge,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(24.dp))
                EventInfoSection(event = event)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Criado por @${event.creatorUsername}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

                // CORREÇÃO: O botão grande de confirmação foi REMOVIDO daqui.
            }
        }
    }
}


@Composable
fun EventInfoSection(event: Event) {
    val dateFormatter = remember { SimpleDateFormat("dd 'de' MMMM", Locale("pt", "BR")) }

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        EventInfoItem(
            icon = Icons.Default.LocationOn,
            text = event.location
        )

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
            style = MaterialTheme.typography.bodyMedium
        )
    }
}