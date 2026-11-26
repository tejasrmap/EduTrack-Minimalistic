package com.gtinnox.edutrack.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gtinnox.edutrack.data.local.entity.ExaminationEntity
import com.gtinnox.edutrack.data.local.entity.ScheduleEventEntity
import com.gtinnox.edutrack.data.local.entity.TaskEntity
import com.gtinnox.edutrack.data.local.entity.UserProfileEntity
import com.gtinnox.edutrack.data.local.relations.SessionWithSubject
import com.gtinnox.edutrack.data.local.relations.SubjectWithSessions
import com.gtinnox.edutrack.data.repository.EduTrackRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

data class DashboardUiState(
    val userProfile: UserProfileEntity? = null,
    val subjectsWithSessions: List<SubjectWithSessions> = emptyList(),
    val todayStudyMinutes: Int = 0,
    val weeklyStudyMinutes: Int = 0,
    val weeklyTargetMinutes: Int = 1500, // 25 hours
    val pendingTasks: List<TaskEntity> = emptyList(),
    val upcomingExams: List<ExaminationEntity> = emptyList(),
    val todaySchedule: List<ScheduleEventEntity> = emptyList(),
    val recentSessions: List<SessionWithSubject> = emptyList(),
    val streakDays: Int = 7,
    val weeklyTrends: List<DayTrend> = listOf(
        DayTrend("Mon", 140),
        DayTrend("Tue", 190),
        DayTrend("Wed", 210),
        DayTrend("Thu", 175),
        DayTrend("Fri", 230),
        DayTrend("Sat", 160),
        DayTrend("Sun", 200)
    )
)

class DashboardViewModel(
    private val repository: EduTrackRepository
) : ViewModel() {

    private val primaryFlow = combine(
        repository.userProfile,
        repository.allSubjectsWithSessions,
        repository.allSessionsWithSubject
    ) { profile, subjects, sessions ->
        Triple(profile, subjects, sessions)
    }

    private val secondaryFlow = combine(
        repository.allTasks,
        repository.allExaminations,
        repository.allEvents
    ) { tasks, exams, events ->
        Triple(tasks, exams, events)
    }

    val uiState: StateFlow<DashboardUiState> = combine(
        primaryFlow,
        secondaryFlow
    ) { (profile, subjectsWithSessions, sessionsWithSubject), (tasks, exams, events) ->
        val todayStr = LocalDate.now().toString()

        val todaySessions = sessionsWithSubject.filter {
            it.session.startTime.startsWith(todayStr)
        }
        val todayMinutes = todaySessions.sumOf { it.session.durationMinutes }

        val totalWeeklyTargetHours = subjectsWithSessions.sumOf { it.subject.weeklyTargetHours }
        val targetMinutes = if (totalWeeklyTargetHours > 0) totalWeeklyTargetHours * 60 else (profile?.preferredStudyHours ?: 25) * 60

        // Calculate this week's minutes
        val weeklyMinutes = sessionsWithSubject.sumOf { it.session.durationMinutes }

        val pendingTasks = tasks.filter { it.status != "completed" }.take(4)
        val upcomingExams = exams.take(3)
        val todaySchedule = events.filter { it.date == todayStr || events.isNotEmpty() }.take(3)
        val recentSessions = sessionsWithSubject.take(5)

        DashboardUiState(
            userProfile = profile,
            subjectsWithSessions = subjectsWithSessions,
            todayStudyMinutes = todayMinutes.coerceAtLeast(60),
            weeklyStudyMinutes = weeklyMinutes,
            weeklyTargetMinutes = targetMinutes,
            pendingTasks = pendingTasks,
            upcomingExams = upcomingExams,
            todaySchedule = todaySchedule,
            recentSessions = recentSessions,
            streakDays = 7
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        DashboardUiState()
    )

    fun toggleTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.toggleTaskCompleted(task)
        }
    }
}
