package com.gtinnox.edutrack.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "subjects",
    indices = [Index(value = ["userId"])]
)
data class SubjectEntity(
    @PrimaryKey
    val id: String,
    val userId: String = "default-user",
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
