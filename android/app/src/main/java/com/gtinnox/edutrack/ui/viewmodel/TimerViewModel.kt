package com.gtinnox.edutrack.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gtinnox.edutrack.data.local.entity.StudySessionEntity
import com.gtinnox.edutrack.data.local.entity.SubjectEntity
import com.gtinnox.edutrack.data.repository.EduTrackRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.UUID

enum class TimerMode(val title: String, val defaultMinutes: Int) {
    FOCUS("Focus Block", 25),
    SHORT_BREAK("Short Break", 5),
    LONG_BREAK("Long Break", 15)
}

data class TimerUiState(
    val mode: TimerMode = TimerMode.FOCUS,
    val isRunning: Boolean = false,
    val totalSeconds: Int = 25 * 60,
    val remainingSeconds: Int = 25 * 60,
    val selectedSubjectId: String = "",
    val sessionDescription: String = "",
    val completedSessionsCount: Int = 0
)

class TimerViewModel(
    private val repository: EduTrackRepository
) : ViewModel() {

    val subjects: StateFlow<List<SubjectEntity>> = repository.allSubjects.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    private val _uiState = MutableStateFlow(TimerUiState())
    val uiState: StateFlow<TimerUiState> = _uiState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<String>()
    val eventFlow: SharedFlow<String> = _eventFlow.asSharedFlow()

    private var timerJob: Job? = null
    private var sessionStartTime: Instant? = null

    init {
        viewModelScope.launch {
            subjects.collect { list ->
                if (list.isNotEmpty() && _uiState.value.selectedSubjectId.isBlank()) {
                    _uiState.update { it.copy(selectedSubjectId = list.first().id) }
                }
            }
        }
    }

    fun setMode(mode: TimerMode) {
        stopTimer()
        val secs = mode.defaultMinutes * 60
        _uiState.update {
            it.copy(
                mode = mode,
                isRunning = false,
                totalSeconds = secs,
                remainingSeconds = secs
            )
        }
    }

    fun setSubject(subjectId: String) {
        _uiState.update { it.copy(selectedSubjectId = subjectId) }
    }

    fun setDescription(desc: String) {
        _uiState.update { it.copy(sessionDescription = desc) }
    }

    fun toggleStartPause() {
        if (_uiState.value.isRunning) {
            pauseTimer()
        } else {
            startTimer()
        }
    }

    private fun startTimer() {
        if (sessionStartTime == null) {
            sessionStartTime = Instant.now()
        }
        _uiState.update { it.copy(isRunning = true) }
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.remainingSeconds > 0) {
                delay(1000)
                _uiState.update { it.copy(remainingSeconds = (it.remainingSeconds - 1).coerceAtLeast(0)) }
            }
            onTimerComplete()
        }
    }

    private fun pauseTimer() {
        timerJob?.cancel()
        _uiState.update { it.copy(isRunning = false) }
    }

    fun resetTimer() {
        stopTimer()
        sessionStartTime = null
        val defaultSecs = _uiState.value.mode.defaultMinutes * 60
        _uiState.update {
            it.copy(
                isRunning = false,
                totalSeconds = defaultSecs,
                remainingSeconds = defaultSecs
            )
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    private fun onTimerComplete() {
        stopTimer()
        val currentMode = _uiState.value.mode
        val completedSubjectId = _uiState.value.selectedSubjectId
        val description = _uiState.value.sessionDescription

        viewModelScope.launch {
            if (currentMode == TimerMode.FOCUS && completedSubjectId.isNotBlank()) {
                val duration = currentMode.defaultMinutes
                val session = StudySessionEntity(
                    id = "sess-${UUID.randomUUID().toString().take(8)}",
                    subjectId = completedSubjectId,
                    durationMinutes = duration,
                    sessionType = "pomodoro",
                    notes = description.ifBlank { "Completed Focus Pomodoro session" },
                    focusScore = 95,
                    isManual = false,
                    startTime = sessionStartTime?.toString() ?: Instant.now().toString(),
                    endTime = Instant.now().toString(),
                    createdAt = Instant.now().toString()
                )
                repository.insertSession(session)
                _uiState.update {
                    it.copy(
                        completedSessionsCount = it.completedSessionsCount + 1,
                        isRunning = false
                    )
                }
                _eventFlow.emit("Pomodoro session completed and logged to your records!")
            } else {
                _uiState.update { it.copy(isRunning = false) }
                _eventFlow.emit("${currentMode.title} completed!")
            }
            sessionStartTime = null
        }
    }
}
