package com.gtinnox.edutrack.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gtinnox.edutrack.data.local.entity.NoteEntity
import com.gtinnox.edutrack.data.local.entity.SubjectEntity
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
import java.time.Instant
import java.util.UUID

class NoteViewModel(
    private val repository: EduTrackRepository
) : ViewModel() {

    val subjects: StateFlow<List<SubjectEntity>> = repository.allSubjects.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val filteredNotes: StateFlow<List<NoteEntity>> = combine(
        repository.activeNotes,
        _searchQuery
    ) { notes, query ->
        if (query.isBlank()) {
            notes
        } else {
            notes.filter {
                it.title.contains(query, ignoreCase = true) ||
                it.content.contains(query, ignoreCase = true) ||
                it.tags.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    private val _eventFlow = MutableSharedFlow<String>()
    val eventFlow: SharedFlow<String> = _eventFlow.asSharedFlow()

    fun setSearch(q: String) {
        _searchQuery.value = q
    }

    fun saveNote(
        id: String?,
        title: String,
        content: String,
        subjectId: String?,
        tags: String = ""
    ) {
        viewModelScope.launch {
            if (title.isBlank()) {
                _eventFlow.emit("Note title is required")
                return@launch
            }
            val noteId = id ?: "note-${UUID.randomUUID().toString().take(8)}"
            val now = Instant.now().toString()
            val entity = NoteEntity(
                id = noteId,
                title = title.trim(),
                content = content.trim(),
                subjectId = subjectId,
                tags = tags.trim(),
                isPinned = false,
                isFavourite = false,
                isArchived = false,
                createdAt = now,
                updatedAt = now
            )
            if (id == null) {
                repository.insertNote(entity)
                _eventFlow.emit("Revision note saved")
            } else {
                repository.updateNote(entity)
                _eventFlow.emit("Note updated")
            }
        }
    }

    fun togglePin(note: NoteEntity) {
        viewModelScope.launch {
            repository.togglePinNote(note)
        }
    }

    fun toggleFavorite(note: NoteEntity) {
        viewModelScope.launch {
            repository.toggleFavoriteNote(note)
        }
    }

    fun deleteNote(note: NoteEntity) {
        viewModelScope.launch {
            repository.deleteNote(note)
            _eventFlow.emit("Note deleted")
        }
    }
}
