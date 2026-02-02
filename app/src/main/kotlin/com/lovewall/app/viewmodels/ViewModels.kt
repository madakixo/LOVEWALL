package com.lovewall.app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lovewall.app.models.LocationUpdate
import com.lovewall.app.models.Message
import com.lovewall.app.models.Note
import com.lovewall.app.repositories.EavesdropRepo
import com.lovewall.app.repositories.LocationRepo
import com.lovewall.app.repositories.MessagingRepo
import com.lovewall.app.webrtc.WebRtcManager
import com.lovewall.app.utils.coupleId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import javax.inject.Inject

@HiltViewModel
class MessagingViewModel @Inject constructor(
    private val repo: MessagingRepo
) : ViewModel() {
    val messages: StateFlow<List<Message>> = repo.getMessagesFlow(coupleId).stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.Lazily, emptyList())

    fun sendNote(note: Note) = viewModelScope.launch {
        repo.sendNote(coupleId, note)
    }

    fun sendBatchNotes(notes: List<Note>) = viewModelScope.launch {
        repo.sendBatchNotes(coupleId, notes)
    }
}

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val repo: LocationRepo
) : ViewModel() {
    val location: StateFlow<LocationUpdate> = repo.getLocationFlow(coupleId).stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.Lazily, LocationUpdate(0.0, 0.0, 0))

    fun startSharing() {
        // Start LocationService
    }
}

@HiltViewModel
class EavesdropViewModel @Inject constructor(
    private val webRtcManager: WebRtcManager,
    private val repo: EavesdropRepo
) : ViewModel() {
    val sessionActive = kotlinx.coroutines.flow.MutableStateFlow(false)

    fun startSession() = viewModelScope.launch {
        repo.logConsent(coupleId, true)
        webRtcManager.startEavesdropSession(true)
        sessionActive.value = true
        delay(300000)  // 5 min
        stopSession()
    }

    fun stopSession() {
        webRtcManager.close()
        sessionActive.value = false
    }
}
