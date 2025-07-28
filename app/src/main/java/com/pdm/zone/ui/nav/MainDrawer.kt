package com.pdm.zone.ui.nav

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.pdm.zone.ui.screens.login.LoginActivity
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainDrawer(navController: NavHostController) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text("Menu", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleLarge)
                HorizontalDivider()
//                NavigationDrawerItem(
//                    label = { Text("Configurações") },
//                    selected = false,
//                    onClick = {
//                        navController.navigate("settings")
//                        scope.launch { drawerState.close() }
//                    }
//                )
//                NavigationDrawerItem(
//                    label = { Text("Sobre") },
//                    selected = false,
//                    onClick = {
//                        navController.navigate("about")
//                        scope.launch { drawerState.close() }
//                    }
//                )
                NavigationDrawerItem(
                    label = { Text("Sair") },
                    selected = false,
                    onClick = {
                        FirebaseAuth.getInstance().signOut()
                        context.startActivity(Intent(context, LoginActivity::class.java))
                        (context as? android.app.Activity)?.finish()
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Zone") },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            },
            bottomBar = {
                val items = listOf(
                    BottomNavItem.HomeButton,
                    BottomNavItem.ListButton,
                    BottomNavItem.ProfileButton,
                )
                BottomNavBar(navController = navController, items = items)
            },
            floatingActionButton = {
                FloatingActionButton(onClick = {
                    navController.navigate("eventRegister")
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Adicionar")
                }
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                MainNavHost(navController = navController)
            }
        }
    }
}
