package com.pdm.zone.ui.screens.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.pdm.zone.data.model.Event
import com.pdm.zone.data.model.User
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// UI State
data class SearchUiState(
    val query: String = "",
    val isLoading: Boolean = false,
    val users: List<User> = emptyList(),
    val events: List<Event> = emptyList(),
    val error: String? = null
)

class SearchViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState = _uiState.asStateFlow()

    private var searchJob: Job? = null

    fun updateQuery(newQuery: String) {
        _uiState.update { it.copy(query = newQuery) }
        triggerSearch()
    }

    private fun triggerSearch() {
        searchJob?.cancel()
        val q = _uiState.value.query.trim()
        if (q.isEmpty()) {
            _uiState.update { it.copy(users = emptyList(), events = emptyList(), error = null, isLoading = false) }
            return
        }
        searchJob = viewModelScope.launch {
            delay(350) // debounce
            performSearch(q)
        }
    }

    private suspend fun performSearch(query: String) {
        _uiState.update { it.copy(isLoading = true, error = null) }
        try {
            val usersTask = db.collection("users")
                .orderBy("username", Query.Direction.ASCENDING)
                .startAt(query)
                .endAt(query + '\uf8ff')
                .limit(15)
                .get()

            val eventsTask = db.collection("events")
                .orderBy("title", Query.Direction.ASCENDING)
                .startAt(query)
                .endAt(query + '\uf8ff')
                .limit(15)
                .get()

            val usersSnap = usersTask.await()
            val eventsSnap = eventsTask.await()

            val users = usersSnap.toObjects(User::class.java)
            val events = eventsSnap.toObjects(Event::class.java)

            _uiState.update { it.copy(isLoading = false, users = users, events = events) }
        } catch (e: Exception) {
            _uiState.update { it.copy(isLoading = false, error = "Erro ao buscar: ${e.message}") }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchPage(navController: NavHostController, viewModel: SearchViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    var textFieldValue by remember { mutableStateOf(TextFieldValue(uiState.query)) }

    LaunchedEffect(textFieldValue.text) {
        if (textFieldValue.text != uiState.query) {
            viewModel.updateQuery(textFieldValue.text)
        }
    }

    Scaffold(topBar = {
        TopAppBar(title = {
            TextField(
                value = textFieldValue,
                onValueChange = { textFieldValue = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Buscar usuários ou eventos") },
                singleLine = true
            )
        })
    }) { innerPadding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)) {
            when {
                uiState.query.isEmpty() -> {
                    Text(
                        "Digite para pesquisar...",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                uiState.error != null -> {
                    Text(uiState.error ?: "Erro", modifier = Modifier.align(Alignment.Center))
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (uiState.users.isNotEmpty()) {
                            item { Text("Usuários", style = MaterialTheme.typography.titleMedium) }
                            items(uiState.users) { user ->
                                ElevatedCard(onClick = {
                                    if (user.username.isNotBlank()) {
                                        navController.navigate("profile/${user.username}")
                                    }
                                }, modifier = Modifier.fillMaxWidth()) {
                                    Column(Modifier.padding(12.dp)) {
                                        Text("@${user.username}", style = MaterialTheme.typography.titleSmall)
                                        val name = listOf(user.firstName, user.lastName).filter { it.isNotBlank() }.joinToString(" ")
                                        if (name.isNotBlank()) Text(name, style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                        }
                        if (uiState.events.isNotEmpty()) {
                            item { Spacer(modifier = Modifier.height(8.dp)) }
                            item { Text("Eventos", style = MaterialTheme.typography.titleMedium) }
                            items(uiState.events, key = { it.id }) { event ->
                                ElevatedCard(onClick = {
                                    if (event.id.isNotBlank()) {
                                        navController.navigate("eventDetails/${event.id}")
                                    }
                                }, modifier = Modifier.fillMaxWidth()) {
                                    Column(Modifier.padding(12.dp)) {
                                        Text(event.title, style = MaterialTheme.typography.titleSmall)
                                        if (event.location.isNotBlank()) Text(event.location, style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                        }
                        if (uiState.users.isEmpty() && uiState.events.isEmpty()) {
                            item { Text("Nenhum resultado encontrado", style = MaterialTheme.typography.bodyMedium) }
                        }
                    }
                }
            }
        }
    }
}
