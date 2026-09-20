package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class UserRole(val persianLabel: String) {
    CUSTOMER("مشتری"),
    EMPLOYEE("اپراتور / کارمند"),
    ADMIN("مدیر سیستم")
}

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val id: String,
    val name: String,
    val phone: String,
    val nationalCode: String,
    val role: UserRole = UserRole.CUSTOMER,
    val profileImage: String = "",
    val walletBalance: Long = 0L,
    val branchId: String = "central",
    val createdAt: Long = System.currentTimeMillis()
)
