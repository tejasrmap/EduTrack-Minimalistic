package com.gtinnox.edutrack.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gtinnox.edutrack.data.local.entity.SubjectEntity
import com.gtinnox.edutrack.data.local.entity.TaskEntity
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

class AssignmentViewModel(
    private val repository: EduTrackRepository
) : ViewModel() {

    val subjects: StateFlow<List<SubjectEntity>> = repository.allSubjects.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    private val _filterSubjectId = MutableStateFlow<String?>(null)
    val filterSubjectId: StateFlow<String?> = _filterSubjectId.asStateFlow()

    private val _filterStatus = MutableStateFlow<String?>("all") // "all", "pending", "completed"
    val filterStatus: StateFlow<String?> = _filterStatus.asStateFlow()

    val filteredTasks: StateFlow<List<TaskEntity>> = combine(
        repository.allTasks,
        _filterSubjectId,
        _filterStatus
    ) { tasks, subjectId, status ->
        tasks.filter { task ->
            val matchSubject = subjectId == null || task.subjectId == subjectId
            val matchStatus = when (status) {
                "pending" -> task.status != "completed"
                "completed" -> task.status == "completed"
                else -> true
            }
            matchSubject && matchStatus
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    private val _eventFlow = MutableSharedFlow<String>()
    val eventFlow: SharedFlow<String> = _eventFlow.asSharedFlow()

    fun setFilterSubject(id: String?) {
        _filterSubjectId.value = id
    }

    fun setFilterStatus(status: String) {
        _filterStatus.value = status
    }

    fun saveTask(
        id: String?,
        title: String,
        description: String,
        subjectId: String,
        category: String,
        priority: String,
        dueDate: String,
        dueTime: String = "23:59",
        estimatedMinutes: Int = 60
    ) {
        viewModelScope.launch {
            if (title.isBlank()) {
                _eventFlow.emit("Task title is required")
                return@launch
            }
            if (subjectId.isBlank()) {
                _eventFlow.emit("Please select a subject")
                return@launch
            }

            val taskId = id ?: "task-${UUID.randomUUID().toString().take(8)}"
            val entity = TaskEntity(
                id = taskId,
                title = title.trim(),
                description = description.trim(),
                subjectId = subjectId,
                category = category.ifBlank { "assignment" },
                priority = priority.ifBlank { "medium" },
                status = "not_started",
                dueDate = dueDate,
                dueTime = dueTime,
                estimatedMinutes = estimatedMinutes,
                createdAt = Instant.now().toString()
            )

            if (id == null) {
                repository.insertTask(entity)
                _eventFlow.emit("Assignment added successfully")
            } else {
                repository.updateTask(entity)
                _eventFlow.emit("Assignment updated")
            }
        }
    }

    fun toggleTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.toggleTaskCompleted(task)
            val msg = if (task.status == "completed") "Marked incomplete" else "Assignment completed!"
            _eventFlow.emit(msg)
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.deleteTask(task)
            _eventFlow.emit("Task deleted")
        }
    }
}
