package com.pdm.zone.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.pdm.zone.ui.components.EventCard
import com.pdm.zone.viewmodel.EventViewModel

@Composable
fun HomePage(navController: NavHostController, eventViewModel: EventViewModel) {
    val events = eventViewModel.allEvents

    Scaffold { innerPadding ->
        LazyColumn(
            contentPadding = PaddingValues(
                top = innerPadding.calculateTopPadding() + 16.dp,
                bottom = 16.dp,
                start = 16.dp,
                end = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(events) { event ->
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