package com.smartkdfarm.app.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CallReceived
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowOutward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
internal fun DashboardKhataScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .padding(top = 24.dp, bottom = 140.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        KhataHeader()
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            KhataBalancePanel()
            KhataActionRow()
            KhataFilterPanel()
            KhataSectionTitle()
            khataTransactions.forEach { item ->
                KhataTransactionRow(item)
            }
        }
    }
}

@Composable
private fun KhataHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MintBright.copy(alpha = 0.22f),
                border = BorderStroke(1.dp, MintBright.copy(alpha = 0.42f)),
                modifier = Modifier.size(80.dp),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.AutoAwesome,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(40.dp),
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(8.dp)
                            .size(16.dp)
                            .background(Mint, CircleShape)
                            .border(3.dp, DashboardBg, CircleShape),
                    )
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Khata Book",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 30.sp,
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "Financial Ledger",
                    color = Mint,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = Color.White.copy(alpha = 0.07f),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
            modifier = Modifier.size(58.dp),
        ) {
            IconButton(onClick = {}) {
                BadgedBox(badge = { Badge(containerColor = Red, modifier = Modifier.size(12.dp)) }) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "Notifications",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun KhataBalancePanel() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(34.dp),
        color = Color.Transparent,
        border = BorderStroke(1.dp, Mint.copy(alpha = 0.28f)),
        shadowElevation = 12.dp,
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        listOf(
                            Color(0xFF314D43).copy(alpha = 0.9f),
                            GlassDark.copy(alpha = 0.96f),
                            Color(0xFF061A16),
                        )
                    )
                )
                .padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.AccountBalanceWallet,
                    contentDescription = null,
                    tint = WhiteSoft,
                    modifier = Modifier.size(24.dp),
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Running Balance",
                    color = WhiteSoft,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                    ),
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "+₹40,250",
                    modifier = Modifier.weight(1f),
                    color = Mint,
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 43.sp,
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.width(12.dp))
                DashboardGlowIcon(
                    icon = Icons.AutoMirrored.Filled.TrendingUp,
                    tint = Mint,
                    glow = Mint,
                    modifier = Modifier.size(72.dp),
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                KhataSummaryCard("INCOME", "₹51,250", Mint, Icons.Filled.ArrowUpward, Modifier.weight(1f))
                KhataSummaryCard("EXPENSE", "₹11,000", Red, Icons.Filled.ArrowDownward, Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun KhataSummaryCard(
    label: String,
    value: String,
    accent: Color,
    icon: ImageVector,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.height(112.dp),
        shape = RoundedCornerShape(24.dp),
        color = accent.copy(alpha = 0.08f),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.32f)),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(accent.copy(alpha = 0.16f))
                        .border(1.dp, accent.copy(alpha = 0.36f), CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(19.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = label,
                    color = WhiteSoft,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 11.sp,
                        letterSpacing = 1.sp,
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Text(
                text = value,
                color = accent,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 25.sp,
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun KhataActionRow() {
    Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
        KhataActionButton("Income", Icons.Filled.Add, MintBright, Modifier.weight(1f))
        KhataActionButton("Expense", Icons.Filled.Remove, Color(0xFFFF3A49), Modifier.weight(1f))
    }
}

@Composable
private fun KhataActionButton(
    label: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = {},
        modifier = modifier.height(76.dp),
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color, contentColor = Color.White),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp),
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(Color.White.copy(alpha = 0.24f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(27.dp))
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                fontSize = 19.sp,
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun KhataFilterPanel() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            shape = RoundedCornerShape(24.dp),
            color = Color.White.copy(alpha = 0.04f),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.16f)),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(7.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                KhataFilterChip("All", true, Modifier.weight(1f))
                KhataFilterChip("Income", false, Modifier.weight(1f))
                KhataFilterChip("Expense", false, Modifier.weight(1f))
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            KhataRoundIcon(Icons.Filled.CalendarToday)
            Spacer(modifier = Modifier.width(12.dp))
            KhataRoundIcon(Icons.Filled.FileDownload)
        }
    }
}

@Composable
private fun KhataFilterChip(label: String, selected: Boolean, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxHeight(),
        shape = RoundedCornerShape(20.dp),
        color = if (selected) Color.White.copy(alpha = 0.12f) else Color.Transparent,
        border = if (selected) BorderStroke(1.dp, Color.White.copy(alpha = 0.18f)) else null,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label,
                color = if (selected) Color.White else WhiteSoft,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp,
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun KhataRoundIcon(icon: ImageVector) {
    Surface(
        modifier = Modifier.size(52.dp),
        shape = CircleShape,
        color = Color.White.copy(alpha = 0.06f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.14f)),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = null, tint = WhiteSoft, modifier = Modifier.size(23.dp))
        }
    }
}

@Composable
private fun KhataSectionTitle() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "TRANSACTION HISTORY",
            color = Color.White.copy(alpha = 0.84f),
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                fontSize = 17.sp,
                letterSpacing = 1.sp,
            ),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Icon(
            imageVector = Icons.Filled.AutoAwesome,
            contentDescription = null,
            tint = Mint,
            modifier = Modifier.size(18.dp),
        )
    }
}

@Composable
private fun KhataTransactionRow(item: KhataTransactionUi) {
    val accent = if (item.income) Mint else Red
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = Color.Transparent,
        border = BorderStroke(1.dp, accent.copy(alpha = 0.24f)),
        shadowElevation = 8.dp,
    ) {
        Row(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        listOf(
                            accent.copy(alpha = 0.17f),
                            GlassDark.copy(alpha = 0.9f),
                            Color(0xFF06251D),
                        )
                    )
                )
                .padding(horizontal = 16.dp, vertical = 17.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(accent.copy(alpha = 0.12f))
                    .border(1.dp, accent.copy(alpha = 0.32f), RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = if (item.income) Icons.Filled.ArrowOutward else Icons.AutoMirrored.Filled.CallReceived,
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(28.dp),
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.date,
                        color = WhiteSoft,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                        ),
                        maxLines = 1,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(modifier = Modifier.size(4.dp).background(WhiteSoft, CircleShape))
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = accent.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, accent.copy(alpha = 0.28f)),
                    ) {
                        Text(
                            text = item.category,
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp),
                            color = accent,
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 11.sp,
                            ),
                            maxLines = 1,
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = item.amount,
                color = accent,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 23.sp,
                ),
                maxLines = 1,
            )
        }
    }
}

private data class KhataTransactionUi(
    val title: String,
    val date: String,
    val category: String,
    val amount: String,
    val income: Boolean,
)

private val khataTransactions = listOf(
    KhataTransactionUi("Bulk Milk Delivery", "Jan 27", "MILK SALES", "+₹24,500", true),
    KhataTransactionUi("Feed Purchase - Cattle Mix", "Jan 26", "FEED", "-₹3,200", false),
    KhataTransactionUi("Individual Milk Sales", "Jan 26", "MILK SALES", "+₹8,750", true),
    KhataTransactionUi("Tractor Diesel Run", "Jan 25", "FUEL", "-₹1,800", false),
    KhataTransactionUi("Ghee Sale - 5kg", "Jan 24", "PRODUCTS", "+₹6,000", true),
    KhataTransactionUi("Veterinary Checkup", "Jan 24", "MEDICAL", "-₹1,500", false),
)
