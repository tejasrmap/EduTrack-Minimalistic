package com.gtinnox.edutrack.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val message: String,
    val type: String, // 'deadline', 'exam', 'reminder', 'goal', 'streak'
    val isRead: Boolean = false,
    val timestamp: String,
    val linkTo: String? = null
)
