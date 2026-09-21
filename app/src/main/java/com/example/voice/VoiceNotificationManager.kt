package com.example.voice

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.data.GeoTask
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class VoiceNotificationManager(private val context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private val _isTtsReady = MutableStateFlow(false)
    val isTtsReady: StateFlow<Boolean> = _isTtsReady.asStateFlow()

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    companion object {
        const val CHANNEL_ID = "geotask_gps_voice_alerts"
        const val CHANNEL_NAME = "GPS Task Voice Alerts"
        const val CHANNEL_DESC = "Notifications and voice announcements triggered by GPS proximity"
    }

    fun init() {
        createNotificationChannel()
        if (tts == null) {
            tts = TextToSpeech(context, this)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.let { engine ->
                val result = engine.setLanguage(Locale.US)
                if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                    engine.setSpeechRate(0.95f)
                    engine.setPitch(1.0f)
                    _isTtsReady.value = true

                    engine.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                        override fun onStart(utteranceId: String?) {
                            _isSpeaking.value = true
                        }

                        override fun onDone(utteranceId: String?) {
                            _isSpeaking.value = false
                        }

                        @Deprecated("Deprecated in Java")
                        override fun onError(utteranceId: String?) {
                            _isSpeaking.value = false
                        }
                    })
                }
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESC
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 300, 200, 300)
                setSound(
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun speak(text: String, utteranceId: String = "geotask_voice") {
        if (_isTtsReady.value && tts != null) {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
        }
    }

    fun stopSpeaking() {
        tts?.stop()
        _isSpeaking.value = false
    }

    fun triggerTaskProximityAlert(task: GeoTask, distanceMeters: Float) {
        // 1. Post Push Notification
        showPushNotification(task, distanceMeters)

        // 2. Voice readout if enabled
        if (task.isVoicePushEnabled) {
            val loc = if (task.locationName.isNotBlank()) task.locationName else "target waypoint"
            val speech = buildString {
                append("Task alert! You are ")
                if (distanceMeters < 50) {
                    append("at $loc. ")
                } else {
                    append("${distanceMeters.toInt()} meters from $loc. ")
                }
                append("Priority: ${task.priority}. ")
                append("Task: ${task.title}. ")
                if (task.description.isNotBlank()) {
                    append(task.description)
                }
            }
            speak(speech, "task_${task.id}")
        }
    }

    private fun showPushNotification(task: GeoTask, distanceMeters: Float) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("TASK_ID", task.id)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            task.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val locText = if (task.locationName.isNotBlank()) " @ ${task.locationName}" else ""
        val distText = if (distanceMeters < 100) "In Trigger Zone" else "${distanceMeters.toInt()}m away"

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_map)
            .setContentTitle("📍 Task Triggered: ${task.title}")
            .setContentText("$distText$locText - ${task.description}")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("${task.title}\nLocation: ${task.locationName} ($distText)\nCategory: ${task.category} | Priority: ${task.priority}\n\n${task.description}"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        try {
            NotificationManagerCompat.from(context).notify(task.id.toInt(), builder.build())
        } catch (_: SecurityException) {
            // Permission not yet granted
        }
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
    }
}
