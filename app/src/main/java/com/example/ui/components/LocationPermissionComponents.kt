package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.RoseCritical

enum class LocationPermissionStatus {
    NOT_REQUESTED,
    PRECISE_GRANTED,
    COARSE_ONLY,
    DENIED,
    PERMANENTLY_DENIED
}

fun openAppSettings(context: Context) {
    try {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    } catch (_: Exception) {
        val fallbackIntent = Intent(Settings.ACTION_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(fallbackIntent)
    }
}

@Composable
fun LocationPermissionBanner(
    status: LocationPermissionStatus,
    onRequestPermission: () -> Unit,
    onShowRationale: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    AnimatedVisibility(
        visible = status != LocationPermissionStatus.PRECISE_GRANTED,
        modifier = modifier
    ) {
        val containerColor = when (status) {
            LocationPermissionStatus.COARSE_ONLY -> AmberAccent.copy(alpha = 0.12f)
            LocationPermissionStatus.PERMANENTLY_DENIED -> RoseCritical.copy(alpha = 0.12f)
            else -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
        }

        val iconColor = when (status) {
            LocationPermissionStatus.COARSE_ONLY -> AmberAccent
            LocationPermissionStatus.PERMANENTLY_DENIED -> RoseCritical
            else -> MaterialTheme.colorScheme.primary
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .testTag("location_permission_banner"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = containerColor)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = when (status) {
                            LocationPermissionStatus.COARSE_ONLY -> Icons.Default.WarningAmber
                            LocationPermissionStatus.PERMANENTLY_DENIED -> Icons.Default.Settings
                            else -> Icons.Default.GpsFixed
                        },
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier
                            .size(24.dp)
                            .padding(top = 2.dp)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = when (status) {
                                LocationPermissionStatus.COARSE_ONLY -> "Approximate Location Active"
                                LocationPermissionStatus.PERMANENTLY_DENIED -> "Precise GPS Permission Disabled"
                                else -> "Precise Location Needed for Geofencing"
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(3.dp))

                        Text(
                            text = when (status) {
                                LocationPermissionStatus.COARSE_ONLY ->
                                    "Approximate location cannot reliably trigger 200m–300m track alerts. Upgrade to Precise Location (Fine) for accurate asset notifications."
                                LocationPermissionStatus.PERMANENTLY_DENIED ->
                                    "Location access is disabled in Android settings. Enable 'Precise Location' in app settings to trigger proximity voice alerts on railway assets."
                                else ->
                                    "Grant ACCESS_FINE_LOCATION to detect arrival at railway assets, curves, points, and stations, and receive voice push alerts."
                            },
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (status == LocationPermissionStatus.DENIED || status == LocationPermissionStatus.NOT_REQUESTED) {
                        TextButton(
                            onClick = onShowRationale,
                            modifier = Modifier.testTag("btn_permission_why")
                        ) {
                            Text("Why is this needed?", fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    if (status == LocationPermissionStatus.PERMANENTLY_DENIED) {
                        Button(
                            onClick = { openAppSettings(context) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = RoseCritical,
                                contentColor = MaterialTheme.colorScheme.surface
                            ),
                            modifier = Modifier.testTag("btn_open_app_settings")
                        ) {
                            Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Open App Settings", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    } else {
                        Button(
                            onClick = onRequestPermission,
                            modifier = Modifier.testTag("btn_request_location_perm")
                        ) {
                            Icon(Icons.Default.GpsFixed, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (status == LocationPermissionStatus.COARSE_ONLY) "Enable Precise GPS" else "Grant Precise Location",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LocationPermissionRationaleDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.GpsFixed,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
        },
        title = {
            Text(
                text = "Precise GPS Location Required",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "DFCC GeoTask is a civil engineering asset management tool designed for the SMUN to SNL corridor. To trigger automated notifications, the following permissions are used:",
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.GpsFixed, contentDescription = null, tint = EmeraldSuccess, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("ACCESS_FINE_LOCATION (Precise)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tracks exact position within 2–5 meters along the railway track alignment to trigger voice announcements when you arrive within a 200m–400m geofence radius of points, crossings, curves, or bridges.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = AmberAccent, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("POST_NOTIFICATIONS (Push Alerts)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Delivers heads-up push banners and vibration alerts even when your phone screen is off during track inspection.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Text(
                    text = "When prompted by Android, please select 'Precise' and 'While using the app'.",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                modifier = Modifier.testTag("dialog_grant_location_btn")
            ) {
                Text("Continue to Request")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("dialog_dismiss_location_btn")
            ) {
                Text("Not Now")
            }
        }
    )
}
