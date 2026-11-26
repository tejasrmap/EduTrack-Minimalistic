package com.gtinnox.edutrack.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "schedule_events",
    foreignKeys = [
        ForeignKey(
            entity = SubjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["subjectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["subjectId"])]
)
data class ScheduleEventEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val subjectId: String,
    val date: String, // YYYY-MM-DD
    val startTime: String, // HH:mm
    val endTime: String, // HH:mm
    val description: String = "",
    val reminder: Boolean = true,
    val isCompleted: Boolean = false,
    val createdAt: String
)
