package com.pdm.zone.ui.screens.event

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pdm.zone.data.model.Event
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Date

// Estado da UI específico para a tela de lista de eventos
data class ListUiState(
    val isLoading: Boolean = true,
    val confirmedEvents: List<Event> = emptyList(),
    val error: String? = null
)

class ListViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow(ListUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadUserConfirmedEvents()
    }

    fun loadUserConfirmedEvents() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val currentUserId = auth.currentUser?.uid
                if (currentUserId == null) {
                    _uiState.update { it.copy(isLoading = false, error = "Usuário não autenticado") }
                    return@launch
                }

                // Busca eventos que o usuário confirmou presença
                // Note que removemos o orderBy para evitar o erro de índice
                val eventsSnapshot = db.collection("events")
                    .whereArrayContains("attendees", currentUserId)
                    .get()
                    .await()

                val allConfirmedEvents = eventsSnapshot.toObjects(Event::class.java)

                // Separar apenas eventos futuros e ordenar por data
                val currentDate = Date()
                val upcomingEvents = allConfirmedEvents
                    .filter { event -> event.eventDate?.after(currentDate) ?: false }
                    .sortedBy { it.eventDate }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        confirmedEvents = upcomingEvents,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Falha ao carregar eventos: ${e.message}"
                    )
                }
            }
        }
    }
}