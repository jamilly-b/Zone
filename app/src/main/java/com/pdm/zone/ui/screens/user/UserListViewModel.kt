package com.pdm.zone.ui.screens.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.pdm.zone.data.model.Event // <-- Importe o modelo Event
import com.pdm.zone.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class UserListUiState(
    val users: List<User> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val screenTitle: String = ""
)

class UserListViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _uiState = MutableStateFlow(UserListUiState())
    val uiState: StateFlow<UserListUiState> = _uiState.asStateFlow()

    // CORREÇÃO: 'username' foi renomeado para 'listId' para ser mais genérico.
    fun loadUsers(listId: String, type: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                // Obter a lista de UIDs de diferentes fontes, dependendo do tipo.
                val uidsToFetch: List<String> = when (type.lowercase()) {
                    "seguidores", "seguindo" -> {
                        _uiState.update { it.copy(screenTitle = type.replaceFirstChar { it.uppercase() }) }
                        val userSnapshot = db.collection("users").whereEqualTo("username", listId).limit(1).get().await()
                        if (userSnapshot.isEmpty) throw Exception("Usuário '$listId' não encontrado.")

                        val user = userSnapshot.documents.first().toObject(User::class.java)
                            ?: throw Exception("Falha ao ler dados do usuário.")

                        if (type.lowercase() == "seguidores") user.followers else user.following
                    }
                    // NOVIDADE: Lógica para carregar a lista de confirmados de um evento.
                    "confirmados" -> {
                        _uiState.update { it.copy(screenTitle = "Confirmados no Evento") }
                        val eventSnapshot = db.collection("events").document(listId).get().await()
                        if (!eventSnapshot.exists()) throw Exception("Evento não encontrado.")

                        val event = eventSnapshot.toObject(Event::class.java)
                            ?: throw Exception("Falha ao ler dados do evento.")

                        event.attendees // Retorna a lista de UIDs dos participantes
                    }
                    else -> emptyList()
                }

                if (uidsToFetch.isEmpty()) {
                    _uiState.update { it.copy(isLoading = false, users = emptyList()) }
                    return@launch
                }

                // A lógica para buscar os perfis dos usuários com base nos UIDs é a mesma.
                val usersSnapshot = db.collection("users")
                    .whereIn(FieldPath.documentId(), uidsToFetch)
                    .get()
                    .await()

                val userList = usersSnapshot.toObjects(User::class.java).mapIndexed { index, user ->
                    user.copy(uid = usersSnapshot.documents[index].id)
                }

                _uiState.update { it.copy(isLoading = false, users = userList) }

            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Erro ao carregar lista: ${e.message}") }
            }
        }
    }
}