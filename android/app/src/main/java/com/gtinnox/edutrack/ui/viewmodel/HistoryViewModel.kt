package com.gtinnox.edutrack.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gtinnox.edutrack.data.local.entity.StudySessionEntity
import com.gtinnox.edutrack.data.local.entity.SubjectEntity
import com.gtinnox.edutrack.data.local.relations.SessionWithSubject
import com.gtinnox.edutrack.data.repository.EduTrackRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HistoryViewModel(
    private val repository: EduTrackRepository
) : ViewModel() {

    val subjects: StateFlow<List<SubjectEntity>> = repository.allSubjects.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    private val _selectedSubjectId = MutableStateFlow<String?>(null)
    val selectedSubjectId: StateFlow<String?> = _selectedSubjectId.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val filteredSessions: StateFlow<List<SessionWithSubject>> = combine(
        repository.allSessionsWithSubject,
        _selectedSubjectId,
        _searchQuery
    ) { sessions, subjectId, query ->
        sessions.filter { item ->
            val matchSubject = subjectId == null || item.session.subjectId == subjectId
            val matchQuery = query.isBlank() ||
                    (item.subject?.name?.contains(query, ignoreCase = true) == true) ||
                    (item.session.notes.contains(query, ignoreCase = true))
            matchSubject && matchQuery
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    private val _eventFlow = MutableSharedFlow<String>()
    val eventFlow: SharedFlow<String> = _eventFlow.asSharedFlow()

    fun setSubjectFilter(id: String?) {
        _selectedSubjectId.value = id
    }

    fun setSearchQuery(q: String) {
        _searchQuery.value = q
    }

    fun deleteSession(session: StudySessionEntity) {
        viewModelScope.launch {
            repository.deleteSession(session)
            _eventFlow.emit("Session record deleted")
        }
    }
}
