package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "geo_tasks")
data class GeoTask(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val locationName: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val radiusMeters: Float = 300f,
    val triggerCondition: String = "ON_ARRIVAL", // ON_ARRIVAL, NEARBY, ON_DEPARTURE
    val isVoicePushEnabled: Boolean = true,
    val priority: String = "MEDIUM", // HIGH, MEDIUM, LOW
    val category: String = "Inspection", // Inspection, Maintenance, Signal, Operations, Meeting, General
    val dueDateEpoch: Long? = null,
    val isCompleted: Boolean = false,
    val googleCalendarEventId: String? = null,
    val isSyncedWithCalendar: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val lastTriggeredEpoch: Long = 0L,
    val source: String = "LOCAL", // LOCAL, CALENDAR, SHEETS_IMPORT, ROSTER_DUTY
    val isRecurring: Boolean = false,
    val recurrenceType: String = "NONE", // NONE, DAILY, WEEKLY, MONTHLY, CUSTOM
    val recurrenceInterval: Int = 1, // Repeat step (e.g. 1 for daily/weekly/monthly or N days for custom)
    val recurrenceParentId: Long? = null
)
