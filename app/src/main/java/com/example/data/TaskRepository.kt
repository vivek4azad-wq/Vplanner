package com.example.data

import kotlinx.coroutines.flow.Flow

class TaskRepository(private val taskDao: TaskDao) {

    val allTasks: Flow<List<GeoTask>> = taskDao.getAllTasks()
    val pendingTasks: Flow<List<GeoTask>> = taskDao.getPendingTasks()

    suspend fun getTaskById(id: Long): GeoTask? = taskDao.getTaskById(id)

    suspend fun getActiveGeofenceTasks(): List<GeoTask> = taskDao.getActiveGeofenceTasks()

    suspend fun insertTask(task: GeoTask): Long = taskDao.insertTask(task)

    suspend fun insertTasks(tasks: List<GeoTask>): List<Long> = taskDao.insertTasks(tasks)

    suspend fun updateTask(task: GeoTask) = taskDao.updateTask(task)

    suspend fun deleteTask(task: GeoTask) = taskDao.deleteTask(task)

    suspend fun deleteTaskById(id: Long) = taskDao.deleteTaskById(id)

    suspend fun getRecurringTasks(): List<GeoTask> = taskDao.getRecurringTasks()

    suspend fun toggleCompleted(id: Long, currentCompleted: Boolean): GeoTask? {
        val willBeCompleted = !currentCompleted
        taskDao.setCompleted(id, willBeCompleted)

        if (willBeCompleted) {
            val task = taskDao.getTaskById(id)
            if (task != null && task.isRecurring && task.recurrenceType != "NONE") {
                val nextTask = createNextRecurringInstance(task)
                val newId = taskDao.insertTask(nextTask)
                return nextTask.copy(id = newId)
            }
        }
        return null
    }

    fun calculateNextDueEpoch(baseEpoch: Long?, recurrenceType: String, interval: Int): Long {
        val base = if (baseEpoch != null && baseEpoch > 0) baseEpoch else System.currentTimeMillis()
        val step = if (interval > 0) interval else 1
        val oneDayMillis = 86_400_000L

        return when (recurrenceType.uppercase()) {
            "DAILY" -> base + (step * oneDayMillis)
            "WEEKLY" -> base + (step * 7 * oneDayMillis)
            "MONTHLY" -> {
                val cal = java.util.Calendar.getInstance().apply {
                    timeInMillis = base
                    add(java.util.Calendar.MONTH, step)
                }
                cal.timeInMillis
            }
            "CUSTOM" -> base + (step * oneDayMillis)
            else -> base + oneDayMillis
        }
    }

    fun createNextRecurringInstance(task: GeoTask): GeoTask {
        val nextDue = calculateNextDueEpoch(task.dueDateEpoch, task.recurrenceType, task.recurrenceInterval)
        return task.copy(
            id = 0,
            dueDateEpoch = nextDue,
            isCompleted = false,
            createdAt = System.currentTimeMillis(),
            lastTriggeredEpoch = 0L,
            googleCalendarEventId = null,
            isSyncedWithCalendar = false,
            recurrenceParentId = task.recurrenceParentId ?: task.id
        )
    }

    suspend fun markTriggered(id: Long) {
        taskDao.updateLastTriggered(id, System.currentTimeMillis())
    }

    suspend fun updateCalendarSync(id: Long, synced: Boolean, eventId: String?) {
        taskDao.updateCalendarSync(id, synced, eventId)
    }

    suspend fun deleteAll() = taskDao.deleteAllTasks()
}
