package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.GeoTaskApplication
import com.example.calendar.CalendarEventItem
import com.example.calendar.CalendarSyncManager
import com.example.data.GeoTask
import com.example.location.GpsLocation
import com.example.location.StationPreset
import com.example.sheets.SheetsImportManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class TaskFilter {
    ALL,
    NEARBY,
    PENDING,
    RECURRING,
    COMPLETED,
    HIGH_PRIORITY
}

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as GeoTaskApplication
    private val repository = app.repository
    val locationTracker = app.locationTracker
    val voiceManager = app.voiceNotificationManager
    val calendarManager = CalendarSyncManager(application)
    val sheetsManager = SheetsImportManager()
    val userProfileManager = com.example.data.UserProfileManager(application)
    val rosterManager = com.example.data.DutyRosterManager()

    val userProfile: StateFlow<com.example.data.UserProfile> = userProfileManager.profile
    val userPreferences: StateFlow<com.example.data.UserPreferences> = userProfileManager.preferences
    val calendarSettings: StateFlow<com.example.data.CalendarAccountSettings> = userProfileManager.calendarSettings

    val currentLocation: StateFlow<GpsLocation?> = locationTracker.currentLocation
    val isTrackingRealGps: StateFlow<Boolean> = locationTracker.isTrackingRealGps
    val isSpeaking: StateFlow<Boolean> = voiceManager.isSpeaking

    private val _locationPermissionStatus = MutableStateFlow(
        if (locationTracker.hasFineLocationPermission()) {
            com.example.ui.components.LocationPermissionStatus.PRECISE_GRANTED
        } else if (locationTracker.hasCoarseLocationPermission()) {
            com.example.ui.components.LocationPermissionStatus.COARSE_ONLY
        } else {
            com.example.ui.components.LocationPermissionStatus.NOT_REQUESTED
        }
    )
    val locationPermissionStatus: StateFlow<com.example.ui.components.LocationPermissionStatus> =
        _locationPermissionStatus.asStateFlow()

    private val _showPermissionRationaleDialog = MutableStateFlow(false)
    val showPermissionRationaleDialog: StateFlow<Boolean> = _showPermissionRationaleDialog.asStateFlow()

    private val _filter = MutableStateFlow(TaskFilter.ALL)
    val filter: StateFlow<TaskFilter> = _filter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _calendarEvents = MutableStateFlow<List<CalendarEventItem>>(emptyList())
    val calendarEvents: StateFlow<List<CalendarEventItem>> = _calendarEvents.asStateFlow()

    private val _isCalendarLoading = MutableStateFlow(false)
    val isCalendarLoading: StateFlow<Boolean> = _isCalendarLoading.asStateFlow()

    private val _isSheetsImporting = MutableStateFlow(false)
    val isSheetsImporting: StateFlow<Boolean> = _isSheetsImporting.asStateFlow()

    private val _snackbarMessage = MutableSharedFlow<String>()
    val snackbarMessage: SharedFlow<String> = _snackbarMessage.asSharedFlow()

    val allTasks: StateFlow<List<GeoTask>> = repository.allTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Combined filtered task list
    val filteredTasks: StateFlow<List<GeoTask>> = combine(
        allTasks,
        _filter,
        _searchQuery,
        currentLocation
    ) { tasks, filter, query, location ->
        tasks.filter { task ->
            val matchesQuery = query.isBlank() ||
                    task.title.contains(query, ignoreCase = true) ||
                    task.description.contains(query, ignoreCase = true) ||
                    task.locationName.contains(query, ignoreCase = true) ||
                    task.category.contains(query, ignoreCase = true)

            if (!matchesQuery) return@filter false

            when (filter) {
                TaskFilter.ALL -> true
                TaskFilter.PENDING -> !task.isCompleted
                TaskFilter.RECURRING -> task.isRecurring
                TaskFilter.COMPLETED -> task.isCompleted
                TaskFilter.HIGH_PRIORITY -> task.priority == "HIGH" && !task.isCompleted
                TaskFilter.NEARBY -> {
                    if (task.isCompleted || location == null) false
                    else {
                        val dist = locationTracker.calculateDistanceMeters(
                            location.latitude,
                            location.longitude,
                            task.latitude,
                            task.longitude
                        )
                        dist <= (task.radiusMeters * 1.5f) // Nearby window
                    }
                }
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Wire proximity geofence alerts
        locationTracker.onGeofenceTriggered = { task, distance ->
            viewModelScope.launch {
                voiceManager.triggerTaskProximityAlert(task, distance)
                repository.markTriggered(task.id)
                _snackbarMessage.emit("📍 Triggered: ${task.title} (${distance.toInt()}m)")
            }
        }

        // Periodically monitor location against tasks
        viewModelScope.launch {
            combine(allTasks, currentLocation) { tasks, _ -> tasks }.collect { tasks ->
                locationTracker.evaluateTasksForProximity(tasks)
            }
        }

        // Initial check: if database is completely empty, populate with default DFCC railway tasks
        viewModelScope.launch {
            val initialTasks = repository.getActiveGeofenceTasks()
            if (initialTasks.isEmpty()) {
                val defaults = sheetsManager.getDefaultDfccTasks()
                repository.insertTasks(defaults)
            }
        }
    }

    fun setFilter(newFilter: TaskFilter) {
        _filter.value = newFilter
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun addTask(task: GeoTask) {
        viewModelScope.launch {
            repository.insertTask(task)
            _snackbarMessage.emit("Task added: ${task.title}")
        }
    }

    fun updateTask(task: GeoTask) {
        viewModelScope.launch {
            repository.updateTask(task)
            _snackbarMessage.emit("Task updated")
        }
    }

    fun deleteTask(task: GeoTask) {
        viewModelScope.launch {
            repository.deleteTask(task)
            _snackbarMessage.emit("Task removed")
        }
    }

    fun toggleComplete(task: GeoTask) {
        viewModelScope.launch {
            val nextInstance = repository.toggleCompleted(task.id, task.isCompleted)
            if (!task.isCompleted) {
                if (nextInstance != null) {
                    _snackbarMessage.emit("✅ ${task.title}: Completed! Next instance scheduled (${nextInstance.recurrenceType})")
                } else {
                    _snackbarMessage.emit("✅ ${task.title}: Completed")
                }
            } else {
                _snackbarMessage.emit("↩️ ${task.title}: Marked pending")
            }
        }
    }

    fun saveUserProfile(profile: com.example.data.UserProfile) {
        userProfileManager.updateProfile(profile)
        viewModelScope.launch {
            _snackbarMessage.emit("Profile details updated for ${profile.name}")
        }
    }

    fun saveUserPreferences(preferences: com.example.data.UserPreferences) {
        userProfileManager.updatePreferences(preferences)
        viewModelScope.launch {
            _snackbarMessage.emit("Preferences updated")
        }
    }

    fun saveCalendarSettings(settings: com.example.data.CalendarAccountSettings) {
        userProfileManager.updateCalendarSettings(settings)
    }

    fun disconnectGoogleCalendar() {
        userProfileManager.disconnectCalendar()
        viewModelScope.launch {
            _snackbarMessage.emit("Google Calendar disconnected")
        }
    }

    fun reconnectGoogleCalendar(email: String = "vivek4azad@gmail.com") {
        userProfileManager.reconnectCalendar(email)
        viewModelScope.launch {
            _snackbarMessage.emit("Reconnected to $email")
        }
    }

    fun syncAllTasksToCalendar() {
        viewModelScope.launch {
            val tasks = allTasks.value
            var count = 0
            tasks.forEach { task ->
                val result = calendarManager.exportTaskToCalendar(task)
                if (result.isSuccess) {
                    repository.updateCalendarSync(task.id, true, result.getOrNull()?.toString())
                    count++
                }
            }
            userProfileManager.markCalendarSynced()
            _snackbarMessage.emit("📅 Synced $count tasks with Google Calendar!")
        }
    }

    fun importRosterDutyAsTask(duty: com.example.data.CivilDuty, recurrenceType: String = "NONE") {
        viewModelScope.launch {
            val task = rosterManager.dutyToGeoTask(duty, recurrenceType)
            repository.insertTask(task)
            _snackbarMessage.emit("Imported [${duty.stationCode}] ${duty.activity}")
        }
    }

    fun importAllRosterDuties(): Int {
        val duties = rosterManager.allDuties
        viewModelScope.launch {
            val tasks = duties.map { rosterManager.dutyToGeoTask(it) }
            repository.insertTasks(tasks)
        }
        return duties.size
    }

    fun speakTask(task: GeoTask) {
        val dist = locationTracker.getDistanceTo(task)
        val distText = if (dist != null) "Distance: ${dist.toInt()} meters away." else ""
        val text = "Task: ${task.title}. Location: ${task.locationName}. Priority: ${task.priority}. $distText ${task.description}"
        voiceManager.speak(text, "task_${task.id}")
    }

    fun stopSpeaking() {
        voiceManager.stopSpeaking()
    }

    fun simulateLocation(preset: StationPreset) {
        locationTracker.setSimulatedLocation(
            latitude = preset.latitude,
            longitude = preset.longitude,
            label = "${preset.name} (${preset.code})"
        )
        viewModelScope.launch {
            _snackbarMessage.emit("📍 Simulated GPS moved to ${preset.name}")
        }
    }

    fun setCustomCoordinates(lat: Double, lng: Double, label: String = "Custom Waypoint") {
        locationTracker.setSimulatedLocation(lat, lng, label)
        viewModelScope.launch {
            _snackbarMessage.emit("📍 GPS moved to $label")
        }
    }

    fun triggerProximityNow(task: GeoTask) {
        locationTracker.forceTriggerTask(task)
    }

    fun openRationaleDialog() {
        _showPermissionRationaleDialog.value = true
    }

    fun dismissRationaleDialog() {
        _showPermissionRationaleDialog.value = false
    }

    fun updateLocationPermissionResult(
        fineGranted: Boolean,
        coarseGranted: Boolean,
        shouldShowRationale: Boolean
    ) {
        val newStatus = when {
            fineGranted -> com.example.ui.components.LocationPermissionStatus.PRECISE_GRANTED
            coarseGranted -> com.example.ui.components.LocationPermissionStatus.COARSE_ONLY
            shouldShowRationale -> com.example.ui.components.LocationPermissionStatus.DENIED
            else -> com.example.ui.components.LocationPermissionStatus.PERMANENTLY_DENIED
        }
        _locationPermissionStatus.value = newStatus

        viewModelScope.launch {
            when (newStatus) {
                com.example.ui.components.LocationPermissionStatus.PRECISE_GRANTED -> {
                    val started = locationTracker.startRealGpsTracking()
                    if (started) {
                        _snackbarMessage.emit("🎯 Precise GPS tracking active (High Accuracy)")
                    }
                }
                com.example.ui.components.LocationPermissionStatus.COARSE_ONLY -> {
                    locationTracker.startRealGpsTracking()
                    _snackbarMessage.emit("⚠️ Approximate location granted. Precise Location recommended for track asset alerts.")
                }
                com.example.ui.components.LocationPermissionStatus.DENIED -> {
                    locationTracker.stopRealGpsTracking()
                    _snackbarMessage.emit("❌ Location permission denied. GPS task alerts require location access.")
                }
                com.example.ui.components.LocationPermissionStatus.PERMANENTLY_DENIED -> {
                    locationTracker.stopRealGpsTracking()
                    _snackbarMessage.emit("⚙️ Location permission blocked. Please enable Precise Location in App Settings.")
                }
                else -> Unit
            }
        }
    }

    fun syncPermissionStatusWithSystem() {
        val hasFine = locationTracker.hasFineLocationPermission()
        val hasCoarse = locationTracker.hasCoarseLocationPermission()
        if (hasFine) {
            _locationPermissionStatus.value = com.example.ui.components.LocationPermissionStatus.PRECISE_GRANTED
        } else if (hasCoarse) {
            _locationPermissionStatus.value = com.example.ui.components.LocationPermissionStatus.COARSE_ONLY
        }
    }

    fun startGpsTracking(onPermissionRequired: () -> Unit = {}) {
        if (!locationTracker.hasLocationPermission()) {
            onPermissionRequired()
            viewModelScope.launch {
                _snackbarMessage.emit("📍 Location permission required for live GPS tracking")
            }
            return
        }

        val started = locationTracker.startRealGpsTracking()
        viewModelScope.launch {
            if (started) {
                val isFine = locationTracker.hasFineLocationPermission()
                _snackbarMessage.emit(
                    if (isFine) "🎯 Live GPS Tracking Active (Precise Fix)"
                    else "⚠️ Live GPS Tracking Active (Approximate Fix)"
                )
            } else {
                _snackbarMessage.emit("Could not start GPS tracking. Check device location services.")
            }
        }
    }

    fun stopGpsTracking() {
        locationTracker.stopRealGpsTracking()
        viewModelScope.launch {
            _snackbarMessage.emit("⏸️ Real GPS paused. Switched to section reference.")
        }
    }

    fun syncTaskToCalendar(task: GeoTask) {
        viewModelScope.launch {
            val result = calendarManager.exportTaskToCalendar(task)
            if (result.isSuccess) {
                val eventId = result.getOrNull()
                repository.updateCalendarSync(task.id, true, eventId?.toString())
                _snackbarMessage.emit("📅 Synced '${task.title}' with Google Calendar!")
            } else {
                _snackbarMessage.emit("Calendar export error: ${result.exceptionOrNull()?.message ?: "Check permissions"}")
            }
        }
    }

    fun loadCalendarEvents() {
        viewModelScope.launch {
            _isCalendarLoading.value = true
            try {
                val events = calendarManager.fetchUpcomingCalendarEvents()
                _calendarEvents.value = events
                if (events.isEmpty()) {
                    _snackbarMessage.emit("No upcoming calendar events found or permission pending")
                } else {
                    _snackbarMessage.emit("Fetched ${events.size} Google Calendar events")
                }
            } catch (e: Exception) {
                _snackbarMessage.emit("Calendar load error: ${e.message}")
            } finally {
                _isCalendarLoading.value = false
            }
        }
    }

    fun importCalendarEventAsTask(event: CalendarEventItem, lat: Double = 28.5672, lng: Double = 77.5540) {
        viewModelScope.launch {
            val newTask = GeoTask(
                title = event.title,
                description = event.description,
                locationName = if (event.location.isNotBlank()) event.location else "Calendar Event Location",
                latitude = lat,
                longitude = lng,
                dueDateEpoch = event.startEpoch,
                isSyncedWithCalendar = true,
                googleCalendarEventId = event.id.toString(),
                source = "CALENDAR"
            )
            repository.insertTask(newTask)
            _snackbarMessage.emit("Imported '${event.title}' as GeoTask")
        }
    }

    fun importFromGoogleSheet(sheetUrlOrId: String = SheetsImportManager.DEFAULT_SHEET_URL) {
        viewModelScope.launch {
            _isSheetsImporting.value = true
            try {
                val result = sheetsManager.fetchTasksFromGoogleSheet(sheetUrlOrId)
                if (result.isSuccess) {
                    val imported = result.getOrDefault(emptyList())
                    repository.insertTasks(imported)
                    _snackbarMessage.emit("✅ Successfully imported ${imported.size} tasks from Google Sheet!")
                } else {
                    _snackbarMessage.emit("Sheet import notice: Used standard DFCC railway data.")
                }
            } catch (e: Exception) {
                _snackbarMessage.emit("Sheet import note: ${e.message}")
            } finally {
                _isSheetsImporting.value = false
            }
        }
    }

    fun loadDefaultDfccSuite() {
        viewModelScope.launch {
            val defaults = sheetsManager.getDefaultDfccTasks()
            repository.insertTasks(defaults)
            _snackbarMessage.emit("Loaded DFCC Western & Eastern Corridor tasks")
        }
    }

    fun clearAllTasks() {
        viewModelScope.launch {
            repository.deleteAll()
            _snackbarMessage.emit("All tasks cleared")
        }
    }
}
