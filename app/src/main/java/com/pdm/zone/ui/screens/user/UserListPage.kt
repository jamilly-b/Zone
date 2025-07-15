package com.pdm.zone.ui.screens.user

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.pdm.zone.data.model.User
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.navigation.NavHostController
import com.pdm.zone.ui.nav.BackHeader

@Composable
fun UserListPage(type: String, userId: String, navController: NavHostController) {
    val users = remember {
        when (type) {
            "seguidos" -> getFollowingUsers(userId)
            "seguidores" -> getFollowerUsers(userId)
            else -> emptyList()
        }
    }
    Scaffold(
        topBar = { BackHeader(navController) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            items(users, key = { it.uid }) { user ->
                UserItem(user = user, onClick = {
                    navController.navigate("userProfile/${user.uid}")
                })
            }
        }
    }
}

fun getFollowingUsers(userId: String): List<User> {
    return listOf(
        User(
            uid = "following_1",
            name = "João Silva",
            username = "@joaosilva",
            photoUrl = null,
            biography = "Desenvolvedor Android",
            birthday = "1990-01-01",
            createdTime = "2023-01-01",
            followers = listOf("1"),
            following = listOf("1", "2"),
            createdEvents = listOf("1")
        ),
        User(
            uid = "following_2",
            name = "Maria Santos",
            username = "@mariasantos",
            photoUrl = null,
            biography = "Designer UX/UI",
            birthday = "1992-05-15",
            createdTime = "2023-02-01",
            followers = listOf("1", "2"),
            following = listOf("1"),
            createdEvents = listOf("2")
        ),
        User(
            uid = "following_3",
            name = "Carlos Oliveira",
            username = "@carlosoliveira",
            photoUrl = null,
            biography = "Product Manager",
            birthday = "1988-10-20",
            createdTime = "2023-03-01",
            followers = listOf("1", "3"),
            following = listOf("1", "2", "3"),
            createdEvents = listOf("3")
        )
    )
}

fun getFollowerUsers(userId: String): List<User> {
    return listOf(
        User(
            uid = "follower_1",
            name = "Ana Costa",
            username = "@anacosta",
            photoUrl = null,
            biography = "Engenheira de Software",
            birthday = "1995-03-10",
            createdTime = "2023-04-01",
            followers = listOf("2"),
            following = listOf("1"),
            createdEvents = listOf("4")
        ),
        User(
            uid = "follower_2",
            name = "Pedro Almeida",
            username = "@pedroalmeida",
            photoUrl = null,
            biography = "Analista de Dados",
            birthday = "1993-07-25",
            createdTime = "2023-05-01",
            followers = listOf("3"),
            following = listOf("1", "2"),
            createdEvents = listOf("5")
        ),
        User(
            uid = "follower_3",
            name = "Laura Ferreira",
            username = "@lauraferreira",
            photoUrl = null,
            biography = "Marketing Digital",
            birthday = "1991-12-05",
            createdTime = "2023-06-01",
            followers = listOf("4"),
            following = listOf("1", "3"),
            createdEvents = listOf("6")
        ),
        User(
            uid = "follower_4",
            name = "Ricardo Souza",
            username = "@ricardosouza",
            photoUrl = null,
            biography = "Scrum Master",
            birthday = "1987-09-18",
            createdTime = "2023-07-01",
            followers = listOf("5"),
            following = listOf("1"),
            createdEvents = listOf("7")
        )
    )
}
