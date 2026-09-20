package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class NotificationType {
    ORDER_SUBMITTED,
    STATUS_CHANGED,
    NEEDS_REVISION,
    ORDER_COMPLETED,
    PAYMENT_SUCCESS,
    WALLET_DEPOSIT,
    SYSTEM_ANNOUNCEMENT
}

@Entity(tableName = "notifications")
data class AppNotification(
    @PrimaryKey
    val id: String,
    val userId: String,
    val title: String,
    val message: String,
    val type: NotificationType = NotificationType.SYSTEM_ANNOUNCEMENT,
    val orderId: String? = null,
    val isRead: Boolean = false,
    val date: Long = System.currentTimeMillis()
)
