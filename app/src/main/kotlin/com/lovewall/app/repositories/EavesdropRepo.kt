package com.lovewall.app.repositories

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EavesdropRepo @Inject constructor(
    private val firestore: FirebaseFirestore  // For consent logs; signaling via Socket.IO
) {
    suspend fun logConsent(coupleId: String, consented: Boolean) {
        firestore.collection("couples/$coupleId/consents").document("eavesdrop").set(mapOf("consented" to consented)).await()
    }
}
