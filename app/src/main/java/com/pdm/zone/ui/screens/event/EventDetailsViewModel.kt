package com.pdm.zone.ui.screens.event

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
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
    val isCurrentUserConfirmed: Boolean = false,
    val isCurrentUserInterested: Boolean = false,
    val error: String? = null
)

class EventDetailsViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

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
                    if (event != null) {
                        val currentUserId = auth.currentUser?.uid
                        val isConfirmed = event.attendees.contains(currentUserId)
                        val isInterested = event.interestedUsers.contains(currentUserId)

                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                event = event,
                                isCurrentUserConfirmed = isConfirmed,
                                isCurrentUserInterested = isInterested,
                                error = null
                            )
                        }
                    } else {
                        _uiState.update { it.copy(isLoading = false, error = "Falha ao ler dados do evento.") }
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "Evento não encontrado.") }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = "Falha ao carregar o evento: ${e.message}")
                }
            }
        }
    }

    fun togglePresenceConfirmation() {
        viewModelScope.launch {
            val currentState = _uiState.value
            val event = currentState.event ?: return@launch
            val currentUserId = auth.currentUser?.uid ?: return@launch

            val isCurrentlyConfirmed = currentState.isCurrentUserConfirmed

            val newAttendeesList = if (isCurrentlyConfirmed) {
                event.attendees - currentUserId
            } else {
                event.attendees + currentUserId
            }
            _uiState.update {
                it.copy(
                    isCurrentUserConfirmed = !isCurrentlyConfirmed,
                    event = event.copy(attendees = newAttendeesList)
                )
            }

            try {
                val eventRef = db.collection("events").document(event.id)
                val fieldUpdate = if (isCurrentlyConfirmed) {
                    FieldValue.arrayRemove(currentUserId)
                } else {
                    FieldValue.arrayUnion(currentUserId)
                }
                eventRef.update("attendees", fieldUpdate).await()
            } catch (e: Exception) {
                _uiState.update {
                    currentState.copy(error = "Ocorreu uma falha. Tente novamente.")
                }
            }
        }
    }

    fun toggleInterest() {
        viewModelScope.launch {
            val currentState = _uiState.value
            val event = currentState.event ?: return@launch
            val currentUserId = auth.currentUser?.uid ?: return@launch
            val isCurrentlyInterested = currentState.isCurrentUserInterested
            val newInterestedList = if (isCurrentlyInterested) event.interestedUsers - currentUserId else event.interestedUsers + currentUserId
            _uiState.update { it.copy(isCurrentUserInterested = !isCurrentlyInterested, event = event.copy(interestedUsers = newInterestedList)) }
            try {
                val eventRef = db.collection("events").document(event.id)
                val fieldUpdate = if (isCurrentlyInterested) FieldValue.arrayRemove(currentUserId) else FieldValue.arrayUnion(currentUserId)
                eventRef.update("interestedUsers", fieldUpdate).await()
            } catch (e: Exception) {
                _uiState.update { currentState.copy(error = "Ocorreu uma falha. Tente novamente.") }
            }
        }
    }
}