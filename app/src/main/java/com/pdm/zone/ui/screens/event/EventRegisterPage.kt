package com.pdm.zone.ui.screens.event

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.net.Uri
import android.widget.DatePicker
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.pdm.zone.ui.components.DataField
import coil.compose.rememberAsyncImagePainter
import com.pdm.zone.ui.nav.BackHeader
import com.pdm.zone.ui.theme.OnSurface
import com.pdm.zone.ui.theme.Secondary
import java.util.*

@Composable
fun EventRegisterPage(
    navController: NavHostController,
    onSubmit: () -> Unit = {},
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    var title by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var startTime by remember { mutableStateOf("") }
    var endTime by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    // Date picker
    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                date = "%02d/%02d/%04d".format(dayOfMonth, month + 1, year)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    // Image picker
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BackHeader(navController)

        // Campo de imagem
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clickable { imagePickerLauncher.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            if (imageUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(imageUri),
                    contentDescription = "Imagem do evento",
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Text("Clique para adicionar imagem", color = MaterialTheme.colorScheme.secondary)
            }
        }

        // Demais campos
        DataField(value = title, onValueChange = { title = it }, label = "Título", modifier = Modifier.fillMaxWidth())
        DataField(value = location, onValueChange = { location = it }, label = "Local", modifier = Modifier.fillMaxWidth())

        // Campo de data
        OutlinedTextField(
            value = date,
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

        // Time pickers
        val startTimePicker = remember {
            TimePickerDialog(
                context,
                { _, hour: Int, minute: Int ->
                    startTime = "%02d:%02d".format(hour, minute)
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).apply {
                setTitle("Horário de Início")
            }
        }

        val endTimePicker = remember {
            TimePickerDialog(
                context,
                { _, hour: Int, minute: Int ->
                    endTime = "%02d:%02d".format(hour, minute)
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).apply {
                setTitle("Horário de Fim")
            }
        }

        // Horário
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // Campo de horário de início
            OutlinedTextField(
                value = startTime,
                onValueChange = {},
                label = { Text("Início") },
                modifier = Modifier
                    .weight(1f)
                    .clickable { startTimePicker.show() },
                readOnly = true,
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledLabelColor = MaterialTheme.colorScheme.secondary,
                    disabledBorderColor = MaterialTheme.colorScheme.secondary,
                    disabledContainerColor = MaterialTheme.colorScheme.surface,
                )
            )

            // Campo de horário de fim
            OutlinedTextField(
                value = endTime,
                onValueChange = {},
                label = { Text("Fim") },
                modifier = Modifier
                    .weight(1f)
                    .clickable { endTimePicker.show() },
                readOnly = true,
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledLabelColor = MaterialTheme.colorScheme.secondary,
                    disabledBorderColor = MaterialTheme.colorScheme.secondary,
                    disabledContainerColor = MaterialTheme.colorScheme.surface,
                )
            )
        }

        DataField(value = description, onValueChange = { description = it }, label = "Descrição", modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onSubmit,
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        ) {
            Text("Cadastrar Evento")
        }
    }
}
