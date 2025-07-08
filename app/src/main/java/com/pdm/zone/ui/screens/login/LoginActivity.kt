package com.pdm.zone.ui.screens.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pdm.zone.MainActivity
import com.pdm.zone.ui.components.DataField
import com.pdm.zone.ui.components.PasswordField
import com.pdm.zone.ui.theme.ZoneTheme
import com.pdm.zone.ui.theme.Secondary
import androidx.core.view.WindowCompat
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.pdm.zone.R
import com.pdm.zone.ui.screens.home.Home
import com.pdm.zone.ui.screens.home.HomePage

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            ZoneTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LoginPage(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun LoginPage(modifier: Modifier = Modifier) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    val activity = LocalContext.current as? Activity

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painter = painterResource(id = R.drawable.logo_zone_quadrado),
            contentDescription = "Logo do aplicativo",
            modifier = Modifier
                .size(240.dp)
                .padding(bottom = 32.dp)
        )

        DataField(
            value = email,
            onValueChange = { email = it },
            label = "E-mail",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        PasswordField(
            value = password,
            onValueChange = { password = it },
            label = "Senha",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                Toast.makeText(activity, "Login OK!", Toast.LENGTH_SHORT).show()
                activity?.startActivity(Intent(activity, MainActivity::class.java))
            },
            enabled = email.isNotEmpty() && password.isNotEmpty(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Entrar", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = {
                Toast.makeText(activity, "Login com Google", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                painter = painterResource(id = R.drawable.logo_google),
                contentDescription = "Google logo",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Entrar com Google")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Ainda não tem uma conta? Cadastre-se",
            color = Secondary,
            modifier = Modifier.clickable {
                activity?.startActivity(Intent(activity, RegisterActivity::class.java))
            }
        )
    }
}
