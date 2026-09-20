package com.example.data.repository

import com.example.data.local.ChatDao
import com.example.data.model.ChatMessage
import com.example.data.model.UserRole
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class ChatRepository(private val chatDao: ChatDao) {

    fun getMessagesByOrder(orderId: String): Flow<List<ChatMessage>> =
        chatDao.getMessagesByOrder(orderId)

    suspend fun sendMessage(
        orderId: String,
        senderId: String,
        senderName: String,
        senderRole: UserRole,
        message: String,
        isAi: Boolean = false
    ): ChatMessage {
        val chatMessage = ChatMessage(
            id = "msg_" + UUID.randomUUID().toString().take(8),
            orderId = orderId,
            senderId = senderId,
            senderName = senderName,
            senderRole = senderRole,
            message = message,
            isAi = isAi
        )
        chatDao.insertMessage(chatMessage)
        return chatMessage
    }
}
