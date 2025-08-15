package com.pdm.zone.ui.nav

import android.R.string
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.pdm.zone.ui.screens.event.EventDetailsPage
import com.pdm.zone.ui.screens.event.EventRegisterPage
import com.pdm.zone.ui.screens.event.ListPage
import com.pdm.zone.ui.screens.user.ProfilePage
import com.pdm.zone.ui.screens.user.UserListPage
import androidx.navigation.navArgument
import com.pdm.zone.data.SessionManager
import com.pdm.zone.ui.screens.home.HomeScreenWithDrawer
import com.pdm.zone.ui.screens.user.UserCreatedEventList

//Rotas dos menus
@Composable
fun MainNavHost(navController: NavHostController) {
    val currentUserState = SessionManager.currentUser.collectAsState()
    val currentUser = currentUserState.value

    NavHost(navController, startDestination = Route.Home) {

        composable<Route.Home> {
            HomeScreenWithDrawer(navController, username = currentUser?.username)
        }
        composable<Route.List> { ListPage(navController) }

        // Tela de cadastro de evento
        composable("eventRegister") {
            EventRegisterPage(navController = navController, onSubmit = {
                navController.popBackStack()
            })
        }

        // Detalhes do evento
        composable("eventDetails/{eventId}") { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId")
            if (eventId != null) {
                EventDetailsPage(eventId = eventId, navController)
            }
        }

        // Rota de lista de usuários
        composable("userList/{type}/{listId}") { backStackEntry ->
            val type = backStackEntry.arguments?.getString("type")
            val listId = backStackEntry.arguments?.getString("listId")
            if (type != null && listId != null) {
                UserListPage(type = type, listId = listId, navController = navController)
            }
        }

        // Rota para o perfil de qualquer usuário
        composable(
            route = "profile/{username}",
            arguments = listOf(navArgument("username") { nullable = false })
        ) { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username")
            if (username != null) {
                ProfilePage(navController = navController, username = username)
            }
        }

        // Rota de lista de eventos
        composable("EventList/createdEvents/{username}") { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username")

            UserCreatedEventList(
                username = username,
                onClick = {
                    navController.popBackStack()
                },
                navController = navController
            )
        }

    }
}