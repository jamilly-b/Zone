import com.pdm.zone.data.model.User
import com.pdm.zone.viewmodel.EventViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.pdm.zone.data.model.Event
import com.pdm.zone.ui.theme.Primary
import com.pdm.zone.ui.theme.Secondary
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailsPage(
    eventId: Int,
    navController: NavHostController,
    event: Event? = null,
    eventViewModel: EventViewModel,
    onBackClick: () -> Unit = {},
    onConfirmPresence: (Event) -> Unit = {}
) {
    val currentUser by eventViewModel.currentUser
    
    // Buscar o evento mais atualizado do ViewModel
    val currentEvent = eventViewModel.getEventById(eventId) ?: event ?: Event(
        id = 1,
        title = "Lorem ipsum dolor",
        location = "Rua XXX, Várzea - Recife, PE",
        dateTime = "21 de jun | 19h - 23h",
        description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nulla nec augue a ligula iaculis aliquam in sit amet libero.",
        imageRes = android.R.drawable.ic_menu_gallery,
        category = "Evento",
        attendees = listOf(),
        confirmedCount = 30,
        date = "21 de jun",
        startTime = "19h",
        endTime = "23h",
        creatorId = "usuario_criador"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Imagem e botão de voltar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
                    .background(Color.Gray.copy(alpha = 0.3f))
            )

            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = currentEvent.title,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Primary
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        text = "${currentEvent.confirmedCount} Confirmados",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                // Confirmar presença
                val isConfirmed = currentUser.favoriteEvents.contains(currentEvent.id.toString())

                IconButton(
                    onClick = {
                        if (isConfirmed) {
                            eventViewModel.unconfirmEventPresence(currentEvent)
                        } else {
                            eventViewModel.confirmEventPresence(currentEvent)
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (isConfirmed) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = if (isConfirmed) "Desfavoritar" else "Favoritar",
                        tint = Primary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            // Descrição
            Text(
                text = currentEvent.description,
                fontSize = 14.sp,
                color = Color.Black,
                lineHeight = 20.sp,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            EventInfoSection(event = currentEvent)

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun EventInfoSection(event: Event) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Localização
        EventInfoItem(
            icon = Icons.Default.LocationOn,
            text = event.location
        )

        // Horário
        val timeText = if (event.startTime != null && event.endTime != null) {
            "${event.startTime} - ${event.endTime}"
        } else {
            event.dateTime
        }

        // Data
        event.date?.let { date ->
            EventInfoItem(
                icon = Icons.Default.DateRange,
                text = date
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
            fontSize = 14.sp,
            color = Color.Black
        )
    }
}