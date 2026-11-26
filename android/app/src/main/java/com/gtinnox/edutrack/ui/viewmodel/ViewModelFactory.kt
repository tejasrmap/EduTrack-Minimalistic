package com.gtinnox.edutrack.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gtinnox.edutrack.data.repository.EduTrackRepository

class ViewModelFactory(
    private val repository: EduTrackRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(DashboardViewModel::class.java) ->
                DashboardViewModel(repository) as T
            modelClass.isAssignableFrom(SubjectViewModel::class.java) ->
                SubjectViewModel(repository) as T
            modelClass.isAssignableFrom(SessionViewModel::class.java) ->
                SessionViewModel(repository) as T
            modelClass.isAssignableFrom(TimerViewModel::class.java) ->
                TimerViewModel(repository) as T
            modelClass.isAssignableFrom(PlannerViewModel::class.java) ->
                PlannerViewModel(repository) as T
            modelClass.isAssignableFrom(AssignmentViewModel::class.java) ->
                AssignmentViewModel(repository) as T
            modelClass.isAssignableFrom(ExamViewModel::class.java) ->
                ExamViewModel(repository) as T
            modelClass.isAssignableFrom(GoalViewModel::class.java) ->
                GoalViewModel(repository) as T
            modelClass.isAssignableFrom(NoteViewModel::class.java) ->
                NoteViewModel(repository) as T
            modelClass.isAssignableFrom(AnalyticsViewModel::class.java) ->
                AnalyticsViewModel(repository) as T
            modelClass.isAssignableFrom(HistoryViewModel::class.java) ->
                HistoryViewModel(repository) as T
            modelClass.isAssignableFrom(SettingsViewModel::class.java) ->
                SettingsViewModel(repository) as T
            modelClass.isAssignableFrom(ProfileViewModel::class.java) ->
                ProfileViewModel(repository) as T
            modelClass.isAssignableFrom(OnboardingViewModel::class.java) ->
                OnboardingViewModel(repository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
