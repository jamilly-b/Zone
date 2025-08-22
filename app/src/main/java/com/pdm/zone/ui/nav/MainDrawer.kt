package com.pdm.zone.ui.nav

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.pdm.zone.data.SessionManager
import com.pdm.zone.ui.screens.login.LoginActivity
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainDrawer(
    username: String?,
    navController: NavHostController,
    content: @Composable () -> Unit
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val sessionUser by SessionManager.currentUser.collectAsState()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(24.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Foto do usuário
                    if (!sessionUser?.profilePic.isNullOrBlank()) {
                        AsyncImage(
                            model = sessionUser?.profilePic,
                            contentDescription = "Foto do usuário",
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Ícone padrão",
                            modifier = Modifier.size(100.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = sessionUser?.username ?: username ?: "Usuário",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                NavigationDrawerItem(
                    label = { Text("Sair") },
                    icon = {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Sair")
                    },
                    selected = false,
                    onClick = {
                        SessionManager.clearSession()
                        context.startActivity(Intent(context, LoginActivity::class.java))
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
//                TopAppBar(
//                    title = {},
//                    navigationIcon = {
//                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
//                            if (!sessionUser?.profilePic.isNullOrBlank()) {
//                                AsyncImage(
//                                    model = sessionUser?.profilePic,
//                                    contentDescription = "Abrir menu",
//                                    modifier = Modifier
//                                        .size(40.dp)
//                                        .clip(CircleShape)
//                                )
//                            } else {
//                                Icon(
//                                    imageVector = Icons.Default.AccountCircle,
//                                    contentDescription = "Abrir menu",
//                                    modifier = Modifier.size(40.dp)
//                                )
//                            }
//                        }
//                    }
//                )
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                content()
            }
        }
    }
}

