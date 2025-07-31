package com.pdm.zone

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.pdm.zone.data.SessionManager
import com.pdm.zone.ui.nav.MainDrawer
import com.pdm.zone.ui.nav.BottomNavBar
import com.pdm.zone.ui.nav.BottomNavItem
import com.pdm.zone.ui.nav.MainDrawer
import com.pdm.zone.ui.nav.MainNavHost
import com.pdm.zone.ui.screens.login.LoginActivity
import com.pdm.zone.ui.theme.ZoneTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        setContent {
            var isUserLoaded by remember { mutableStateOf(false) }

            // Carrega os dados do usuário quando entrar na tela
            LaunchedEffect(Unit) {
                SessionManager.loadUserAfterLogin(currentUser.uid)
                isUserLoaded = true
            }

            // Enquanto não carregar os dados, mostra uma tela de loading
            if (!isUserLoaded) {
                ZoneTheme {
                    Box(modifier = Modifier.fillMaxSize()) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                }
            } else {
                val navController = rememberNavController()
                ZoneTheme {
                    Scaffold(
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
                    }                }
            }
        }
    }
}
