package com.example.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CafeService
import com.example.data.model.Order
import com.example.data.model.OrderStatus
import com.example.data.model.ServiceCategory
import com.example.data.model.UserRole
import com.example.ui.MainViewModel
import com.example.ui.components.CafeNetTopBar
import com.example.ui.components.RoleSwitchBanner
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.PersianGold
import com.example.ui.theme.PersianGoldContainer
import com.example.ui.theme.PersianNavy
import com.example.ui.theme.PersianNavyContainer
import com.example.ui.theme.PersianNavyDark
import com.example.ui.theme.PersianNavyLight
import com.example.util.PersianUtils

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onNavigateToCategory: (ServiceCategory) -> Unit,
    onNavigateToServiceDetail: (String) -> Unit,
    onNavigateToCreateOrder: (String) -> Unit,
    onNavigateToOrderDetail: (String) -> Unit,
    onNavigateToWallet: () -> Unit,
    onNavigateToAiChat: () -> Unit,
    onNavigateToScanner: () -> Unit,
    onNavigateToCustomerPanel: () -> Unit,
    onNavigateToEmployeePanel: () -> Unit,
    onNavigateToAdminPanel: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToChat: (String) -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val unreadNotifs by viewModel.unreadNotificationsCount.collectAsState()
    val filteredServices by viewModel.filteredServices.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val userOrders by viewModel.userOrders.collectAsState()

    Scaffold(
        topBar = {
            CafeNetTopBar(
                title = "کافینت هوشمند",
                unreadNotificationsCount = unreadNotifs,
                onNotificationClick = onNavigateToNotifications,
                onSupportClick = { onNavigateToChat("general") }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .testTag("home_screen"),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // Role switcher for test / evaluation
            item {
                currentUser?.let { user ->
                    RoleSwitchBanner(
                        currentRole = user.role,
                        onRoleSelected = { viewModel.switchUserRole(it) }
                    )
                }
            }

            // Quick Role Portal Link for Employee & Admin
            item {
                currentUser?.let { user ->
                    if (user.role == UserRole.EMPLOYEE) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp)
                                .clickable { onNavigateToEmployeePanel() },
                            colors = CardDefaults.cardColors(containerColor = PersianNavy),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Badge, contentDescription = null, tint = PersianGold)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        "ورود به پنل کارمندی / اپراتور",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White
                                    )
                                }
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = PersianGold)
                            }
                        }
                    } else if (user.role == UserRole.ADMIN) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp)
                                .clickable { onNavigateToAdminPanel() },
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF8B1E1E)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = PersianGold)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        "ورود به پنل مدیریت کل سیستم",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White
                                    )
                                }
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = PersianGold)
                            }
                        }
                    }
                }
            }

            // Hero Banking Card (موجودی کیف پول و اطلاعات هویتی)
            item {
                BankingHeaderCard(
                    userName = currentUser?.name ?: "کاربر گرامی",
                    walletBalance = currentUser?.walletBalance ?: 0L,
                    onWalletClick = onNavigateToWallet,
                    onScannerClick = onNavigateToScanner,
                    onAiChatClick = onNavigateToAiChat
                )
            }

            // Search Bar
            item {
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.onSearchQueryChanged(it) },
                        placeholder = { Text("جستجوی خدمات (ثنا، خودرو، مالیات، کنکور...)") },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null, tint = PersianNavy)
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("home_service_search_input")
                    )
                }
            }

            // Active Orders Summary (if any)
            if (userOrders.any { it.status != OrderStatus.COMPLETED && it.status != OrderStatus.CANCELLED }) {
                val activeOrder = userOrders.first { it.status != OrderStatus.COMPLETED && it.status != OrderStatus.CANCELLED }
                item {
                    ActiveOrderBanner(
                        order = activeOrder,
                        onClick = { onNavigateToOrderDetail(activeOrder.id) }
                    )
                }
            }

            // Service Categories Grid Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "دسته‌بندی خدمات کافی‌نت",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            // Categories (2x3 Grid representation)
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CategoryCard(
                            category = ServiceCategory.GOVERNMENT,
                            icon = Icons.Default.AccountBalance,
                            badgeCount = "۵ خدمت",
                            onClick = { onNavigateToCategory(ServiceCategory.GOVERNMENT) },
                            modifier = Modifier.weight(1f)
                        )
                        CategoryCard(
                            category = ServiceCategory.VEHICLE,
                            icon = Icons.Default.DirectionsCar,
                            badgeCount = "۵ خدمت",
                            onClick = { onNavigateToCategory(ServiceCategory.VEHICLE) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CategoryCard(
                            category = ServiceCategory.EDUCATION,
                            icon = Icons.Default.School,
                            badgeCount = "۴ خدمت",
                            onClick = { onNavigateToCategory(ServiceCategory.EDUCATION) },
                            modifier = Modifier.weight(1f)
                        )
                        CategoryCard(
                            category = ServiceCategory.FINANCIAL,
                            icon = Icons.Default.Payments,
                            badgeCount = "۳ خدمت",
                            onClick = { onNavigateToCategory(ServiceCategory.FINANCIAL) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CategoryCard(
                            category = ServiceCategory.JUDICIAL,
                            icon = Icons.Default.Gavel,
                            badgeCount = "۲ خدمت",
                            onClick = { onNavigateToCategory(ServiceCategory.JUDICIAL) },
                            modifier = Modifier.weight(1f)
                        )
                        CategoryCard(
                            category = ServiceCategory.OFFICE,
                            icon = Icons.Default.Print,
                            badgeCount = "۴ خدمت",
                            onClick = { onNavigateToCategory(ServiceCategory.OFFICE) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Popular Services Section
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "خدمات پرتقاضا و ویژه",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            items(filteredServices.take(8)) { service ->
                HomeServiceItem(
                    service = service,
                    onClick = { onNavigateToServiceDetail(service.id) },
                    onOrderClick = { onNavigateToCreateOrder(service.id) }
                )
            }
        }
    }
}

@Composable
private fun BankingHeaderCard(
    userName: String,
    walletBalance: Long,
    onWalletClick: () -> Unit,
    onScannerClick: () -> Unit,
    onAiChatClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = PersianNavyDark),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(PersianNavy, PersianNavyDark, Color(0xFF071220))
                    )
                )
                .padding(18.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "خوش آمدید، $userName",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Text(
                            text = "امروز: ${PersianUtils.getPersianDate()}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFA5B8CC)
                        )
                    }

                    Surface(
                        onClick = onWalletClick,
                        shape = RoundedCornerShape(12.dp),
                        color = PersianGoldContainer
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountBalanceWallet,
                                contentDescription = null,
                                tint = PersianGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = PersianUtils.formatToman(walletBalance),
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFF4A3402)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Quick Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionButton(
                        title = "اسکن هوشمند مدارک",
                        subtitle = "تبدیل عکس به PDF",
                        icon = Icons.Default.DocumentScanner,
                        onClick = onScannerClick,
                        modifier = Modifier.weight(1f)
                    )
                    QuickActionButton(
                        title = "دستیار هوش مصنوعی",
                        subtitle = "راهنمای مدارک و قیمت",
                        icon = Icons.Default.Psychology,
                        onClick = onAiChatClick,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickActionButton(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFF162B47),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(PersianGold.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = PersianGold,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = Color(0xFF8FA3B8)
                )
            }
        }
    }
}

@Composable
private fun CategoryCard(
    category: ServiceCategory,
    icon: ImageVector,
    badgeCount: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.testTag("category_card_${category.name}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(PersianNavyContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = PersianNavy,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = category.persianTitle,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = badgeCount,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ActiveOrderBanner(
    order: Order,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = PersianGoldContainer),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.TrackChanges, contentDescription = null, tint = PersianGold)
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "سفارش فعال: ${order.serviceTitle}",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF3B2702)
                    )
                    Text(
                        text = "کد رهگیری: ${order.trackingCode} • وضعیت: ${order.status.persianTitle}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF6B4C08)
                    )
                }
            }
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = PersianGold)
        }
    }
}

@Composable
private fun HomeServiceItem(
    service: CafeService,
    onClick: () -> Unit,
    onOrderClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp)
            .testTag("service_item_${service.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = service.title,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (service.badge.isNotBlank()) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = PersianGoldContainer
                        ) {
                            Text(
                                text = service.badge,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = PersianGold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = service.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = PersianUtils.formatToman(service.price),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = EmeraldGreen
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "⏱ ${service.timeEstimate}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Surface(
                onClick = onOrderClick,
                shape = RoundedCornerShape(10.dp),
                color = PersianNavy
            ) {
                Text(
                    text = "ثبت سفارش",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
        }
    }
}
