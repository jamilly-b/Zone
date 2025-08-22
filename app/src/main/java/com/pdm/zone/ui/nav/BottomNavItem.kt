package com.pdm.zone.ui.nav

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.graphics.vector.ImageVector
import com.pdm.zone.ui.theme.Primary
import kotlinx.serialization.Serializable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.unit.dp
import com.pdm.zone.data.SessionManager
import androidx.compose.ui.Modifier

sealed interface Route {
    @Serializable
    data object Home : Route
    @Serializable
    data object List : Route
    @Serializable
    data object Profile : Route
    @Serializable
    data object Search : Route
}

sealed class BottomNavItem(
    val title: String,
    val icon: ImageVector,
    val route: Route
) {
    data object HomeButton :
        BottomNavItem("Início", Icons.Default.Home, Route.Home)

    data object SearchButton :
        BottomNavItem("Pesquisar", Icons.Default.Search, Route.Search)

    data object ListButton :
        BottomNavItem("Próximos eventos", Icons.Default.Favorite, Route.List)

    data object ProfileButton :
        BottomNavItem("Perfil", Icons.Default.Person, Route.Profile)
}

@Composable
fun BottomNavBar(
    navController: NavHostController,
    items: List<BottomNavItem>
) {
    val currentUser by SessionManager.currentUser.collectAsState()

    NavigationBar(contentColor = Primary) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            val targetRoute = when (item) {
                is BottomNavItem.ProfileButton -> currentUser?.username?.let { "profile/$it" }
                else -> item.route::class.qualifiedName
            }
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        modifier = Modifier.size(32.dp)
                    )},
                selected = when (item) {
                    is BottomNavItem.ProfileButton -> currentRoute == targetRoute
                    else -> currentRoute == item.route::class.qualifiedName
                },
                onClick = {
                    targetRoute?.let { base ->
                        // Pop tudo acima da rota base (se existir) para garantir retorno à raiz da aba
                        var popped = true
                        while (popped) {
                            popped = navController.popBackStack(base, inclusive = false)
                        }
                        // Se já estamos na raiz daquela aba, não navega novamente (evita recriar)
                        if (navController.currentBackStackEntry?.destination?.route != base) {
                            navController.navigate(base) {
                                launchSingleTop = true
                                restoreState = true
                                // Remove outras stacks até a startDestination apenas se ela não for a própria base
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                            }
                        }
                    }
                }
            )
        }
    }
}