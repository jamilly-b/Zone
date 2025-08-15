package com.pdm.zone.ui.nav

import android.net.http.SslCertificate.restoreState
import android.net.http.SslCertificate.saveState
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
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.ui.graphics.vector.ImageVector
import com.pdm.zone.ui.theme.Primary
import kotlinx.serialization.Serializable
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.pdm.zone.data.SessionManager

sealed interface Route {
    @Serializable
    data object Home : Route
    @Serializable
    data object List : Route
    @Serializable
    data object Profile : Route
    @Serializable
    data object Search : Route // nova rota de pesquisa
}

sealed class BottomNavItem(
    val title: String,
    val icon: ImageVector,
    val route: Route
) {
    data object HomeButton :
        BottomNavItem("Início", Icons.Default.Home, Route.Home)

    data object SearchButton :
        BottomNavItem("Pesquisar", Icons.Default.Search, Route.Search) // novo botão

    data object ListButton :
        BottomNavItem("Próximos eventos", Icons.Default.Favorite, Route.List)

    data object ProfileButton :
        BottomNavItem("Perfil", Icons.Default.Person, Route.Profile)
}

@Composable
fun BottomNavBar(
    navController: NavHostController,
    items: List<BottomNavItem>
    // 1. Removido o parâmetro 'currentUserId', que não é mais necessário
) {
    // 2. Usando o SessionManager como única fonte para o usuário logado.
    val currentUser by SessionManager.currentUser.collectAsState()

    NavigationBar(contentColor = Primary) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        // 3. O LaunchedEffect antigo foi completamente REMOVIDO daqui.

        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
                label = { Text(text = item.title, fontSize = 12.sp) },
                alwaysShowLabel = true,
                selected = when (item) {
                    // Compara a rota atual com a rota de perfil, usando o username do SessionManager
                    is BottomNavItem.ProfileButton -> currentRoute == "profile/${currentUser?.username}"
                    // Compara outras rotas pelo seu nome de classe qualificado
                    else -> currentRoute == item.route::class.qualifiedName
                },
                // 4. Lógica de clique TOTALMENTE CORRIGIDA
                onClick = {
                    when (item.route) {
                        is Route.Profile -> {
                            // Ação para o perfil: só navega se tiver um username
                            currentUser?.username?.let { username ->
                                navController.navigate("profile/$username") {
                                    // Comportamento padrão para itens da bottom bar
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                        else -> {
                            // Ação para outros itens (Home, List)
                            navController.navigate(item.route) {
                                // Comportamento padrão para itens da bottom bar
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                }
            )
        }
    }
}