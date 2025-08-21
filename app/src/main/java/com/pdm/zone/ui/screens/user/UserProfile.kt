package com.pdm.zone.ui.screens.user

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.pdm.zone.data.model.User
import com.pdm.zone.data.model.Event
import com.pdm.zone.ui.theme.Primary
import com.pdm.zone.ui.components.EventCard
import com.pdm.zone.ui.components.CompactEventCard
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.pdm.zone.R
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ProfilePage(
    navController: NavHostController,
    username: String,
    viewModel: ProfileViewModel = viewModel()
) {

    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(username) {
        viewModel.loadUserProfile(username)
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }

    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    if (uiState.user == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(uiState.error ?: "Perfil não encontrado.")
        }
        return
    }

    val user = uiState.user!!
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Próximos eventos", "Eventos passados")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        item { ProfileHeader(user) }
        item { ProfileStats(user = user, navController = navController) }

        if (uiState.isCurrentUserProfile) {
            item { ProfileActions() }
        } else {
            item {
                FollowActions(
                    isFollowing = uiState.isFollowing,
                    onClick = { viewModel.toggleFollow() }
                )
            }
        }

        item {
            EventTabs(
                selectedTab = selectedTab,
                tabs = tabs,
                onTabSelected = { selectedTab = it }
            )
        }

        when (selectedTab) {
            0 -> {
                if (uiState.isLoadingEvents) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                } else if (uiState.upcomingEvents.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Nenhum evento próximo",
                                color = Color.Gray,
                                fontSize = 16.sp
                            )
                        }
                    }
                } else {
                    items(uiState.upcomingEvents) { event ->
                        CompactEventCard(
                            event = event,
                            onCardClick = { navController.navigate("eventDetails/${it.id}") }
                        )
                    }
                }
            }
            1 -> {
                if (uiState.isLoadingEvents) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                } else if (uiState.pastEvents.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Nenhum evento passado",
                                color = Color.Gray,
                                fontSize = 16.sp
                            )
                        }
                    }
                } else {
                    items(uiState.pastEvents) { event ->
                        CompactEventCard(
                            event = event,
                            onCardClick = { navController.navigate("eventDetails/${it.id}") }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileStats(user: User, navController: NavHostController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StatItem(
            count = user.following.size.toString(),
            label = "Seguindo",
            onClick = {
                navController.navigate("userList/seguindo/${user.username}")
            }
        )
        StatItem(
            count = user.followers.size.toString(),
            label = "Seguidores",
            onClick = {
                navController.navigate("userList/seguidores/${user.username}")
            }
        )
        StatItem(
            count = user.createdEvents.size.toString(),
            label = "Eventos criados",
            onClick = {
                navController.navigate("EventList/createdEvents/${user.username}")
            }
        )
    }
}

@Composable
private fun FollowActions(
    isFollowing: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isFollowing) Color.Gray else Primary
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = if (isFollowing) "Deixar de seguir" else "Seguir",
            color = Color.White,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun ProfileHeader(user: User) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Foto do perfil com fallback
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(Color.Gray.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            if (!user.profilePic.isNullOrBlank()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(user.profilePic)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Foto do perfil",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                )
            } else {
                Text(
                    text = user.firstName.first().toString(),
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "${user.firstName} ${user.lastName}",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Primary
        )

        Text(
            text = user.username,
            fontSize = 16.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = user.biography ?: "",
            fontSize = 14.sp,
            color = Color.Black,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )
    }
}

@Composable
private fun StatItem(count: String, label: String, onClick: (() -> Unit)? = null) {
    Column(
        modifier = Modifier
            .clickable(enabled = onClick != null) { onClick?.invoke() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = count,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

@Composable
private fun ProfileActions() {
    val activity = LocalContext.current as? Activity

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(
            onClick = {
                activity?.startActivity(Intent(activity, UserProfileEdit::class.java))
            },
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = Primary
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "Editar Perfil",
                color = Color.White,
                fontSize = 14.sp
            )
        }

        Button(
            onClick = { /* Implementar compartilhamento */ },
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = Primary
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "Compartilhar perfil",
                color = Color.White,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun EventTabs(
    selectedTab: Int,
    tabs: List<String>,
    onTabSelected: (Int) -> Unit
) {
    TabRow(
        selectedTabIndex = selectedTab,
        modifier = Modifier.fillMaxWidth(),
        containerColor = Color.White,
        contentColor = Primary,
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                color = Primary
            )
        }
    ) {
        tabs.forEachIndexed { index, title ->
            Tab(
                selected = selectedTab == index,
                onClick = { onTabSelected(index) },
                text = {
                    Text(
                        text = title,
                        fontSize = 16.sp,
                        fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                    )
                },
                selectedContentColor = Primary,
                unselectedContentColor = Color.Gray
            )
        }
    }
}