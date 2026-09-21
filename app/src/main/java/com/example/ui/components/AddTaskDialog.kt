package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.GeoTask
import com.example.location.GpsLocation
import com.example.location.StationPreset

@Composable
fun AddTaskDialog(
    currentLocation: GpsLocation?,
    stationPresets: List<StationPreset>,
    onDismiss: () -> Unit,
    onSaveTask: (GeoTask) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var locationName by remember { mutableStateOf("") }
    var latitude by remember { mutableStateOf(currentLocation?.latitude?.toString() ?: "28.5672") }
    var longitude by remember { mutableStateOf(currentLocation?.longitude?.toString() ?: "77.5540") }
    var radiusMeters by remember { mutableFloatStateOf(300f) }
    var priority by remember { mutableStateOf("MEDIUM") }
    var category by remember { mutableStateOf("Inspection") }
    var isVoicePushEnabled by remember { mutableStateOf(true) }
    var syncToCalendar by remember { mutableStateOf(true) }
    var isRecurring by remember { mutableStateOf(false) }
    var recurrenceType by remember { mutableStateOf("DAILY") } // DAILY, WEEKLY, MONTHLY, CUSTOM
    var customIntervalDays by remember { mutableStateOf("3") }

    val categories = listOf("Inspection", "Maintenance", "Signal", "Operations", "Meeting", "General")
    val priorities = listOf("HIGH", "MEDIUM", "LOW")
    val recurrenceOptions = listOf("DAILY" to "Daily", "WEEKLY" to "Weekly", "MONTHLY" to "Monthly", "CUSTOM" to "Custom")

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .padding(vertical = 24.dp)
                .testTag("add_task_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "New Location Task",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Title Input
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Task Title *") },
                    placeholder = { Text("e.g., Track Joint Inspection") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("task_title_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Description Input
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description & Notes") },
                    placeholder = { Text("Check clearance gauge and track integrity...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("task_desc_input"),
                    minLines = 2,
                    maxLines = 4
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Location Presets
                Text(
                    text = "GPS Target Location",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))

                // Horizontal list of preset station chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Use Current Location chip
                    FilterChip(
                        selected = false,
                        onClick = {
                            if (currentLocation != null) {
                                latitude = currentLocation.latitude.toString()
                                longitude = currentLocation.longitude.toString()
                                locationName = currentLocation.label
                            }
                        },
                        label = { Text("📍 Current GPS") },
                        leadingIcon = {
                            Icon(Icons.Default.MyLocation, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    )

                    stationPresets.forEach { preset ->
                        FilterChip(
                            selected = locationName == preset.name,
                            onClick = {
                                locationName = preset.name
                                latitude = preset.latitude.toString()
                                longitude = preset.longitude.toString()
                            },
                            label = { Text(preset.name) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = locationName,
                    onValueChange = { locationName = it },
                    label = { Text("Location / Landmark Name") },
                    placeholder = { Text("e.g. Dadri Junction Track 4") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("task_location_name_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Lat & Long inputs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = latitude,
                        onValueChange = { latitude = it },
                        label = { Text("Latitude") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("task_lat_input"),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = longitude,
                        onValueChange = { longitude = it },
                        label = { Text("Longitude") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("task_lng_input"),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Radius slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Geofence Radius Trigger",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${radiusMeters.toInt()} meters",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
                Slider(
                    value = radiusMeters,
                    onValueChange = { radiusMeters = it },
                    valueRange = 50f..1500f,
                    steps = 14,
                    modifier = Modifier.testTag("radius_slider")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Priority Selection
                Text(
                    text = "Priority",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    priorities.forEach { p ->
                        FilterChip(
                            selected = priority == p,
                            onClick = { priority = p },
                            label = { Text(p) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Category Selection
                Text(
                    text = "Category",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Recurring Task toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "🔄 Recurring Task",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Automatically spawns next occurrence when completed",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = isRecurring,
                        onCheckedChange = { isRecurring = it },
                        modifier = Modifier.testTag("recurrence_toggle")
                    )
                }

                if (isRecurring) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Recurrence Schedule",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        recurrenceOptions.forEach { (typeKey, label) ->
                            FilterChip(
                                selected = recurrenceType == typeKey,
                                onClick = { recurrenceType = typeKey },
                                label = { Text(label) },
                                modifier = Modifier.testTag("recur_${typeKey.lowercase()}_chip")
                            )
                        }
                    }

                    if (recurrenceType == "CUSTOM") {
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = customIntervalDays,
                            onValueChange = { customIntervalDays = it.filter { char -> char.isDigit() } },
                            label = { Text("Repeat Every (Days)") },
                            placeholder = { Text("e.g. 3") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("custom_interval_input"),
                            singleLine = true
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Voice notification toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Push Voice Notification",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Speaks task aloud when entering GPS radius",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = isVoicePushEnabled,
                        onCheckedChange = { isVoicePushEnabled = it },
                        modifier = Modifier.testTag("voice_toggle")
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Calendar sync toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Sync with Google Calendar",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Creates a linked calendar event with location",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = syncToCalendar,
                        onCheckedChange = { syncToCalendar = it },
                        modifier = Modifier.testTag("calendar_sync_toggle")
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Bottom Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                val latParsed = latitude.toDoubleOrNull() ?: 0.0
                                val lngParsed = longitude.toDoubleOrNull() ?: 0.0
                                val step = if (recurrenceType == "CUSTOM") {
                                    customIntervalDays.toIntOrNull() ?: 1
                                } else 1

                                val newTask = GeoTask(
                                    title = title.trim(),
                                    description = description.trim(),
                                    locationName = locationName.trim().ifEmpty { "Designated Waypoint" },
                                    latitude = latParsed,
                                    longitude = lngParsed,
                                    radiusMeters = radiusMeters,
                                    priority = priority,
                                    category = category,
                                    isVoicePushEnabled = isVoicePushEnabled,
                                    isSyncedWithCalendar = syncToCalendar,
                                    isRecurring = isRecurring,
                                    recurrenceType = if (isRecurring) recurrenceType else "NONE",
                                    recurrenceInterval = step
                                )
                                onSaveTask(newTask)
                            }
                        },
                        enabled = title.isNotBlank(),
                        modifier = Modifier.testTag("save_task_btn")
                    ) {
                        Text("Create Task")
                    }
                }
            }
        }
    }
}
