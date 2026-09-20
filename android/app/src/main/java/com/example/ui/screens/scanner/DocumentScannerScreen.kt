package com.example.ui.screens.scanner

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.components.CafeNetTopBar
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.EmeraldGreenContainer
import com.example.ui.theme.PersianGold
import com.example.ui.theme.PersianNavy
import com.example.ui.theme.PersianNavyDark

@Composable
fun DocumentScannerScreen(
    viewModel: MainViewModel,
    onBackClick: () -> Unit
) {
    var selectedFilter by remember { mutableIntStateOf(0) } // 0: Auto Enhance, 1: B&W Document, 2: Original
    var selectedDocType by remember { mutableStateOf("کارت ملی هوشمند") }
    val capturedPages = remember { mutableStateListOf<String>() }
    var saveSuccessMessage by remember { mutableStateOf("") }

    val docTypes = listOf("کارت ملی هوشمند", "صفحه شناسنامه", "عکس ۴*۳", "فرم دست‌نویس", "سند و قرارداد")

    Scaffold(
        topBar = {
            CafeNetTopBar(
                title = "اسکنر هوشمند مدارک کافینت",
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = PersianNavyDark,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Shutter and Actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                if (capturedPages.isNotEmpty()) {
                                    capturedPages.removeLast()
                                }
                            }
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "اسکن مجدد", tint = Color.White)
                        }

                        // Shutter Button
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(PersianGold.copy(alpha = 0.3f))
                                .border(3.dp, PersianGold, CircleShape)
                                .clickable {
                                    val pageNum = capturedPages.size + 1
                                    capturedPages.add("صفحه $pageNum ($selectedDocType)")
                                    saveSuccessMessage = ""
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(Color.White),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Camera, contentDescription = "گرفتن اسکن", tint = PersianNavy, modifier = Modifier.size(28.dp))
                            }
                        }

                        IconButton(
                            onClick = { selectedFilter = (selectedFilter + 1) % 3 }
                        ) {
                            Icon(Icons.Default.Tune, contentDescription = "فیلترها", tint = PersianGold)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (capturedPages.isNotEmpty()) {
                        Button(
                            onClick = {
                                val fileName = "${selectedDocType.replace(" ", "_")}_اسکن_شده.pdf"
                                viewModel.addScannedDocument(fileName)
                                saveSuccessMessage = "✓ سند با موفقیت با فرمت PDF در بایگانی ذخیره گردید."
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("scanner_save_pdf_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "تولید فایل PDF نهایی (${capturedPages.size} صفحه)",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFF0F1722))
                .testTag("document_scanner_screen"),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Document Type Selector Chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp, horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(docTypes) { type ->
                    val isSelected = selectedDocType == type
                    Surface(
                        onClick = { selectedDocType = type },
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) PersianGold else Color(0xFF1E293B)
                    ) {
                        Text(
                            text = type,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            ),
                            color = if (isSelected) PersianNavyDark else Color.White,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // Viewfinder Camera Simulation Box
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF192537))
                    .border(2.dp, PersianGold.copy(alpha = 0.6f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                // Corner Guide Overlays
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("┌", color = PersianGold, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                        Text("┐", color = PersianGold, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = PersianGold,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "سند ($selectedDocType) را درون کادر قرار دهید",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "تشخیص هوشمند لبه‌ها و صاف‌سازی خودکار زاویه فعال است",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF94A3B8),
                            textAlign = TextAlign.Center
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("└", color = PersianGold, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                        Text("┘", color = PersianGold, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Filter Indicator
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF1E293B)
                ) {
                    Text(
                        text = "فیلتر فعال: " + when (selectedFilter) {
                            0 -> "تقویت هوشمند رنگ و کنتراست (Magic Color)"
                            1 -> "سیاه و سفید اداری (B&W Scan)"
                            else -> "تصویر خام دوربین (Original)"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = PersianGold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            // Success feedback
            if (saveSuccessMessage.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(EmeraldGreenContainer)
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = saveSuccessMessage,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = EmeraldGreen
                    )
                }
            }

            // Captured Thumbnails Strip
            if (capturedPages.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(capturedPages) { page ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF1E293B)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(page, style = MaterialTheme.typography.labelSmall, color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}
