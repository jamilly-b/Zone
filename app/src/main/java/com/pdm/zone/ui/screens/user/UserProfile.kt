package com.pdm.zone.ui.screens.user

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pdm.zone.data.model.User
import com.pdm.zone.ui.theme.Primary
import kotlinx.coroutines.tasks.await

@Composable
fun ProfilePage(navController: NavHostController) {
    val context = LocalContext.current
    val currentUser = FirebaseAuth.getInstance().currentUser
    var user by remember { mutableStateOf<User?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        if (currentUser != null) {
            try {
                val snapshot = FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(currentUser.uid)
                    .get()
                    .await()

                user = snapshot.toObject(User::class.java)?.copy(uid = currentUser.uid)
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Erro ao carregar perfil", Toast.LENGTH_SHORT).show()
            } finally {
                isLoading = false
            }
        } else {
            Toast.makeText(context, "Usuário não logado", Toast.LENGTH_SHORT).show()
            isLoading = false
        }
    }

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Próximos eventos", "Eventos passados")
    val currentUserId = "1"

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (user != null) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            item { ProfileHeader(user!!) }
            item { ProfileStats(user = user!!, navController = navController) }

            if (user!!.uid == currentUser?.uid) {
                item { ProfileActions() }
            } else {
                item {
                    FollowActions(
                        user = user!!,
                        currentUserId = currentUser?.uid ?: "",
                        onFollowChanged = { followed -> /* salvar follow no banco depois */ }
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
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Erro ao carregar perfil")
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
            if (user.profilePic != null) {
                Text(
                    text = user.firstName.first().toString(),
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
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

        // Nome
        Text(
            text = user.firstName + " " + user.lastName,
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
                navController.navigate("userList/seguidos/${user.uid}")
            }
        )
        StatItem(
            count = user.followers.size.toString(),
            label = "Seguidores",
            onClick = {
                navController.navigate("userList/seguidores/${user.uid}")
            }
        )
        StatItem(
            count = user.createdEvents.size.toString(),
            label = "Eventos criados",
            onClick = {
                navController.navigate("EventList/createdEvents/${user.uid}")
            }
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