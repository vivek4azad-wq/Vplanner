package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CalendarAccountSettings
import com.example.data.UserProfile
import com.example.data.UserPreferences
import com.example.ui.TaskViewModel
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.RoseCritical
import com.example.ui.theme.SkyBlue40
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    viewModel: TaskViewModel,
    modifier: Modifier = Modifier
) {
    val profile by viewModel.userProfile.collectAsState()
    val preferences by viewModel.userPreferences.collectAsState()
    val calendarSettings by viewModel.calendarSettings.collectAsState()

    var isEditingProfile by remember { mutableStateOf(false) }
    var editName by remember(profile) { mutableStateOf(profile.name) }
    var editEmail by remember(profile) { mutableStateOf(profile.email) }
    var editDesignation by remember(profile) { mutableStateOf(profile.designation) }
    var editSection by remember(profile) { mutableStateOf(profile.section) }
    var editHq by remember(profile) { mutableStateOf(profile.headquarter) }

    var saveFeedbackMessage by remember { mutableStateOf<String?>(null) }

    // Preference local state
    var prefVoice by remember(preferences) { mutableStateOf(preferences.defaultVoiceEnabled) }
    var prefRadius by remember(preferences) { mutableFloatStateOf(preferences.defaultRadiusMeters) }
    var prefSpeechRate by remember(preferences) { mutableFloatStateOf(preferences.speechRate) }
    var prefVibration by remember(preferences) { mutableStateOf(preferences.vibrationEnabled) }
    var prefAutoRecur by remember(preferences) { mutableStateOf(preferences.autoGenerateNextRecurring) }

    // Calendar local state
    var calAutoSync by remember(calendarSettings) { mutableStateOf(calendarSettings.isAutoSyncEnabled) }

    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("user_profile_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Profile Header
        item {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("profile_hero_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = profile.name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("").ifEmpty { "DF" },
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = profile.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    Text(
                        text = profile.designation,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = profile.section,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    if (saveFeedbackMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = saveFeedbackMessage ?: "",
                            fontSize = 12.sp,
                            color = EmeraldSuccess,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Section 1: Account Details
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("account_details_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Account Details",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        IconButton(
                            onClick = { isEditingProfile = !isEditingProfile },
                            modifier = Modifier.testTag("edit_profile_toggle_btn")
                        ) {
                            Icon(
                                imageVector = if (isEditingProfile) Icons.Default.Done else Icons.Default.Edit,
                                contentDescription = "Edit Profile"
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (isEditingProfile) {
                        OutlinedTextField(
                            value = editName,
                            onValueChange = { editName = it },
                            label = { Text("Full Name") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("profile_name_input"),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = editEmail,
                            onValueChange = { editEmail = it },
                            label = { Text("Email Address") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("profile_email_input"),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = editDesignation,
                            onValueChange = { editDesignation = it },
                            label = { Text("Designation / Role") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("profile_desig_input"),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = editSection,
                            onValueChange = { editSection = it },
                            label = { Text("Assigned Railway Section") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = editHq,
                            onValueChange = { editHq = it },
                            label = { Text("Headquarter / IMSD") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                viewModel.saveUserProfile(
                                    UserProfile(
                                        name = editName.trim(),
                                        email = editEmail.trim(),
                                        designation = editDesignation.trim(),
                                        employeeId = profile.employeeId,
                                        section = editSection.trim(),
                                        headquarter = editHq.trim()
                                    )
                                )
                                isEditingProfile = false
                                saveFeedbackMessage = "Account details saved successfully!"
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("save_profile_btn")
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Save Account Details")
                        }
                    } else {
                        // Display Readonly Profile Info
                        ProfileInfoRow("Official Email", profile.email)
                        ProfileInfoRow("Designation", profile.designation)
                        ProfileInfoRow("Assigned Section", profile.section)
                        ProfileInfoRow("Headquarter", profile.headquarter)
                        ProfileInfoRow("Staff ID", profile.employeeId)
                    }
                }
            }
        }

        // Section 2: Application Preferences
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("preferences_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Application Preferences",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Voice Notification Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Push Voice Announcements",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Speak task aloud automatically when entering geofence",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = prefVoice,
                            onCheckedChange = {
                                prefVoice = it
                                viewModel.saveUserPreferences(preferences.copy(defaultVoiceEnabled = it))
                            },
                            modifier = Modifier.testTag("pref_voice_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Auto Next Recurring Instance Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Auto-Schedule Recurring Tasks",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Automatically create next recurrence when task is completed",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = prefAutoRecur,
                            onCheckedChange = {
                                prefAutoRecur = it
                                viewModel.saveUserPreferences(preferences.copy(autoGenerateNextRecurring = it))
                            },
                            modifier = Modifier.testTag("pref_auto_recur_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Vibration Alert Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Haptic Vibration Triggers",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Vibrate device when crossing into task perimeter",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = prefVibration,
                            onCheckedChange = {
                                prefVibration = it
                                viewModel.saveUserPreferences(preferences.copy(vibrationEnabled = it))
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Default Trigger Radius Slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Default Geofence Trigger Radius",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "${prefRadius.toInt()} meters",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Slider(
                        value = prefRadius,
                        onValueChange = {
                            prefRadius = it
                            viewModel.saveUserPreferences(preferences.copy(defaultRadiusMeters = it))
                        },
                        valueRange = 100f..1000f,
                        steps = 8,
                        modifier = Modifier.testTag("pref_radius_slider")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // TTS Speech Rate Slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Voice Announcement Speed",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = String.format("%.2fx", prefSpeechRate),
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Slider(
                        value = prefSpeechRate,
                        onValueChange = {
                            prefSpeechRate = it
                            viewModel.saveUserPreferences(preferences.copy(speechRate = it))
                        },
                        valueRange = 0.75f..1.25f,
                        steps = 4
                    )
                }
            }
        }

        // Section 3: Connected Google Calendar Management
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("google_calendar_mgmt_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Google Calendar Account",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (calendarSettings.isConnected) EmeraldSuccess.copy(alpha = 0.15f) else RoseCritical.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = if (calendarSettings.isConnected) "● Connected" else "○ Disconnected",
                                color = if (calendarSettings.isConnected) EmeraldSuccess else RoseCritical,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    ProfileInfoRow("Connected Account", calendarSettings.connectedEmail)
                    ProfileInfoRow("Calendar Scopes", "calendar.events, spreadsheets.readonly")
                    ProfileInfoRow(
                        "Last Synchronized",
                        dateFormat.format(Date(calendarSettings.lastSyncEpoch))
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Auto-sync Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Background Calendar Sync",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Mirror new tasks to Google Calendar events with location",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = calAutoSync,
                            onCheckedChange = {
                                calAutoSync = it
                                viewModel.saveCalendarSettings(calendarSettings.copy(isAutoSyncEnabled = it))
                            },
                            modifier = Modifier.testTag("cal_autosync_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Sync & Account Actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                if (calendarSettings.isConnected) {
                                    viewModel.disconnectGoogleCalendar()
                                } else {
                                    viewModel.reconnectGoogleCalendar()
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(if (calendarSettings.isConnected) "Disconnect" else "Reconnect")
                        }

                        Button(
                            onClick = {
                                viewModel.syncAllTasksToCalendar()
                                saveFeedbackMessage = "All corridor tasks synchronized with Google Calendar!"
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Sync Now")
                        }
                    }
                }
            }
        }

        // Section 4: SMUN - SNL Railway Corridor Reference
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("corridor_reference_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Engineering,
                            contentDescription = null,
                            tint = SkyBlue40,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Assigned Corridor: SMUN to SNL",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Eastern Dedicated Freight Corridor (Punjab Section)\nSpan: Km 1167.210 to Km 1249.720 (Length: 82.51 km)\nCivil Maintenance Hubs: SMUN, RPJ, SBJN, NSIR, GVGN, KNNN, CHAN, SNL",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileInfoRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold
        )
        HorizontalDivider(
            modifier = Modifier.padding(top = 4.dp),
            thickness = 0.5.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
        )
    }
}
