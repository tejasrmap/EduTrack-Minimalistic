package com.gtinnox.edutrack.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.gtinnox.edutrack.ui.EduTrackApp
import com.gtinnox.edutrack.ui.theme.EduTrackTheme
import com.gtinnox.edutrack.ui.viewmodel.AnalyticsViewModel
import com.gtinnox.edutrack.ui.viewmodel.AssignmentViewModel
import com.gtinnox.edutrack.ui.viewmodel.AuthViewModel
import com.gtinnox.edutrack.ui.viewmodel.DashboardViewModel
import com.gtinnox.edutrack.ui.viewmodel.ExamViewModel
import com.gtinnox.edutrack.ui.viewmodel.GoalViewModel
import com.gtinnox.edutrack.ui.viewmodel.HistoryViewModel
import com.gtinnox.edutrack.ui.viewmodel.NoteViewModel
import com.gtinnox.edutrack.ui.viewmodel.OnboardingViewModel
import com.gtinnox.edutrack.ui.viewmodel.PlannerViewModel
import com.gtinnox.edutrack.ui.viewmodel.ProfileViewModel
import com.gtinnox.edutrack.ui.viewmodel.SessionViewModel
import com.gtinnox.edutrack.ui.viewmodel.SettingsViewModel
import com.gtinnox.edutrack.ui.viewmodel.SubjectViewModel
import com.gtinnox.edutrack.ui.viewmodel.TimerViewModel
import com.gtinnox.edutrack.ui.viewmodel.ViewModelFactory

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = application as EduTrackApplication
        val repository = app.repository
        val authRepository = app.authRepository
        val factory = ViewModelFactory(repository, authRepository)

        val authViewModel: AuthViewModel by viewModels { factory }
        val dashboardViewModel: DashboardViewModel by viewModels { factory }
        val subjectViewModel: SubjectViewModel by viewModels { factory }
        val sessionViewModel: SessionViewModel by viewModels { factory }
        val timerViewModel: TimerViewModel by viewModels { factory }
        val plannerViewModel: PlannerViewModel by viewModels { factory }
        val assignmentViewModel: AssignmentViewModel by viewModels { factory }
        val examViewModel: ExamViewModel by viewModels { factory }
        val goalViewModel: GoalViewModel by viewModels { factory }
        val noteViewModel: NoteViewModel by viewModels { factory }
        val analyticsViewModel: AnalyticsViewModel by viewModels { factory }
        val historyViewModel: HistoryViewModel by viewModels { factory }
        val settingsViewModel: SettingsViewModel by viewModels { factory }
        val profileViewModel: ProfileViewModel by viewModels { factory }
        val onboardingViewModel: OnboardingViewModel by viewModels { factory }

        setContent {
            val settingsState by settingsViewModel.uiState.collectAsState()

            EduTrackTheme(darkTheme = settingsState.isDarkMode) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    EduTrackApp(
                        repository = repository,
                        authViewModel = authViewModel,
                        dashboardViewModel = dashboardViewModel,
                        subjectViewModel = subjectViewModel,
                        sessionViewModel = sessionViewModel,
                        timerViewModel = timerViewModel,
                        plannerViewModel = plannerViewModel,
                        assignmentViewModel = assignmentViewModel,
                        examViewModel = examViewModel,
                        goalViewModel = goalViewModel,
                        noteViewModel = noteViewModel,
                        analyticsViewModel = analyticsViewModel,
                        historyViewModel = historyViewModel,
                        settingsViewModel = settingsViewModel,
                        profileViewModel = profileViewModel,
                        onboardingViewModel = onboardingViewModel
                    )
                }
            }
        }
    }
}
