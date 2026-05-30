package com.smartkdfarm.app

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CallReceived
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun KhataScreen() {
    AppBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(22.dp)
        ) {
            KhataTopBar()
            KhataBalanceCard()
            KhataActionButtons()
            KhataToolbar()
            KhataHistorySection()
            Spacer(modifier = Modifier.height(112.dp))
        }
    }
}

@Composable
fun KhataTopBar() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF16F2AD), Color(0xFF08B982))
                        )
                    )
                    .border(1.dp, Color.White.copy(alpha = 0.18f), RoundedCornerShape(22.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(34.dp))
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = 7.dp, y = 7.dp)
                        .size(20.dp)
                        .background(AppPrimary, CircleShape)
                        .border(4.dp, AppDarkBg, CircleShape)
                )
            }
            Spacer(modifier = Modifier.width(18.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Khata Book",
                    style = MaterialTheme.typography.headlineMedium.copy(fontSize = 29.sp, fontWeight = FontWeight.Black),
                    color = TextWhite,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    "Financial Ledger",
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 16.sp, fontWeight = FontWeight.Bold),
                    color = AppPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        IconButton(
            onClick = { },
            modifier = Modifier
                .size(54.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(Color.White.copy(alpha = 0.07f))
                .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(22.dp))
        ) {
            BadgedBox(badge = { Badge(containerColor = Color(0xFFFF3045), modifier = Modifier.size(12.dp)) }) {
                Icon(Icons.Outlined.Notifications, contentDescription = "Notifications", tint = TextWhite, modifier = Modifier.size(26.dp))
            }
        }
    }
}

@Composable
fun KhataBalanceCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(34.dp),
        color = Color.Transparent,
        border = androidx.compose.foundation.BorderStroke(1.dp, AppPrimary.copy(alpha = 0.22f))
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        listOf(
                            Color(0xFF314D43).copy(alpha = 0.88f),
                            Color(0xFF102826).copy(alpha = 0.92f),
                            Color(0xFF061715).copy(alpha = 0.96f)
                        )
                    )
                )
                .padding(22.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = TextLight, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        "Running Balance",
                        style = MaterialTheme.typography.titleLarge.copy(fontSize = 17.sp, fontWeight = FontWeight.Bold),
                        color = TextLight
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "+₹40,250",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.headlineLarge.copy(fontSize = 48.sp, fontWeight = FontWeight.Black),
                        color = AppPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(AppPrimary.copy(alpha = 0.15f))
                            .border(1.dp, AppPrimary.copy(alpha = 0.38f), RoundedCornerShape(24.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, tint = AppPrimary, modifier = Modifier.size(34.dp))
                    }
                }
                Spacer(modifier = Modifier.height(28.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    SummarySmallCard("INCOME", "₹51,250", AppPrimary, Icons.Default.ArrowUpward, Modifier.weight(1f))
                    SummarySmallCard("EXPENSE", "₹11,000", Color(0xFFFF5D68), Icons.Default.ArrowDownward, Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun SummarySmallCard(label: String, value: String, color: Color, icon: ImageVector, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.height(104.dp),
        shape = RoundedCornerShape(24.dp),
        color = color.copy(alpha = 0.08f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.32f))
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.16f))
                        .border(1.dp, color.copy(alpha = 0.36f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(19.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(label, style = MaterialTheme.typography.labelLarge.copy(fontSize = 12.sp), color = TextLight)
            }
            Text(value, style = MaterialTheme.typography.headlineMedium.copy(fontSize = 27.sp, fontWeight = FontWeight.Black), color = color)
        }
    }
}

@Composable
fun KhataActionButtons() {
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Button(
            onClick = { },
            modifier = Modifier.weight(1f).height(76.dp),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AppPrimary, contentColor = Color.White),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White.copy(alpha = 0.26f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(28.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                "Income",
                fontWeight = FontWeight.Black,
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Button(
            onClick = { },
            modifier = Modifier.weight(1f).height(76.dp),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF3A49), contentColor = Color.White),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White.copy(alpha = 0.24f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(28.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                "Expense",
                fontWeight = FontWeight.Black,
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun KhataToolbar() {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Surface(
            modifier = Modifier.fillMaxWidth().height(58.dp),
            shape = RoundedCornerShape(24.dp),
            color = Color.White.copy(alpha = 0.04f),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.16f))
        ) {
            Row(
                modifier = Modifier.fillMaxSize().padding(7.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterChipCustom("All", Modifier.weight(1f), true)
                FilterChipCustom("Income", Modifier.weight(1f), false)
                FilterChipCustom("Expense", Modifier.weight(1f), false)
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            ToolbarIconButton(Icons.Default.CalendarToday, "Filter by date")
            Spacer(modifier = Modifier.width(12.dp))
            ToolbarIconButton(Icons.Default.FileDownload, "Download ledger")
        }
    }
}

@Composable
fun FilterChipCustom(label: String, modifier: Modifier = Modifier, active: Boolean) {
    Surface(
        modifier = modifier.fillMaxHeight(),
        shape = RoundedCornerShape(20.dp),
        color = if (active) Color.White.copy(alpha = 0.12f) else Color.Transparent,
        border = if (active) androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.18f)) else null
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                label,
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 14.sp, fontWeight = FontWeight.Black),
                color = if (active) TextWhite else TextGray
            )
        }
    }
}

@Composable
fun ToolbarIconButton(icon: ImageVector, contentDescription: String) {
    Surface(
        modifier = Modifier.size(50.dp),
        shape = CircleShape,
        color = Color.White.copy(alpha = 0.06f),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.14f))
    ) {
        IconButton(onClick = { }) {
            Icon(icon, contentDescription = contentDescription, tint = TextGray, modifier = Modifier.size(22.dp))
        }
    }
}

@Composable
fun KhataHistorySection() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("TRANSACTION HISTORY", style = MaterialTheme.typography.labelLarge.copy(fontSize = 15.sp, letterSpacing = 1.7.sp), color = TextLight)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = AppPrimary, modifier = Modifier.size(18.dp))
        }
        TransactionItem("Bulk Milk Delivery", "Jan 27", "MILK SALES", "+₹24,500", AppPrimary)
        TransactionItem("Feed Purchase - Cattle Mix", "Jan 26", "FEED", "-₹3,200", Color(0xFFFF4E58))
        TransactionItem("Individual Milk Sales", "Jan 26", "MILK SALES", "+₹8,750", AppPrimary)
        TransactionItem("Tractor Diesel Run", "Jan 25", "FUEL", "-₹1,800", Color(0xFFFF4E58))
        TransactionItem("Ghee Sale - 5kg", "Jan 24", "PRODUCTS", "+₹6,000", AppPrimary)
        TransactionItem("Veterinary Checkup", "Jan 24", "MEDICAL", "-₹1,500", Color(0xFFFF4E58))
    }
}

@Composable
fun TransactionItem(title: String, date: String, category: String, amount: String, color: Color) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        color = Color.Transparent,
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.22f))
    ) {
        Row(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        listOf(
                            color.copy(alpha = 0.18f),
                            Color(0xFF122521).copy(alpha = 0.82f),
                            Color(0xFF071A17).copy(alpha = 0.94f)
                        )
                    )
                )
                .padding(horizontal = 18.dp, vertical = 18.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(color.copy(alpha = 0.13f))
                        .border(1.dp, color.copy(alpha = 0.32f), RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        if (amount.startsWith("+")) Icons.Default.ArrowOutward else Icons.AutoMirrored.Filled.CallReceived,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.width(18.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        title,
                        style = MaterialTheme.typography.titleLarge.copy(fontSize = 17.sp, fontWeight = FontWeight.Black),
                        color = TextWhite,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            date,
                            style = MaterialTheme.typography.titleLarge.copy(fontSize = 14.sp, fontWeight = FontWeight.Bold),
                            color = TextGray,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(modifier = Modifier.size(4.dp).background(TextGray, CircleShape))
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = color.copy(alpha = 0.11f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.28f))
                        ) {
                            Text(
                                category,
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp),
                                color = color,
                                style = MaterialTheme.typography.labelLarge.copy(fontSize = 11.sp, fontWeight = FontWeight.Black)
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                amount,
                style = MaterialTheme.typography.headlineMedium.copy(fontSize = 24.sp, fontWeight = FontWeight.Black),
                color = color,
                maxLines = 1
            )
        }
    }
}
