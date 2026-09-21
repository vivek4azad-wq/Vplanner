package com.example

import android.app.Application
import com.example.data.AppDatabase
import com.example.data.TaskRepository
import com.example.location.LocationTracker
import com.example.voice.VoiceNotificationManager

class GeoTaskApplication : Application() {

    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { TaskRepository(database.taskDao()) }
    val voiceNotificationManager by lazy { VoiceNotificationManager(this) }
    val locationTracker by lazy { LocationTracker(this) }

    override fun onCreate() {
        super.onCreate()
        instance = this
        voiceNotificationManager.init()
    }

    companion object {
        lateinit var instance: GeoTaskApplication
            private set
    }
}
