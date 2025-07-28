package com.pdm.zone.ui.screens.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
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

    fun loadUsers(username: String, type: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, screenTitle = type.replaceFirstChar { it.uppercase() }) }

            try {
                // Encontrar o usuário principal pelo username para obter a lista de UIDs
                val initialUserSnapshot = db.collection("users")
                    .whereEqualTo("username", username)
                    .limit(1)
                    .get()
                    .await()

                if (initialUserSnapshot.isEmpty) {
                    _uiState.update { it.copy(isLoading = false, error = "Usuário '$username' não encontrado.") }
                    return@launch
                }

                val initialUser = initialUserSnapshot.documents.first().toObject(User::class.java)
                if (initialUser == null) {
                    _uiState.update { it.copy(isLoading = false, error = "Falha ao ler dados do usuário.") }
                    return@launch
                }

                // Obter a lista de UIDs correta (seguidores ou seguindo)
                val uidsToFetch = when (type.lowercase()) {
                    "seguidores" -> initialUser.followers
                    "seguindo" -> initialUser.following
                    else -> emptyList()
                }

                if (uidsToFetch.isEmpty()) {
                    _uiState.update { it.copy(isLoading = false, users = emptyList()) }
                    return@launch
                }

                // Buscar os dados completos dos usuários com base nos UIDs
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