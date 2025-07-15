package com.pdm.zone.ui.screens.user

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import com.pdm.zone.R
import com.pdm.zone.data.model.User
import com.pdm.zone.ui.theme.Primary
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.text.font.FontWeight

@Composable
fun UserList(navController: NavHostController) {
    val userList = remember { getUsers() }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        items(userList, key = { it.uid }) { user ->
            UserItem(
                user = user,
                onClick = {
                    navController.navigate("userProfile/${user.uid}")
                }
            )
        }
    }
}

@Composable
fun UserItem(
    user: User,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .padding(end = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            if (user.photoUrl != null) {
                Text(
                    text = user.name.first().toString(),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Primary
                )
            } else {
                Text(
                    text = user.name.first().toString(),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Primary
                )
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = user.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = user.username,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}

private fun getUsers(): List<User> = List(10) { i ->
    User(
        uid = "user_$i",
        name = "Usuário $i",
        username = "@usuario$i",
        photoUrl = null,
        biography = "Biografia do usuário $i",
        birthday = "1990-01-01",
        createdTime = "2023-01-01",
        followers = listOf("1", "2"),
        following = listOf("1", "3"),
        createdEvents = listOf("1")
    )
}