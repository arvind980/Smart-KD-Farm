package com.smartkdfarm.app

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen() {
    AppBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            HomeTopBar()
            GreetingCard()
            OverviewSection()
            AlertsSection()
            MilkProductionSection()
            QuickActionsSection()
            RecentActivitySection()
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun HomeTopBar() {
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
                    "Smart KD Farm",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = TextWhite
                )
                Text(
                    "Dairy Management System",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppPrimary
                )
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
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
            IconButton(
                onClick = { },
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.05f))
            ) {
                Icon(Icons.Default.Settings, contentDescription = "Settings", tint = TextWhite)
            }
        }
    }
}

@Composable
fun GreetingCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = Color.White.copy(alpha = 0.05f),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(AppPrimary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.FlashOn, contentDescription = null, tint = AppPrimary)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        "Good Evening, Manager",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextWhite
                    )
                    Text(
                        "All systems running smoothly",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AppPrimary
                    )
                }
            }
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White.copy(alpha = 0.05f),
                border = androidx.compose.foundation.BorderStroke(1.dp, AppPrimary.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = AppPrimary, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "Pro",
                        color = AppPrimary,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

@Composable
fun OverviewSection() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("OVERVIEW", style = MaterialTheme.typography.labelLarge)
                Spacer(modifier = Modifier.width(4.dp))
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = AppPrimary, modifier = Modifier.size(14.dp))
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(8.dp).background(AppPrimary, CircleShape))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Live Data", style = MaterialTheme.typography.bodyMedium, color = AppPrimary)
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MetricCard(
                title = "250L",
                subtitle = "Total Milk",
                label = "12%",
                color = CardBlue,
                icon = Icons.Default.WaterDrop,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "15",
                subtitle = "Active Herd",
                label = "0",
                color = CardPurple,
                icon = Icons.Default.Pets,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "₹8.5K",
                subtitle = "Net Profit",
                label = "5%",
                color = AppPrimary,
                icon = Icons.AutoMirrored.Filled.TrendingUp,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun MetricCard(title: String, subtitle: String, label: String, color: Color, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier = Modifier) {
    GlassCard(
        modifier = modifier.height(180.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
            }
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = AppPrimary.copy(alpha = 0.1f),
                border = androidx.compose.foundation.BorderStroke(1.dp, AppPrimary.copy(alpha = 0.2f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, tint = AppPrimary, modifier = Modifier.size(10.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(label, color = AppPrimary, style = MaterialTheme.typography.labelLarge)
                }
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Text(title, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Black)
        Text(subtitle, style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Today vs Evening", style = MaterialTheme.typography.labelLarge, color = color, fontSize = 10.sp)
            Spacer(modifier = Modifier.weight(1f))
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = color.copy(alpha = 0.3f), modifier = Modifier.size(14.dp))
        }
    }
}

@Composable
fun AlertsSection() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("ALERTS", style = MaterialTheme.typography.labelLarge)
                Spacer(modifier = Modifier.width(4.dp))
                Icon(Icons.Default.Notifications, contentDescription = null, tint = CardRed, modifier = Modifier.size(14.dp))
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(6.dp).background(CardRed, CircleShape))
                Spacer(modifier = Modifier.width(6.dp))
                Text("2 Active", style = MaterialTheme.typography.bodyMedium, color = CardRed)
            }
        }
        AlertCard(
            title = "Deworming Due",
            message = "Animal #04 requires immediate attention",
            time = "2h ago",
            color = CardRed
        )
        AlertCard(
            title = "Low Stock Alert",
            message = "Makka feed - only 50kg remaining",
            time = "5h ago",
            color = CardYellow
        )
    }
}

@Composable
fun AlertCard(title: String, message: String, time: String, color: Color) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = color)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(title, style = MaterialTheme.typography.titleLarge, color = color)
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = color.copy(alpha = 0.1f)
                    ) {
                        Text(
                            "URGENT",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            color = color,
                            style = MaterialTheme.typography.labelLarge,
                            fontSize = 8.sp
                        )
                    }
                }
                Text(message, style = MaterialTheme.typography.bodyMedium)
                Text(time, style = MaterialTheme.typography.bodyMedium)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Icon(Icons.Default.Close, contentDescription = null, tint = TextGray, modifier = Modifier.size(24.dp))
            }
        }
    }
}

@Composable
fun MilkProductionSection() {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(AppPrimary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.BarChart, contentDescription = null, tint = AppPrimary)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Milk Production", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text("3-Month Lactation Forecast", style = MaterialTheme.typography.bodyMedium)
                }
            }
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = AppPrimary.copy(alpha = 0.1f),
                border = androidx.compose.foundation.BorderStroke(1.dp, AppPrimary.copy(alpha = 0.2f))
            ) {
                Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, tint = AppPrimary, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("+44.4%", color = AppPrimary, style = MaterialTheme.typography.labelLarge)
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text("vs Jan", modifier = Modifier.align(Alignment.End), style = MaterialTheme.typography.labelLarge)
        Spacer(modifier = Modifier.height(16.dp))
        
        // Legends
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            LegendItem("Actual", AppPrimary)
            LegendItem("Forecast", CardBlue)
            LegendItem("Target", TextGray)
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Simplified Graph
        Canvas(modifier = Modifier.fillMaxWidth().height(120.dp)) {
            val path = Path()
            path.moveTo(0f, size.height * 0.7f)
            path.quadraticTo(size.width * 0.3f, size.height * 0.5f, size.width * 0.6f, size.height * 0.4f)
            path.quadraticTo(size.width * 0.8f, size.height * 0.45f, size.width, size.height * 0.2f)
            
            drawPath(
                path = path,
                brush = Brush.verticalGradient(listOf(AppPrimary.copy(alpha = 0.5f), Color.Transparent))
            )
            drawPath(
                path = path,
                color = AppPrimary,
                style = Stroke(width = 3.dp.toPx())
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun").forEach {
                Text(it, style = MaterialTheme.typography.labelLarge)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            MetricSmall("AVG DAILY", "8.3L")
            MetricSmall("PEAK", "260L", color = AppPrimary)
            MetricSmall("EFFICIENCY", "94%", color = CardBlue)
        }
    }
}

@Composable
fun LegendItem(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(8.dp).background(color, CircleShape))
        Spacer(modifier = Modifier.width(6.dp))
        Text(label, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
fun MetricSmall(label: String, value: String, color: Color = TextWhite) {
    GlassCard(modifier = Modifier.width(100.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, style = MaterialTheme.typography.labelLarge, fontSize = 9.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.titleLarge, color = color, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
fun QuickActionsSection() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("QUICK ACTIONS", style = MaterialTheme.typography.labelLarge)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("View All", style = MaterialTheme.typography.bodyMedium, color = AppPrimary)
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = AppPrimary, modifier = Modifier.size(16.dp))
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            QuickActionItem("Quick Doodh Entry", "Log milk collection", CardBlue, Icons.Default.WaterDrop, Modifier.weight(1f))
            QuickActionItem("Quick Kharcha", "Add expense", CardYellow, Icons.AutoMirrored.Filled.ReceiptLong, Modifier.weight(1f))
            QuickActionItem("Batch Feed", "Production log", CardPurple, Icons.Default.Inventory2, Modifier.weight(1f))
        }
    }
}

@Composable
fun QuickActionItem(title: String, subtitle: String, color: Color, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier = Modifier) {
    GlassCard(
        modifier = modifier.height(140.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(color.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = color)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(title, style = MaterialTheme.typography.labelLarge, color = TextWhite)
        Text(subtitle, style = MaterialTheme.typography.labelLarge, fontSize = 8.sp)
    }
}

@Composable
fun RecentActivitySection() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("RECENT ACTIVITY", style = MaterialTheme.typography.labelLarge)
                Spacer(modifier = Modifier.width(6.dp))
                Icon(Icons.Default.AccessTime, contentDescription = null, tint = TextGray, modifier = Modifier.size(14.dp))
            }
            Surface(shape = RoundedCornerShape(12.dp), color = Color.White.copy(alpha = 0.05f)) {
                Text("Today", modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp), style = MaterialTheme.typography.labelLarge)
            }
        }
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            ActivityItem("Evening milk collected", "6:30 PM", "125L", AppPrimary)
            HorizontalDivider(color = Color.White.copy(alpha = 0.05f), modifier = Modifier.padding(vertical = 12.dp))
            ActivityItem("Feed distributed", "2:00 PM", "50kg", CardBlue)
            HorizontalDivider(color = Color.White.copy(alpha = 0.05f), modifier = Modifier.padding(vertical = 12.dp))
            ActivityItem("Morning milk collected", "6:00 AM", "125L", AppPrimary)
        }
    }
}

@Composable
fun ActivityItem(title: String, time: String, value: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(10.dp).background(color, CircleShape))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, style = MaterialTheme.typography.bodyLarge)
                Text(time, style = MaterialTheme.typography.bodyMedium)
            }
        }
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = color.copy(alpha = 0.1f)
        ) {
            Text(
                value,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                color = color,
                style = MaterialTheme.typography.labelLarge,
                fontSize = 14.sp
            )
        }
    }
}
