package com.gtinnox.edutrack.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.gtinnox.edutrack.data.local.database.EduTrackDatabase
import com.gtinnox.edutrack.data.repository.EduTrackRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class EduTrackApplication : Application() {

    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    val database by lazy { EduTrackDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { EduTrackRepository(database) }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_STUDY_ALERTS,
                "EduTrack Study Alerts",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for assignment deadlines, study reminders, and exam alerts"
            }
            val timerChannel = NotificationChannel(
                CHANNEL_TIMER,
                "EduTrack Pomodoro Timer",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alerts when focus intervals or breaks complete"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
            manager.createNotificationChannel(timerChannel)
        }
    }

    companion object {
        const val CHANNEL_STUDY_ALERTS = "edutrack_study_alerts"
        const val CHANNEL_TIMER = "edutrack_timer"
    }
}
