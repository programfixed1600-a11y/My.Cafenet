package com.example.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Auth : Screen("auth")
    object Home : Screen("home")
    object ServiceList : Screen("service_list/{categoryId}") {
        fun createRoute(categoryId: String) = "service_list/$categoryId"
    }
    object ServiceDetail : Screen("service_detail/{serviceId}") {
        fun createRoute(serviceId: String) = "service_detail/$serviceId"
    }
    object CreateOrder : Screen("create_order/{serviceId}") {
        fun createRoute(serviceId: String) = "create_order/$serviceId"
    }
    object OrderDetail : Screen("order_detail/{orderId}") {
        fun createRoute(orderId: String) = "order_detail/$orderId"
    }
    object CustomerPanel : Screen("customer_panel")
    object EmployeePanel : Screen("employee_panel")
    object AdminPanel : Screen("admin_panel")
    object Wallet : Screen("wallet")
    object AiChat : Screen("ai_chat")
    object Scanner : Screen("scanner")
    object Notifications : Screen("notifications")
    object Chat : Screen("chat/{orderId}") {
        fun createRoute(orderId: String) = "chat/$orderId"
    }
}
