package com.pdm.zone.ui.screens.user

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.pdm.zone.data.model.User
import com.pdm.zone.ui.screens.login.LoginActivity
import com.pdm.zone.ui.theme.Primary

@Composable
fun ProfilePage() {
    val user = User(
        uid = "1",
        name = "Nome Sobrenome",
        username = "Username",
        photoUrl = null,
        biography = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nulla nec augue a ligula iaculis aliquet in sit amet nisl.",
        birthday = "1990-01-01",
        createdTime = "2023-01-01",
        followers = listOf("1", "2", "3", "4"),
        following = listOf("1", "3", "4", "5", "6", "7") ,
        createdEvents = listOf("1", "3")
    )

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Próximos eventos", "Eventos passados")
    val currentUserId = "1"

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        item {
            ProfileHeader(user = user)
        }

        item {
            ProfileStats(user = user)
        }

        if (user.uid == currentUserId) {
            item { ProfileActions() }
        }
        else{
            item {
                FollowActions(
                    user = user,
                    currentUserId = currentUserId,
                    onFollowChanged = { followed -> /* Atualiza no baco de dados */}
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
        // Foto do perfil
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(Color.Gray.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            // Placeholder para foto do usuário
            if (user.photoUrl != null) {
                Text(
                    text = user.name.first().toString(),
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
            } else {
                Text(
                    text = user.name.first().toString(),
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Nome
        Text(
            text = user.name,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Primary
        )

        // Username
        Text(
            text = user.username,
            fontSize = 16.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Biografia
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
private fun ProfileStats(user: User) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StatItem(count = user.following.size.toString(), label = "Seguindo")
        StatItem(count = user.followers.size.toString(), label = "Seguidores")
        StatItem(count = user.createdEvents.size.toString(), label = "Eventos criados")
    }
}

@Composable
private fun StatItem(count: String, label: String) {
    Column(
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
                activity?.startActivity(Intent(activity, LoginActivity::class.java))
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
private fun FollowActions(
    user: User,
    currentUserId: String,
    onFollowChanged: (Boolean) -> Unit
) {
    var isFollowing by remember { mutableStateOf(user.followers.contains(currentUserId)) }

    Button(
        onClick = {
            isFollowing = !isFollowing
            onFollowChanged(isFollowing)
        },
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