package com.pdm.zone.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.pdm.zone.data.model.Event
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Date

data class HomeUiState(
    val isLoading: Boolean = true,
    val upcomingEvents: List<Event> = emptyList(),
    val error: String? = null
)

class HomeViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadEvents()
    }

    fun loadEvents() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                // Carregar todos os eventos
                val allEventsSnapshot = db.collection("events")
                    .orderBy("eventDate", Query.Direction.ASCENDING)
                    .get()
                    .await()

                val allEvents = allEventsSnapshot.toObjects(Event::class.java)

                // Separar eventos por data (apenas eventos futuros)
                val currentDate = Date()
                val upcomingEvents = allEvents.filter { event ->
                    event.eventDate?.after(currentDate) ?: false
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        upcomingEvents = upcomingEvents
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

    // Chamado quando o usuário confirma/cancela presença em um evento na tela de detalhes
    fun refreshEvents() {
        loadEvents()
    }
}