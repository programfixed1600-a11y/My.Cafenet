package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey
    val id: String,
    val orderId: String = "general",
    val senderId: String,
    val senderName: String,
    val senderRole: UserRole,
    val message: String,
    val isAi: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
