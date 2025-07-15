package com.pdm.zone.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.pdm.zone.R
import com.pdm.zone.data.model.Event
import com.pdm.zone.ui.theme.Primary

@Composable
fun ListPage(navController: NavHostController) {
    val eventsList = remember { getConfirmedEvents() }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        items(eventsList, key = { it.id }) { event ->
            EventConfirmedItem(
                event = event,
                onClick = {
                    navController.navigate("eventDetails/${event.id}")
                }
            )
        }
    }
}

@Composable
fun EventConfirmedItem(
    event: Event,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.placeholder_event),
            contentDescription = "Imagem do evento",
            modifier = Modifier
                .size(120.dp)
                .padding(end = 8.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(text = event.title, fontSize = 20.sp, color = Primary)
            Text(text = event.location, fontSize = 14.sp)
            Text(text = event.dateTime, fontSize = 14.sp)
        }
    }
}

private fun getConfirmedEvents(): List<Event> = List(10) { i ->
    Event(
        id = i,
        title = "Evento $i",
        location = "Local $i",
        dateTime = "${10 + i}/07 - 18:00",
        description = "Descrição do evento $i",
        imageRes = 0,
        category = "Categoria $i"
    )
}
