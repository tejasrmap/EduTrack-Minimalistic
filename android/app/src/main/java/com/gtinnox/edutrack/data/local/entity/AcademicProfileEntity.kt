package com.gtinnox.edutrack.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "academic_profiles",
    foreignKeys = [
        ForeignKey(
            entity = UserAccountEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class AcademicProfileEntity(
    @PrimaryKey
    val userId: String,
    val institution: String = "",
    val degree: String = "",
    val major: String = "",
    val academicYear: String = "",
    val semester: String = "",
    val studentId: String? = null,
    val profilePhotoUri: String? = null,
    val updatedAt: String
)
