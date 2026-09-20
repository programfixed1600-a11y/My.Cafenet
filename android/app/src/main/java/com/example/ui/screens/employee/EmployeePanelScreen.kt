package com.example.ui.screens.employee

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.data.model.Order
import com.example.data.model.OrderStatus
import com.example.ui.MainViewModel
import com.example.ui.components.CafeNetTopBar
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.CrimsonRedContainer
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.EmeraldGreenContainer
import com.example.ui.theme.PersianGold
import com.example.ui.theme.PersianGoldContainer
import com.example.ui.theme.PersianNavy
import com.example.ui.theme.PersianNavyDark
import com.example.util.PersianUtils

@Composable
fun EmployeePanelScreen(
    viewModel: MainViewModel,
    onBackClick: () -> Unit,
    onChatClick: (String) -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val allOrders by viewModel.allOrders.collectAsState()

    var selectedOrderForRevision by remember { mutableStateOf<Order?>(null) }
    var revisionReasonText by remember { mutableStateOf("") }

    var selectedOrderForDelivery by remember { mutableStateOf<Order?>(null) }
    var resultFileName by remember { mutableStateOf("") }

    // Dialog for requesting customer document revision
    if (selectedOrderForRevision != null) {
        val order = selectedOrderForRevision!!
        AlertDialog(
            onDismissRequest = { selectedOrderForRevision = null },
            title = {
                Text("اعلام نقص مدرک به مشتری (${order.trackingCode})", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            },
            text = {
                Column {
                    Text("دلیل رد یا نقص مدرک را بنویسید:", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = revisionReasonText,
                        onValueChange = { revisionReasonText = it },
                        placeholder = { Text("مثال: تصویر کارت ملی واضح نیست و شماره خوانده نمی‌شود...") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateOrderStatus(order.id, OrderStatus.NEEDS_REVISION, revisionReasonText)
                        selectedOrderForRevision = null
                        revisionReasonText = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("ارسال هشدار به مشتری", style = MaterialTheme.typography.labelMedium)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { selectedOrderForRevision = null },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("انصراف")
                }
            }
        )
    }

    // Dialog for delivering final file and completing order
    if (selectedOrderForDelivery != null) {
        val order = selectedOrderForDelivery!!
        AlertDialog(
            onDismissRequest = { selectedOrderForDelivery = null },
            title = {
                Text("تحویل فایل خروجی و تکمیل سفارش", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            },
            text = {
                Column {
                    Text("نام فایل نهایی (PDF رسید ثبت‌نام یا گواهی):", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = resultFileName,
                        onValueChange = { resultFileName = it },
                        placeholder = { Text("مثال: رسید_نهایی_سامانه_ثنا.pdf") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deliverOrderFiles(order.id, resultFileName)
                        selectedOrderForDelivery = null
                        resultFileName = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("تکمیل و تحویل به مشتری", style = MaterialTheme.typography.labelMedium)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { selectedOrderForDelivery = null },
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
                title = "میز کار اپراتور / کارمند کافی‌نت",
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .testTag("employee_panel_screen"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Operator Shift Header Card
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
                                    .background(PersianGold),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Badge, contentDescription = null, tint = Color.White)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = currentUser?.name ?: "اپراتور سامانه",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Text(
                                    text = "شیفت فعال • شعبه مرکزی",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFFA5B8CC)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            StatBox("سفارش‌های فعال", "${allOrders.count { it.status != OrderStatus.COMPLETED }} مورد", PersianGold)
                            StatBox("تکمیل شده امروز", "${allOrders.count { it.status == OrderStatus.COMPLETED }} مورد", EmeraldGreen)
                            StatBox("رضایت مشتریان", "۴.۹ از ۵ ★", Color.White)
                        }
                    }
                }
            }

            // Active Queue Header
            item {
                Text(
                    text = "صف سفارش‌های مشتریان (${allOrders.size})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            items(allOrders) { order ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = order.serviceTitle,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "کد: ${order.trackingCode} • متقاضی: ${order.userName} (${order.userPhone})",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = when (order.status) {
                                    OrderStatus.COMPLETED -> EmeraldGreenContainer
                                    OrderStatus.NEEDS_REVISION -> CrimsonRedContainer
                                    else -> PersianGoldContainer
                                }
                            ) {
                                Text(
                                    text = order.status.persianTitle,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = when (order.status) {
                                            OrderStatus.COMPLETED -> EmeraldGreen
                                            OrderStatus.NEEDS_REVISION -> CrimsonRed
                                            else -> PersianGold
                                        }
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        if (order.customerNotes.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "یادداشت مشتری: ${order.customerNotes}",
                                style = MaterialTheme.typography.bodySmall,
                                color = PersianNavy
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Operator Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            if (order.status == OrderStatus.SUBMITTED || order.status == OrderStatus.DOCUMENT_REVIEW) {
                                Button(
                                    onClick = { viewModel.updateOrderStatus(order.id, OrderStatus.IN_PROGRESS) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = PersianNavy)
                                ) {
                                    Text("شروع انجام", style = MaterialTheme.typography.labelSmall)
                                }
                            }

                            if (order.status == OrderStatus.IN_PROGRESS || order.status == OrderStatus.DOCUMENT_REVIEW) {
                                Button(
                                    onClick = {
                                        selectedOrderForDelivery = order
                                        resultFileName = "گواهی_${order.serviceTitle.replace(" ", "_")}.pdf"
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
                                ) {
                                    Icon(Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("تحویل نهایی", style = MaterialTheme.typography.labelSmall)
                                }

                                OutlinedButton(
                                    onClick = { selectedOrderForRevision = order },
                                    modifier = Modifier.weight(0.8f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("نقص مدرک", style = MaterialTheme.typography.labelSmall, color = CrimsonRed)
                                }
                            }

                            OutlinedButton(
                                onClick = { onChatClick(order.id) },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = "چت با مشتری", tint = PersianGold, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatBox(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = color)
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color(0xFFA5B8CC))
    }
}
