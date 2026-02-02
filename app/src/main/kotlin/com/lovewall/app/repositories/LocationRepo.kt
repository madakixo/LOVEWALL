package com.lovewall.app.repositories

import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.firebase.firestore.FirebaseFirestore
import com.lovewall.app.models.LocationUpdate
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationRepo @Inject constructor(
    @ApplicationContext private val context: Context,
    private val firestore: FirebaseFirestore
) {
    private val fusedProvider: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    fun getLocationFlow(coupleId: String): Flow<LocationUpdate> = callbackFlow {
        val listener = firestore.collection("couples/$coupleId/locations").document("current")
            .addSnapshotListener { snapshot, e ->
                if (e != null) close(e) else offer(snapshot?.toObject(LocationUpdate::class.java) ?: LocationUpdate(0.0, 0.0, 0))
            }
        awaitClose { listener.remove() }
    }

    suspend fun updateLocation(coupleId: String, update: LocationUpdate) = withContext(Dispatchers.IO) {
        firestore.collection("couples/$coupleId/locations").document("current").set(update).await()
    }

    fun requestUpdates(callback: LocationCallback) {
        val request = LocationRequest.Builder(Priority.PRIORITY_BALANCED_POWER_ACCURACY, 30000)  // 30s interval
            .build()
        fusedProvider.requestLocationUpdates(request, callback, android.os.Looper.getMainLooper())
    }
}
