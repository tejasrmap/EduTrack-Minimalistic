package com.gtinnox.edutrack.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gtinnox.edutrack.data.local.entity.ScheduleEventEntity
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
import java.time.LocalDate
import java.util.UUID

class PlannerViewModel(
    private val repository: EduTrackRepository
) : ViewModel() {

    private val _selectedDate = MutableStateFlow(LocalDate.now().toString())
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    val subjects: StateFlow<List<SubjectEntity>> = repository.allSubjects.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val eventsForSelectedDate: StateFlow<List<ScheduleEventEntity>> = combine(
        repository.allEvents,
        _selectedDate
    ) { events, date ->
        events.filter { it.date == date || events.size < 5 }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    private val _eventFlow = MutableSharedFlow<String>()
    val eventFlow: SharedFlow<String> = _eventFlow.asSharedFlow()

    fun setDate(date: String) {
        _selectedDate.value = date
    }

    fun addEvent(
        title: String,
        subjectId: String,
        date: String,
        startTime: String,
        endTime: String,
        description: String = ""
    ) {
        viewModelScope.launch {
            if (title.isBlank() || subjectId.isBlank()) {
                _eventFlow.emit("Please enter a title and select a subject")
                return@launch
            }
            val event = ScheduleEventEntity(
                id = "event-${UUID.randomUUID().toString().take(8)}",
                title = title.trim(),
                subjectId = subjectId,
                date = date,
                startTime = startTime.ifBlank { "09:00" },
                endTime = endTime.ifBlank { "10:30" },
                description = description.trim(),
                reminder = true,
                isCompleted = false,
                createdAt = Instant.now().toString()
            )
            repository.insertEvent(event)
            _eventFlow.emit("Scheduled session added")
        }
    }

    fun toggleEvent(event: ScheduleEventEntity) {
        viewModelScope.launch {
            repository.toggleEventCompleted(event)
        }
    }

    fun deleteEvent(event: ScheduleEventEntity) {
        viewModelScope.launch {
            repository.deleteEvent(event)
            _eventFlow.emit("Scheduled block removed")
        }
    }
}
