package com.gtinnox.edutrack.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gtinnox.edutrack.data.local.entity.StudySessionEntity
import com.gtinnox.edutrack.data.local.relations.SessionWithSubject
import com.gtinnox.edutrack.data.repository.EduTrackRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

sealed class SessionUiEvent {
    data class ShowSnackbar(val message: String) : SessionUiEvent()
    object SessionSaved : SessionUiEvent()
    object SessionDeleted : SessionUiEvent()
}

class SessionViewModel(
    private val repository: EduTrackRepository
) : ViewModel() {

    val allSessionsWithSubject: StateFlow<List<SessionWithSubject>> =
        repository.allSessionsWithSubject.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    private val _eventFlow = MutableSharedFlow<SessionUiEvent>()
    val eventFlow: SharedFlow<SessionUiEvent> = _eventFlow.asSharedFlow()

    fun getSessionWithSubject(id: String): StateFlow<SessionWithSubject?> {
        return repository.getSessionWithSubject(id).stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            null
        )
    }

    fun saveSession(
        id: String?,
        subjectId: String,
        durationMinutes: Int,
        sessionType: String,
        notes: String,
        focusScore: Int = 85,
        isManual: Boolean = true,
        startTime: String? = null
    ) {
        viewModelScope.launch {
            if (subjectId.isBlank()) {
                _eventFlow.emit(SessionUiEvent.ShowSnackbar("Please select a valid academic subject"))
                return@launch
            }
            if (durationMinutes <= 0) {
                _eventFlow.emit(SessionUiEvent.ShowSnackbar("Study duration must be at least 1 minute"))
                return@launch
            }

            val sessionId = id ?: "sess-${UUID.randomUUID().toString().take(8)}"
            val nowStr = Instant.now().toString()
            val startStr = startTime ?: nowStr

            val entity = StudySessionEntity(
                id = sessionId,
                subjectId = subjectId,
                durationMinutes = durationMinutes,
                sessionType = sessionType.ifBlank { "deep_work" },
                notes = notes.trim(),
                focusScore = focusScore.coerceIn(0, 100),
                isManual = isManual,
                startTime = startStr,
                endTime = nowStr,
                createdAt = nowStr
            )

            if (id == null) {
                repository.insertSession(entity)
                _eventFlow.emit(SessionUiEvent.ShowSnackbar("Study session logged successfully ($durationMinutes mins)"))
            } else {
                repository.updateSession(entity)
                _eventFlow.emit(SessionUiEvent.ShowSnackbar("Study session updated"))
            }
            _eventFlow.emit(SessionUiEvent.SessionSaved)
        }
    }

    fun deleteSession(session: StudySessionEntity) {
        viewModelScope.launch {
            repository.deleteSession(session)
            _eventFlow.emit(SessionUiEvent.ShowSnackbar("Session deleted"))
            _eventFlow.emit(SessionUiEvent.SessionDeleted)
        }
    }
}
