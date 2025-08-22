package com.pdm.zone.ui.screens.user

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.DatePicker
import android.widget.Toast
import com.pdm.zone.R
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pdm.zone.MainActivity
import com.pdm.zone.ui.components.DataField
import com.pdm.zone.ui.theme.ZoneTheme
import androidx.core.view.WindowCompat
import java.util.Calendar
import androidx.core.net.toUri

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
    val calendar = Calendar.getInstance()
    var firstName by rememberSaveable { mutableStateOf("") }
    var lastName by rememberSaveable { mutableStateOf("") }
    var userName by rememberSaveable { mutableStateOf("") }
    var dateOfBirth by rememberSaveable { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var biography by rememberSaveable { mutableStateOf("") }
    var isNewUser by remember { mutableStateOf(true) }

    val context = LocalContext.current
    val activity = LocalContext.current as? Activity

    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                dateOfBirth = "%02d/%02d/%04d".format(dayOfMonth, month + 1, year)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    // Carregar info do usuário para editar
    LaunchedEffect(Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val db = FirebaseFirestore.getInstance()
            db.collection("users").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        isNewUser = false

                        firstName = document.getString("firstName") ?: ""
                        lastName = document.getString("lastName") ?: ""
                        userName = document.getString("username") ?: ""
                        dateOfBirth = document.getString("dateOfBirth") ?: ""
                        biography = document.getString("biography") ?: ""

                        val profilePicString = document.getString("profilePic")
                        imageUri = profilePicString?.toUri()
                    } else {
                        isNewUser = true
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Erro ao carregar perfil", Toast.LENGTH_SHORT).show()
                }
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Campo de imagem
        Box(
            modifier = Modifier
                .size(160.dp)
                .clip(CircleShape)
                .border(2.dp, Color.Gray, CircleShape)
                .clickable { imagePickerLauncher.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            if (imageUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(imageUri),
                    contentDescription = "Imagem do usuário",
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.pessoa),
                    contentDescription = "Ícone padrão do usuário",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Alterar Imagem",
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.clickable {
                imagePickerLauncher.launch("image/*")
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

        Spacer(modifier = Modifier.height(16.dp))

        if (isNewUser) {
            DataField(
                value = userName,
                onValueChange = { userName = it },
                label = "Nome de usuário",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))
        }

        OutlinedTextField(
            value = dateOfBirth,
            onValueChange = {},
            label = { Text("Data") },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { datePickerDialog.show() },
            readOnly = true,
            enabled = false,
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledLabelColor = MaterialTheme.colorScheme.secondary,
                disabledBorderColor = MaterialTheme.colorScheme.secondary,
                disabledContainerColor = MaterialTheme.colorScheme.surface,
            )
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
                    val userId = FirebaseAuth.getInstance().currentUser?.uid
                    if (userId != null) {
                        val db = FirebaseFirestore.getInstance()
                        val timestamp = System.currentTimeMillis().toString()

                        val profilePicStringToSave = imageUri?.toString() ?: ""

                        val user = com.pdm.zone.data.model.User(
                            uid = userId,
                            firstName = firstName,
                            lastName = lastName,
                            username = userName,
                            profilePic = profilePicStringToSave,
                            biography = biography,
                            dateOfBirth = dateOfBirth,
                            createdTime = timestamp,
                            followers = emptyList(),
                            following = emptyList(),
                            createdEvents = emptyList(),
                            favoriteEvents = emptyList()
                        )

                        db.collection("users").document(userId)
                            .set(user)
                            .addOnSuccessListener {
                                Toast.makeText(activity, "Perfil salvo com sucesso!", Toast.LENGTH_SHORT).show()
                                activity?.startActivity(Intent(activity, MainActivity::class.java))
                                activity?.finish()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(activity, "Erro ao salvar perfil: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                    } else {
                        Toast.makeText(activity, "Usuário não autenticado!", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = firstName.isNotEmpty() &&
                    lastName.isNotEmpty() &&
                    dateOfBirth.isNotEmpty() &&
                    (!isNewUser || userName.isNotEmpty())
        ) {
            Text("Concluir", fontWeight = FontWeight.Bold)
        }
    }
}
