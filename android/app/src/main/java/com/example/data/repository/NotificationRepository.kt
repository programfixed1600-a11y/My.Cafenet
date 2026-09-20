package com.example.data.repository

import com.example.data.local.NotificationDao
import com.example.data.model.AppNotification
import com.example.data.model.NotificationType
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class NotificationRepository(private val notificationDao: NotificationDao) {

    fun getNotificationsByUser(userId: String): Flow<List<AppNotification>> =
        notificationDao.getNotificationsByUser(userId)

    fun getUnreadCount(userId: String): Flow<Int> =
        notificationDao.getUnreadCount(userId)

    suspend fun markAsRead(id: String) = notificationDao.markAsRead(id)

    suspend fun markAllAsRead(userId: String) = notificationDao.markAllAsRead(userId)

    suspend fun sendNotification(
        userId: String,
        title: String,
        message: String,
        type: NotificationType,
        orderId: String? = null
    ) {
        val notif = AppNotification(
            id = "notif_" + UUID.randomUUID().toString().take(8),
            userId = userId,
            title = title,
            message = message,
            type = type,
            orderId = orderId,
            isRead = false
        )
        notificationDao.insertNotification(notif)
    }

    suspend fun broadcastNotification(title: String, message: String) {
        sendNotification("all", title, message, NotificationType.SYSTEM_ANNOUNCEMENT)
    }
}
