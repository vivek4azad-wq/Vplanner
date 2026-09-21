package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.ui.TaskViewModel
import com.example.ui.screens.CalendarSyncScreen
import com.example.ui.screens.DutiesRosterScreen
import com.example.ui.screens.GpsRadarScreen
import com.example.ui.screens.SheetsImportScreen
import com.example.ui.screens.TaskListScreen
import com.example.ui.screens.UserProfileScreen
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.MyApplicationTheme

enum class MainTab(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    TASKS("Tasks", Icons.Default.Checklist),
    ROSTER("Duties", Icons.Default.Engineering),
    GPS_RADAR("Radar", Icons.Default.NearMe),
    CALENDAR("Calendar", Icons.Default.CalendarMonth),
    PROFILE("Profile", Icons.Default.AccountCircle)
}

class MainActivity : ComponentActivity() {

    private val viewModel: TaskViewModel by viewModels()

    override fun onResume() {
        super.onResume()
        viewModel.syncPermissionStatusWithSystem()
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                var currentTab by remember { mutableStateOf(MainTab.TASKS) }
                val snackbarHostState = remember { SnackbarHostState() }
                val isSpeaking by viewModel.isSpeaking.collectAsState()
                val showRationaleDialog by viewModel.showPermissionRationaleDialog.collectAsState()

                val notificationPermissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission()
                ) { _ -> }

                // Permission Launchers
                val locationPermissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestMultiplePermissions()
                ) { permissions ->
                    val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
                    val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
                    val shouldShowRationale = androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale(
                        this@MainActivity,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                    viewModel.updateLocationPermissionResult(fineGranted, coarseGranted, shouldShowRationale)

                    // Also prompt for notifications on Android 13+ if not yet granted
                    if ((fineGranted || coarseGranted) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        if (ContextCompat.checkSelfPermission(
                                this@MainActivity,
                                Manifest.permission.POST_NOTIFICATIONS
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    }
                }

                val calendarPermissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestMultiplePermissions()
                ) { permissions ->
                    val readGranted = permissions[Manifest.permission.READ_CALENDAR] == true
                    if (readGranted) {
                        viewModel.loadCalendarEvents()
                    }
                }

                // Show Educational Rationale Dialog when requested
                if (showRationaleDialog) {
                    com.example.ui.components.LocationPermissionRationaleDialog(
                        onConfirm = {
                            viewModel.dismissRationaleDialog()
                            locationPermissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        },
                        onDismiss = {
                            viewModel.dismissRationaleDialog()
                        }
                    )
                }

                // Request initial notifications permission on Android 13+
                LaunchedEffect(Unit) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        if (ContextCompat.checkSelfPermission(
                                this@MainActivity,
                                Manifest.permission.POST_NOTIFICATIONS
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    }
                }

                // Listen to snackbar messages from ViewModel
                LaunchedEffect(Unit) {
                    viewModel.snackbarMessage.collect { message ->
                        snackbarHostState.showSnackbar(message)
                    }
                }

                Scaffold(
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = {
                                androidx.compose.foundation.layout.Column(
                                    horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "DFCC GeoTask",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "GPS Triggered • Voice Alerts",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            },
                            actions = {
                                if (isSpeaking) {
                                    IconButton(
                                        onClick = { viewModel.stopSpeaking() },
                                        modifier = Modifier.testTag("mute_tts_btn")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.VolumeOff,
                                            contentDescription = "Stop Voice Announcement",
                                            tint = AmberAccent
                                        )
                                    }
                                } else {
                                    IconButton(
                                        onClick = {
                                            viewModel.voiceManager.speak("DFCC GeoTask voice push notifications are active.")
                                        },
                                        modifier = Modifier.testTag("test_tts_btn")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.VolumeUp,
                                            contentDescription = "Test Voice Engine",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            },
                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        )
                    },
                    bottomBar = {
                        NavigationBar(
                            modifier = Modifier.testTag("bottom_nav_bar")
                        ) {
                            MainTab.entries.forEach { tab ->
                                NavigationBarItem(
                                    selected = currentTab == tab,
                                    onClick = { currentTab = tab },
                                    icon = {
                                        Icon(
                                            imageVector = tab.icon,
                                            contentDescription = tab.title,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    },
                                    label = { Text(tab.title, fontSize = 11.sp) },
                                    modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                                )
                            }
                        }
                    },
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        when (currentTab) {
                            MainTab.TASKS -> TaskListScreen(
                                viewModel = viewModel,
                                onRequestLocationPermission = {
                                    locationPermissionLauncher.launch(
                                        arrayOf(
                                            Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.ACCESS_COARSE_LOCATION
                                        )
                                    )
                                }
                            )

                            MainTab.ROSTER -> DutiesRosterScreen(
                                viewModel = viewModel
                            )

                            MainTab.GPS_RADAR -> GpsRadarScreen(
                                viewModel = viewModel,
                                onRequestLocationPermission = {
                                    locationPermissionLauncher.launch(
                                        arrayOf(
                                            Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.ACCESS_COARSE_LOCATION
                                        )
                                    )
                                }
                            )

                            MainTab.CALENDAR -> CalendarSyncScreen(
                                viewModel = viewModel,
                                onRequestCalendarPermission = {
                                    calendarPermissionLauncher.launch(
                                        arrayOf(
                                            Manifest.permission.READ_CALENDAR,
                                            Manifest.permission.WRITE_CALENDAR
                                        )
                                    )
                                }
                            )

                            MainTab.PROFILE -> UserProfileScreen(
                                viewModel = viewModel
                            )
                        }
                    }
                }
            }
        }
    }
}

