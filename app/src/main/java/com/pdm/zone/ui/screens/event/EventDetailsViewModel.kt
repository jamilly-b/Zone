package com.pdm.zone.ui.screens.event

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.pdm.zone.data.model.Event
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class EventDetailsUiState(
    val isLoading: Boolean = true,
    val event: Event? = null,
    val error: String? = null
)

class EventDetailsViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _uiState = MutableStateFlow(EventDetailsUiState())
    val uiState: StateFlow<EventDetailsUiState> = _uiState.asStateFlow()

    fun loadEvent(eventId: String) {
        if (eventId.isBlank()) {
            _uiState.update { it.copy(isLoading = false, error = "ID do evento inválido.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val documentSnapshot = db.collection("events").document(eventId).get().await()

                if (documentSnapshot.exists()) {
                    val event = documentSnapshot.toObject(Event::class.java)
                    _uiState.update {
                        it.copy(isLoading = false, event = event, error = null)
                    }
                } else {
                    _uiState.update {
                        it.copy(isLoading = false, error = "Evento não encontrado.")
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = "Falha ao carregar o evento: ${e.message}")
                }
            }
        }
    }
}