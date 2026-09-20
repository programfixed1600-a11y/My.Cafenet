package com.example.ui.screens.admin

import androidx.compose.foundation.background
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CafeService
import com.example.data.model.ServiceCategory
import com.example.ui.MainViewModel
import com.example.ui.components.CafeNetTopBar
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.PersianGold
import com.example.ui.theme.PersianNavy
import com.example.ui.theme.PersianNavyDark
import com.example.util.PersianUtils

@Composable
fun AdminPanelScreen(
    viewModel: MainViewModel,
    onBackClick: () -> Unit
) {
    val totalRevenue by viewModel.totalRevenue.collectAsState()
    val totalOrdersCount by viewModel.totalOrdersCount.collectAsState()
    val allServices by viewModel.allServicesAdmin.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Services & Prices, 1: Broadcast Notification, 2: Branches

    var editingService by remember { mutableStateOf<CafeService?>(null) }
    var editPriceText by remember { mutableStateOf("") }

    var broadcastTitle by remember { mutableStateOf("") }
    var broadcastMessage by remember { mutableStateOf("") }
    var broadcastSentFeedback by remember { mutableStateOf(false) }

    // Dialog for updating service price
    if (editingService != null) {
        val service = editingService!!
        AlertDialog(
            onDismissRequest = { editingService = null },
            title = {
                Text("تغییر تعرفه خدمت: ${service.title}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            },
            text = {
                Column {
                    Text("مبلغ جدید تعرفه (تومان):", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editPriceText,
                        onValueChange = { editPriceText = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val newPrice = editPriceText.toLongOrNull() ?: service.price
                        viewModel.updateServicePrice(service.id, newPrice)
                        editingService = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PersianNavy),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("ذخیره تغییرات", style = MaterialTheme.typography.labelMedium)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { editingService = null },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("انصراف")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            CafeNetTopBar(
                title = "پنل مدیریت جامع کافی‌نت هوشمند",
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .testTag("admin_panel_screen"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Admin KPI Overview
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = PersianNavyDark)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFB71C1C)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = Color.White)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "مدیریت کل سیستم کافی‌نت",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Text(
                                    text = "وضعیت سامانه‌های متصل: آنلاین و پایدار ✓",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = EmeraldGreen
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            AdminKpiBox("درآمد کل دریافتی", PersianUtils.formatToman(totalRevenue ?: 0L), EmeraldGreen)
                            AdminKpiBox("تعداد کل سفارش‌ها", "$totalOrdersCount سفارش", PersianGold)
                            AdminKpiBox("شعب فعال", "۲ شعبه تهران", Color.White)
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
                        text = { Text("مدیریت تعرفه‌ها", style = MaterialTheme.typography.labelMedium) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("ارسال اطلاعیه همگانی", style = MaterialTheme.typography.labelMedium) }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("شعب کافی‌نت", style = MaterialTheme.typography.labelMedium) }
                    )
                }
            }

            // Tab 0: Service & Price Editor
            if (selectedTab == 0) {
                items(allServices) { service ->
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
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = service.title,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "${service.category.persianTitle} • نرخ جاری: ${PersianUtils.formatToman(service.price)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = EmeraldGreen
                                )
                            }

                            IconButton(
                                onClick = {
                                    editingService = service
                                    editPriceText = service.price.toString()
                                }
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = "ویرایش قیمت", tint = PersianNavy)
                            }
                        }
                    }
                }
            } else if (selectedTab == 1) {
                // Tab 1: Broadcast Notification
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Campaign, contentDescription = null, tint = PersianGold)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "ارسال پیام و اطلاعیه فوری به کلیه مشتریان",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            OutlinedTextField(
                                value = broadcastTitle,
                                onValueChange = { broadcastTitle = it },
                                label = { Text("عنوان اطلاعیه") },
                                placeholder = { Text("مثال: آغاز ثبت‌نام فروش فوق‌العاده خودرو") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = broadcastMessage,
                                onValueChange = { broadcastMessage = it },
                                label = { Text("متن پیام اعلان") },
                                placeholder = { Text("متن اعلان برای نمایش به همه کاربران...") },
                                minLines = 3,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Button(
                                onClick = {
                                    if (broadcastTitle.isNotBlank() && broadcastMessage.isNotBlank()) {
                                        viewModel.broadcastAnnouncement(broadcastTitle, broadcastMessage)
                                        broadcastTitle = ""
                                        broadcastMessage = ""
                                        broadcastSentFeedback = true
                                    }
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PersianNavy),
                                modifier = Modifier.fillMaxWidth().height(48.dp)
                            ) {
                                Text("ارسال اعلان به همه کاربران", style = MaterialTheme.typography.labelMedium)
                            }

                            if (broadcastSentFeedback) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("✓ اعلان همگانی با موفقیت برای تمامی کاربران ارسال شد.", color = EmeraldGreen, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            } else {
                // Tab 2: Branches
                item {
                    BranchCard(
                        name = "شعبه مرکزی کافینت هوشمند (میدان انقلاب)",
                        address = "تهران، میدان انقلاب، ابتدای کارگر شمالی، پلاک ۱۲",
                        phone = "02166954321",
                        manager = "مهندس رضایی",
                        activeStaff = 4,
                        activeOrders = 18
                    )
                }
                item {
                    BranchCard(
                        name = "شعبه شماره ۲ (تجریش)",
                        address = "تهران، میدان تجریش، مجتمع تجاری ارگ، طبقه اول",
                        phone = "02122718899",
                        manager = "خانم طاهری",
                        activeStaff = 3,
                        activeOrders = 9
                    )
                }
            }
        }
    }
}

@Composable
private fun AdminKpiBox(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = color)
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color(0xFFA5B8CC))
    }
}

@Composable
private fun BranchCard(name: String, address: String, phone: String, manager: String, activeStaff: Int, activeOrders: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Store, contentDescription = null, tint = PersianNavy)
                Spacer(modifier = Modifier.width(8.dp))
                Text(name, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text("آدرس: $address", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("تلفن: $phone • مدیر شعبه: $manager", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("پرسنل فعال: $activeStaff نفر", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = PersianNavy)
                Text("سفارش‌های در دست اقدام: $activeOrders مورد", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = EmeraldGreen)
            }
        }
    }
}
