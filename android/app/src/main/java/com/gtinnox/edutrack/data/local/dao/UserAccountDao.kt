package com.gtinnox.edutrack.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.gtinnox.edutrack.data.local.entity.UserAccountEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserAccountDao {
    @Query("SELECT * FROM user_accounts WHERE id = :userId LIMIT 1")
    fun getUserById(userId: String): Flow<UserAccountEntity?>

    @Query("SELECT * FROM user_accounts WHERE id = :userId LIMIT 1")
    suspend fun getUserByIdDirect(userId: String): UserAccountEntity?

    @Query("SELECT * FROM user_accounts WHERE LOWER(email) = LOWER(:email) LIMIT 1")
    suspend fun getUserByEmail(email: String): UserAccountEntity?

    @Query("SELECT COUNT(*) FROM user_accounts")
    suspend fun getUserCount(): Int

    @Query("SELECT * FROM user_accounts ORDER BY createdAt DESC")
    fun getAllUsers(): Flow<List<UserAccountEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: UserAccountEntity)

    @Update
    suspend fun updateUser(user: UserAccountEntity)

    @Query("UPDATE user_accounts SET isOnboardingCompleted = :isCompleted WHERE id = :userId")
    suspend fun setOnboardingCompleted(userId: String, isCompleted: Boolean)

    @Query("UPDATE user_accounts SET tutorialsCompleted = :tutorials WHERE id = :userId")
    suspend fun updateTutorialsCompleted(userId: String, tutorials: String)

    @Query("DELETE FROM user_accounts WHERE id = :userId")
    suspend fun deleteUser(userId: String)
}
