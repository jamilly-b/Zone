package com.pdm.zone.ui.screens.event

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.content.pm.PackageManager
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
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.pdm.zone.R
import com.pdm.zone.data.model.Event
import com.pdm.zone.ui.theme.Primary
import com.pdm.zone.ui.theme.Secondary
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.ui.draw.clip
import com.google.android.gms.maps.GoogleMap
import com.pdm.zone.BuildConfig

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

                    // ------------------ Ícones provisórios ------------------
                    Row {
                        IconButton(onClick = onInterestClick) {
                            Icon(
                                imageVector = if (isInterested) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = if (isInterested) "Tenho Interesse" else "Remover dos Interesses",
                                tint = if (isInterested) Primary else Color.Gray,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }

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
                    color = Primary,
                    modifier = Modifier.clickable {
                        navController.navigate("profile/${event.creatorUsername}")
                    }
                )
                Spacer(modifier = Modifier.height(24.dp))
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

//    // Logs de diagnóstico da chave
//    LaunchedEffect(Unit) {
//        val buildConfigKey = BuildConfig.PLACES_API_KEY
//        val appInfo = try {
//            context.packageManager.getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
//        } catch (e: Exception) { null }
//        val manifestKey = appInfo?.metaData?.getString("com.google.android.geo.API_KEY")
//        Log.d("MAPS_KEY_DEBUG", "BuildConfig.PLACES_API_KEY='${'$'}buildConfigKey'")
//        Log.d("MAPS_KEY_DEBUG", "Manifest meta-data com.google.android.geo.API_KEY='${'$'}manifestKey'")
//    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 180.dp)
    ) {
        Text(
            text = "Localização",
            style = MaterialTheme.typography.titleMedium,
            color = Primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        if (lat != null && lng != null) {
            val mapView = remember { MapView(context) }
            // Lifecycle handling
            DisposableEffect(mapView) {
                mapView.onCreate(null)
                mapView.onResume()
                onDispose {
                    mapView.onPause()
                    mapView.onDestroy()
                }
            }
            val target = remember(lat, lng) { LatLng(lat, lng) }
            // Configure map once
            LaunchedEffect(mapView, target) {
                mapView.getMapAsync { googleMap: GoogleMap ->
                    googleMap.uiSettings.isMapToolbarEnabled = false
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(target, 15f))
                    googleMap.clear()
                    googleMap.addMarker(
                        MarkerOptions()
                            .position(target)
                            .title(event.title)
                            .snippet(event.location)
                    )
                }
            }
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
                AndroidView(factory = { mapView }) { mView ->
                    // Add listeners after map ready
                    mView.getMapAsync { googleMap ->
                        googleMap.setOnMapClickListener { openExternalMap() }
                        googleMap.setOnMarkerClickListener {
                            openExternalMap(); true
                        }
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