package com.smartkdfarm.app

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun KhataScreen() {
    AppBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            KhataTopBar()
            KhataBalanceCard()
            KhataActionButtons()
            KhataHistorySection()
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun KhataTopBar() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(AppPrimary),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.Black)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    "Khata Book",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = TextWhite
                )
                Text(
                    "Financial Ledger",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppPrimary
                )
            }
        }
        IconButton(
            onClick = { },
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White.copy(alpha = 0.05f))
        ) {
            BadgedBox(badge = { Badge(containerColor = Color.Red, modifier = Modifier.size(6.dp)) }) {
                Icon(Icons.Outlined.Notifications, contentDescription = "Notifications", tint = TextWhite)
            }
        }
    }
}

@Composable
fun KhataBalanceCard() {
    GlassCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = TextGray, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Running Balance", style = MaterialTheme.typography.bodyMedium)
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("+₹40,250", style = MaterialTheme.typography.headlineLarge.copy(fontSize = 38.sp), fontWeight = FontWeight.Black, color = AppPrimary)
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(AppPrimary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, tint = AppPrimary, modifier = Modifier.size(28.dp))
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            SummarySmallCard("INCOME", "₹51,250", AppPrimary, Icons.Default.ArrowUpward, Modifier.weight(1f))
            SummarySmallCard("EXPENSE", "₹11,000", CardRed, Icons.Default.ArrowDownward, Modifier.weight(1f))
        }
    }
}

@Composable
fun SummarySmallCard(label: String, value: String, color: Color, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = color.copy(alpha = 0.05f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(label, style = MaterialTheme.typography.labelLarge, color = TextGray, fontSize = 10.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black))
        }
    }
}

@Composable
fun KhataActionButtons() {
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Button(
            onClick = { },
            modifier = Modifier.weight(1f).height(64.dp),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AppPrimary, contentColor = Color.Black)
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Income", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
        }
        Button(
            onClick = { },
            modifier = Modifier.weight(1f).height(64.dp),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CardRed, contentColor = Color.White)
        ) {
            Icon(Icons.Default.Remove, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Expense", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
fun KhataHistorySection() {
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("TRANSACTION HISTORY", style = MaterialTheme.typography.labelLarge)
                Spacer(modifier = Modifier.width(6.dp))
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = AppPrimary, modifier = Modifier.size(14.dp))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Icon(Icons.Default.CalendarToday, contentDescription = null, tint = TextGray, modifier = Modifier.size(20.dp))
                Icon(Icons.Default.FileDownload, contentDescription = null, tint = TextGray, modifier = Modifier.size(20.dp))
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            FilterChipCustom("All", "", true)
            FilterChipCustom("Income", "", false)
            FilterChipCustom("Expense", "", false)
        }
        TransactionItem("Bulk Milk Delivery", "Jan 27", "MILK SALES", "+₹24,500", AppPrimary)
        TransactionItem("Feed Purchase - Cattle Mix", "Jan 26", "FEED", "-₹3,200", CardRed)
        TransactionItem("Individual Milk Sales", "Jan 26", "MILK SALES", "+₹8,750", AppPrimary)
        TransactionItem("Tractor Diesel Run", "Jan 25", "FUEL", "-₹1,800", CardRed)
        TransactionItem("Ghee Sale - 5kg", "Jan 24", "PRODUCTS", "+₹6,000", AppPrimary)
        TransactionItem("Veterinary Checkup", "Jan 24", "MEDICAL", "-₹1,500", CardRed)
    }
}

@Composable
fun TransactionItem(title: String, date: String, category: String, amount: String, color: Color) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(color.copy(alpha = 0.05f))
                        .border(1.dp, color.copy(alpha = 0.2f), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        if (amount.startsWith("+")) Icons.AutoMirrored.Filled.ArrowForward else Icons.Default.CallReceived,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(date, style = MaterialTheme.typography.labelLarge, fontSize = 10.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(modifier = Modifier.size(4.dp).background(TextGray, CircleShape))
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = color.copy(alpha = 0.1f)
                        ) {
                            Text(
                                category,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                color = color,
                                style = MaterialTheme.typography.labelLarge,
                                fontSize = 8.sp
                            )
                        }
                    }
                }
            }
            Text(amount, style = MaterialTheme.typography.titleLarge, color = color, fontWeight = FontWeight.Black)
        }
    }
}
