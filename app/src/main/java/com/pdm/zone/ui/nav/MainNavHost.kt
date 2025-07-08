package com.pdm.zone.ui.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.pdm.zone.ui.screens.home.HomePage
import com.pdm.zone.ui.screens.home.ListPage
import com.pdm.zone.ui.screens.user.ProfilePage

@Composable
fun MainNavHost(navController: NavHostController) {
    NavHost(navController, startDestination = Route.Home) {
        composable<Route.Home> { HomePage() }
        composable<Route.List> { ListPage() }
        composable<Route.Profile> { ProfilePage() }
    }
}
