package com.lovewall.app.repositories

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.lovewall.app.models.Message
import com.lovewall.app.models.Note
import com.lovewall.app.models.VoiceNote
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessagingRepo @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) {
    fun getMessagesFlow(coupleId: String, shardDate: String = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())): Flow<List<Message>> = callbackFlow {
        val path = "couples/$coupleId/messages/$shardDate"
        val listener = firestore.collection(path)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(50)
            .addSnapshotListener { snapshot, e ->
                if (e != null) close(e) else offer(snapshot?.toObjects(Message::class.java) ?: emptyList())
            }
        awaitClose { listener.remove() }
    }

    suspend fun sendNote(coupleId: String, note: Note) = withContext(Dispatchers.IO) {
        val shardDate = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date(note.timestamp))
        firestore.collection("couples/$coupleId/notes/$shardDate").document(note.id).set(note).await()
    }

    suspend fun sendBatchNotes(coupleId: String, notes: List<Note>) = withContext(Dispatchers.IO) {
        val batch = firestore.batch()
        notes.forEach { note ->
            val shardDate = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date(note.timestamp))
            val ref = firestore.collection("couples/$coupleId/notes/$shardDate").document(note.id)
            batch.set(ref, note)
        }
        batch.commit().await()
    }

    suspend fun sendVoiceNote(coupleId: String, voiceNote: VoiceNote, audioFile: File) = withContext(Dispatchers.IO) {
        val ref = storage.reference.child("voice/${voiceNote.id}")
        ref.putFile(Uri.fromFile(audioFile)).await()
        firestore.collection("couples/$coupleId/voice").document(voiceNote.id).set(voiceNote).await()
    }
}
