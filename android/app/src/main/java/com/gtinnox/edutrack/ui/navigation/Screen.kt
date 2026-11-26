package com.gtinnox.edutrack.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Timer
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object Subjects : Screen("subjects")
    object SubjectDetail : Screen("subjects/{subjectId}") {
        fun createRoute(subjectId: String) = "subjects/$subjectId"
    }
    object AddEditSubject : Screen("subjects_edit?subjectId={subjectId}") {
        fun createRoute(subjectId: String? = null) = if (subjectId != null) "subjects_edit?subjectId=$subjectId" else "subjects_edit"
    }

    object AddEditSession : Screen("sessions_edit?sessionId={sessionId}&subjectId={subjectId}") {
        fun createRoute(sessionId: String? = null, subjectId: String? = null): String {
            return when {
                sessionId != null && subjectId != null -> "sessions_edit?sessionId=$sessionId&subjectId=$subjectId"
                sessionId != null -> "sessions_edit?sessionId=$sessionId"
                subjectId != null -> "sessions_edit?subjectId=$subjectId"
                else -> "sessions_edit"
            }
        }
    }
    object SessionDetail : Screen("sessions/{sessionId}") {
        fun createRoute(sessionId: String) = "sessions/$sessionId"
    }

    object Timer : Screen("timer")
    object Planner : Screen("planner")
    object Assignments : Screen("assignments")
    object Examinations : Screen("examinations")
    object Goals : Screen("goals")
    object Notes : Screen("notes")
    object Analytics : Screen("analytics")
    object History : Screen("history")
    object Notifications : Screen("notifications")
    object Profile : Screen("profile")
    object Settings : Screen("settings")
    object Onboarding : Screen("onboarding")
}

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Dashboard : BottomNavItem("dashboard", "Dashboard", Icons.Default.Dashboard)
    object Subjects : BottomNavItem("subjects", "Subjects", Icons.AutoMirrored.Filled.MenuBook)
    object Timer : BottomNavItem("timer", "Timer", Icons.Default.Timer)
    object Planner : BottomNavItem("planner", "Planner", Icons.Default.CalendarMonth)
    object Analytics : BottomNavItem("analytics", "Analytics", Icons.Default.BarChart)
    object More : BottomNavItem("more_menu", "More", Icons.Default.MoreHoriz)
}
