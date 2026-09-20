package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OrderStatus
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.CrimsonRedContainer
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.EmeraldGreenContainer
import com.example.ui.theme.PersianGold
import com.example.ui.theme.PersianGoldContainer
import com.example.ui.theme.PersianNavy

@Composable
fun OrderStatusTimeline(
    currentStatus: OrderStatus,
    modifier: Modifier = Modifier
) {
    val steps = listOf(
        "ثبت درخواست",
        "بررسی مدارک",
        "در حال انجام",
        "تکمیل سفارش"
    )

    val isNeedsRevision = currentStatus == OrderStatus.NEEDS_REVISION
    val isCancelled = currentStatus == OrderStatus.CANCELLED

    val activeStepIndex = when (currentStatus) {
        OrderStatus.SUBMITTED -> 0
        OrderStatus.DOCUMENT_REVIEW -> 1
        OrderStatus.IN_PROGRESS -> 2
        OrderStatus.NEEDS_REVISION -> 1 // stuck at review
        OrderStatus.COMPLETED -> 3
        OrderStatus.CANCELLED -> 0
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "وضعیت سفارش:",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            when {
                                isCancelled -> CrimsonRedContainer
                                isNeedsRevision -> CrimsonRedContainer
                                currentStatus == OrderStatus.COMPLETED -> EmeraldGreenContainer
                                else -> PersianGoldContainer
                            }
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = currentStatus.persianTitle,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = when {
                            isCancelled -> CrimsonRed
                            isNeedsRevision -> CrimsonRed
                            currentStatus == OrderStatus.COMPLETED -> EmeraldGreen
                            else -> PersianGold
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Step Indicator Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                steps.forEachIndexed { index, stepTitle ->
                    val isDone = index < activeStepIndex || (index == 3 && currentStatus == OrderStatus.COMPLETED)
                    val isCurrent = index == activeStepIndex && currentStatus != OrderStatus.COMPLETED

                    val circleColor by animateColorAsState(
                        targetValue = when {
                            isNeedsRevision && index == 1 -> CrimsonRed
                            isCancelled -> CrimsonRed
                            isDone -> EmeraldGreen
                            isCurrent -> PersianNavy
                            else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        },
                        label = "circleColor"
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(circleColor),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isDone) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            } else if (isNeedsRevision && index == 1) {
                                Icon(
                                    imageVector = Icons.Default.PriorityHigh,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            } else if (isCurrent) {
                                Icon(
                                    imageVector = Icons.Default.HourglassBottom,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                            } else {
                                Text(
                                    text = "${index + 1}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = stepTitle,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                fontWeight = if (isCurrent || isDone) FontWeight.Bold else FontWeight.Normal
                            ),
                            color = if (isCurrent || isDone) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            textAlign = TextAlign.Center
                        )
                    }

                    if (index < steps.size - 1) {
                        val lineColor = if (index < activeStepIndex) EmeraldGreen else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        Box(
                            modifier = Modifier
                                .weight(0.6f)
                                .height(3.dp)
                                .padding(bottom = 18.dp)
                                .background(lineColor, RoundedCornerShape(2.dp))
                        )
                    }
                }
            }
        }
    }
}
