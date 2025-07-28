package com.pdm.zone.ui.screens.event

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.pdm.zone.ui.components.DataField
import com.pdm.zone.ui.nav.BackHeader
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun EventRegisterPage(
    navController: NavHostController,
    viewModel: EventRegisterViewModel = viewModel(),
    onSubmit: () -> Unit = { navController.popBackStack() },
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    var title by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val selectedCalendar = remember { Calendar.getInstance() }
    var dateText by remember { mutableStateOf("") }
    var startTimeText by remember { mutableStateOf("") }
    var endTimeText by remember { mutableStateOf("") }

    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    val timeFormatter = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year: Int, month: Int, dayOfMonth: Int ->
                selectedCalendar.set(Calendar.YEAR, year)
                selectedCalendar.set(Calendar.MONTH, month)
                selectedCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                dateText = dateFormatter.format(selectedCalendar.time)
            },
            selectedCalendar.get(Calendar.YEAR),
            selectedCalendar.get(Calendar.MONTH),
            selectedCalendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.minDate = System.currentTimeMillis()
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            Toast.makeText(context, "Evento cadastrado com sucesso!", Toast.LENGTH_SHORT).show()
            viewModel.resetState()
            onSubmit()
        }
    }
    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.resetState()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    )  {
        BackHeader(navController)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.medium)
                .clip(MaterialTheme.shapes.medium)
                .clickable { imagePickerLauncher.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            if (imageUri != null) {
                AsyncImage(
                    model = imageUri,
                    contentDescription = "Imagem do evento",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text("Clique para adicionar imagem", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        DataField(value = title, onValueChange = { title = it }, label = "Título", enabled = !uiState.isLoading, modifier = Modifier.fillMaxWidth())
        DataField(value = location, onValueChange = { location = it }, label = "Local", enabled = !uiState.isLoading, modifier = Modifier.fillMaxWidth())
        ClickableOutlinedTextField(value = dateText, onClick = { datePickerDialog.show() }, label = "Data", enabled = !uiState.isLoading, modifier = Modifier.fillMaxWidth())

        val startTimePicker = rememberTimePicker(context) { hour, minute ->
            selectedCalendar.set(Calendar.HOUR_OF_DAY, hour)
            selectedCalendar.set(Calendar.MINUTE, minute)
            startTimeText = timeFormatter.format(selectedCalendar.time)
        }
        val endTimePicker = rememberTimePicker(context) { hour, minute ->
            val endCalendar = Calendar.getInstance().apply {
                time = selectedCalendar.time
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
            }
            endTimeText = timeFormatter.format(endCalendar.time)
        }

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
            ClickableOutlinedTextField(value = startTimeText, onClick = { startTimePicker.show() }, label = "Início", modifier = Modifier.weight(1f), enabled = !uiState.isLoading)
            ClickableOutlinedTextField(value = endTimeText, onClick = { endTimePicker.show() }, label = "Fim", modifier = Modifier.weight(1f), enabled = !uiState.isLoading)
        }

        DataField(value = description, onValueChange = { description = it }, label = "Descrição", enabled = !uiState.isLoading, modifier = Modifier.fillMaxWidth())

        Button(
            onClick = {
                viewModel.createEvent(
                    context = context,
                    title = title,
                    description = description,
                    location = location,
                    imageUri = imageUri,
                    eventCalendar = selectedCalendar,
                    startTime = startTimeText,
                    endTime = endTimeText
                )
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading,
            shape = MaterialTheme.shapes.medium
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text("Cadastrar Evento")
            }
        }
    }
}

@Composable
private fun ClickableOutlinedTextField(
    value: String,
    onClick: () -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Box(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            enabled = enabled
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable(enabled = enabled, onClick = onClick)
        )
    }
}

@Composable
private fun rememberTimePicker(context: android.content.Context, onTimeSet: (Int, Int) -> Unit): TimePickerDialog {
    val calendar = Calendar.getInstance()
    return remember {
        TimePickerDialog(
            context,
            { _, hour: Int, minute: Int -> onTimeSet(hour, minute) },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )
    }
}