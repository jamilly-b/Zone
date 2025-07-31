package com.pdm.zone.ui.screens.user

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.pdm.zone.R
import com.pdm.zone.data.model.Event
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserCreatedEventList(
    username: String?,
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit
)
{    val db = FirebaseFirestore.getInstance()
    var events by remember { mutableStateOf<List<Event>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(username) {
        if (username != null) {
            try {
                val result = db.collection("events")
                    .whereEqualTo("creatorUsername", username)
                    .get()
                    .await()

                events = result.toObjects(Event::class.java)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Eventos de $username") })
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when {
                isLoading -> CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                events.isEmpty() -> Text("Nenhum evento encontrado.", modifier = Modifier.padding(16.dp))
                else -> LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    items(events) { event ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Row (modifier = modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .clickable { onClick(event.id) },
                                verticalAlignment = Alignment.CenterVertically)
                            {
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
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(text = event.title, style = MaterialTheme.typography.titleMedium)
                                    Text(text = event.description, style = MaterialTheme.typography.bodyMedium)
                                }
                            }

                        }
                    }
                }
            }
        }
    }
}

