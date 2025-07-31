package com.pdm.zone.ui.screens.home

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.pdm.zone.ui.nav.MainDrawer

@Composable
fun HomeScreenWithDrawer(navController: NavHostController, username: String?) {
    MainDrawer(navController = navController, username = username) {
        HomePage(navController)
    }
}
