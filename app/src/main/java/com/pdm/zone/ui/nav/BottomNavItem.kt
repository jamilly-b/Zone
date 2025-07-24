package com.pdm.zone.ui.nav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.ui.graphics.vector.ImageVector
import com.pdm.zone.ui.theme.Primary
import kotlinx.serialization.Serializable
import androidx.compose.material3.*

sealed interface Route {
    @Serializable
    data object Home : Route
    @Serializable
    data object List : Route
    @Serializable
    data object Profile : Route
}

sealed class BottomNavItem(
    val title: String,
    val icon: ImageVector,
    val route: Route
) {
    data object HomeButton :
        BottomNavItem("Início", Icons.Default.Home, Route.Home)

    data object ListButton :
        BottomNavItem("Próximos eventos", Icons.Default.Favorite, Route.List)

    data object ProfileButton :
        BottomNavItem("Perfil", Icons.Default.Person, Route.Profile)
}

@Composable
fun BottomNavBar(
    navController: NavHostController,
    items: List<BottomNavItem>,
    currentUserId: String
) {
    NavigationBar(contentColor = Primary) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
                label = { Text(text = item.title, fontSize = 12.sp) },
                alwaysShowLabel = true,
                selected = currentRoute == when (item) {
                    is BottomNavItem.HomeButton -> Route.Home.toString()
                    is BottomNavItem.ListButton -> Route.List.toString()
                    is BottomNavItem.ProfileButton -> "profile/$currentUserId"
                },
                onClick = {
                    if (item.route is Route.Profile) {
                        navController.navigate("profile/${currentUserId}") {
                            navController.graph.startDestinationRoute?.let {
                                popUpTo(it) {
                                    saveState = true
                                }
                                restoreState = true
                            }
                            launchSingleTop = true
                        }
                    } else {
                        navController.navigate(item.route) {
                            navController.graph.startDestinationRoute?.let {
                                popUpTo(it) {
                                    saveState = true
                                }
                                restoreState = true
                            }
                            launchSingleTop = true
                        }
                    }
                })
        }
    }
}