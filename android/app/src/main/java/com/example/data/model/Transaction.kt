package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TransactionType(val persianTitle: String) {
    DEPOSIT("افزایش موجودی"),
    ORDER_PAYMENT("پرداخت سفارش"),
    REFUND("بازگشت وجه"),
    BONUS("هدیه ثبت‌نام / تخفیف")
}

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey
    val id: String,
    val userId: String,
    val amount: Long,
    val type: TransactionType,
    val transactionId: String,
    val description: String,
    val isSuccess: Boolean = true,
    val date: Long = System.currentTimeMillis()
)
