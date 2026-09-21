package com.example.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class UserProfile(
    val name: String = "Vivek Azad",
    val email: String = "vivek4azad@gmail.com",
    val designation: String = "APM / Civil (Eastern DFC)",
    val employeeId: String = "DFCC-CIVIL-1167",
    val section: String = "SMUN to SNL (Km 1167.210 - 1249.720)",
    val headquarter: String = "SMUN (Shambhu New)"
)

data class UserPreferences(
    val defaultVoiceEnabled: Boolean = true,
    val defaultRadiusMeters: Float = 300f,
    val speechRate: Float = 0.95f,
    val speechPitch: Float = 1.0f,
    val vibrationEnabled: Boolean = true,
    val autoGenerateNextRecurring: Boolean = true,
    val defaultPriority: String = "HIGH",
    val defaultCategory: String = "Inspection"
)

data class CalendarAccountSettings(
    val connectedEmail: String = "vivek4azad@gmail.com",
    val isConnected: Boolean = true,
    val isAutoSyncEnabled: Boolean = true,
    val syncReminderMinutes: Int = 15,
    val lastSyncEpoch: Long = System.currentTimeMillis()
)

class UserProfileManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("geotask_user_profile_prefs", Context.MODE_PRIVATE)

    private val _profile = MutableStateFlow(loadProfile())
    val profile: StateFlow<UserProfile> = _profile.asStateFlow()

    private val _preferences = MutableStateFlow(loadPreferences())
    val preferences: StateFlow<UserPreferences> = _preferences.asStateFlow()

    private val _calendarSettings = MutableStateFlow(loadCalendarSettings())
    val calendarSettings: StateFlow<CalendarAccountSettings> = _calendarSettings.asStateFlow()

    private fun loadProfile(): UserProfile {
        return UserProfile(
            name = prefs.getString("prof_name", "Vivek Azad") ?: "Vivek Azad",
            email = prefs.getString("prof_email", "vivek4azad@gmail.com") ?: "vivek4azad@gmail.com",
            designation = prefs.getString("prof_desig", "APM / Civil (Eastern DFC)") ?: "APM / Civil (Eastern DFC)",
            employeeId = prefs.getString("prof_emp_id", "DFCC-CIVIL-1167") ?: "DFCC-CIVIL-1167",
            section = prefs.getString("prof_section", "SMUN to SNL (Km 1167.210 - 1249.720)") ?: "SMUN to SNL (Km 1167.210 - 1249.720)",
            headquarter = prefs.getString("prof_hq", "SMUN (Shambhu New)") ?: "SMUN (Shambhu New)"
        )
    }

    private fun loadPreferences(): UserPreferences {
        return UserPreferences(
            defaultVoiceEnabled = prefs.getBoolean("pref_voice", true),
            defaultRadiusMeters = prefs.getFloat("pref_radius", 300f),
            speechRate = prefs.getFloat("pref_speech_rate", 0.95f),
            speechPitch = prefs.getFloat("pref_speech_pitch", 1.0f),
            vibrationEnabled = prefs.getBoolean("pref_vibration", true),
            autoGenerateNextRecurring = prefs.getBoolean("pref_auto_recur", true),
            defaultPriority = prefs.getString("pref_priority", "HIGH") ?: "HIGH",
            defaultCategory = prefs.getString("pref_category", "Inspection") ?: "Inspection"
        )
    }

    private fun loadCalendarSettings(): CalendarAccountSettings {
        return CalendarAccountSettings(
            connectedEmail = prefs.getString("cal_email", "vivek4azad@gmail.com") ?: "vivek4azad@gmail.com",
            isConnected = prefs.getBoolean("cal_connected", true),
            isAutoSyncEnabled = prefs.getBoolean("cal_auto_sync", true),
            syncReminderMinutes = prefs.getInt("cal_reminder_mins", 15),
            lastSyncEpoch = prefs.getLong("cal_last_sync", System.currentTimeMillis())
        )
    }

    fun updateProfile(newProfile: UserProfile) {
        prefs.edit()
            .putString("prof_name", newProfile.name)
            .putString("prof_email", newProfile.email)
            .putString("prof_desig", newProfile.designation)
            .putString("prof_emp_id", newProfile.employeeId)
            .putString("prof_section", newProfile.section)
            .putString("prof_hq", newProfile.headquarter)
            .apply()
        _profile.value = newProfile
    }

    fun updatePreferences(newPreferences: UserPreferences) {
        prefs.edit()
            .putBoolean("pref_voice", newPreferences.defaultVoiceEnabled)
            .putFloat("pref_radius", newPreferences.defaultRadiusMeters)
            .putFloat("pref_speech_rate", newPreferences.speechRate)
            .putFloat("pref_speech_pitch", newPreferences.speechPitch)
            .putBoolean("pref_vibration", newPreferences.vibrationEnabled)
            .putBoolean("pref_auto_recur", newPreferences.autoGenerateNextRecurring)
            .putString("pref_priority", newPreferences.defaultPriority)
            .putString("pref_category", newPreferences.defaultCategory)
            .apply()
        _preferences.value = newPreferences
    }

    fun updateCalendarSettings(newSettings: CalendarAccountSettings) {
        prefs.edit()
            .putString("cal_email", newSettings.connectedEmail)
            .putBoolean("cal_connected", newSettings.isConnected)
            .putBoolean("cal_auto_sync", newSettings.isAutoSyncEnabled)
            .putInt("cal_reminder_mins", newSettings.syncReminderMinutes)
            .putLong("cal_last_sync", newSettings.lastSyncEpoch)
            .apply()
        _calendarSettings.value = newSettings
    }

    fun markCalendarSynced() {
        val updated = _calendarSettings.value.copy(lastSyncEpoch = System.currentTimeMillis())
        updateCalendarSettings(updated)
    }

    fun disconnectCalendar() {
        val updated = _calendarSettings.value.copy(isConnected = false)
        updateCalendarSettings(updated)
    }

    fun reconnectCalendar(email: String = "vivek4azad@gmail.com") {
        val updated = _calendarSettings.value.copy(isConnected = true, connectedEmail = email)
        updateCalendarSettings(updated)
    }
}
