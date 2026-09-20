package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class OrderStatus(val persianTitle: String, val stepIndex: Int) {
    SUBMITTED("ثبت درخواست", 0),
    DOCUMENT_REVIEW("بررسی مدارک", 1),
    IN_PROGRESS("در حال انجام", 2),
    NEEDS_REVISION("نیاز به اصلاح مدارک", 3),
    COMPLETED("تکمیل شده", 4),
    CANCELLED("لغو شده", -1)
}

enum class PaymentStatus(val persianTitle: String) {
    UNPAID("پرداخت نشده"),
    PAID("پرداخت شده"),
    REFUNDED("مسترد شده")
}

enum class FileType {
    IMAGE,
    PDF,
    SCAN
}

data class DocumentFile(
    val id: String,
    val name: String,
    val uriOrPath: String,
    val fileType: FileType,
    val uploadTime: Long = System.currentTimeMillis(),
    val sizeFormatted: String = "1.2 MB",
    val isResultFile: Boolean = false
)

@Entity(tableName = "orders")
data class Order(
    @PrimaryKey
    val id: String,
    val trackingCode: String,
    val userId: String,
    val userName: String,
    val userPhone: String,
    val userNationalCode: String,
    val serviceId: String,
    val serviceTitle: String,
    val category: ServiceCategory,
    val employeeId: String = "",
    val employeeName: String = "",
    val status: OrderStatus = OrderStatus.SUBMITTED,
    val revisionNotes: String = "",
    val customerNotes: String = "",
    val uploadedFiles: List<DocumentFile> = emptyList(),
    val resultFiles: List<DocumentFile> = emptyList(),
    val price: Long,
    val discountCode: String = "",
    val discountAmount: Long = 0L,
    val finalPrice: Long = price,
    val paymentStatus: PaymentStatus = PaymentStatus.UNPAID,
    val rating: Int = 0,
    val reviewComment: String = "",
    val branchId: String = "central",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
