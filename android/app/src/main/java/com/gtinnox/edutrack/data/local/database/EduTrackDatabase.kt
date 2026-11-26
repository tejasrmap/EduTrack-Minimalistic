package com.gtinnox.edutrack.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.gtinnox.edutrack.data.local.dao.ExaminationDao
import com.gtinnox.edutrack.data.local.dao.GoalDao
import com.gtinnox.edutrack.data.local.dao.NoteDao
import com.gtinnox.edutrack.data.local.dao.NotificationDao
import com.gtinnox.edutrack.data.local.dao.ScheduleEventDao
import com.gtinnox.edutrack.data.local.dao.StudySessionDao
import com.gtinnox.edutrack.data.local.dao.SubjectDao
import com.gtinnox.edutrack.data.local.dao.TaskDao
import com.gtinnox.edutrack.data.local.dao.UserProfileDao
import com.gtinnox.edutrack.data.local.entity.ExaminationEntity
import com.gtinnox.edutrack.data.local.entity.GoalEntity
import com.gtinnox.edutrack.data.local.entity.NoteEntity
import com.gtinnox.edutrack.data.local.entity.NotificationEntity
import com.gtinnox.edutrack.data.local.entity.ScheduleEventEntity
import com.gtinnox.edutrack.data.local.entity.StudySessionEntity
import com.gtinnox.edutrack.data.local.entity.SubjectEntity
import com.gtinnox.edutrack.data.local.entity.TaskEntity
import com.gtinnox.edutrack.data.local.entity.UserProfileEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        SubjectEntity::class,
        StudySessionEntity::class,
        TaskEntity::class,
        ExaminationEntity::class,
        GoalEntity::class,
        NoteEntity::class,
        ScheduleEventEntity::class,
        NotificationEntity::class,
        UserProfileEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class EduTrackDatabase : RoomDatabase() {

    abstract fun subjectDao(): SubjectDao
    abstract fun studySessionDao(): StudySessionDao
    abstract fun taskDao(): TaskDao
    abstract fun examinationDao(): ExaminationDao
    abstract fun goalDao(): GoalDao
    abstract fun noteDao(): NoteDao
    abstract fun scheduleEventDao(): ScheduleEventDao
    abstract fun notificationDao(): NotificationDao
    abstract fun userProfileDao(): UserProfileDao

    companion object {
        @Volatile
        private var INSTANCE: EduTrackDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): EduTrackDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EduTrackDatabase::class.java,
                    "edutrack_minimal_academic.db"
                )
                    .addCallback(EduTrackDatabaseCallback(scope))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class EduTrackDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateDatabase(database)
                }
            }
        }

        private suspend fun populateDatabase(db: EduTrackDatabase) {
            db.userProfileDao().insertOrUpdate(DatabaseSeed.initialProfile)
            db.subjectDao().insertSubjects(DatabaseSeed.initialSubjects)
            db.studySessionDao().insertSessions(DatabaseSeed.initialSessions)
            db.taskDao().insertTasks(DatabaseSeed.initialTasks)
            db.examinationDao().insertExaminations(DatabaseSeed.initialExams)
            db.goalDao().insertGoals(DatabaseSeed.initialGoals)
            db.noteDao().insertNotes(DatabaseSeed.initialNotes)
            db.scheduleEventDao().insertEvents(DatabaseSeed.initialEvents)
            db.notificationDao().insertNotifications(DatabaseSeed.initialNotifications)
        }
    }
}
