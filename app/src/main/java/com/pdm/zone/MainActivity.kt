package com.pdm.zone

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.pdm.zone.data.SessionManager
import com.pdm.zone.ui.nav.BottomNavBar
import com.pdm.zone.ui.nav.BottomNavItem
import com.pdm.zone.ui.nav.MainNavHost
import com.pdm.zone.ui.screens.login.LoginActivity
import com.pdm.zone.ui.theme.ZoneTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser == null) {
            // Usuário não logado: redireciona para Login e encerra
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else {
            // Usuário logado: carrega a sessão e define o conteúdo da UI
            lifecycleScope.launch {
                SessionManager.loadUserAfterLogin(currentUser.uid)
            }

            setContent {
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
                    }
                }
            }
        }
    }
}