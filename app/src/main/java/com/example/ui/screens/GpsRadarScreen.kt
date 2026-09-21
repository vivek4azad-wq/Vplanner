package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.GeoTask
import com.example.location.StationPreset
import com.example.ui.TaskViewModel
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.RoseCritical
import com.example.ui.theme.SkyBlue40
import com.example.ui.theme.SkyBlue80
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun GpsRadarScreen(
    viewModel: TaskViewModel,
    onRequestLocationPermission: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentLocation by viewModel.currentLocation.collectAsState()
    val isTrackingRealGps by viewModel.isTrackingRealGps.collectAsState()
    val allTasks by viewModel.allTasks.collectAsState()
    val locationPermissionStatus by viewModel.locationPermissionStatus.collectAsState()

    // Filter non-completed tasks with valid coordinates and sort by distance
    val tasksWithDistance = allTasks
        .filter { !it.isCompleted && (it.latitude != 0.0 || it.longitude != 0.0) }
        .map { task ->
            val dist = viewModel.locationTracker.getDistanceTo(task) ?: Float.MAX_VALUE
            task to dist
        }
        .sortedBy { it.second }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Location Permission Banner
        item {
            com.example.ui.components.LocationPermissionBanner(
                status = locationPermissionStatus,
                onRequestPermission = onRequestLocationPermission,
                onShowRationale = { viewModel.openRationaleDialog() },
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Telemetry Card
        item {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("radar_telemetry_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "GPS TELEMETRY & RADAR",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = currentLocation?.label ?: "Acquiring Coordinates...",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isTrackingRealGps) EmeraldSuccess.copy(alpha = 0.15f) else AmberAccent.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = if (isTrackingRealGps) "LIVE FIX" else "SIMULATED",
                                color = if (isTrackingRealGps) EmeraldSuccess else AmberAccent,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("LATITUDE", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    text = String.format("%.5f°", currentLocation?.latitude ?: 0.0),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }

                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("LONGITUDE", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    text = String.format("%.5f°", currentLocation?.longitude ?: 0.0),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }

                        Surface(
                            modifier = Modifier.weight(0.8f),
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("ACCURACY", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    text = "±${currentLocation?.accuracyMeters?.toInt() ?: 0}m",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                if (isTrackingRealGps) {
                                    viewModel.stopGpsTracking()
                                } else {
                                    if (locationPermissionStatus != com.example.ui.components.LocationPermissionStatus.PRECISE_GRANTED) {
                                        onRequestLocationPermission()
                                    }
                                    viewModel.startGpsTracking(onPermissionRequired = onRequestLocationPermission)
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.GpsFixed, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isTrackingRealGps) "Pause Real GPS" else "Enable Real GPS")
                        }
                    }
                }
            }
        }

        // Visual Radar Canvas Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    val primaryColor = MaterialTheme.colorScheme.primary
                    val gridColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                    val alertColor = EmeraldSuccess

                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val centerX = size.width / 2
                        val centerY = size.height / 2
                        val maxRadius = minOf(centerX, centerY) * 0.82f

                        // Concentric circles
                        drawCircle(color = gridColor, radius = maxRadius * 0.33f, center = Offset(centerX, centerY), style = Stroke(width = 1.5f))
                        drawCircle(color = gridColor, radius = maxRadius * 0.66f, center = Offset(centerX, centerY), style = Stroke(width = 1.5f))
                        drawCircle(color = gridColor, radius = maxRadius, center = Offset(centerX, centerY), style = Stroke(width = 2.0f))

                        // Crosshairs
                        drawLine(color = gridColor, start = Offset(centerX - maxRadius, centerY), end = Offset(centerX + maxRadius, centerY), strokeWidth = 1.5f)
                        drawLine(color = gridColor, start = Offset(centerX, centerY - maxRadius), end = Offset(centerX, centerY + maxRadius), strokeWidth = 1.5f)

                        // Center User Blip
                        drawCircle(color = primaryColor.copy(alpha = 0.25f), radius = 18f, center = Offset(centerX, centerY))
                        drawCircle(color = primaryColor, radius = 7f, center = Offset(centerX, centerY))

                        // Task Blips
                        val current = currentLocation
                        if (current != null) {
                            val maxRangeMeters = 5000f // 5km window for radar
                            tasksWithDistance.take(8).forEach { (task, dist) ->
                                if (dist < maxRangeMeters) {
                                    val normDist = (dist / maxRangeMeters).coerceIn(0.1f, 1.0f) * maxRadius
                                    // Pseudo-angle based on delta lat/lng
                                    val dLat = (task.latitude - current.latitude)
                                    val dLng = (task.longitude - current.longitude)
                                    val angle = Math.atan2(dLat, dLng)

                                    val blipX = (centerX + (normDist * cos(angle))).toFloat()
                                    val blipY = (centerY - (normDist * sin(angle))).toFloat()

                                    val isInside = dist <= task.radiusMeters
                                    val blipColor = if (isInside) alertColor else AmberAccent

                                    drawCircle(color = blipColor.copy(alpha = 0.3f), radius = 14f, center = Offset(blipX, blipY))
                                    drawCircle(color = blipColor, radius = 5f, center = Offset(blipX, blipY))
                                }
                            }
                        }
                    }

                    // Overlay labels
                    Column(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(14.dp)
                    ) {
                        Text("RADAR RANGE: 5 KM", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Text("Outer Ring: 5000m | Middle: 3300m | Inner: 1600m", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    // Legend
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).background(MaterialTheme.colorScheme.primary, CircleShape))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("You", fontSize = 10.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).background(EmeraldSuccess, CircleShape))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Triggered (In Zone)", fontSize = 10.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).background(AmberAccent, CircleShape))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Nearby Waypoint", fontSize = 10.sp)
                        }
                    }
                }
            }
        }

        // Test Proximity Simulator Section
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.NearMe,
                            contentDescription = null,
                            tint = SkyBlue40,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Test Proximity Simulator",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Jump GPS to any DFCC station to test geofence voice triggers",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        viewModel.locationTracker.dfccStations.forEach { preset ->
                            val isCurrent = currentLocation?.label?.contains(preset.name) == true
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.simulateLocation(preset) },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isCurrent) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                border = if (isCurrent) androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(
                                            text = "${preset.name} (${preset.code})",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                        Text(
                                            text = "${preset.corridor} • ${preset.latitude}, ${preset.longitude}",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    FilledTonalButton(
                                        onClick = { viewModel.simulateLocation(preset) },
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                        modifier = Modifier.height(32.dp)
                                    ) {
                                        Text(if (isCurrent) "Current" else "Jump Here", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Live Geofence Monitor Header
        item {
            Text(
                text = "NEAREST ACTIVE TASKS (${tasksWithDistance.size})",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // Items sorted by distance
        items(tasksWithDistance, key = { it.first.id }) { (task, distance) ->
            val isInside = distance <= task.radiusMeters

            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = if (isInside) EmeraldSuccess.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (isInside) {
                                Text(
                                    text = "🎯 INSIDE GEOFENCE",
                                    color = EmeraldSuccess,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                            }
                            Text(
                                text = if (distance < 1000) "${distance.toInt()} m away" else String.format("%.2f km away", distance / 1000f),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isInside) EmeraldSuccess else MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(3.dp))

                        Text(
                            text = task.title,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )

                        Text(
                            text = "${task.locationName} (Trigger Radius: ${task.radiusMeters.toInt()}m)",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { viewModel.speakTask(task) }) {
                            Icon(Icons.Default.VolumeUp, contentDescription = "Voice read", tint = SkyBlue40)
                        }
                        IconButton(onClick = { viewModel.triggerProximityNow(task) }) {
                            Icon(Icons.Default.NotificationsActive, contentDescription = "Trigger alert now", tint = AmberAccent)
                        }
                    }
                }
            }
        }
    }
}
