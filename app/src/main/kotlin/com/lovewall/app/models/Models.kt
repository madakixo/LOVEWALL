package com.lovewall.app.models

import java.util.UUID

data class Note(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val color: Int,
    val expiry: Long,
    val positionX: Float,
    val positionY: Float,
    val timestamp: Long = System.currentTimeMillis()
)

data class Message(
    val id: String,
    val text: String,
    val senderId: String,
    val timestamp: Long
)

data class VoiceNote(
    val id: String,
    val audioUrl: String,
    val duration: Int
)

data class Sticker(
    val id: String,
    val imageRes: Int,
    val positionX: Float,
    val positionY: Float
)

data class LocationUpdate(
    val lat: Double,
    val lon: Double,
    val timestamp: Long
)
