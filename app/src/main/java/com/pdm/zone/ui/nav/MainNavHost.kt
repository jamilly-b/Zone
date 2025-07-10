package com.pdm.zone.ui.nav

import EventDetailsPage
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.pdm.zone.ui.screens.event.EventRegisterPage
import com.pdm.zone.ui.screens.home.HomePage
import com.pdm.zone.ui.screens.home.ListPage
import com.pdm.zone.ui.screens.user.ProfilePage
import com.pdm.zone.viewmodel.EventViewModel

@Composable
fun MainNavHost(
    navController: NavHostController,
    eventViewModel: EventViewModel
) {
    NavHost(navController, startDestination = "home") {
        composable("home") {
            HomePage(navController, eventViewModel)
        }

        composable("list") {
            ListPage(eventViewModel = eventViewModel)
        }

        composable("profile") {
            ProfilePage(eventViewModel = eventViewModel)
        }

        composable("eventDetails/{eventId}") { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId")?.toIntOrNull()
            eventId?.let {
                EventDetailsPage(
                    eventId = it,
                    navController = navController,
                    eventViewModel = eventViewModel
                )
            }
        }

        composable("eventRegister") {
            EventRegisterPage(navController = navController, onSubmit = {
                navController.popBackStack()
            })
        }
    }
}