package com.pdm.zone.ui.screens.event

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.pdm.zone.R
import com.pdm.zone.data.model.Event
import com.pdm.zone.ui.theme.Primary
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ListPage(
    navController: NavHostController,
    viewModel: ListViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

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
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            else -> {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(uiState.events, key = { it.id }) { event ->
                        EventConfirmedItem(
                            event = event,
                            onClick = {
                                navController.navigate("eventDetails/${event.id}")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EventConfirmedItem(
    event: Event,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy | HH:mm", Locale("pt", "BR")) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(event.imageUrl)
                .crossfade(true)
                .build(),
            placeholder = painterResource(R.drawable.placeholder_event),
            contentDescription = event.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(100.dp)
                .padding(end = 8.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(text = event.title, fontSize = 20.sp, color = Primary)
            Text(text = event.location, fontSize = 14.sp)
            event.eventDate?.let {
                Text(text = dateFormatter.format(it), fontSize = 14.sp)
            }
        }
    }
}