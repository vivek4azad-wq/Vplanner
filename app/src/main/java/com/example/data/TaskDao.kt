package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("SELECT * FROM geo_tasks ORDER BY isCompleted ASC, priority DESC, createdAt DESC")
    fun getAllTasks(): Flow<List<GeoTask>>

    @Query("SELECT * FROM geo_tasks WHERE isCompleted = 0 ORDER BY priority DESC, createdAt DESC")
    fun getPendingTasks(): Flow<List<GeoTask>>

    @Query("SELECT * FROM geo_tasks WHERE id = :id")
    suspend fun getTaskById(id: Long): GeoTask?

    @Query("SELECT * FROM geo_tasks WHERE isCompleted = 0 AND (latitude != 0.0 OR longitude != 0.0)")
    suspend fun getActiveGeofenceTasks(): List<GeoTask>

    @Query("SELECT * FROM geo_tasks WHERE isRecurring = 1")
    suspend fun getRecurringTasks(): List<GeoTask>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: GeoTask): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<GeoTask>): List<Long>

    @Update
    suspend fun updateTask(task: GeoTask)

    @Delete
    suspend fun deleteTask(task: GeoTask)

    @Query("DELETE FROM geo_tasks WHERE id = :id")
    suspend fun deleteTaskById(id: Long)

    @Query("UPDATE geo_tasks SET lastTriggeredEpoch = :timestamp WHERE id = :id")
    suspend fun updateLastTriggered(id: Long, timestamp: Long)

    @Query("UPDATE geo_tasks SET isCompleted = :isCompleted WHERE id = :id")
    suspend fun setCompleted(id: Long, isCompleted: Boolean)

    @Query("UPDATE geo_tasks SET isSyncedWithCalendar = :synced, googleCalendarEventId = :eventId WHERE id = :id")
    suspend fun updateCalendarSync(id: Long, synced: Boolean, eventId: String?)

    @Query("DELETE FROM geo_tasks")
    suspend fun deleteAllTasks()
}
