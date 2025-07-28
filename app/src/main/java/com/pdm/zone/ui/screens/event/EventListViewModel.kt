package com.pdm.zone.ui.screens.event

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.pdm.zone.data.model.Event
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// Estado da UI específico para a tela de lista de eventos
data class ListUiState(
    val isLoading: Boolean = true,
    val events: List<Event> = emptyList(),
    val error: String? = null
)

class ListViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val _uiState = MutableStateFlow(ListUiState())
    val uiState = _uiState.asStateFlow()

    init {
        // TODO: No futuro, você pode passar o ID do usuário e buscar
        // apenas os eventos que ele confirmou.
        loadAllEvents()
    }

    private fun loadAllEvents() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val snapshot = db.collection("events").get().await()
                val events = snapshot.toObjects(Event::class.java)
                _uiState.update { it.copy(isLoading = false, events = events) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Falha ao carregar eventos: ${e.message}") }
            }
        }
    }
}