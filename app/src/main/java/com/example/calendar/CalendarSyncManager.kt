package com.example.calendar

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.CalendarContract
import com.example.data.GeoTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.TimeZone

data class CalendarEventItem(
    val id: Long,
    val title: String,
    val description: String,
    val location: String,
    val startEpoch: Long,
    val endEpoch: Long
)

class CalendarSyncManager(private val context: Context) {

    @SuppressLint("MissingPermission")
    suspend fun getPrimaryCalendarId(): Long? = withContext(Dispatchers.IO) {
        val projection = arrayOf(
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.ACCOUNT_NAME,
            CalendarContract.Calendars.IS_PRIMARY,
            CalendarContract.Calendars.VISIBLE
        )
        val uri: Uri = CalendarContract.Calendars.CONTENT_URI
        var cursor: Cursor? = null
        try {
            cursor = context.contentResolver.query(uri, projection, null, null, null)
            if (cursor != null && cursor.moveToFirst()) {
                val idCol = cursor.getColumnIndexOrThrow(CalendarContract.Calendars._ID)
                return@withContext cursor.getLong(idCol)
            }
        } catch (_: Exception) {
        } finally {
            cursor?.close()
        }
        return@withContext null
    }

    suspend fun exportTaskToCalendar(task: GeoTask): Result<Long> = withContext(Dispatchers.IO) {
        try {
            val calId = getPrimaryCalendarId() ?: 1L
            val startMillis = task.dueDateEpoch ?: System.currentTimeMillis()
            val endMillis = startMillis + (60 * 60 * 1000) // 1 hour duration

            val values = ContentValues().apply {
                put(CalendarContract.Events.DTSTART, startMillis)
                put(CalendarContract.Events.DTEND, endMillis)
                put(CalendarContract.Events.TITLE, "[DFCC Task] ${task.title}")
                put(
                    CalendarContract.Events.DESCRIPTION,
                    "${task.description}\n\nCategory: ${task.category}\nPriority: ${task.priority}\nGPS: ${task.latitude}, ${task.longitude} (Radius: ${task.radiusMeters}m)"
                )
                put(CalendarContract.Events.EVENT_LOCATION, "${task.locationName} (${task.latitude}, ${task.longitude})")
                put(CalendarContract.Events.CALENDAR_ID, calId)
                put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
            }

            val uri: Uri? = context.contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
            val eventId = uri?.lastPathSegment?.toLongOrNull()
            if (eventId != null) {
                Result.success(eventId)
            } else {
                Result.failure(Exception("Could not create calendar event"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    @SuppressLint("MissingPermission")
    suspend fun fetchUpcomingCalendarEvents(limit: Int = 20): List<CalendarEventItem> = withContext(Dispatchers.IO) {
        val events = mutableListOf<CalendarEventItem>()
        val projection = arrayOf(
            CalendarContract.Events._ID,
            CalendarContract.Events.TITLE,
            CalendarContract.Events.DESCRIPTION,
            CalendarContract.Events.EVENT_LOCATION,
            CalendarContract.Events.DTSTART,
            CalendarContract.Events.DTEND
        )
        val now = System.currentTimeMillis()
        val selection = "(${CalendarContract.Events.DTSTART} >= ?)"
        val selectionArgs = arrayOf(now.toString())
        val sortOrder = "${CalendarContract.Events.DTSTART} ASC"

        var cursor: Cursor? = null
        try {
            cursor = context.contentResolver.query(
                CalendarContract.Events.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder
            )
            if (cursor != null) {
                val idIndex = cursor.getColumnIndex(CalendarContract.Events._ID)
                val titleIndex = cursor.getColumnIndex(CalendarContract.Events.TITLE)
                val descIndex = cursor.getColumnIndex(CalendarContract.Events.DESCRIPTION)
                val locIndex = cursor.getColumnIndex(CalendarContract.Events.EVENT_LOCATION)
                val startIndex = cursor.getColumnIndex(CalendarContract.Events.DTSTART)
                val endIndex = cursor.getColumnIndex(CalendarContract.Events.DTEND)

                while (cursor.moveToNext() && events.size < limit) {
                    val id = if (idIndex != -1) cursor.getLong(idIndex) else 0L
                    val title = if (titleIndex != -1) cursor.getString(titleIndex) ?: "Untitled Event" else "Untitled Event"
                    val desc = if (descIndex != -1) cursor.getString(descIndex) ?: "" else ""
                    val loc = if (locIndex != -1) cursor.getString(locIndex) ?: "" else ""
                    val start = if (startIndex != -1) cursor.getLong(startIndex) else now
                    val end = if (endIndex != -1) cursor.getLong(endIndex) else start

                    events.add(CalendarEventItem(id, title, desc, loc, start, end))
                }
            }
        } catch (_: Exception) {
        } finally {
            cursor?.close()
        }
        events
    }
}
