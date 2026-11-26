package com.gtinnox.edutrack.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.gtinnox.edutrack.data.local.entity.ScheduleEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduleEventDao {
    @Query("SELECT * FROM schedule_events ORDER BY date ASC, startTime ASC")
    fun getAllEvents(): Flow<List<ScheduleEventEntity>>

    @Query("SELECT * FROM schedule_events WHERE date = :date ORDER BY startTime ASC")
    fun getEventsForDate(date: String): Flow<List<ScheduleEventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: ScheduleEventEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvents(events: List<ScheduleEventEntity>)

    @Update
    suspend fun updateEvent(event: ScheduleEventEntity)

    @Delete
    suspend fun deleteEvent(event: ScheduleEventEntity)

    @Query("DELETE FROM schedule_events WHERE id = :id")
    suspend fun deleteEventById(id: String)
}
