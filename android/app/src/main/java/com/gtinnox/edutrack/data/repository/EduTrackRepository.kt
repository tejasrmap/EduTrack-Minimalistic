package com.gtinnox.edutrack.data.repository

import com.google.gson.Gson
import com.gtinnox.edutrack.data.local.database.DatabaseSeed
import com.gtinnox.edutrack.data.local.database.EduTrackDatabase
import com.gtinnox.edutrack.data.local.entity.ExaminationEntity
import com.gtinnox.edutrack.data.local.entity.GoalEntity
import com.gtinnox.edutrack.data.local.entity.NoteEntity
import com.gtinnox.edutrack.data.local.entity.NotificationEntity
import com.gtinnox.edutrack.data.local.entity.ScheduleEventEntity
import com.gtinnox.edutrack.data.local.entity.StudySessionEntity
import com.gtinnox.edutrack.data.local.entity.SubjectEntity
import com.gtinnox.edutrack.data.local.entity.TaskEntity
import com.gtinnox.edutrack.data.local.entity.UserProfileEntity
import com.gtinnox.edutrack.data.local.relations.SessionWithSubject
import com.gtinnox.edutrack.data.local.relations.SubjectWithSessions
import kotlinx.coroutines.flow.Flow
import java.time.Instant
import java.time.LocalDate

class EduTrackRepository(private val database: EduTrackDatabase) {

    private val subjectDao = database.subjectDao()
    private val sessionDao = database.studySessionDao()
    private val taskDao = database.taskDao()
    private val examDao = database.examinationDao()
    private val goalDao = database.goalDao()
    private val noteDao = database.noteDao()
    private val eventDao = database.scheduleEventDao()
    private val notifDao = database.notificationDao()
    private val profileDao = database.userProfileDao()

    // --- Subjects ---
    val allSubjects: Flow<List<SubjectEntity>> = subjectDao.getAllSubjects()
    val allSubjectsWithSessions: Flow<List<SubjectWithSessions>> = subjectDao.getAllSubjectsWithSessions()

    fun getSubjectById(id: String): Flow<SubjectEntity?> = subjectDao.getSubjectById(id)
    fun getSubjectWithSessions(id: String): Flow<SubjectWithSessions?> = subjectDao.getSubjectWithSessions(id)

    suspend fun insertSubject(subject: SubjectEntity) = subjectDao.insertSubject(subject)
    suspend fun updateSubject(subject: SubjectEntity) = subjectDao.updateSubject(subject)
    suspend fun deleteSubject(subject: SubjectEntity) = subjectDao.deleteSubject(subject)
    suspend fun deleteSubjectById(id: String) = subjectDao.deleteSubjectById(id)

    // --- Study Sessions ---
    val allSessions: Flow<List<StudySessionEntity>> = sessionDao.getAllSessions()
    val allSessionsWithSubject: Flow<List<SessionWithSubject>> = sessionDao.getAllSessionsWithSubject()
    val totalStudyMinutes: Flow<Int?> = sessionDao.getTotalStudyMinutes()

    fun getSessionById(id: String): Flow<StudySessionEntity?> = sessionDao.getSessionById(id)
    fun getSessionWithSubject(id: String): Flow<SessionWithSubject?> = sessionDao.getSessionWithSubject(id)
    fun getSessionsForSubject(subjectId: String): Flow<List<StudySessionEntity>> = sessionDao.getSessionsForSubject(subjectId)
    fun getStudyMinutesForSubject(subjectId: String): Flow<Int?> = sessionDao.getStudyMinutesForSubject(subjectId)

    suspend fun insertSession(session: StudySessionEntity) = sessionDao.insertSession(session)
    suspend fun updateSession(session: StudySessionEntity) = sessionDao.updateSession(session)
    suspend fun deleteSession(session: StudySessionEntity) = sessionDao.deleteSession(session)
    suspend fun deleteSessionById(id: String) = sessionDao.deleteSessionById(id)

    // --- Tasks / Assignments ---
    val allTasks: Flow<List<TaskEntity>> = taskDao.getAllTasks()

    fun getTasksForSubject(subjectId: String): Flow<List<TaskEntity>> = taskDao.getTasksForSubject(subjectId)
    fun getTaskById(id: String): Flow<TaskEntity?> = taskDao.getTaskById(id)

    suspend fun insertTask(task: TaskEntity) = taskDao.insertTask(task)
    suspend fun updateTask(task: TaskEntity) = taskDao.updateTask(task)
    suspend fun deleteTask(task: TaskEntity) = taskDao.deleteTask(task)
    suspend fun deleteTaskById(id: String) = taskDao.deleteTaskById(id)
    suspend fun toggleTaskCompleted(task: TaskEntity) {
        val newStatus = if (task.status == "completed") "in_progress" else "completed"
        val completedAt = if (newStatus == "completed") Instant.now().toString() else null
        taskDao.updateTask(task.copy(status = newStatus, completedAt = completedAt))
    }

    // --- Examinations ---
    val allExaminations: Flow<List<ExaminationEntity>> = examDao.getAllExaminations()

    fun getExaminationsForSubject(subjectId: String): Flow<List<ExaminationEntity>> = examDao.getExaminationsForSubject(subjectId)
    fun getExaminationById(id: String): Flow<ExaminationEntity?> = examDao.getExaminationById(id)

    suspend fun insertExamination(exam: ExaminationEntity) = examDao.insertExamination(exam)
    suspend fun updateExamination(exam: ExaminationEntity) = examDao.updateExamination(exam)
    suspend fun deleteExamination(exam: ExaminationEntity) = examDao.deleteExamination(exam)
    suspend fun deleteExaminationById(id: String) = examDao.deleteExaminationById(id)

    // --- Goals ---
    val allGoals: Flow<List<GoalEntity>> = goalDao.getAllGoals()

    fun getGoalById(id: String): Flow<GoalEntity?> = goalDao.getGoalById(id)

    suspend fun insertGoal(goal: GoalEntity) = goalDao.insertGoal(goal)
    suspend fun updateGoal(goal: GoalEntity) = goalDao.updateGoal(goal)
    suspend fun deleteGoal(goal: GoalEntity) = goalDao.deleteGoal(goal)
    suspend fun deleteGoalById(id: String) = goalDao.deleteGoalById(id)
    suspend fun toggleGoalCompleted(goal: GoalEntity) {
        val newCompleted = !goal.isCompleted
        val completedAt = if (newCompleted) Instant.now().toString() else null
        goalDao.updateGoal(goal.copy(isCompleted = newCompleted, completedAt = completedAt))
    }

    // --- Notes ---
    val activeNotes: Flow<List<NoteEntity>> = noteDao.getActiveNotes()
    val allNotes: Flow<List<NoteEntity>> = noteDao.getAllNotes()

    fun getNoteById(id: String): Flow<NoteEntity?> = noteDao.getNoteById(id)

    suspend fun insertNote(note: NoteEntity) = noteDao.insertNote(note)
    suspend fun updateNote(note: NoteEntity) = noteDao.updateNote(note)
    suspend fun deleteNote(note: NoteEntity) = noteDao.deleteNote(note)
    suspend fun deleteNoteById(id: String) = noteDao.deleteNoteById(id)
    suspend fun togglePinNote(note: NoteEntity) = noteDao.updateNote(note.copy(isPinned = !note.isPinned))
    suspend fun toggleFavoriteNote(note: NoteEntity) = noteDao.updateNote(note.copy(isFavourite = !note.isFavourite))
    suspend fun archiveNote(note: NoteEntity) = noteDao.updateNote(note.copy(isArchived = true))

    // --- Schedule Events ---
    val allEvents: Flow<List<ScheduleEventEntity>> = eventDao.getAllEvents()

    fun getEventsForDate(date: String): Flow<List<ScheduleEventEntity>> = eventDao.getEventsForDate(date)

    suspend fun insertEvent(event: ScheduleEventEntity) = eventDao.insertEvent(event)
    suspend fun updateEvent(event: ScheduleEventEntity) = eventDao.updateEvent(event)
    suspend fun deleteEvent(event: ScheduleEventEntity) = eventDao.deleteEvent(event)
    suspend fun deleteEventById(id: String) = eventDao.deleteEventById(id)
    suspend fun toggleEventCompleted(event: ScheduleEventEntity) = eventDao.updateEvent(event.copy(isCompleted = !event.isCompleted))

    // --- Notifications ---
    val allNotifications: Flow<List<NotificationEntity>> = notifDao.getAllNotifications()

    suspend fun insertNotification(item: NotificationEntity) = notifDao.insertNotification(item)
    suspend fun markNotificationRead(id: String) = notifDao.markAsRead(id)
    suspend fun markAllNotificationsRead() = notifDao.markAllAsRead()
    suspend fun deleteNotification(id: String) = notifDao.deleteNotification(id)
    suspend fun clearAllNotifications() = notifDao.clearAllNotifications()

    // --- User Profile ---
    val userProfile: Flow<UserProfileEntity?> = profileDao.getUserProfile()

    suspend fun getUserProfileDirect(): UserProfileEntity? = profileDao.getUserProfileDirect()
    suspend fun updateProfile(profile: UserProfileEntity) = profileDao.insertOrUpdate(profile)
    suspend fun setOnboarded(id: String, isOnboarded: Boolean) = profileDao.setOnboarded(id, isOnboarded)

    // --- Reset & Seed ---
    suspend fun resetToDefaultData() {
        notifDao.clearAllNotifications()
        database.clearAllTables()
        profileDao.insertOrUpdate(DatabaseSeed.initialProfile)
        subjectDao.insertSubjects(DatabaseSeed.initialSubjects)
        sessionDao.insertSessions(DatabaseSeed.initialSessions)
        taskDao.insertTasks(DatabaseSeed.initialTasks)
        examDao.insertExaminations(DatabaseSeed.initialExams)
        goalDao.insertGoals(DatabaseSeed.initialGoals)
        noteDao.insertNotes(DatabaseSeed.initialNotes)
        eventDao.insertEvents(DatabaseSeed.initialEvents)
        notifDao.insertNotifications(DatabaseSeed.initialNotifications)
    }

    // --- Data Export & Import ---
    data class BackupPayload(
        val profile: UserProfileEntity?,
        val subjects: List<SubjectEntity>,
        val sessions: List<StudySessionEntity>,
        val tasks: List<TaskEntity>,
        val examinations: List<ExaminationEntity>,
        val goals: List<GoalEntity>,
        val notes: List<NoteEntity>,
        val events: List<ScheduleEventEntity>
    )

    suspend fun exportDataAsJson(
        subjects: List<SubjectEntity>,
        sessions: List<StudySessionEntity>,
        tasks: List<TaskEntity>,
        exams: List<ExaminationEntity>,
        goals: List<GoalEntity>,
        notes: List<NoteEntity>,
        events: List<ScheduleEventEntity>,
        profile: UserProfileEntity?
    ): String {
        val payload = BackupPayload(
            profile = profile,
            subjects = subjects,
            sessions = sessions,
            tasks = tasks,
            examinations = exams,
            goals = goals,
            notes = notes,
            events = events
        )
        return Gson().toJson(payload)
    }

    suspend fun importDataFromJson(jsonString: String): Boolean {
        return try {
            val payload = Gson().fromJson(jsonString, BackupPayload::class.java)
            if (payload != null) {
                payload.profile?.let { profileDao.insertOrUpdate(it) }
                if (payload.subjects.isNotEmpty()) subjectDao.insertSubjects(payload.subjects)
                if (payload.sessions.isNotEmpty()) sessionDao.insertSessions(payload.sessions)
                if (payload.tasks.isNotEmpty()) taskDao.insertTasks(payload.tasks)
                if (payload.examinations.isNotEmpty()) examDao.insertExaminations(payload.examinations)
                if (payload.goals.isNotEmpty()) goalDao.insertGoals(payload.goals)
                if (payload.notes.isNotEmpty()) noteDao.insertNotes(payload.notes)
                if (payload.events.isNotEmpty()) eventDao.insertEvents(payload.events)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }
}
