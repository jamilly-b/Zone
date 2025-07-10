package com.pdm.zone.ui.screens.user

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pdm.zone.ui.components.DataField
import com.pdm.zone.ui.theme.ZoneTheme
import androidx.core.view.WindowCompat
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import android.app.DatePickerDialog
import android.content.Intent
import androidx.compose.foundation.clickable
import com.pdm.zone.R
import com.pdm.zone.ui.theme.Secondary
import java.util.Calendar
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.pdm.zone.MainActivity

class UserProfileEdit : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            ZoneTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    UserRegisterPage(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun UserRegisterPage(modifier: Modifier = Modifier) {
    var firstName by rememberSaveable { mutableStateOf("") }
    var lastName by rememberSaveable { mutableStateOf("") }
    var userName by rememberSaveable { mutableStateOf("") }
    var dateOfBirth by rememberSaveable { mutableStateOf("") }
    var profilePic by rememberSaveable { mutableStateOf("") }
    var biography by rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current


    val activity = LocalContext.current as? Activity

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .size(160.dp)
                .clip(CircleShape)
                .border(2.dp, Color.Gray, CircleShape)
                .clickable {
                    // lógica faço depois
                }
        ) {
            Image(
                painter = painterResource(id = R.drawable.pessoa),
                contentDescription = "Foto de perfil padrão",
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Alterar Imagem",
            color = Secondary,
            modifier = Modifier.clickable {
                // lógica faço depois
            }
        )

        DataField(
            value = firstName,
            onValueChange = { firstName = it },
            label = "Nome",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        DataField(
            value = lastName,
            onValueChange = { lastName = it },
            label = "Sobrenome",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        DataField(
            value = userName,
            onValueChange = { userName = it },
            label = "Nome de Usuário",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        DataField(
            value = dateOfBirth,
            onValueChange = { dateOfBirth = it },
            label = "Data de Nascimento",
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    val calendar = Calendar.getInstance()
                    val year = calendar.get(Calendar.YEAR)
                    val month = calendar.get(Calendar.MONTH)
                    val day = calendar.get(Calendar.DAY_OF_MONTH)

                    DatePickerDialog(context, { _, y, m, d ->
                        dateOfBirth = String.format("%02d/%02d/%04d", d, m + 1, y)
                    }, year, month, day).show()
                }
        )

        Spacer(modifier = Modifier.height(24.dp))

        DataField(
            value = biography,
            onValueChange = { biography = it },
            label = "Biografia",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (firstName.isBlank() || lastName.isBlank() || userName.isBlank() || dateOfBirth.isBlank()) {
                    Toast.makeText(activity, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(activity, "Cadastro realizado!", Toast.LENGTH_SHORT).show()
                    activity?.startActivity(Intent(activity, MainActivity::class.java))
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = firstName.isNotEmpty() && lastName.isNotEmpty() && userName.isNotEmpty() && dateOfBirth.isNotEmpty()
        ) {
            Text("Concluir", fontWeight = FontWeight.Bold)
        }
    }
}
