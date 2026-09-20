package com.example.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.model.ServiceCategory
import com.example.ui.MainViewModel
import com.example.ui.screens.admin.AdminPanelScreen
import com.example.ui.screens.ai.AiChatScreen
import com.example.ui.screens.auth.AuthScreen
import com.example.ui.screens.chat.OperatorChatScreen
import com.example.ui.screens.customer.CustomerPanelScreen
import com.example.ui.screens.employee.EmployeePanelScreen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.notifications.NotificationsScreen
import com.example.ui.screens.order.CreateOrderScreen
import com.example.ui.screens.order.OrderDetailScreen
import com.example.ui.screens.scanner.DocumentScannerScreen
import com.example.ui.screens.services.ServiceDetailScreen
import com.example.ui.screens.services.ServiceListScreen
import com.example.ui.screens.splash.SplashScreen
import com.example.ui.screens.wallet.WalletScreen
import com.example.ui.theme.PersianGold
import com.example.ui.theme.PersianNavy

@Composable
fun CafeNetNavGraph(
    viewModel: MainViewModel,
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in listOf(
        Screen.Home.route,
        Screen.CustomerPanel.route,
        Screen.Wallet.route,
        Screen.AiChat.route,
        Screen.Scanner.route
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    NavigationBarItem(
                        selected = currentRoute == Screen.Home.route,
                        onClick = {
                            if (currentRoute != Screen.Home.route) {
                                navController.navigate(Screen.Home.route) {
                                    popUpTo(Screen.Home.route) { inclusive = true }
                                }
                            }
                        },
                        icon = { Icon(Icons.Default.Home, contentDescription = "خانه") },
                        label = { Text("خانه", style = MaterialTheme.typography.labelSmall) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PersianNavy,
                            selectedTextColor = PersianNavy,
                            indicatorColor = PersianGold.copy(alpha = 0.2f)
                        ),
                        modifier = Modifier.testTag("nav_item_home")
                    )

                    NavigationBarItem(
                        selected = currentRoute == Screen.Scanner.route,
                        onClick = {
                            if (currentRoute != Screen.Scanner.route) {
                                navController.navigate(Screen.Scanner.route)
                            }
                        },
                        icon = { Icon(Icons.Default.DocumentScanner, contentDescription = "اسکنر") },
                        label = { Text("اسکنر مدارک", style = MaterialTheme.typography.labelSmall) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PersianNavy,
                            selectedTextColor = PersianNavy,
                            indicatorColor = PersianGold.copy(alpha = 0.2f)
                        ),
                        modifier = Modifier.testTag("nav_item_scanner")
                    )

                    NavigationBarItem(
                        selected = currentRoute == Screen.AiChat.route,
                        onClick = {
                            if (currentRoute != Screen.AiChat.route) {
                                navController.navigate(Screen.AiChat.route)
                            }
                        },
                        icon = { Icon(Icons.Default.Psychology, contentDescription = "هوش مصنوعی") },
                        label = { Text("دستیار هوشمند", style = MaterialTheme.typography.labelSmall) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PersianNavy,
                            selectedTextColor = PersianNavy,
                            indicatorColor = PersianGold.copy(alpha = 0.2f)
                        ),
                        modifier = Modifier.testTag("nav_item_ai")
                    )

                    NavigationBarItem(
                        selected = currentRoute == Screen.Wallet.route,
                        onClick = {
                            if (currentRoute != Screen.Wallet.route) {
                                navController.navigate(Screen.Wallet.route)
                            }
                        },
                        icon = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = "کیف پول") },
                        label = { Text("کیف پول", style = MaterialTheme.typography.labelSmall) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PersianNavy,
                            selectedTextColor = PersianNavy,
                            indicatorColor = PersianGold.copy(alpha = 0.2f)
                        ),
                        modifier = Modifier.testTag("nav_item_wallet")
                    )

                    NavigationBarItem(
                        selected = currentRoute == Screen.CustomerPanel.route,
                        onClick = {
                            if (currentRoute != Screen.CustomerPanel.route) {
                                navController.navigate(Screen.CustomerPanel.route)
                            }
                        },
                        icon = { Icon(Icons.Default.Person, contentDescription = "پروفایل") },
                        label = { Text("سفارش‌های من", style = MaterialTheme.typography.labelSmall) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PersianNavy,
                            selectedTextColor = PersianNavy,
                            indicatorColor = PersianGold.copy(alpha = 0.2f)
                        ),
                        modifier = Modifier.testTag("nav_item_profile")
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(Screen.Splash.route) {
                SplashScreen(
                    onNavigateNext = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Auth.route) {
                AuthScreen(
                    viewModel = viewModel,
                    onAuthSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Auth.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToCategory = { category ->
                        navController.navigate(Screen.ServiceList.createRoute(category.name))
                    },
                    onNavigateToServiceDetail = { serviceId ->
                        navController.navigate(Screen.ServiceDetail.createRoute(serviceId))
                    },
                    onNavigateToCreateOrder = { serviceId ->
                        navController.navigate(Screen.CreateOrder.createRoute(serviceId))
                    },
                    onNavigateToOrderDetail = { orderId ->
                        navController.navigate(Screen.OrderDetail.createRoute(orderId))
                    },
                    onNavigateToWallet = {
                        navController.navigate(Screen.Wallet.route)
                    },
                    onNavigateToAiChat = {
                        navController.navigate(Screen.AiChat.route)
                    },
                    onNavigateToScanner = {
                        navController.navigate(Screen.Scanner.route)
                    },
                    onNavigateToCustomerPanel = {
                        navController.navigate(Screen.CustomerPanel.route)
                    },
                    onNavigateToEmployeePanel = {
                        navController.navigate(Screen.EmployeePanel.route)
                    },
                    onNavigateToAdminPanel = {
                        navController.navigate(Screen.AdminPanel.route)
                    },
                    onNavigateToNotifications = {
                        navController.navigate(Screen.Notifications.route)
                    },
                    onNavigateToChat = { orderId ->
                        navController.navigate(Screen.Chat.createRoute(orderId))
                    }
                )
            }

            composable(
                route = Screen.ServiceList.route,
                arguments = listOf(navArgument("categoryId") { type = NavType.StringType })
            ) { backStackEntry ->
                val catString = backStackEntry.arguments?.getString("categoryId") ?: ServiceCategory.GOVERNMENT.name
                val category = runCatching { ServiceCategory.valueOf(catString) }.getOrDefault(ServiceCategory.GOVERNMENT)
                ServiceListScreen(
                    category = category,
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() },
                    onServiceClick = { serviceId ->
                        navController.navigate(Screen.ServiceDetail.createRoute(serviceId))
                    },
                    onOrderClick = { serviceId ->
                        navController.navigate(Screen.CreateOrder.createRoute(serviceId))
                    }
                )
            }

            composable(
                route = Screen.ServiceDetail.route,
                arguments = listOf(navArgument("serviceId") { type = NavType.StringType })
            ) { backStackEntry ->
                val serviceId = backStackEntry.arguments?.getString("serviceId") ?: ""
                ServiceDetailScreen(
                    serviceId = serviceId,
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() },
                    onOrderClick = { sId ->
                        navController.navigate(Screen.CreateOrder.createRoute(sId))
                    },
                    onAiChatClick = {
                        navController.navigate(Screen.AiChat.route)
                    }
                )
            }

            composable(
                route = Screen.CreateOrder.route,
                arguments = listOf(navArgument("serviceId") { type = NavType.StringType })
            ) { backStackEntry ->
                val serviceId = backStackEntry.arguments?.getString("serviceId") ?: ""
                CreateOrderScreen(
                    serviceId = serviceId,
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() },
                    onOrderCreated = { orderId ->
                        navController.navigate(Screen.OrderDetail.createRoute(orderId)) {
                            popUpTo(Screen.Home.route)
                        }
                    }
                )
            }

            composable(
                route = Screen.OrderDetail.route,
                arguments = listOf(navArgument("orderId") { type = NavType.StringType })
            ) { backStackEntry ->
                val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
                OrderDetailScreen(
                    orderId = orderId,
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() },
                    onChatClick = { oId ->
                        navController.navigate(Screen.Chat.createRoute(oId))
                    }
                )
            }

            composable(Screen.CustomerPanel.route) {
                CustomerPanelScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() },
                    onNavigateToOrderDetail = { orderId ->
                        navController.navigate(Screen.OrderDetail.createRoute(orderId))
                    },
                    onNavigateToWallet = {
                        navController.navigate(Screen.Wallet.route)
                    },
                    onNavigateToScanner = {
                        navController.navigate(Screen.Scanner.route)
                    }
                )
            }

            composable(Screen.EmployeePanel.route) {
                EmployeePanelScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() },
                    onChatClick = { orderId ->
                        navController.navigate(Screen.Chat.createRoute(orderId))
                    }
                )
            }

            composable(Screen.AdminPanel.route) {
                AdminPanelScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Screen.Wallet.route) {
                WalletScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Screen.Scanner.route) {
                DocumentScannerScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Screen.AiChat.route) {
                AiChatScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Screen.Notifications.route) {
                NotificationsScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() },
                    onNavigateToOrder = { orderId ->
                        navController.navigate(Screen.OrderDetail.createRoute(orderId))
                    }
                )
            }

            composable(
                route = Screen.Chat.route,
                arguments = listOf(navArgument("orderId") { type = NavType.StringType })
            ) { backStackEntry ->
                val orderId = backStackEntry.arguments?.getString("orderId") ?: "general"
                OperatorChatScreen(
                    orderId = orderId,
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}
