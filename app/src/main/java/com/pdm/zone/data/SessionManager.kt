package com.pdm.zone.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pdm.zone.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

object SessionManager {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser = _currentUser.asStateFlow()

    suspend fun loadUserAfterLogin(uid: String) {
        try {
            val document = db.collection("users").document(uid).get().await()
            _currentUser.value = document.toObject(User::class.java)?.copy(uid = document.id)
        } catch (e: Exception) {
            _currentUser.value = null
        }
    }

    fun clearSession() {
        auth.signOut()
        _currentUser.value = null
    }

    val isLoggedIn: Boolean
        get() = auth.currentUser != null && _currentUser.value != null
}