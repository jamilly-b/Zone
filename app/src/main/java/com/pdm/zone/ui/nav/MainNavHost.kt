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

@Composable
fun MainNavHost(navController: NavHostController) {
    NavHost(navController, startDestination = Route.Home) {
        composable<Route.Home> { HomePage(navController) }
        composable<Route.List> { ListPage(navController) }
        composable<Route.Profile> { ProfilePage() }

        composable("eventDetails/{eventId}") { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId")?.toIntOrNull()
            eventId?.let {
                EventDetailsPage(eventId = it, navController)
            }
        }

        composable("eventRegister") {
            EventRegisterPage(navController = navController, onSubmit = {
                navController.popBackStack()
            })
        }
    }
}

