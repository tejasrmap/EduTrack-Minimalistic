package com.gtinnox.edutrack.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gtinnox.edutrack.data.local.entity.SubjectEntity
import com.gtinnox.edutrack.data.local.relations.SubjectWithSessions
import com.gtinnox.edutrack.data.repository.EduTrackRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.UUID

sealed interface SubjectDetailUiState {
    object Loading : SubjectDetailUiState
    data class Success(val subjectWithSessions: SubjectWithSessions) : SubjectDetailUiState
    object NotFound : SubjectDetailUiState
    data class Error(val message: String) : SubjectDetailUiState
}

sealed class SubjectUiEvent {
    data class ShowSnackbar(val message: String) : SubjectUiEvent()
    object SubjectSaved : SubjectUiEvent()
    object SubjectDeleted : SubjectUiEvent()
}

class SubjectViewModel(
    private val repository: EduTrackRepository
) : ViewModel() {

    val allSubjectsWithSessions: StateFlow<List<SubjectWithSessions>> =
        repository.allSubjectsWithSessions.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    val allSubjects: StateFlow<List<SubjectEntity>> =
        repository.allSubjects.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    private val _eventFlow = MutableSharedFlow<SubjectUiEvent>()
    val eventFlow: SharedFlow<SubjectUiEvent> = _eventFlow.asSharedFlow()

    private val _subjectDetailState = MutableStateFlow<SubjectDetailUiState>(SubjectDetailUiState.Loading)
    val subjectDetailState: StateFlow<SubjectDetailUiState> = _subjectDetailState.asStateFlow()

    private var detailJob: Job? = null

    fun loadSubjectDetail(subjectId: String) {
        detailJob?.cancel()
        if (subjectId.isBlank()) {
            _subjectDetailState.value = SubjectDetailUiState.NotFound
            return
        }
        _subjectDetailState.value = SubjectDetailUiState.Loading
        detailJob = viewModelScope.launch {
            try {
                repository.getSubjectWithSessions(subjectId)
                    .catch { e ->
                        _subjectDetailState.value = SubjectDetailUiState.Error(e.message ?: "Failed to load curriculum module")
                    }
                    .collect { result ->
                        if (result != null) {
                            _subjectDetailState.value = SubjectDetailUiState.Success(result)
                        } else {
                            _subjectDetailState.value = SubjectDetailUiState.NotFound
                        }
                    }
            } catch (e: Exception) {
                _subjectDetailState.value = SubjectDetailUiState.Error(e.message ?: "An unexpected error occurred")
            }
        }
    }

    fun getSubjectWithSessions(subjectId: String): StateFlow<SubjectWithSessions?> {
        return repository.getSubjectWithSessions(subjectId).stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            null
        )
    }

    fun saveSubject(
        id: String?,
        name: String,
        code: String,
        color: String,
        description: String,
        semester: String,
        instructor: String,
        weeklyTargetHours: Int,
        attendanceRate: Int = 95
    ) {
        viewModelScope.launch {
            if (name.isBlank()) {
                _eventFlow.emit(SubjectUiEvent.ShowSnackbar("Subject name is required"))
                return@launch
            }
            if (code.isBlank()) {
                _eventFlow.emit(SubjectUiEvent.ShowSnackbar("Course code is required"))
                return@launch
            }
            if (weeklyTargetHours <= 0) {
                _eventFlow.emit(SubjectUiEvent.ShowSnackbar("Weekly target hours must be greater than 0"))
                return@launch
            }

            val subjectId = id ?: "subj-${UUID.randomUUID().toString().take(8)}"
            val entity = SubjectEntity(
                id = subjectId,
                name = name.trim(),
                code = code.trim().uppercase(),
                color = color.ifBlank { "#6477D9" },
                description = description.trim(),
                semester = semester.ifBlank { "Fall 2026" },
                instructor = instructor.trim(),
                weeklyTargetHours = weeklyTargetHours,
                attendanceRate = attendanceRate,
                createdAt = Instant.now().toString()
            )

            if (id == null) {
                repository.insertSubject(entity)
                _eventFlow.emit(SubjectUiEvent.ShowSnackbar("Subject added successfully"))
            } else {
                repository.updateSubject(entity)
                _eventFlow.emit(SubjectUiEvent.ShowSnackbar("Subject updated successfully"))
            }
            _eventFlow.emit(SubjectUiEvent.SubjectSaved)
        }
    }

    fun deleteSubject(subject: SubjectEntity) {
        viewModelScope.launch {
            repository.deleteSubject(subject)
            _eventFlow.emit(SubjectUiEvent.ShowSnackbar("Deleted '${subject.name}' and all associated records"))
            _eventFlow.emit(SubjectUiEvent.SubjectDeleted)
        }
    }
}
