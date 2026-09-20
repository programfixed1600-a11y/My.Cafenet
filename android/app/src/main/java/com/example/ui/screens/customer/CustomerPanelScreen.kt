package com.example.ui.screens.customer

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Order
import com.example.data.model.OrderStatus
import com.example.ui.MainViewModel
import com.example.ui.components.CafeNetTopBar
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.PersianGold
import com.example.ui.theme.PersianGoldContainer
import com.example.ui.theme.PersianNavy
import com.example.ui.theme.PersianNavyDark
import com.example.util.PersianUtils

@Composable
fun CustomerPanelScreen(
    viewModel: MainViewModel,
    onBackClick: () -> Unit,
    onNavigateToOrderDetail: (String) -> Unit,
    onNavigateToWallet: () -> Unit,
    onNavigateToScanner: () -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val userOrders by viewModel.userOrders.collectAsState()
    val scannedDocs by viewModel.scannedDocuments.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Orders, 1: Documents Archive, 2: Profile Info

    Scaffold(
        topBar = {
            CafeNetTopBar(
                title = "پنل کاربری و سفارش‌های من",
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .testTag("customer_panel_screen"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Profile Card Header
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = PersianNavyDark),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(PersianNavy, PersianNavyDark, Color(0xFF0B1B2B))
                                )
                            )
                            .padding(18.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(CircleShape)
                                        .background(PersianGold),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Person,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = currentUser?.name ?: "کاربر محترم",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White
                                    )
                                    Text(
                                        text = "شماره تماس: ${currentUser?.phone ?: "-"}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFFAEC4D9)
                                    )
                                    Text(
                                        text = "کد ملی: ${currentUser?.nationalCode ?: "-"}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFF8FA7BD)
                                    )
                                }

                                Surface(
                                    onClick = onNavigateToWallet,
                                    shape = RoundedCornerShape(10.dp),
                                    color = PersianGoldContainer
                                ) {
                                    Column(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "کیف پول",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = PersianGold
                                        )
                                        Text(
                                            text = PersianUtils.formatToman(currentUser?.walletBalance ?: 0L),
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                            color = Color(0xFF4A3402)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Tab Selector
            item {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = PersianNavy,
                    modifier = Modifier.clip(RoundedCornerShape(12.dp))
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("سفارش‌های من (${userOrders.size})", style = MaterialTheme.typography.labelMedium) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("بایگانی مدارک", style = MaterialTheme.typography.labelMedium) }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("اطلاعات حساب", style = MaterialTheme.typography.labelMedium) }
                    )
                }
            }

            // Tab Content
            if (selectedTab == 0) {
                if (userOrders.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "هنوز سفارشی ثبت نکرده‌اید.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    items(userOrders) { order ->
                        CustomerOrderItem(
                            order = order,
                            onClick = { onNavigateToOrderDetail(order.id) }
                        )
                    }
                }
            } else if (selectedTab == 1) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "مدارک اسکن شده و ارسالی شما",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Surface(
                            onClick = onNavigateToScanner,
                            shape = RoundedCornerShape(8.dp),
                            color = PersianNavy
                        ) {
                            Text(
                                text = "+ اسکن مدرک جدید",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                items(scannedDocs) { doc ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Description, contentDescription = null, tint = PersianNavy)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(doc.name, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                                    Text(doc.sizeFormatted, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                                }
                            }
                            Text("آماده ارسال", style = MaterialTheme.typography.labelSmall, color = EmeraldGreen)
                        }
                    }
                }
            } else {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "اطلاعات هویتی و امنیتی",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            InfoRow("نام کامل:", currentUser?.name ?: "")
                            InfoRow("شماره همراه ثبت‌شده:", currentUser?.phone ?: "")
                            InfoRow("کد ملی تایید شده:", currentUser?.nationalCode ?: "")
                            InfoRow("وضعیت احراز هویت شاهکار:", "تایید شده ✓")
                            InfoRow("شعبه ارائه‌دهنده خدمت:", "مرکزی (میدان انقلاب)")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CustomerOrderItem(
    order: Order,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = order.serviceTitle,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "کد رهگیری: ${order.trackingCode}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = when (order.status) {
                            OrderStatus.COMPLETED -> Color(0xFFE8F5E9)
                            OrderStatus.NEEDS_REVISION -> Color(0xFFFFEBEE)
                            else -> PersianGoldContainer
                        }
                    ) {
                        Text(
                            text = order.status.persianTitle,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = when (order.status) {
                                    OrderStatus.COMPLETED -> EmeraldGreen
                                    OrderStatus.NEEDS_REVISION -> Color(0xFFC62828)
                                    else -> PersianGold
                                }
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = PersianUtils.formatToman(order.finalPrice),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = EmeraldGreen
                    )
                }
            }

            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = PersianNavy)
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
    }
}
