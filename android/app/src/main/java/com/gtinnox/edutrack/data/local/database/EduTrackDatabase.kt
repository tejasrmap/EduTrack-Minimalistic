package com.gtinnox.edutrack.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.gtinnox.edutrack.data.local.dao.AcademicProfileDao
import com.gtinnox.edutrack.data.local.dao.ExaminationDao
import com.gtinnox.edutrack.data.local.dao.GoalDao
import com.gtinnox.edutrack.data.local.dao.NoteDao
import com.gtinnox.edutrack.data.local.dao.NotificationDao
import com.gtinnox.edutrack.data.local.dao.ScheduleEventDao
import com.gtinnox.edutrack.data.local.dao.StudyPreferencesDao
import com.gtinnox.edutrack.data.local.dao.StudySessionDao
import com.gtinnox.edutrack.data.local.dao.SubjectDao
import com.gtinnox.edutrack.data.local.dao.TaskDao
import com.gtinnox.edutrack.data.local.dao.UserAccountDao
import com.gtinnox.edutrack.data.local.dao.UserProfileDao
import com.gtinnox.edutrack.data.local.entity.AcademicProfileEntity
import com.gtinnox.edutrack.data.local.entity.ExaminationEntity
import com.gtinnox.edutrack.data.local.entity.GoalEntity
import com.gtinnox.edutrack.data.local.entity.NoteEntity
import com.gtinnox.edutrack.data.local.entity.NotificationEntity
import com.gtinnox.edutrack.data.local.entity.ScheduleEventEntity
import com.gtinnox.edutrack.data.local.entity.StudyPreferencesEntity
import com.gtinnox.edutrack.data.local.entity.StudySessionEntity
import com.gtinnox.edutrack.data.local.entity.SubjectEntity
import com.gtinnox.edutrack.data.local.entity.TaskEntity
import com.gtinnox.edutrack.data.local.entity.UserAccountEntity
import com.gtinnox.edutrack.data.local.entity.UserProfileEntity
import kotlinx.coroutines.CoroutineScope

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
        UserProfileEntity::class,
        UserAccountEntity::class,
        AcademicProfileEntity::class,
        StudyPreferencesEntity::class
    ],
    version = 2,
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
    abstract fun userAccountDao(): UserAccountDao
    abstract fun academicProfileDao(): AcademicProfileDao
    abstract fun studyPreferencesDao(): StudyPreferencesDao

    companion object {
        @Volatile
        private var INSTANCE: EduTrackDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // 1. Create user_accounts table
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `user_accounts` (
                        `id` TEXT NOT NULL,
                        `fullName` TEXT NOT NULL,
                        `email` TEXT NOT NULL,
                        `passwordHash` TEXT NOT NULL,
                        `passwordSalt` TEXT NOT NULL,
                        `createdAt` TEXT NOT NULL,
                        `updatedAt` TEXT NOT NULL,
                        `isOnboardingCompleted` INTEGER NOT NULL DEFAULT 0,
                        `tutorialsCompleted` TEXT NOT NULL DEFAULT '',
                        PRIMARY KEY(`id`)
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_user_accounts_email` ON `user_accounts` (`email`)")

                // 2. Create academic_profiles table
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `academic_profiles` (
                        `userId` TEXT NOT NULL,
                        `institution` TEXT NOT NULL,
                        `degree` TEXT NOT NULL,
                        `major` TEXT NOT NULL,
                        `academicYear` TEXT NOT NULL,
                        `semester` TEXT NOT NULL,
                        `studentId` TEXT,
                        `profilePhotoUri` TEXT,
                        `updatedAt` TEXT NOT NULL,
                        PRIMARY KEY(`userId`),
                        FOREIGN KEY(`userId`) REFERENCES `user_accounts`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                    """.trimIndent()
                )

                // 3. Create study_preferences table
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `study_preferences` (
                        `userId` TEXT NOT NULL,
                        `dailyStudyGoalMinutes` INTEGER NOT NULL,
                        `weeklyStudyGoalHours` INTEGER NOT NULL,
                        `focusDurationMinutes` INTEGER NOT NULL,
                        `shortBreakMinutes` INTEGER NOT NULL,
                        `longBreakMinutes` INTEGER NOT NULL,
                        `reminderPreferences` INTEGER NOT NULL,
                        `typicalStudyTime` TEXT NOT NULL,
                        `preferredStudyDays` TEXT NOT NULL,
                        `updatedAt` TEXT NOT NULL,
                        PRIMARY KEY(`userId`),
                        FOREIGN KEY(`userId`) REFERENCES `user_accounts`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                    """.trimIndent()
                )

                // 4. Safely add userId column with index to all user-owned tables
                val tablesWithUserId = listOf(
                    "subjects", "study_sessions", "tasks", "examinations",
                    "goals", "notes", "schedule_events", "notifications"
                )
                for (table in tablesWithUserId) {
                    db.execSQL("ALTER TABLE `$table` ADD COLUMN `userId` TEXT NOT NULL DEFAULT 'default-user'")
                    db.execSQL("CREATE INDEX IF NOT EXISTS `index_${table}_userId` ON `$table` (`userId`)")
                }
            }
        }

        fun getDatabase(context: Context, scope: CoroutineScope): EduTrackDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EduTrackDatabase::class.java,
                    "edutrack_minimal_academic.db"
                )
                    .addMigrations(MIGRATION_1_2)
                    .fallbackToDestructiveMigrationOnDowngrade()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
