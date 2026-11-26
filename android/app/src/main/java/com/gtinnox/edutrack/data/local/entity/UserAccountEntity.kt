package com.gtinnox.edutrack.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "user_accounts",
    indices = [Index(value = ["email"], unique = true)]
)
data class UserAccountEntity(
    @PrimaryKey
    val id: String,
    val fullName: String,
    val email: String,
    val passwordHash: String,
    val passwordSalt: String,
    val createdAt: String,
    val updatedAt: String,
    val isOnboardingCompleted: Boolean = false,
    val tutorialsCompleted: String = "" // Comma-separated keys: "dashboard,subjects,timer,planner,analytics,history"
)
