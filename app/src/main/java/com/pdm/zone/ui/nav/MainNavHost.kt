package com.pdm.zone.ui.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.pdm.zone.ui.screens.event.EventDetailsPage
import com.pdm.zone.ui.screens.event.EventRegisterPage
import com.pdm.zone.ui.screens.home.HomePage
import com.pdm.zone.ui.screens.home.ListPage
import com.pdm.zone.ui.screens.user.ProfilePage
import com.pdm.zone.ui.screens.user.UserListPage

@Composable
fun MainNavHost(navController: NavHostController) {
    NavHost(navController, startDestination = Route.Home) {
        composable<Route.Home> { HomePage(navController) }
        composable<Route.List> { ListPage(navController) }
        composable<Route.Profile> { ProfilePage(navController) }

        composable("eventRegister") {
            EventRegisterPage(navController = navController, onSubmit = {
                navController.popBackStack()
            })
        }

        composable("eventDetails/{eventId}") { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId")?.toIntOrNull()
            eventId?.let {
                EventDetailsPage(eventId = it, navController)
            }
        }

        composable("userList/{type}/{userId}") { backStackEntry ->
            val type = backStackEntry.arguments?.getString("type")
            val userId = backStackEntry.arguments?.getString("userId")
            if (type != null && userId != null) {
                UserListPage(type = type, userId = userId, navController = navController)
            }
        }

        // Rota para perfil de usuário específico
        composable("userProfile/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")
            if (userId != null) {
                ProfilePage(navController)
            }
        }

        // Rota para lista de eventos criados por um usuário
        composable("EventList/createdEvents/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")
            if (userId != null) {
                ListPage(navController)
            }
        }
    }
}

