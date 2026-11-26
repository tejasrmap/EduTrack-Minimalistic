package com.gtinnox.edutrack.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gtinnox.edutrack.data.local.entity.GoalEntity
import com.gtinnox.edutrack.data.repository.EduTrackRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.UUID

class GoalViewModel(
    private val repository: EduTrackRepository
) : ViewModel() {

    val allGoals: StateFlow<List<GoalEntity>> = repository.allGoals.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    private val _eventFlow = MutableSharedFlow<String>()
    val eventFlow: SharedFlow<String> = _eventFlow.asSharedFlow()

    fun saveGoal(
        id: String?,
        title: String,
        type: String,
        targetValue: Double,
        currentValue: Double,
        unit: String,
        targetDate: String
    ) {
        viewModelScope.launch {
            if (title.isBlank() || targetValue <= 0) {
                _eventFlow.emit("Please enter a goal title and target value")
                return@launch
            }
            val goalId = id ?: "goal-${UUID.randomUUID().toString().take(8)}"
            val entity = GoalEntity(
                id = goalId,
                title = title.trim(),
                type = type,
                targetValue = targetValue,
                currentValue = currentValue,
                unit = unit.ifBlank { "hours" },
                targetDate = targetDate,
                isCompleted = currentValue >= targetValue,
                createdAt = Instant.now().toString()
            )
            if (id == null) {
                repository.insertGoal(entity)
                _eventFlow.emit("Academic target created")
            } else {
                repository.updateGoal(entity)
                _eventFlow.emit("Target updated")
            }
        }
    }

    fun toggleGoal(goal: GoalEntity) {
        viewModelScope.launch {
            repository.toggleGoalCompleted(goal)
            _eventFlow.emit(if (!goal.isCompleted) "Target achieved! Great work!" else "Target reopened")
        }
    }

    fun deleteGoal(goal: GoalEntity) {
        viewModelScope.launch {
            repository.deleteGoal(goal)
            _eventFlow.emit("Target removed")
        }
    }
}
