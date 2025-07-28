package com.pdm.zone.ui.nav

import android.R.attr.defaultValue
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.pdm.zone.ui.screens.event.EventDetailsPage
import com.pdm.zone.ui.screens.event.EventRegisterPage
import com.pdm.zone.ui.screens.home.HomePage
import com.pdm.zone.ui.screens.home.ListPage
import com.pdm.zone.ui.screens.user.ProfilePage
import com.pdm.zone.ui.screens.user.UserListPage

//Rotas dos menus
@Composable
fun MainNavHost(navController: NavHostController) {
    NavHost(navController, startDestination = Route.Home) {

        composable<Route.Home> { HomePage(navController) }
        composable<Route.List> { ListPage(navController) }

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

        // Rota de lista de usuários
        composable("userList/{type}/{username}") { backStackEntry ->
            val type = backStackEntry.arguments?.getString("type")
            val username = backStackEntry.arguments?.getString("username")
            if (type != null && username != null) {
                // IMPORTANTE: Você precisará ajustar a UserListPage para receber 'username'
                UserListPage(type = type, username = username, navController = navController)
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

        // Rota de lista de eventos criados
        composable("EventList/createdEvents/{username}") { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username")
            if (username != null) {
                ListPage(navController)
            }
        }

    }
}


