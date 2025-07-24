package com.pdm.zone.ui.nav

import android.R.attr.defaultValue
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.pdm.zone.ui.screens.event.EventDetailsPage
import com.pdm.zone.ui.screens.event.EventRegisterPage
import com.pdm.zone.ui.screens.home.HomePage
import com.pdm.zone.ui.screens.home.ListPage
import com.pdm.zone.ui.screens.user.ProfilePage
import com.pdm.zone.ui.screens.user.UserListPage

@Composable
fun MainNavHost(navController: NavHostController, currentUserId: String) {
    NavHost(navController, startDestination = Route.Home) {

        composable<Route.Home> { HomePage(navController) }
        composable<Route.List> { ListPage(navController) }
        composable<Route.Profile> {
            ProfilePage(navController = navController, userId = currentUserId)
        }

        // Tela de cadastro de evento
        composable("eventRegister") {
            EventRegisterPage(navController = navController, onSubmit = {
                navController.popBackStack()
            })
        }

        // Detalhes do evento
        composable("eventDetails/{eventId}") { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId")?.toIntOrNull()
            eventId?.let {
                EventDetailsPage(eventId = it, navController)
            }
        }

        // Lista de seguidores ou seguindo
        composable("userList/{type}/{userId}") { backStackEntry ->
            val type = backStackEntry.arguments?.getString("type")
            val userId = backStackEntry.arguments?.getString("userId")
            if (type != null && userId != null) {
                UserListPage(type = type, userId = userId, navController = navController)
            }
        }

        // Rota para o perfil de qualquer usuário (inclusive outros)
        composable(
            route = "profile/{userId}",
            arguments = listOf(navArgument("userId") {
                nullable = true
                defaultValue = null
            })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")
            ProfilePage(navController = navController, userId = userId)
        }

        // Lista de eventos criados por um usuário
        composable("EventList/createdEvents/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")
            if (userId != null) {
                ListPage(navController)
            }
        }
    }
}


