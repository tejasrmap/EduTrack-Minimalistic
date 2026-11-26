package com.gtinnox.edutrack.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.gtinnox.edutrack.data.repository.EduTrackRepository
import com.gtinnox.edutrack.ui.theme.PrimaryForest
import com.gtinnox.edutrack.ui.analytics.AnalyticsScreen
import com.gtinnox.edutrack.ui.assignments.AssignmentsScreen
import com.gtinnox.edutrack.ui.components.AcademicHeader
import com.gtinnox.edutrack.ui.components.EduTrackBottomBar
import com.gtinnox.edutrack.ui.components.GlobalSearchDialog
import com.gtinnox.edutrack.ui.components.QuickAddBottomSheet
import com.gtinnox.edutrack.ui.dashboard.DashboardScreen
import com.gtinnox.edutrack.ui.examinations.ExaminationsScreen
import com.gtinnox.edutrack.ui.goals.GoalsScreen
import com.gtinnox.edutrack.ui.history.HistoryScreen
import com.gtinnox.edutrack.ui.navigation.MoreMenuScreen
import com.gtinnox.edutrack.ui.navigation.Screen
import com.gtinnox.edutrack.ui.notes.NotesScreen
import com.gtinnox.edutrack.ui.notifications.NotificationsScreen
import com.gtinnox.edutrack.ui.onboarding.OnboardingScreen
import com.gtinnox.edutrack.ui.planner.PlannerScreen
import com.gtinnox.edutrack.ui.profile.ProfileScreen
import com.gtinnox.edutrack.ui.sessions.AddEditSessionScreen
import com.gtinnox.edutrack.ui.sessions.SessionDetailScreen
import com.gtinnox.edutrack.ui.settings.SettingsScreen
import com.gtinnox.edutrack.ui.subjects.AddEditSubjectScreen
import com.gtinnox.edutrack.ui.subjects.SubjectDetailScreen
import com.gtinnox.edutrack.ui.subjects.SubjectsScreen
import com.gtinnox.edutrack.ui.theme.AcademicIndigo
import com.gtinnox.edutrack.ui.timer.TimerScreen
import com.gtinnox.edutrack.ui.viewmodel.AnalyticsViewModel
import com.gtinnox.edutrack.ui.viewmodel.AssignmentViewModel
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

@Composable
fun EduTrackApp(
    repository: EduTrackRepository,
    dashboardViewModel: DashboardViewModel,
    subjectViewModel: SubjectViewModel,
    sessionViewModel: SessionViewModel,
    timerViewModel: TimerViewModel,
    plannerViewModel: PlannerViewModel,
    assignmentViewModel: AssignmentViewModel,
    examViewModel: ExamViewModel,
    goalViewModel: GoalViewModel,
    noteViewModel: NoteViewModel,
    analyticsViewModel: AnalyticsViewModel,
    historyViewModel: HistoryViewModel,
    settingsViewModel: SettingsViewModel,
    profileViewModel: ProfileViewModel,
    onboardingViewModel: OnboardingViewModel
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    var showQuickAdd by remember { mutableStateOf(false) }
    var showGlobalSearch by remember { mutableStateOf(false) }

    val allSubjects by subjectViewModel.allSubjects.collectAsState()
    val allTasks by assignmentViewModel.filteredTasks.collectAsState()
    val allExams by examViewModel.subjects.collectAsState()
    val allNotes by noteViewModel.filteredNotes.collectAsState()
    val notifications by repository.allNotifications.collectAsState(initial = emptyList())
    val unreadCount = notifications.count { !it.isRead }

    val topLevelRoutes = listOf(
        Screen.Dashboard.route,
        Screen.Subjects.route,
        Screen.Timer.route,
        Screen.Planner.route,
        Screen.Analytics.route,
        "more_menu"
    )
    val isTopLevel = currentRoute in topLevelRoutes

    if (showQuickAdd) {
        QuickAddBottomSheet(
            onDismiss = { showQuickAdd = false },
            onAddSession = { navController.navigate(Screen.AddEditSession.createRoute()) },
            onAddSubject = { navController.navigate(Screen.AddEditSubject.createRoute()) },
            onAddTask = { navController.navigate(Screen.Assignments.route) },
            onAddExam = { navController.navigate(Screen.Examinations.route) },
            onAddNote = { navController.navigate(Screen.Notes.route) },
            onAddEvent = { navController.navigate(Screen.Planner.route) }
        )
    }

    if (showGlobalSearch) {
        GlobalSearchDialog(
            subjects = allSubjects,
            tasks = allTasks,
            exams = emptyList(),
            notes = allNotes,
            onDismiss = { showGlobalSearch = false },
            onSelectSubject = { subId ->
                navController.navigate(Screen.SubjectDetail.createRoute(subId))
            },
            onSelectTask = {
                navController.navigate(Screen.Assignments.route)
            }
        )
    }

    Scaffold(
        bottomBar = {
            if (isTopLevel) {
                EduTrackBottomBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(Screen.Dashboard.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        },
        floatingActionButton = {
            if (isTopLevel && currentRoute != Screen.Timer.route) {
                FloatingActionButton(
                    onClick = { showQuickAdd = true },
                    containerColor = PrimaryForest,
                    contentColor = androidx.compose.ui.graphics.Color.White,
                    shape = androidx.compose.foundation.shape.CircleShape,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Quick Add")
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    viewModel = dashboardViewModel,
                    onNavigateToTimer = { subId ->
                        if (subId != null) timerViewModel.setSubject(subId)
                        navController.navigate(Screen.Timer.route)
                    },
                    onNavigateToSubject = { subId ->
                        navController.navigate(Screen.SubjectDetail.createRoute(subId))
                    },
                    onNavigateToAssignments = {
                        navController.navigate(Screen.Assignments.route)
                    },
                    onNavigateToExams = {
                        navController.navigate(Screen.Examinations.route)
                    },
                    onLogSession = {
                        navController.navigate(Screen.AddEditSession.createRoute())
                    },
                    onSearchClick = { showGlobalSearch = true },
                    onNotificationsClick = { navController.navigate(Screen.Notifications.route) },
                    unreadNotificationsCount = unreadCount
                )
            }

            composable(Screen.Subjects.route) {
                SubjectsScreen(
                    viewModel = subjectViewModel,
                    onNavigateToDetail = { subId ->
                        navController.navigate(Screen.SubjectDetail.createRoute(subId))
                    },
                    onNavigateToAdd = {
                        navController.navigate(Screen.AddEditSubject.createRoute())
                    }
                )
            }

            composable(
                route = Screen.SubjectDetail.route,
                arguments = listOf(navArgument("subjectId") { type = NavType.StringType })
            ) { backStack ->
                val subjectId = backStack.arguments?.getString("subjectId") ?: ""
                SubjectDetailScreen(
                    subjectId = subjectId,
                    viewModel = subjectViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToEdit = { subId ->
                        navController.navigate(Screen.AddEditSubject.createRoute(subId))
                    },
                    onNavigateToTimer = { subId ->
                        timerViewModel.setSubject(subId)
                        navController.navigate(Screen.Timer.route)
                    }
                )
            }

            composable(
                route = Screen.AddEditSubject.route,
                arguments = listOf(navArgument("subjectId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                })
            ) { backStack ->
                val subjectId = backStack.arguments?.getString("subjectId")
                AddEditSubjectScreen(
                    subjectId = subjectId,
                    viewModel = subjectViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.AddEditSession.route,
                arguments = listOf(
                    navArgument("sessionId") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    },
                    navArgument("subjectId") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                )
            ) { backStack ->
                val sessionId = backStack.arguments?.getString("sessionId")
                val subjectId = backStack.arguments?.getString("subjectId")
                AddEditSessionScreen(
                    sessionId = sessionId,
                    initialSubjectId = subjectId,
                    sessionViewModel = sessionViewModel,
                    subjectViewModel = subjectViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.SessionDetail.route,
                arguments = listOf(navArgument("sessionId") { type = NavType.StringType })
            ) { backStack ->
                val sessionId = backStack.arguments?.getString("sessionId") ?: ""
                SessionDetailScreen(
                    sessionId = sessionId,
                    viewModel = sessionViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToEdit = { id ->
                        navController.navigate(Screen.AddEditSession.createRoute(sessionId = id))
                    }
                )
            }

            composable(Screen.Timer.route) {
                TimerScreen(viewModel = timerViewModel)
            }

            composable(Screen.Planner.route) {
                PlannerScreen(viewModel = plannerViewModel)
            }

            composable(Screen.Analytics.route) {
                AnalyticsScreen(viewModel = analyticsViewModel)
            }

            composable("more_menu") {
                MoreMenuScreen(
                    onNavigate = { route -> navController.navigate(route) }
                )
            }

            composable(Screen.Assignments.route) {
                AssignmentsScreen(viewModel = assignmentViewModel)
            }

            composable(Screen.Examinations.route) {
                ExaminationsScreen(viewModel = examViewModel)
            }

            composable(Screen.Goals.route) {
                GoalsScreen(viewModel = goalViewModel)
            }

            composable(Screen.Notes.route) {
                NotesScreen(viewModel = noteViewModel)
            }

            composable(Screen.History.route) {
                HistoryScreen(
                    viewModel = historyViewModel,
                    onNavigateToDetail = { sessId ->
                        navController.navigate(Screen.SessionDetail.createRoute(sessId))
                    }
                )
            }

            composable(Screen.Notifications.route) {
                NotificationsScreen(
                    repository = repository,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    viewModel = profileViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Settings.route) {
                SettingsScreen(
                    viewModel = settingsViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Onboarding.route) {
                OnboardingScreen(
                    viewModel = onboardingViewModel,
                    onFinish = {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}
