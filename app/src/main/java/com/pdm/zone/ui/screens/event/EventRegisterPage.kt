@file:OptIn(ExperimentalMaterial3Api::class)

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
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
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.pdm.zone.BuildConfig
import com.pdm.zone.data.model.EventCategory
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
    var selectedCategory by remember { mutableStateOf<EventCategory?>(null) }

    var placeId by remember { mutableStateOf<String?>(null) }
    var latitude by remember { mutableStateOf<Double?>(null) }
    var longitude by remember { mutableStateOf<Double?>(null) }
    var fullAddress by remember { mutableStateOf<String?>(null) }
    var predictions by remember { mutableStateOf<List<AutocompletePrediction>>(emptyList()) }

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

    // Places init
    val placesClient: PlacesClient = remember {
        if (!Places.isInitialized()) {
            Places.initialize(context, BuildConfig.PLACES_API_KEY)
        }
        Places.createClient(context)
    }

    fun searchPlaces(query: String) {
        if (query.length < 3) { predictions = emptyList(); return }
        val request = FindAutocompletePredictionsRequest.builder()
            .setQuery(query)
            .build()
        placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { response ->
                // Não altera texto do usuário, apenas mostra lista
                predictions = response.autocompletePredictions
            }
            .addOnFailureListener { predictions = emptyList() }
    }

    fun fetchPlaceDetails(id: String) {
        val placeRequest = FetchPlaceRequest.newInstance(id, listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS))
        placesClient.fetchPlace(placeRequest)
            .addOnSuccessListener { result ->
                val place = result.place
                placeId = place.id
                latitude = place.latLng?.latitude
                longitude = place.latLng?.longitude
                fullAddress = place.address
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

        // Campo de Local com Autocomplete
        Column(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = location,
                onValueChange = {
                    location = it
                    placeId = null
                    latitude = null
                    longitude = null
                    fullAddress = null
                    searchPlaces(it)
                },
                label = { Text("Local") },
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth()
            )
            // Lista de sugestões (não bloqueia foco)
            if (predictions.isNotEmpty()) {
                Surface(
                    tonalElevation = 2.dp,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        predictions.take(5).forEach { prediction ->
                            Text(
                                text = prediction.getFullText(null).toString(),
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        // Ao selecionar, preenche e limpa lista
                                        location = prediction.getFullText(null).toString()
                                        fetchPlaceDetails(prediction.placeId)
                                        predictions = emptyList()
                                    }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }
            if (fullAddress != null) {
                Text(fullAddress!!, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
            }
        }

        DataField(value = description, onValueChange = { description = it }, label = "Descrição", enabled = !uiState.isLoading, modifier = Modifier.fillMaxWidth())

        CategoryDropdown(
            selectedCategory = selectedCategory,
            onCategorySelected = { selectedCategory = it },
            enabled = !uiState.isLoading
        )

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

        Button(
            onClick = {
                viewModel.createEvent(
                    context = context,
                    title = title,
                    description = description,
                    location = location,
                    category = selectedCategory!!,
                    imageUri = imageUri,
                    eventCalendar = selectedCalendar,
                    startTime = startTimeText,
                    endTime = endTimeText,
                    placeId = placeId,
                    latitude = latitude,
                    longitude = longitude,
                    address = fullAddress
                )
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading && selectedCategory != null,
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
fun CategoryDropdown(
    selectedCategory: EventCategory?,
    onCategorySelected: (EventCategory) -> Unit,
    enabled: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { if (enabled) expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedCategory?.displayName ?: "",
            onValueChange = {},
            readOnly = true,
            enabled = enabled,
            label = { Text("Categoria") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            EventCategory.values().forEach { category ->
                DropdownMenuItem(
                    text = { Text(category.displayName) },
                    onClick = {
                        onCategorySelected(category)
                        expanded = false
                    }
                )
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