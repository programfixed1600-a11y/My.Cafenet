package com.example.ui.screens.order

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OrderStatus
import com.example.ui.MainViewModel
import com.example.ui.components.CafeNetTopBar
import com.example.ui.components.OrderStatusTimeline
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.EmeraldGreenContainer
import com.example.ui.theme.PersianGold
import com.example.ui.theme.PersianGoldContainer
import com.example.ui.theme.PersianNavy
import com.example.util.PersianUtils

@Composable
fun OrderDetailScreen(
    orderId: String,
    viewModel: MainViewModel,
    onBackClick: () -> Unit,
    onChatClick: (String) -> Unit
) {
    val userOrders by viewModel.userOrders.collectAsState()
    val allOrders by viewModel.allOrders.collectAsState()
    val order = userOrders.find { it.id == orderId } ?: allOrders.find { it.id == orderId }

    var userRating by remember { mutableIntStateOf(order?.rating ?: 5) }
    var userComment by remember { mutableStateOf(order?.reviewComment ?: "") }
    var ratingSubmitted by remember { mutableStateOf(order?.rating != null && order.rating > 0) }

    if (order == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("سفارش مورد نظر یافت نشد.")
        }
        return
    }

    Scaffold(
        topBar = {
            CafeNetTopBar(
                title = "پیگیری سفارش: ${order.trackingCode}",
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { onChatClick(order.id) },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PersianNavy),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("order_chat_operator_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (order.employeeName.isNotBlank()) "گفتگو با ${order.employeeName}" else "گفتگو با پشتیبانی",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .testTag("order_detail_screen"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Status Timeline
            item {
                OrderStatusTimeline(currentStatus = order.status)
            }

            // Needs Revision Alert (if status is NEEDS_REVISION)
            if (order.status == OrderStatus.NEEDS_REVISION) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = CrimsonRed)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "نیاز به اصلاح مدارک",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = CrimsonRed
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = order.revisionNotes.ifBlank { "تصویر ارسالی دارای تار بودن یا ناخوانایی است. لطفاً مدرک را مجدداً ارسال نمایید." },
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFFB71C1C)
                            )
                        }
                    }
                }
            }

            // Order Metadata Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "اطلاعات سفارش",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        DetailRow("عنوان خدمت:", order.serviceTitle)
                        DetailRow("کد رهگیری ملی:", order.trackingCode)
                        DetailRow("متقاضی:", order.userName)
                        DetailRow("شماره تماس:", order.userPhone)
                        DetailRow("کد ملی:", order.userNationalCode)
                        DetailRow("اپراتور مسئول:", order.employeeName.ifBlank { "در انتظار تخصیص به اپراتور" })
                        DetailRow("مبلغ کل پرداختی:", PersianUtils.formatToman(order.finalPrice))
                        DetailRow("وضعیت پرداخت:", order.paymentStatus.persianTitle)
                    }
                }
            }

            // Result Delivered Files (Ready to Download)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (order.status == OrderStatus.COMPLETED) Color(0xFFF1F8E9) else MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = if (order.status == OrderStatus.COMPLETED) EmeraldGreen else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "فایل‌های نهایی و خروجی (${order.resultFiles.size} فایل)",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        if (order.resultFiles.isEmpty()) {
                            Text(
                                text = if (order.status == OrderStatus.COMPLETED) "سفارش تکمیل شده و فایل‌ها در حال بارگذاری نهایی هستند." else "پس از اتمام مراحل توسط اپراتور، فایل‌های نهایی (PDF رسید یا گواهی) در این بخش قرار می‌گیرند.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            order.resultFiles.forEach { file ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color.White)
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = CrimsonRed)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(file.name, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                                            Text(file.sizeFormatted, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                                        }
                                    }

                                    Button(
                                        onClick = { /* Simulated download */ },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
                                    ) {
                                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("دانلود", style = MaterialTheme.typography.labelSmall)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 5-Star Rating & Review Section (if completed)
            if (order.status == OrderStatus.COMPLETED) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "ثبت نظر و امتیاز به عملکرد اپراتور",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                (1..5).forEach { starIndex ->
                                    IconButton(
                                        onClick = {
                                            if (!ratingSubmitted) {
                                                userRating = starIndex
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = if (starIndex <= userRating) Icons.Default.Star else Icons.Default.StarBorder,
                                            contentDescription = "ستاره $starIndex",
                                            tint = PersianGold,
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            if (!ratingSubmitted) {
                                OutlinedTextField(
                                    value = userComment,
                                    onValueChange = { userComment = it },
                                    placeholder = { Text("نظر شما درباره سرعت و کیفیت انجام خدمت...") },
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Button(
                                    onClick = {
                                        viewModel.rateOrder(order.id, userRating, userComment)
                                        ratingSubmitted = true
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = PersianNavy)
                                ) {
                                    Text("ثبت امتیاز و نظر", style = MaterialTheme.typography.labelMedium)
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(EmeraldGreenContainer)
                                        .padding(10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "✓ از ثبت نظر ارزشمند شما سپاسگزاریم.",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = EmeraldGreen
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
