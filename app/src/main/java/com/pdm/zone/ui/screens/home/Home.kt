package com.pdm.zone.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.pdm.zone.data.model.EventCategory
import com.pdm.zone.ui.components.EventCard
import kotlin.text.category

@Composable
fun HomePage(
    navController: NavHostController,
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedCategory by remember { mutableStateOf<EventCategory?>(null) }

    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
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
                    Column {
                        // Filtros de categoria
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            items(EventCategory.values()) { category ->
                                FilterChip(
                                    selected = selectedCategory == category,
                                    onClick = {
                                        selectedCategory = if (selectedCategory == category) null else category
                                    },
                                    label = { Text(category.displayName) }
                                )
                            }
                        }

                        // Lista de eventos filtrados
                        val filteredEvents = selectedCategory?.let { cat ->
                            uiState.events.filter { it.category == cat }
                        } ?: uiState.events

                        LazyColumn(
                            contentPadding = PaddingValues(all = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(filteredEvents, key = { it.id }) { event ->
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
            }
        }
    }
}