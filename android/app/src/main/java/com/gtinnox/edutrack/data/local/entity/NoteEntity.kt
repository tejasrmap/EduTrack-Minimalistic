package com.gtinnox.edutrack.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey
    val id: String,
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
