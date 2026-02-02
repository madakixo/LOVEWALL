package com.lovewall.app.location

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.lovewall.app.models.LocationUpdate
import com.lovewall.app.repositories.LocationRepo
import com.lovewall.app.utils.coupleId
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LocationService : Service() {
    @Inject lateinit var repo: LocationRepo

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()
        startForeground(1, createNotification())
        repo.requestUpdates(callback)
        return START_STICKY
    }

    private fun createNotification(): android.app.Notification {
        return androidx.core.app.NotificationCompat.Builder(this, "location_channel")
            .setContentTitle("LoveWall Location Sharing")
            .setContentText("Sharing location with your partner...")
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setPriority(androidx.core.app.NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()
    }

    private fun createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = android.app.NotificationChannel(
                "location_channel",
                "Location Sharing",
                android.app.NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(android.app.NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private val callback = object : LocationCallback() {
        private var lastUpdateTime = 0L
        override fun onLocationResult(result: LocationResult) {
            if (System.currentTimeMillis() - lastUpdateTime < 30000) return
            val location = result.lastLocation ?: return
            kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                repo.updateLocation(coupleId, LocationUpdate(location.latitude, location.longitude, System.currentTimeMillis()))
            }
            lastUpdateTime = System.currentTimeMillis()
        }
    }
}
