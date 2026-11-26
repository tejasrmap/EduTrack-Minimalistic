package com.gtinnox.edutrack.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "notes",
    indices = [Index(value = ["userId"])]
)
data class NoteEntity(
    @PrimaryKey
    val id: String,
    val userId: String = "default-user",
    val title: String,
    val content: String,
    val subjectId: String? = null,
    val tags: String = "", // Comma-separated tags
    val isPinned: Boolean = false,
    val isFavourite: Boolean = false,
    val isArchived: Boolean = false,
    val createdAt: String,
    val updatedAt: String
)
