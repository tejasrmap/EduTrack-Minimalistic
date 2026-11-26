package com.gtinnox.edutrack.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subjects")
data class SubjectEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val code: String,
    val color: String,
    val description: String,
    val semester: String,
    val instructor: String,
    val weeklyTargetHours: Int,
    val attendanceRate: Int = 95,
    val createdAt: String
)
