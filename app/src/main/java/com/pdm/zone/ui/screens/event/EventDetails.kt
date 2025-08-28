package com.pdm.zone.ui.screens.event

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
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
import androidx.compose.ui.draw.clip
import com.pdm.zone.BuildConfig
import androidx.compose.ui.text.font.FontWeight

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
                        onInterestClick = { viewModel.toggleInterest() },
                        navController = navController
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
    onInterestClick: () -> Unit,
    navController: NavHostController
) {
    val context = LocalContext.current
    val dateFormatter = remember { SimpleDateFormat("dd 'de' MMMM", Locale("pt", "BR")) }
    val formattedDate = remember(event.eventDate, event.startTime, event.endTime) {
        event.eventDate?.let { date ->
            val base = dateFormatter.format(date).replaceFirstChar { it.uppercase() }
            if (event.startTime != null && event.endTime != null) {
                "$base | ${event.startTime} - ${event.endTime}"
            } else base
        } ?: "Data não informada"
    }

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
                // Título
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.headlineLarge,
                    color = Primary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                // Autor
                Text(
                    text = "Por @${event.creatorUsername}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Primary,
                    modifier = Modifier.clickable { navController.navigate("profile/${event.creatorUsername}") }
                )
                Spacer(modifier = Modifier.height(12.dp))
                // Data (calendário)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = formattedDate,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                // Local (ícone de casa + nome)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = event.location,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onConfirmClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isConfirmed) Primary else Color.LightGray,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(50),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
                    ) {
                        Text("Eu vou")
                    }
                    IconButton(onClick = onInterestClick) {
                        Icon(
                            imageVector = if (isInterested) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (isInterested) "Tenho Interesse" else "Remover dos Interesses",
                            tint = if (isInterested) Primary else Color.Gray,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                // Métricas
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        text = "${event.confirmedCount} Confirmados",
                        style = MaterialTheme.typography.bodySmall,
                        color = Secondary,
                        modifier = Modifier.clickable {
                            navController.navigate("userList/confirmados/${event.id}")
                        }
                    )
                    Text(
                        text = "${event.interestedCount} Interessados",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Descrição",
                    style = MaterialTheme.typography.titleMedium,
                    color = Primary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = event.description,
                    style = MaterialTheme.typography.bodyLarge,
                    lineHeight = 24.sp,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Localização",
                    style = MaterialTheme.typography.titleMedium,
                    color = Primary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = event.location,
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                EventLocationMap(event)
            }
        }
    }
}

@Composable
private fun EventLocationMap(event: Event) {
    val lat = event.latitude
    val lng = event.longitude
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 180.dp)
    ) {
        if (lat != null && lng != null) {
            var useFallback by remember { mutableStateOf(false) }
            val key = BuildConfig.PLACES_API_KEY
            val staticMapUrl = remember(lat, lng, useFallback, key) {
                if (useFallback || key.isBlank() || key == "YOUR_API_KEY") {
                    "https://staticmap.openstreetmap.de/staticmap.php?center=$lat,$lng&zoom=15&size=640x320&markers=$lat,$lng,red-pushpin"
                } else {
                    "https://maps.googleapis.com/maps/api/staticmap?center=$lat,$lng&zoom=15&size=640x320&scale=2&markers=color:red%7C$lat,$lng&key=$key"
                }
            }
            var hadError by remember { mutableStateOf(false) }
            fun openExternalMap() {
                val gmmIntentUri = Uri.parse("geo:$lat,$lng?q=$lat,$lng(${Uri.encode(event.title.ifBlank { "Evento" })})")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply { setPackage("com.google.android.apps.maps") }
                if (mapIntent.resolveActivity(context.packageManager) == null) {
                    context.startActivity(
                        Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=$lat,$lng"))
                    )
                } else context.startActivity(mapIntent)
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { openExternalMap() }
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(staticMapUrl)
                        .listener(
                            onSuccess = { _, _ ->
                                hadError = false
                            },
                            onError = { _, _ ->
                                if (!useFallback) {
                                    useFallback = true
                                } else {
                                    hadError = true
                                }
                            }
                        )
                        .build(),
                    contentDescription = "Mapa do evento",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.placeholder_event),
                    error = painterResource(R.drawable.placeholder_event)
                )
                if (hadError) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.4f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Mapa indisponível",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        } else {
            Text(
                text = "Erro no mapa da localização.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    }
}
