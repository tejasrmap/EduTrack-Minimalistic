package com.gtinnox.edutrack.data.local.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.gtinnox.edutrack.data.local.entity.StudySessionEntity
import com.gtinnox.edutrack.data.local.entity.SubjectEntity
import com.gtinnox.edutrack.data.local.entity.TaskEntity

data class SubjectWithSessions(
    @Embedded val subject: SubjectEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "subjectId"
    )
    val sessions: List<StudySessionEntity> = emptyList(),
    @Relation(
        parentColumn = "id",
        entityColumn = "subjectId"
    )
    val tasks: List<TaskEntity> = emptyList()
)

data class SessionWithSubject(
    @Embedded val session: StudySessionEntity,
    @Relation(
        parentColumn = "subjectId",
        entityColumn = "id"
    )
    val subject: SubjectEntity? = null
)
