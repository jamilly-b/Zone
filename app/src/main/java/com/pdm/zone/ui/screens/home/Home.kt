package com.pdm.zone.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.pdm.zone.data.model.EventCategory
import com.pdm.zone.ui.components.EventCard
import com.pdm.zone.ui.theme.Primary

@Composable
fun HomePage(
    navController: NavHostController,
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedCategory by remember { mutableStateOf<EventCategory?>(null) }

    // Efeito para atualizar eventos quando a tela é recomposta
    LaunchedEffect(Unit) {
        viewModel.loadEvents()
    }

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
                    LazyColumn(
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        // Filtros de categoria
                        item {
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
                        }

                        // Título da página
                        item {
                            Text(
                                text = "Próximos Eventos",
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp,
                                color = Primary,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }


                        // Lista de eventos próximos filtrados
                        val filteredEvents = selectedCategory?.let { cat ->
                            uiState.upcomingEvents.filter { it.category == cat }
                        } ?: uiState.upcomingEvents

                        if (filteredEvents.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(100.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Nenhum evento encontrado",
                                        color = Color.Gray
                                    )
                                }
                            }
                        } else {
                            items(filteredEvents) { event ->
                                EventCard(
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