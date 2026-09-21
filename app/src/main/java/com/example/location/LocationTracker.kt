package com.example.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.core.content.ContextCompat
import com.example.data.GeoTask
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class GpsLocation(
    val latitude: Double,
    val longitude: Double,
    val accuracyMeters: Float = 5.0f,
    val isSimulated: Boolean = false,
    val label: String = "Current Location"
)

data class StationPreset(
    val name: String,
    val code: String,
    val latitude: Double,
    val longitude: Double,
    val corridor: String
)

class LocationTracker(private val context: Context) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    // Default to SMUN (Shambhu New Km 1167.210) Section Origin
    private val _currentLocation = MutableStateFlow<GpsLocation?>(
        GpsLocation(30.3472, 76.7125, 5f, isSimulated = true, label = "SMUN - Shambhu New (Km 1167.210)")
    )
    val currentLocation: StateFlow<GpsLocation?> = _currentLocation.asStateFlow()

    private val _isTrackingRealGps = MutableStateFlow(false)
    val isTrackingRealGps: StateFlow<Boolean> = _isTrackingRealGps.asStateFlow()

    // Trigger callback when entering a task's radius
    var onGeofenceTriggered: ((task: GeoTask, distanceMeters: Float) -> Unit)? = null

    // Track recently triggered tasks to avoid rapid duplicate alerts
    private val triggeredCache = mutableMapOf<Long, Long>()

    // Stations along user's assigned Section: SMUN to SNL (Km 1167.210 to 1249.720)
    val dfccStations = listOf(
        StationPreset("SMUN - Shambhu New", "SMUN", 30.3472, 76.7125, "Km 1167.210 (Section Origin)"),
        StationPreset("RPJ Link Line", "RPJ", 30.4842, 76.5930, "Km 1175.500 (Br 108/114/115/120)"),
        StationPreset("SBJN - Sarai Banjara New", "SBJN", 30.5501, 76.4952, "Km 1187.130 (Yard & Sidings)"),
        StationPreset("NSIR - New Sirhind", "NSIR", 30.6354, 76.3881, "Km 1205.700 (Curves 351-355)"),
        StationPreset("GVGN - Mandi Gobindgarh New", "GVGN", 30.6652, 76.3054, "Km 1216.500 (Curve 365, Line 5)"),
        StationPreset("KNNN - Khanna New", "KNNN", 30.7056, 76.2185, "Km 1225.400 (RGM & M/C Sidings)"),
        StationPreset("CHAN - Chawa Pail New", "CHAN", 30.7550, 76.1052, "Km 1238.800 (Unimate Yard & Points)"),
        StationPreset("SNL - Sanehwal New", "SNL", 30.8250, 75.9850, "Km 1249.720 (Section Terminus)"),
        StationPreset("Level Crossing 164", "LC 164", 30.7300, 76.1600, "Km 1233.500 (LC Gate Maintenance)"),
        StationPreset("Major Bridge 165/170", "BR 165", 30.5900, 76.4400, "Km 1195.000 (Bridge Inspection)")
    )

    fun hasFineLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun hasCoarseLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun hasLocationPermission(): Boolean {
        return hasFineLocationPermission() || hasCoarseLocationPermission()
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            val lastLoc = result.lastLocation ?: return
            if (_isTrackingRealGps.value) {
                val isFine = hasFineLocationPermission()
                _currentLocation.value = GpsLocation(
                    latitude = lastLoc.latitude,
                    longitude = lastLoc.longitude,
                    accuracyMeters = lastLoc.accuracy,
                    isSimulated = false,
                    label = if (isFine) "Live GPS (Precise Fix)" else "Live GPS (Approximate Fix)"
                )
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun startRealGpsTracking(): Boolean {
        if (!hasLocationPermission()) {
            _isTrackingRealGps.value = false
            return false
        }

        try {
            val priority = if (hasFineLocationPermission()) {
                Priority.PRIORITY_HIGH_ACCURACY
            } else {
                Priority.PRIORITY_BALANCED_POWER_ACCURACY
            }

            val locationRequest = LocationRequest.Builder(
                priority,
                4000L
            ).apply {
                setMinUpdateIntervalMillis(2000L)
                setMinUpdateDistanceMeters(2f)
            }.build()

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
            _isTrackingRealGps.value = true

            // Fetch last known location right away
            fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
                if (loc != null && _isTrackingRealGps.value) {
                    val isFine = hasFineLocationPermission()
                    _currentLocation.value = GpsLocation(
                        latitude = loc.latitude,
                        longitude = loc.longitude,
                        accuracyMeters = loc.accuracy,
                        isSimulated = false,
                        label = if (isFine) "Live GPS (Precise Fix)" else "Live GPS (Approximate Fix)"
                    )
                }
            }
            return true
        } catch (_: SecurityException) {
            _isTrackingRealGps.value = false
            return false
        }
    }

    fun stopRealGpsTracking() {
        try {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        } catch (_: Exception) {}
        _isTrackingRealGps.value = false
    }

    fun setSimulatedLocation(latitude: Double, longitude: Double, label: String = "Simulated Location") {
        _currentLocation.value = GpsLocation(
            latitude = latitude,
            longitude = longitude,
            accuracyMeters = 3f,
            isSimulated = true,
            label = label
        )
    }

    fun calculateDistanceMeters(fromLat: Double, fromLng: Double, toLat: Double, toLng: Double): Float {
        val results = FloatArray(1)
        Location.distanceBetween(fromLat, fromLng, toLat, toLng, results)
        return results[0]
    }

    fun getDistanceTo(task: GeoTask): Float? {
        val current = _currentLocation.value ?: return null
        if (task.latitude == 0.0 && task.longitude == 0.0) return null
        return calculateDistanceMeters(current.latitude, current.longitude, task.latitude, task.longitude)
    }

    fun evaluateTasksForProximity(tasks: List<GeoTask>) {
        val current = _currentLocation.value ?: return
        val now = System.currentTimeMillis()

        for (task in tasks) {
            if (task.isCompleted) continue
            if (task.latitude == 0.0 && task.longitude == 0.0) continue

            val distance = calculateDistanceMeters(
                current.latitude,
                current.longitude,
                task.latitude,
                task.longitude
            )

            val radius = if (task.radiusMeters > 0) task.radiusMeters else 300f
            val isInside = distance <= radius

            if (isInside) {
                val lastTrigger = triggeredCache[task.id] ?: 0L
                // Trigger at most once every 10 minutes per task unless forced
                if (now - lastTrigger > 10 * 60 * 1000) {
                    triggeredCache[task.id] = now
                    onGeofenceTriggered?.invoke(task, distance)
                }
            }
        }
    }

    fun forceTriggerTask(task: GeoTask) {
        val dist = getDistanceTo(task) ?: 0f
        triggeredCache[task.id] = System.currentTimeMillis()
        onGeofenceTriggered?.invoke(task, dist)
    }
}
