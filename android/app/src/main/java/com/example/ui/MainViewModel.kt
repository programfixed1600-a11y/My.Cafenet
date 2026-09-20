package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.AppNotification
import com.example.data.model.CafeService
import com.example.data.model.ChatMessage
import com.example.data.model.DocumentFile
import com.example.data.model.FileType
import com.example.data.model.NotificationType
import com.example.data.model.Order
import com.example.data.model.OrderStatus
import com.example.data.model.PaymentStatus
import com.example.data.model.ServiceCategory
import com.example.data.model.Transaction
import com.example.data.model.User
import com.example.data.model.UserRole
import com.example.data.repository.AiAssistantRepository
import com.example.data.repository.AuthRepository
import com.example.data.repository.ChatRepository
import com.example.data.repository.NotificationRepository
import com.example.data.repository.OrderRepository
import com.example.data.repository.ServiceRepository
import com.example.data.repository.SmartChecklistResult
import com.example.data.repository.WalletRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application, viewModelScope)

    val authRepository = AuthRepository(db.userDao())
    val serviceRepository = ServiceRepository(db.serviceDao())
    val orderRepository = OrderRepository(db.orderDao())
    val walletRepository = WalletRepository(db.transactionDao(), db.userDao())
    val notificationRepository = NotificationRepository(db.notificationDao())
    val chatRepository = ChatRepository(db.chatDao())
    val aiAssistantRepository = AiAssistantRepository(db.serviceDao())

    // Active User
    val currentUser: StateFlow<User?> = authRepository.currentUser

    // Services
    val activeServices: StateFlow<List<CafeService>> = serviceRepository.getAllActiveServices()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allServicesAdmin: StateFlow<List<CafeService>> = serviceRepository.getAllServicesAdmin()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Search query for services
    val searchQuery = MutableStateFlow("")
    val filteredServices: StateFlow<List<CafeService>> = combine(
        activeServices,
        searchQuery
    ) { services, query ->
        if (query.isBlank()) {
            services
        } else {
            services.filter {
                it.title.contains(query, ignoreCase = true) ||
                it.description.contains(query, ignoreCase = true) ||
                it.category.persianTitle.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // User Orders
    val userOrders: StateFlow<List<Order>> = currentUser.flatMapLatest { user ->
        if (user != null) orderRepository.getOrdersByUser(user.id)
        else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // All Orders for Admin
    val allOrders: StateFlow<List<Order>> = orderRepository.getAllOrders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Employee Orders
    val employeeOrders: StateFlow<List<Order>> = currentUser.flatMapLatest { user ->
        if (user != null && (user.role == UserRole.EMPLOYEE || user.role == UserRole.ADMIN)) {
            orderRepository.getOrdersForEmployee(user.id)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Notifications
    val userNotifications: StateFlow<List<AppNotification>> = currentUser.flatMapLatest { user ->
        if (user != null) notificationRepository.getNotificationsByUser(user.id)
        else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreadNotificationsCount: StateFlow<Int> = currentUser.flatMapLatest { user ->
        if (user != null) notificationRepository.getUnreadCount(user.id)
        else flowOf(0)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // User Transactions
    val userTransactions: StateFlow<List<Transaction>> = currentUser.flatMapLatest { user ->
        if (user != null) walletRepository.getTransactionsByUser(user.id)
        else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Admin Metrics
    val totalRevenue: StateFlow<Long?> = orderRepository.getTotalRevenue()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    val totalOrdersCount: StateFlow<Int> = orderRepository.getTotalOrdersCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // AI Chat State
    private val _aiChatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                id = "ai_welcome",
                orderId = "ai",
                senderId = "ai_bot",
                senderName = "دستیار هوشمند کافینت",
                senderRole = UserRole.EMPLOYEE,
                message = "سلام! 👋 من دستیار هوشمند کافینت هستم. در مورد مدارک لازم، نحوه ثبت‌نام، هزینه‌ها و زمان انجام خدمات مختلف چه سوالی دارید؟",
                isAi = true
            )
        )
    )
    val aiChatMessages: StateFlow<List<ChatMessage>> = _aiChatMessages.asStateFlow()
    val isAiThinking = MutableStateFlow(false)

    // Scanned Documents Archive in memory / local
    val scannedDocuments = MutableStateFlow<List<DocumentFile>>(emptyList())

    // Action Handlers
    fun onSearchQueryChanged(newQuery: String) {
        searchQuery.value = newQuery
    }

    fun switchUserRole(role: UserRole) {
        viewModelScope.launch {
            authRepository.switchActiveUserRole(role)
        }
    }

    fun login(phone: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            authRepository.loginWithPhone(phone)
            onComplete()
        }
    }

    fun register(name: String, phone: String, nationalCode: String, role: UserRole, onComplete: () -> Unit) {
        viewModelScope.launch {
            authRepository.registerUser(name, phone, nationalCode, role)
            onComplete()
        }
    }

    fun depositWallet(amount: Long, onComplete: () -> Unit) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            walletRepository.deposit(user.id, amount)
            authRepository.updateWalletBalanceLocally(user.walletBalance + amount)
            notificationRepository.sendNotification(
                userId = user.id,
                title = "افزایش اعتبار کیف پول",
                message = "مبلغ ${com.example.util.PersianUtils.formatToman(amount)} با موفقیت به کیف پول شما واریز گردید.",
                type = NotificationType.WALLET_DEPOSIT
            )
            onComplete()
        }
    }

    fun createOrder(
        service: CafeService,
        customerName: String,
        phone: String,
        nationalCode: String,
        customerNotes: String,
        uploadedFiles: List<DocumentFile>,
        payWithWallet: Boolean,
        discountCode: String = "",
        onComplete: (Order) -> Unit
    ) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            val discountAmount = if (discountCode == "CAFE10" || discountCode == "HOLIDAY") {
                (service.price * 0.1).toLong()
            } else 0L

            val finalPrice = (service.price - discountAmount).coerceAtLeast(0L)

            if (payWithWallet) {
                walletRepository.payForOrder(user.id, finalPrice, service.title)
                authRepository.updateWalletBalanceLocally((user.walletBalance - finalPrice).coerceAtLeast(0L))
            }

            val order = orderRepository.createOrder(
                userId = user.id,
                userName = customerName.ifBlank { user.name },
                userPhone = phone.ifBlank { user.phone },
                userNationalCode = nationalCode.ifBlank { user.nationalCode },
                serviceId = service.id,
                serviceTitle = service.title,
                category = service.category,
                price = service.price,
                discountCode = discountCode,
                discountAmount = discountAmount,
                customerNotes = customerNotes,
                uploadedFiles = uploadedFiles,
                payWithWallet = payWithWallet
            )

            notificationRepository.sendNotification(
                userId = user.id,
                title = "ثبت سفارش موفق (${order.trackingCode})",
                message = "سفارش شما برای ${service.title} با موفقیت ثبت شد و در صف بررسی اپراتور قرار گرفت.",
                type = NotificationType.ORDER_SUBMITTED,
                orderId = order.id
            )

            onComplete(order)
        }
    }

    fun updateOrderStatus(orderId: String, status: OrderStatus, notes: String = "") {
        viewModelScope.launch {
            orderRepository.updateOrderStatus(orderId, status, notes)
            val order = orderRepository.getOrderById(orderId)
            // Send notification to customer
            val targetUserId = "cust_01"
            notificationRepository.sendNotification(
                userId = targetUserId,
                title = "تغییر وضعیت سفارش: ${status.persianTitle}",
                message = if (notes.isNotBlank()) "توضیحات اپراتور: $notes" else "وضعیت سفارش شما به ${status.persianTitle} تغییر یافت.",
                type = if (status == OrderStatus.NEEDS_REVISION) NotificationType.NEEDS_REVISION else NotificationType.STATUS_CHANGED,
                orderId = orderId
            )
        }
    }

    fun assignEmployee(orderId: String, employeeId: String, employeeName: String) {
        viewModelScope.launch {
            orderRepository.assignEmployeeToOrder(orderId, employeeId, employeeName)
        }
    }

    fun deliverOrderFiles(orderId: String, fileName: String) {
        viewModelScope.launch {
            val resultFile = DocumentFile(
                id = "res_" + UUID.randomUUID().toString().take(8),
                name = fileName.ifBlank { "گواهی_نهایی_تایید_شده.pdf" },
                uriOrPath = "delivered_docs/$fileName",
                fileType = FileType.PDF,
                sizeFormatted = "1.8 MB",
                isResultFile = true
            )
            orderRepository.attachResultFiles(orderId, listOf(resultFile))
            notificationRepository.sendNotification(
                userId = "cust_01",
                title = "سفارش شما تکمیل شد! 🎉",
                message = "فایل خروجی نهایی سفارش آماده دانلود می‌باشد. لطفاً در بخش جزییات سفارش آن را دریافت فرمایید.",
                type = NotificationType.ORDER_COMPLETED,
                orderId = orderId
            )
        }
    }

    fun rateOrder(orderId: String, rating: Int, comment: String) {
        viewModelScope.launch {
            orderRepository.rateOrder(orderId, rating, comment)
        }
    }

    fun updateServicePrice(serviceId: String, newPrice: Long) {
        viewModelScope.launch {
            serviceRepository.updatePrice(serviceId, newPrice)
        }
    }

    fun addNewService(title: String, category: ServiceCategory, description: String, price: Long, requiredDocs: List<String>, timeEstimate: String) {
        viewModelScope.launch {
            serviceRepository.addNewService(title, category, description, price, requiredDocs, timeEstimate)
        }
    }

    fun broadcastAnnouncement(title: String, message: String) {
        viewModelScope.launch {
            notificationRepository.broadcastNotification(title, message)
        }
    }

    fun markNotificationAsRead(id: String) {
        viewModelScope.launch {
            notificationRepository.markAsRead(id)
        }
    }

    fun sendAiPrompt(prompt: String) {
        if (prompt.isBlank()) return
        val user = currentUser.value
        val userMsg = ChatMessage(
            id = "msg_" + UUID.randomUUID().toString().take(8),
            orderId = "ai",
            senderId = user?.id ?: "guest",
            senderName = user?.name ?: "کاربر",
            senderRole = UserRole.CUSTOMER,
            message = prompt,
            isAi = false
        )
        _aiChatMessages.value = _aiChatMessages.value + userMsg
        isAiThinking.value = true

        viewModelScope.launch {
            val responseText = aiAssistantRepository.getAiResponse(prompt)
            val aiMsg = ChatMessage(
                id = "ai_" + UUID.randomUUID().toString().take(8),
                orderId = "ai",
                senderId = "ai_bot",
                senderName = "دستیار هوشمند کافینت",
                senderRole = UserRole.EMPLOYEE,
                message = responseText,
                isAi = true
            )
            _aiChatMessages.value = _aiChatMessages.value + aiMsg
            isAiThinking.value = false
        }
    }

    fun sendOrderChatMessage(orderId: String, message: String) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            chatRepository.sendMessage(
                orderId = orderId,
                senderId = user.id,
                senderName = user.name,
                senderRole = user.role,
                message = message
            )
        }
    }

    fun addScannedDocument(name: String, type: FileType = FileType.SCAN) {
        val newDoc = DocumentFile(
            id = "doc_" + UUID.randomUUID().toString().take(8),
            name = name,
            uriOrPath = "scans/$name",
            fileType = type,
            sizeFormatted = "1.4 MB"
        )
        scannedDocuments.value = listOf(newDoc) + scannedDocuments.value
    }
}
