package com.example.ui.screens.order

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Discount
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import com.example.data.model.DocumentFile
import com.example.data.model.FileType
import com.example.data.model.Order
import com.example.data.repository.SmartChecklistResult
import com.example.ui.MainViewModel
import com.example.ui.components.CafeNetTopBar
import com.example.ui.components.SmartChecklistDialog
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.PersianGold
import com.example.ui.theme.PersianGoldContainer
import com.example.ui.theme.PersianNavy
import com.example.util.PersianUtils
import java.util.UUID

@Composable
fun CreateOrderScreen(
    serviceId: String,
    viewModel: MainViewModel,
    onBackClick: () -> Unit,
    onOrderCreated: (String) -> Unit
) {
    val allServices by viewModel.activeServices.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val service = allServices.find { it.id == serviceId }

    var customerName by remember { mutableStateOf(currentUser?.name ?: "") }
    var phone by remember { mutableStateOf(currentUser?.phone ?: "") }
    var nationalCode by remember { mutableStateOf(currentUser?.nationalCode ?: "") }
    var customerNotes by remember { mutableStateOf("") }
    var discountCode by remember { mutableStateOf("") }
    var discountApplied by remember { mutableStateOf(false) }

    val uploadedFiles = remember {
        mutableStateListOf(
            DocumentFile(
                id = "doc_init_1",
                name = "کارت_ملی_هوشمند.jpg",
                uriOrPath = "sample/ncard.jpg",
                fileType = FileType.IMAGE,
                sizeFormatted = "1.2 MB"
            )
        )
    }

    // 0: Digital Wallet, 1: Shaparak Shetab Direct Gateway
    var paymentMethod by remember { mutableStateOf(0) }

    var showChecklistDialog by remember { mutableStateOf(false) }
    var checklistResult by remember {
        mutableStateOf(SmartChecklistResult(true, emptyList(), emptyList()))
    }

    if (service == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("خدمت مورد نظر یافت نشد.")
        }
        return
    }

    val discountAmount = if (discountApplied) (service.price * 0.1).toLong() else 0L
    val finalPrice = (service.price - discountAmount).coerceAtLeast(0L)
    val userBalance = currentUser?.walletBalance ?: 0L
    val hasEnoughWalletBalance = userBalance >= finalPrice

    if (showChecklistDialog) {
        SmartChecklistDialog(
            checklistResult = checklistResult,
            onConfirm = {
                showChecklistDialog = false
                viewModel.createOrder(
                    service = service,
                    customerName = customerName,
                    phone = phone,
                    nationalCode = nationalCode,
                    customerNotes = customerNotes,
                    uploadedFiles = uploadedFiles.toList(),
                    payWithWallet = paymentMethod == 0,
                    discountCode = if (discountApplied) discountCode else "",
                    onComplete = { order ->
                        onOrderCreated(order.id)
                    }
                )
            },
            onDismiss = { showChecklistDialog = false }
        )
    }

    Scaffold(
        topBar = {
            CafeNetTopBar(
                title = "ثبت سفارش: ${service.title}",
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
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "مبلغ قابل پرداخت:",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = PersianUtils.formatToman(finalPrice),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = EmeraldGreen
                        )
                    }

                    Button(
                        onClick = {
                            val result = viewModel.orderRepository.validateDocumentChecklist(
                                requiredDocs = service.requiredDocuments,
                                uploadedFiles = uploadedFiles.toList(),
                                customerName = customerName,
                                nationalCode = nationalCode,
                                phone = phone
                            )
                            checklistResult = result
                            showChecklistDialog = true
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PersianNavy),
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("submit_order_button")
                    ) {
                        Text(
                            text = "تایید و پرداخت سفارش",
                            style = MaterialTheme.typography.labelLarge.copy(fontSize = 15.sp)
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
                .testTag("create_order_screen"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Customer Info Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "مشخصات متقاضی",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = customerName,
                            onValueChange = { customerName = it },
                            label = { Text("نام و نام خانوادگی") },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("order_input_name")
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("شماره تماس") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = nationalCode,
                            onValueChange = { nationalCode = it },
                            label = { Text("کد ملی ۱۰ رقمی") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Documents Upload Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "بارگذاری و اسکن مدارک (${uploadedFiles.size} فایل)",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "مدارک الزامی: ${service.requiredDocuments.joinToString("، ")}",
                            style = MaterialTheme.typography.bodySmall,
                            color = PersianGold
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Upload Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    val nextNum = uploadedFiles.size + 1
                                    uploadedFiles.add(
                                        DocumentFile(
                                            id = "doc_" + UUID.randomUUID().toString().take(6),
                                            name = "اسکن_هوشمند_مدرک_$nextNum.pdf",
                                            uriOrPath = "scans/doc_$nextNum.pdf",
                                            fileType = FileType.SCAN,
                                            sizeFormatted = "1.5 MB"
                                        )
                                    )
                                },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.DocumentScanner, contentDescription = null, tint = PersianNavy, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("اسکن دوربین", style = MaterialTheme.typography.labelSmall)
                            }

                            OutlinedButton(
                                onClick = {
                                    val nextNum = uploadedFiles.size + 1
                                    uploadedFiles.add(
                                        DocumentFile(
                                            id = "doc_" + UUID.randomUUID().toString().take(6),
                                            name = "تصویر_مدرک_$nextNum.jpg",
                                            uriOrPath = "gallery/pic_$nextNum.jpg",
                                            fileType = FileType.IMAGE,
                                            sizeFormatted = "2.1 MB"
                                        )
                                    )
                                },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.FileUpload, contentDescription = null, tint = PersianGold, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("انتخاب فایل", style = MaterialTheme.typography.labelSmall)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Uploaded Files List
                        uploadedFiles.forEachIndexed { index, file ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (file.fileType == FileType.PDF || file.fileType == FileType.SCAN) Icons.Default.PictureAsPdf else Icons.Default.Description,
                                        contentDescription = null,
                                        tint = PersianNavy,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = file.name,
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = file.sizeFormatted,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = { uploadedFiles.removeAt(index) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "حذف فایل",
                                        tint = CrimsonRed,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Customer Notes Field
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "توضیحات و نکات تکمیلی برای اپراتور",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = customerNotes,
                            onValueChange = { customerNotes = it },
                            placeholder = { Text("مثال: ترجیحاً نوبت در ساعت‌های عصر ثبت گردد یا اولویت با شعبه تجریش باشد...") },
                            minLines = 3,
                            maxLines = 5,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Discount Code Box
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "کد تخفیف",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = discountCode,
                                onValueChange = { discountCode = it },
                                placeholder = { Text("کد تخفیف (مثال: CAFE10)") },
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            )

                            Button(
                                onClick = {
                                    if (discountCode.trim().equals("CAFE10", ignoreCase = true) || discountCode.trim().equals("HOLIDAY", ignoreCase = true)) {
                                        discountApplied = true
                                    }
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PersianGold)
                            ) {
                                Text("اعمال کد", style = MaterialTheme.typography.labelMedium)
                            }
                        }

                        if (discountApplied) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "✓ کد تخفیف ۱۰٪ با موفقیت اعمال گردید (-${PersianUtils.formatToman(discountAmount)})",
                                style = MaterialTheme.typography.bodySmall,
                                color = EmeraldGreen
                            )
                        }
                    }
                }
            }

            // Payment Method Selection
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "روش پرداخت",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        // Wallet Option
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .border(
                                    1.dp,
                                    if (paymentMethod == 0) PersianNavy else MaterialTheme.colorScheme.outline,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { paymentMethod = 0 }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = paymentMethod == 0,
                                    onClick = { paymentMethod = 0 },
                                    colors = RadioButtonDefaults.colors(selectedColor = PersianNavy)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = "پرداخت از کیف پول دیجیتال",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "موجودی: ${PersianUtils.formatToman(userBalance)}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (hasEnoughWalletBalance) EmeraldGreen else CrimsonRed
                                    )
                                }
                            }
                            Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = PersianGold)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Shetab Shaparak Option
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .border(
                                    1.dp,
                                    if (paymentMethod == 1) PersianNavy else MaterialTheme.colorScheme.outline,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { paymentMethod = 1 }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = paymentMethod == 1,
                                    onClick = { paymentMethod = 1 },
                                    colors = RadioButtonDefaults.colors(selectedColor = PersianNavy)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = "درگاه مستقیم شاپرک (کلیه کارت‌های شتاب)",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "اتصال امن به درگاه به‌پرداخت ملت / سامان",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Icon(Icons.Default.CreditCard, contentDescription = null, tint = PersianNavy)
                        }
                    }
                }
            }
        }
    }
}
