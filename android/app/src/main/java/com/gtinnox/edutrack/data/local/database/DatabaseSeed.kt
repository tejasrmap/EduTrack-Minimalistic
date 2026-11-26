package com.gtinnox.edutrack.data.local.database

import com.gtinnox.edutrack.data.local.entity.ExaminationEntity
import com.gtinnox.edutrack.data.local.entity.GoalEntity
import com.gtinnox.edutrack.data.local.entity.NoteEntity
import com.gtinnox.edutrack.data.local.entity.NotificationEntity
import com.gtinnox.edutrack.data.local.entity.ScheduleEventEntity
import com.gtinnox.edutrack.data.local.entity.StudySessionEntity
import com.gtinnox.edutrack.data.local.entity.SubjectEntity
import com.gtinnox.edutrack.data.local.entity.TaskEntity
import com.gtinnox.edutrack.data.local.entity.UserProfileEntity

object DatabaseSeed {
    val initialProfile = UserProfileEntity(
        id = "user-default-1",
        name = "Julian Vance",
        email = "julian.vance@university.edu",
        university = "Oxford University / Institute of Technology",
        program = "B.Sc. Computer Science & Mathematics",
        academicYear = "Year 2 (Sophomore)",
        semester = "Fall Semester 2026",
        preferredStudyHours = 25,
        dailyTargetMinutes = 180,
        avatar = "JV",
        isOnboarded = true,
        createdAt = "2026-09-01T08:00:00Z"
    )

    val initialSubjects = listOf(
        SubjectEntity(
            id = "subj-1",
            name = "Algorithms & Data Structures",
            code = "CS 201",
            color = "#6477D9",
            description = "Graph algorithms, dynamic programming, asymptotic analysis, and advanced tree data structures.",
            semester = "Fall 2026",
            instructor = "Prof. Alistair Sterling",
            weeklyTargetHours = 7,
            attendanceRate = 96,
            createdAt = "2026-09-01T08:00:00Z"
        ),
        SubjectEntity(
            id = "subj-2",
            name = "Linear Algebra & Differential Calculus",
            code = "MATH 210",
            color = "#16A37B",
            description = "Vector spaces, eigenvalues, matrix decompositions, and ordinary differential equations.",
            semester = "Fall 2026",
            instructor = "Dr. Evelyn Montgomery",
            weeklyTargetHours = 6,
            attendanceRate = 92,
            createdAt = "2026-09-01T08:00:00Z"
        ),
        SubjectEntity(
            id = "subj-3",
            name = "Computer Systems & Architecture",
            code = "CS 240",
            color = "#E6A23C",
            description = "Assembly language, memory hierarchies, pipelining, and processor micro-architectures.",
            semester = "Fall 2026",
            instructor = "Prof. Marcus Chen",
            weeklyTargetHours = 5,
            attendanceRate = 88,
            createdAt = "2026-09-01T08:00:00Z"
        ),
        SubjectEntity(
            id = "subj-4",
            name = "Database Management Systems",
            code = "CS 220",
            color = "#8E67E8",
            description = "Relational algebra, SQL query optimization, concurrency control, and ACID transactions.",
            semester = "Fall 2026",
            instructor = "Dr. Sarah Jenkins",
            weeklyTargetHours = 4,
            attendanceRate = 95,
            createdAt = "2026-09-01T08:00:00Z"
        ),
        SubjectEntity(
            id = "subj-5",
            name = "Probability & Statistics for Engineers",
            code = "STAT 250",
            color = "#38BDF8",
            description = "Random variables, hypothesis testing, Bayesian inference, and stochastic processes.",
            semester = "Fall 2026",
            instructor = "Dr. Robert Vance",
            weeklyTargetHours = 4,
            attendanceRate = 90,
            createdAt = "2026-09-01T08:00:00Z"
        )
    )

    val initialSessions = listOf(
        StudySessionEntity(
            id = "sess-1",
            subjectId = "subj-1",
            durationMinutes = 60,
            sessionType = "pomodoro",
            notes = "Solved Dijkstra's algorithm problems with priority queue implementations.",
            focusScore = 92,
            isManual = false,
            startTime = "2026-11-25T09:00:00Z",
            endTime = "2026-11-25T10:00:00Z",
            createdAt = "2026-11-25T10:00:00Z"
        ),
        StudySessionEntity(
            id = "sess-2",
            subjectId = "subj-2",
            durationMinutes = 90,
            sessionType = "deep_work",
            notes = "Completed Gram-Schmidt orthonormalization homework set.",
            focusScore = 88,
            isManual = false,
            startTime = "2026-11-25T14:00:00Z",
            endTime = "2026-11-25T15:30:00Z",
            createdAt = "2026-11-25T15:30:00Z"
        ),
        StudySessionEntity(
            id = "sess-3",
            subjectId = "subj-3",
            durationMinutes = 50,
            sessionType = "revision",
            notes = "Reviewed RISC-V instruction encoding formats and pipeline hazard detection.",
            focusScore = 95,
            isManual = true,
            startTime = "2026-11-24T16:00:00Z",
            endTime = "2026-11-24T16:50:00Z",
            createdAt = "2026-11-24T16:50:00Z"
        ),
        StudySessionEntity(
            id = "sess-4",
            subjectId = "subj-4",
            durationMinutes = 75,
            sessionType = "assignment",
            notes = "Wrote SQL queries for B+ Tree index optimization benchmarks.",
            focusScore = 90,
            isManual = false,
            startTime = "2026-11-24T19:00:00Z",
            endTime = "2026-11-24T20:15:00Z",
            createdAt = "2026-11-24T20:15:00Z"
        )
    )

    val initialTasks = listOf(
        TaskEntity(
            id = "task-1",
            title = "Dynamic Programming Problem Set 4",
            description = "Implement memoized Longest Common Subsequence and Knapsack variants.",
            subjectId = "subj-1",
            category = "assignment",
            priority = "high",
            status = "in_progress",
            dueDate = "2026-11-29",
            dueTime = "23:59",
            estimatedMinutes = 180,
            createdAt = "2026-11-20T10:00:00Z"
        ),
        TaskEntity(
            id = "task-2",
            title = "Eigenvalue & Diagonalization Quiz Revision",
            description = "Review characteristic polynomials and geometric multiplicity theorems.",
            subjectId = "subj-2",
            category = "revision",
            priority = "high",
            status = "not_started",
            dueDate = "2026-11-30",
            dueTime = "14:00",
            estimatedMinutes = 120,
            createdAt = "2026-11-21T09:00:00Z"
        ),
        TaskEntity(
            id = "task-3",
            title = "RISC-V Cache Simulation Laboratory",
            description = "Write C simulator for 2-way set associative L1 cache with LRU replacement.",
            subjectId = "subj-3",
            category = "project",
            priority = "medium",
            status = "in_progress",
            dueDate = "2026-12-04",
            dueTime = "18:00",
            estimatedMinutes = 240,
            createdAt = "2026-11-22T11:00:00Z"
        ),
        TaskEntity(
            id = "task-4",
            title = "Database Normalization Exercises",
            description = "Convert unnormalized relations into 3NF and BCNF with functional dependencies.",
            subjectId = "subj-4",
            category = "homework",
            priority = "medium",
            status = "completed",
            dueDate = "2026-11-24",
            dueTime = "23:59",
            estimatedMinutes = 90,
            completedAt = "2026-11-24T20:15:00Z",
            createdAt = "2026-11-18T10:00:00Z"
        )
    )

    val initialExams = listOf(
        ExaminationEntity(
            id = "exam-1",
            title = "Algorithms Midterm Examination",
            subjectId = "subj-1",
            examDate = "2026-12-08",
            examTime = "09:30",
            location = "Computing Sciences Hall 302",
            examType = "midterm",
            preparationStatus = "in_progress",
            targetGrade = "A",
            notes = "Focus on amortized complexity analysis and bipartite matching proofs.",
            createdAt = "2026-10-15T08:00:00Z"
        ),
        ExaminationEntity(
            id = "exam-2",
            title = "Linear Algebra Examination",
            subjectId = "subj-2",
            examDate = "2026-12-12",
            examTime = "14:00",
            location = "Mathematics Auditorium B",
            examType = "midterm",
            preparationStatus = "not_prepared",
            targetGrade = "A+",
            notes = "Singular Value Decomposition and Jordan Canonical Forms.",
            createdAt = "2026-10-15T08:00:00Z"
        )
    )

    val initialGoals = listOf(
        GoalEntity(
            id = "goal-1",
            title = "Achieve 25 Study Hours This Week",
            type = "weekly_study",
            targetValue = 25.0,
            currentValue = 18.5,
            unit = "hours",
            targetDate = "2026-11-30",
            isCompleted = false,
            createdAt = "2026-11-24T00:00:00Z"
        ),
        GoalEntity(
            id = "goal-2",
            title = "Complete 8 Pomodoro Focus Blocks",
            type = "daily_study",
            targetValue = 8.0,
            currentValue = 5.0,
            unit = "sessions",
            targetDate = "2026-11-26",
            isCompleted = false,
            createdAt = "2026-11-26T00:00:00Z"
        )
    )

    val initialNotes = listOf(
        NoteEntity(
            id = "note-1",
            title = "Master Theorem for Recurrence Relations",
            content = "# Master Theorem\n\nGiven recurrence `T(n) = aT(n/b) + f(n)`:\n\n1. If `f(n) = O(n^(log_b(a) - ε))` then `T(n) = Θ(n^log_b(a))`.\n2. If `f(n) = Θ(n^log_b(a) * log^k(n))` then `T(n) = Θ(n^log_b(a) * log^(k+1)(n))`.\n3. If `f(n) = Ω(n^(log_b(a) + ε))` and regularity condition holds, `T(n) = Θ(f(n))`.",
            subjectId = "subj-1",
            tags = "Algorithms,Complexity,ExamPrep",
            isPinned = true,
            isFavourite = true,
            isArchived = false,
            createdAt = "2026-11-10T14:00:00Z",
            updatedAt = "2026-11-24T18:00:00Z"
        ),
        NoteEntity(
            id = "note-2",
            title = "Gram-Schmidt Orthonormalization Steps",
            content = "# Gram-Schmidt Process\n\nLet basis be `{v_1, v_2, ..., v_k}`.\n\n- `u_1 = v_1`\n- `u_2 = v_2 - proj_{u_1}(v_2)`\n- `u_3 = v_3 - proj_{u_1}(v_3) - proj_{u_2}(v_3)`\n\nNormalize: `e_i = u_i / ||u_i||`.",
            subjectId = "subj-2",
            tags = "Math,LinearAlgebra",
            isPinned = false,
            isFavourite = true,
            isArchived = false,
            createdAt = "2026-11-12T10:00:00Z",
            updatedAt = "2026-11-20T11:00:00Z"
        )
    )

    val initialEvents = listOf(
        ScheduleEventEntity(
            id = "event-1",
            title = "CS 201 Lecture: Network Flows",
            subjectId = "subj-1",
            date = "2026-11-26",
            startTime = "09:00",
            endTime = "10:30",
            description = "Ford-Fulkerson algorithm and max-flow min-cut theorem.",
            reminder = true,
            isCompleted = true,
            createdAt = "2026-11-20T08:00:00Z"
        ),
        ScheduleEventEntity(
            id = "event-2",
            title = "MATH 210 Seminar: SVD Applications",
            subjectId = "subj-2",
            date = "2026-11-26",
            startTime = "11:00",
            endTime = "12:30",
            description = "Image compression and principal component analysis.",
            reminder = true,
            isCompleted = false,
            createdAt = "2026-11-20T08:00:00Z"
        ),
        ScheduleEventEntity(
            id = "event-3",
            title = "CS 240 Lab: Pipeline Hazards",
            subjectId = "subj-3",
            date = "2026-11-26",
            startTime = "14:00",
            endTime = "16:00",
            description = "Data forwarding vs pipeline stalling benchmarks.",
            reminder = true,
            isCompleted = false,
            createdAt = "2026-11-20T08:00:00Z"
        )
    )

    val initialNotifications = listOf(
        NotificationEntity(
            id = "notif-1",
            title = "Upcoming Assignment Deadline",
            message = "Dynamic Programming Problem Set 4 is due in 3 days.",
            type = "deadline",
            isRead = false,
            timestamp = "2026-11-26T08:00:00Z",
            linkTo = "assignments"
        ),
        NotificationEntity(
            id = "notif-2",
            title = "Study Streak Maintained!",
            message = "Congratulations! You have maintained a 7-day academic study streak.",
            type = "streak",
            isRead = true,
            timestamp = "2026-11-25T21:00:00Z",
            linkTo = "dashboard"
        )
    )
}
