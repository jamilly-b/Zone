package com.pdm.zone.ui.screens.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.pdm.zone.data.model.User
import com.pdm.zone.data.model.Event
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Date

data class ProfileUiState(
    val user: User? = null,
    val isLoading: Boolean = true,
    val isCurrentUserProfile: Boolean = false,
    val isFollowing: Boolean = false,
    val error: String? = null,
    val upcomingEvents: List<Event> = emptyList(),
    val pastEvents: List<Event> = emptyList(),
    val isLoadingEvents: Boolean = false
)

class ProfileViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun loadUserProfile(username: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val currentUserId = auth.currentUser?.uid
                if (currentUserId == null) {
                    _uiState.update { it.copy(isLoading = false, error = "Usuário não autenticado.") }
                    return@launch
                }

                val querySnapshot = db.collection("users")
                    .whereEqualTo("username", username)
                    .get()
                    .await()

                if (querySnapshot.isEmpty) {
                    _uiState.update { it.copy(isLoading = false, error = "Usuário não encontrado.") }
                    return@launch
                }

                val userDocument = querySnapshot.documents.first()
                val profileUser = userDocument.toObject(User::class.java)?.copy(uid = userDocument.id)

                if (profileUser == null) {
                    _uiState.update { it.copy(isLoading = false, error = "Falha ao ler dados do perfil.") }
                    return@launch
                }

                val isCurrentUserProfile = profileUser.uid == currentUserId
                val isFollowing = profileUser.followers.contains(currentUserId)

                _uiState.update {
                    it.copy(
                        user = profileUser,
                        isCurrentUserProfile = isCurrentUserProfile,
                        isFollowing = isFollowing,
                        isLoading = false
                    )
                }

                // Carregar eventos do usuário
                loadUserEvents(profileUser.uid)

            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Erro ao carregar perfil: ${e.message}") }
            }
        }
    }

    private fun loadUserEvents(userId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingEvents = true) }
            try {
                val eventsSnapshot = db.collection("events")
                    .whereArrayContains("attendees", userId)
                    .get()
                    .await()

                val allEvents = eventsSnapshot.documents.mapNotNull { doc ->
                    doc.toObject(Event::class.java)
                }

                val currentDate = Date()
                val (upcoming, past) = allEvents.partition { event ->
                    event.eventDate?.after(currentDate) ?: false
                }

                _uiState.update {
                    it.copy(
                        upcomingEvents = upcoming,
                        pastEvents = past,
                        isLoadingEvents = false
                    )
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoadingEvents = false,
                        error = "Erro ao carregar eventos: ${e.message}"
                    )
                }
            }
        }
    }

    fun toggleFollow() {
        viewModelScope.launch {
            val profileUser = _uiState.value.user ?: return@launch
            val currentUser = auth.currentUser ?: return@launch

            val isCurrentlyFollowing = _uiState.value.isFollowing

            try {
                // 1. Atualizar o estado UI imediatamente para feedback instantâneo
                val updatedFollowersList = profileUser.followers.toMutableList()

                if (isCurrentlyFollowing) {
                    updatedFollowersList.remove(currentUser.uid)
                } else {
                    updatedFollowersList.add(currentUser.uid)
                }

                // Criar uma cópia atualizada do user com a nova lista de seguidores
                val updatedUser = profileUser.copy(followers = updatedFollowersList)

                // Atualizar o estado com o user modificado e o novo status de isFollowing
                _uiState.update {
                    it.copy(
                        user = updatedUser,
                        isFollowing = !isCurrentlyFollowing
                    )
                }

                // 2. Persistir as mudanças no Firestore
                val currentUserRef = db.collection("users").document(currentUser.uid)
                val profileUserRef = db.collection("users").document(profileUser.uid)

                if (isCurrentlyFollowing) {
                    currentUserRef.update("following", FieldValue.arrayRemove(profileUser.uid))
                    profileUserRef.update("followers", FieldValue.arrayRemove(currentUser.uid))
                } else {
                    currentUserRef.update("following", FieldValue.arrayUnion(profileUser.uid))
                    profileUserRef.update("followers", FieldValue.arrayUnion(currentUser.uid))
                }
            } catch(e: Exception) {
                // Em caso de erro, reverter para o estado anterior
                _uiState.update { it.copy(isFollowing = isCurrentlyFollowing, error = "Ocorreu um erro ao atualizar.") }
            }
        }
    }
}