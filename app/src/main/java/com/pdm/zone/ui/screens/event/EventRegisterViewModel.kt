package com.pdm.zone.ui.screens.event

import android.R.attr.category
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.pdm.zone.data.model.Event
import com.pdm.zone.data.model.EventCategory
import com.pdm.zone.data.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.util.Calendar
import java.util.UUID

data class EventRegisterUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

class EventRegisterViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow(EventRegisterUiState())
    val uiState = _uiState.asStateFlow()

    private val MAX_IMAGE_DIMENSION = 1280.0f

    fun createEvent(
        context: Context,
        title: String,
        description: String,
        location: String,
        imageUri: Uri?,
        category: EventCategory,
        eventCalendar: Calendar,
        startTime: String,
        endTime: String
    ) {
        if (title.isBlank() || description.isBlank() || location.isBlank() || imageUri == null || startTime.isBlank() || endTime.isBlank()) {
            _uiState.update { it.copy(error = "Por favor, preencha todos os campos e selecione uma imagem.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val currentUser = auth.currentUser ?: throw IllegalStateException("Nenhum usuário logado.")
                val userDoc = db.collection("users").document(currentUser.uid).get().await()
                val creatorUsername = userDoc.toObject(User::class.java)?.username ?: "usuário_anônimo"

                val imageUrl = resizeAndUploadImage(context, imageUri)

                val newEvent = Event(
                    title = title,
                    description = description,
                    location = location,
                    category = category,
                    imageUrl = imageUrl,
                    creatorId = currentUser.uid,
                    creatorUsername = creatorUsername,
                    eventDate = eventCalendar.time,
                    startTime = startTime,
                    endTime = endTime
                )

                db.collection("events").add(newEvent).await()
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }

            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Falha ao criar evento: ${e.message}") }
            }
        }
    }

    private suspend fun resizeAndUploadImage(context: Context, imageUri: Uri): String {
        // Executa a manipulação de bitmap em uma thread de background (I/O)
        val imageBytes = withContext(Dispatchers.IO) {
            // 1. Converte a Uri para Bitmap
            val originalBitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                android.graphics.ImageDecoder.decodeBitmap(android.graphics.ImageDecoder.createSource(context.contentResolver, imageUri))
            } else {
                @Suppress("DEPRECATION")
                MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
            }

            // 2. Calcula as novas dimensões mantendo a proporção
            val width = originalBitmap.width
            val height = originalBitmap.height
            val scaleRatio = if (width > height) MAX_IMAGE_DIMENSION / width else MAX_IMAGE_DIMENSION / height
            val newWidth = (width * scaleRatio).toInt()
            val newHeight = (height * scaleRatio).toInt()

            // 3. Cria um novo Bitmap redimensionado
            val resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true)

            // 4. Comprime o Bitmap em um ByteArray
            val outputStream = ByteArrayOutputStream()
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream) // Qualidade 85%
            outputStream.toByteArray()
        }

        // 5. Faz o upload do ByteArray para o Storage
        val storageRef = storage.reference.child("event_images/${UUID.randomUUID()}.jpg")
        storageRef.putBytes(imageBytes).await()
        return storageRef.downloadUrl.await().toString()
    }

    fun resetState() {
        _uiState.update { EventRegisterUiState() }
    }
}