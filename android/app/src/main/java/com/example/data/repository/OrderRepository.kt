package com.example.data.repository

import com.example.data.local.OrderDao
import com.example.data.model.DocumentFile
import com.example.data.model.Order
import com.example.data.model.OrderStatus
import com.example.data.model.PaymentStatus
import com.example.data.model.ServiceCategory
import kotlinx.coroutines.flow.Flow
import java.util.Random
import java.util.UUID

data class SmartChecklistResult(
    val isValid: Boolean,
    val warnings: List<String>,
    val missingDocuments: List<String>
)

class OrderRepository(private val orderDao: OrderDao) {

    fun getAllOrders(): Flow<List<Order>> = orderDao.getAllOrders()

    fun getOrdersByUser(userId: String): Flow<List<Order>> = orderDao.getOrdersByUser(userId)

    fun getOrdersForEmployee(employeeId: String): Flow<List<Order>> = orderDao.getOrdersForEmployee(employeeId)

    fun getOrderById(orderId: String): Flow<Order?> = orderDao.getOrderById(orderId)

    fun getOrderByTrackingCode(code: String): Flow<Order?> = orderDao.getOrderByTrackingCode(code)

    fun getTotalRevenue(): Flow<Long?> = orderDao.getTotalRevenue()

    fun getTotalOrdersCount(): Flow<Int> = orderDao.getTotalOrdersCount()

    /**
     * Smart Pre-submission Document Checklist
     */
    fun validateDocumentChecklist(
        requiredDocs: List<String>,
        uploadedFiles: List<DocumentFile>,
        customerName: String,
        nationalCode: String,
        phone: String
    ): SmartChecklistResult {
        val warnings = mutableListOf<String>()
        val missingDocs = mutableListOf<String>()

        if (customerName.isBlank()) {
            warnings.add("نام و نام خانوادگی وارد نشده است.")
        }
        if (nationalCode.isBlank()) {
            warnings.add("کد ملی وارد نشده است.")
        }
        if (phone.isBlank()) {
            warnings.add("شماره موبایل وارد نشده است.")
        }

        // Check required documents
        if (requiredDocs.isNotEmpty() && uploadedFiles.isEmpty()) {
            warnings.add("هیچ فایلی برای مدارک مورد نیاز بارگذاری نشده است.")
            missingDocs.addAll(requiredDocs)
        } else if (uploadedFiles.size < requiredDocs.size && requiredDocs.isNotEmpty()) {
            warnings.add("تعداد مدارک بارگذاری شده (${uploadedFiles.size}) کمتر از مدارک لازم (${requiredDocs.size}) است.")
        }

        return SmartChecklistResult(
            isValid = warnings.isEmpty() && missingDocs.isEmpty(),
            warnings = warnings,
            missingDocuments = missingDocs
        )
    }

    suspend fun createOrder(
        userId: String,
        userName: String,
        userPhone: String,
        userNationalCode: String,
        serviceId: String,
        serviceTitle: String,
        category: ServiceCategory,
        price: Long,
        discountCode: String,
        discountAmount: Long,
        customerNotes: String,
        uploadedFiles: List<DocumentFile>,
        payWithWallet: Boolean
    ): Order {
        val trackingCode = "CN-" + (10000 + Random().nextInt(90000))
        val finalPrice = (price - discountAmount).coerceAtLeast(0L)
        val order = Order(
            id = "ord_" + UUID.randomUUID().toString().take(8),
            trackingCode = trackingCode,
            userId = userId,
            userName = userName,
            userPhone = userPhone,
            userNationalCode = userNationalCode,
            serviceId = serviceId,
            serviceTitle = serviceTitle,
            category = category,
            employeeId = "",
            employeeName = "",
            status = OrderStatus.SUBMITTED,
            customerNotes = customerNotes,
            uploadedFiles = uploadedFiles,
            price = price,
            discountCode = discountCode,
            discountAmount = discountAmount,
            finalPrice = finalPrice,
            paymentStatus = if (payWithWallet) PaymentStatus.PAID else PaymentStatus.UNPAID
        )
        orderDao.insertOrder(order)
        return order
    }

    suspend fun updateOrderStatus(
        orderId: String,
        status: OrderStatus,
        notes: String = ""
    ) {
        orderDao.updateOrderStatus(orderId, status, notes)
    }

    suspend fun assignEmployeeToOrder(orderId: String, employeeId: String, employeeName: String) {
        val order = orderDao.getOrderByIdOnce(orderId) ?: return
        val updated = order.copy(
            employeeId = employeeId,
            employeeName = employeeName,
            status = if (order.status == OrderStatus.SUBMITTED) OrderStatus.DOCUMENT_REVIEW else order.status,
            updatedAt = System.currentTimeMillis()
        )
        orderDao.updateOrder(updated)
    }

    suspend fun attachResultFiles(orderId: String, resultFiles: List<DocumentFile>) {
        val order = orderDao.getOrderByIdOnce(orderId) ?: return
        val currentResults = order.resultFiles.toMutableList()
        currentResults.addAll(resultFiles)
        val updated = order.copy(
            resultFiles = currentResults,
            status = OrderStatus.COMPLETED,
            updatedAt = System.currentTimeMillis()
        )
        orderDao.updateOrder(updated)
    }

    suspend fun updatePaymentStatus(orderId: String, paymentStatus: PaymentStatus) {
        orderDao.updatePaymentStatus(orderId, paymentStatus)
    }

    suspend fun rateOrder(orderId: String, rating: Int, comment: String) {
        orderDao.rateOrder(orderId, rating, comment)
    }
}
