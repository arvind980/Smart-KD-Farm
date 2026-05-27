package com.smartkdfarm.app.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
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
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun DashboardHomeScreen(
    onBackToLogin: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .padding(top = 24.dp, bottom = 140.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        DashboardHeader(
            title = "Smart KD Farm",
            subtitle = "Dairy Management System",
            showSettings = true,
            onSettingsClick = onBackToLogin,
        )
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            GreetingBanner()
            SectionHeader("OVERVIEW", "Live Data", Mint)
            OverviewRow()
            SectionHeader("ALERTS", "2 Active", Red)
            AlertsColumn()
            ProductionCard()
            SectionHeader("QUICK ACTIONS", "View All", Mint)
            QuickActionsRow()
            SectionHeader("RECENT ACTIVITY", "Today", WhiteSoft)
            RecentActivityCard()
        }
    }
}

@Composable
private fun GreetingBanner() {
    Surface(
        shape = RoundedCornerShape(30.dp),
        color = GlassDark.copy(alpha = 0.92f),
        border = BorderStroke(1.dp, GlassStroke.copy(alpha = 0.9f)),
        shadowElevation = 10.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 18.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            DashboardGlowIcon(
                icon = Icons.Filled.FlashOn,
                tint = Mint,
                glow = Mint,
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    "Good Evening, Admin",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    "All systems running smoothly",
                    color = Mint,
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 13.sp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    trailing: String,
    trailingColor: Color,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            color = Color.White.copy(alpha = 0.84f),
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp,
                fontSize = 17.sp,
            ),
        )
        Text(
            text = trailing,
            color = trailingColor,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
            ),
        )
    }
}

@Composable
private fun OverviewRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        OverviewCard(Blue, Icons.Filled.WaterDrop, "12%", "250L", "Total Milk", "Today vs Evening")
        OverviewCard(Purple, Icons.Filled.Pets, "0", "15", "Active Herd", "All Healthy")
        OverviewCard(Mint, Icons.AutoMirrored.Filled.TrendingUp, "+₹809", "₹8.5K", "Net Profit", "This Month")
    }
}

@Composable
private fun OverviewCard(
    accent: Color,
    icon: ImageVector,
    badge: String,
    value: String,
    title: String,
    footer: String,
) {
    Surface(
        shape = RoundedCornerShape(32.dp),
        color = GlassDark.copy(alpha = 0.95f),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.18f)),
        shadowElevation = 10.dp,
        modifier = Modifier.width(230.dp),
    ) {
        Column(
            modifier = Modifier.padding(17.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                DashboardGlowIcon(icon = icon, tint = accent, glow = accent)
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = accent.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, accent.copy(alpha = 0.28f)),
                ) {
                    Text(
                        text = badge,
                        color = accent,
                        modifier = Modifier.padding(horizontal = 11.dp, vertical = 7.dp),
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                        ),
                    )
                }
            }
            Text(
                text = value,
                color = Color.White,
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 34.sp,
                ),
            )
            Text(
                text = title,
                color = WhiteSoft,
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 17.sp),
            )
            HorizontalDivider(color = accent.copy(alpha = 0.18f))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = footer,
                    color = accent,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                    ),
                    modifier = Modifier.weight(1f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Surface(
                    shape = CircleShape,
                    color = accent.copy(alpha = 0.10f),
                    modifier = Modifier.size(38.dp),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("›", color = accent, fontSize = 21.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun AlertsColumn() {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        AlertCard(Red, "Deworming Due", "URGENT", "Animal #04 requires immediate attention", "2h ago")
        AlertCard(Yellow, "Low Stock Alert", "", "Makka feed - only 50kg remaining", "5h ago")
    }
}

@Composable
private fun AlertCard(
    accent: Color,
    title: String,
    badge: String,
    message: String,
    time: String,
) {
    Surface(
        shape = RoundedCornerShape(30.dp),
        color = GlassDark.copy(alpha = 0.96f),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.36f)),
        shadowElevation = 12.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 18.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.Top,
        ) {
            DashboardGlowIcon(icon = Icons.Filled.ErrorOutline, tint = accent, glow = accent)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        color = accent,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                        ),
                    )
                    if (badge.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = accent.copy(alpha = 0.10f),
                            border = BorderStroke(1.dp, accent.copy(alpha = 0.35f)),
                        ) {
                            Text(
                                badge,
                                color = accent,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            )
                        }
                    }
                }
                Text(
                    text = message,
                    color = Color.White.copy(alpha = 0.75f),
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 15.sp),
                )
                Text(
                    text = time,
                    color = Color.White.copy(alpha = 0.38f),
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                )
            }
        }
    }
}

@Composable
private fun ProductionCard() {
    val monthLabels = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun")
    val bars = listOf(0.64f, 0.71f, 0.79f, 0.91f, 0.87f, 0.95f)
    Surface(
        shape = RoundedCornerShape(34.dp),
        color = GlassDark.copy(alpha = 0.95f),
        border = BorderStroke(1.dp, GlassStroke.copy(alpha = 0.9f)),
        shadowElevation = 12.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    DashboardGlowIcon(icon = Icons.Filled.BarChart, tint = Mint, glow = Mint)
                    Column {
                        Text(
                            "Milk Production",
                            color = Color.White,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 22.sp,
                            ),
                        )
                        Text(
                            "3-Month Lactation Forecast",
                            color = WhiteSoft,
                            style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp),
                        )
                    }
                }
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Mint.copy(alpha = 0.08f),
                    border = BorderStroke(1.dp, Mint.copy(alpha = 0.25f)),
                ) {
                    Text(
                        "+44.4%",
                        color = Mint,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                        ),
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(22.dp)) {
                LegendDot(Mint, "Actual")
                LegendDot(Blue, "Forecast")
                LegendDot(Color.White.copy(alpha = 0.5f), "Target")
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Bottom,
            ) {
                bars.forEach { value ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(140.dp),
                        contentAlignment = Alignment.BottomCenter,
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height((140 * value).dp)
                                .background(
                                    color = Mint.copy(alpha = 0.22f),
                                    shape = RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp),
                                )
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                monthLabels.forEach { month ->
                    Text(
                        text = month,
                        color = Color.White.copy(alpha = 0.42f),
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                    )
                }
            }

            HorizontalDivider(color = Color.White.copy(alpha = 0.08f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                MetricMiniCard("AVG DAILY", "8.3L", Color.White)
                MetricMiniCard("PEAK", "260L", Mint)
                MetricMiniCard("EFFICIENCY", "94%", Blue)
            }
        }
    }
}

@Composable
private fun LegendDot(color: Color, label: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(12.dp).background(color, CircleShape))
        Text(text = label, color = Color.White.copy(alpha = 0.72f), style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp))
    }
}

@Composable
private fun RowScope.MetricMiniCard(
    label: String,
    value: String,
    valueColor: Color,
) {
    Surface(
        modifier = Modifier.weight(1f),
        shape = RoundedCornerShape(24.dp),
        color = Color.White.copy(alpha = 0.04f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.07f)),
    ) {
        Column(
            modifier = Modifier.padding(vertical = 18.dp, horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = label,
                color = Color.White.copy(alpha = 0.46f),
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                ),
            )
            Text(
                text = value,
                color = valueColor,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 19.sp,
                ),
            )
        }
    }
}

@Composable
private fun QuickActionsRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        QuickActionCard("Quick Doodh\nEntry", "Log milk collection", Blue, Icons.Filled.WaterDrop)
        QuickActionCard("Quick Kharcha", "Add expense", Yellow, Icons.AutoMirrored.Filled.ReceiptLong)
        QuickActionCard("Batch Feed", "Production log", Purple, Icons.Filled.Inventory2)
    }
}

@Composable
private fun QuickActionCard(
    title: String,
    subtitle: String,
    accent: Color,
    icon: ImageVector,
) {
    Surface(
        shape = RoundedCornerShape(30.dp),
        color = GlassDark.copy(alpha = 0.95f),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.18f)),
        modifier = Modifier.width(190.dp),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            DashboardGlowIcon(icon = icon, tint = accent, glow = accent)
            Text(
                text = title,
                color = Color.White,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = subtitle,
                color = Color.White.copy(alpha = 0.42f),
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
            )
        }
    }
}

@Composable
private fun RecentActivityCard() {
    Surface(
        shape = RoundedCornerShape(30.dp),
        color = GlassDark.copy(alpha = 0.95f),
        border = BorderStroke(1.dp, GlassStroke.copy(alpha = 0.85f)),
    ) {
        Column {
            RecentRow("Evening milk collected", "6:30 PM", "125L", Mint, Mint)
            HorizontalDivider(color = Color.White.copy(alpha = 0.06f))
            RecentRow("Feed distributed", "2:00 PM", "50kg", Blue, Blue)
            HorizontalDivider(color = Color.White.copy(alpha = 0.06f))
            RecentRow("Morning milk collected", "6:00 AM", "125L", Mint, Mint)
        }
    }
}

@Composable
private fun RecentRow(
    title: String,
    time: String,
    badge: String,
    dotColor: Color,
    badgeColor: Color,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(modifier = Modifier.size(12.dp).background(dotColor, CircleShape))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                    ),
                )
                Text(
                    text = time,
                    color = Color.White.copy(alpha = 0.42f),
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                )
            }
        }
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = badgeColor.copy(alpha = 0.10f),
            border = BorderStroke(1.dp, badgeColor.copy(alpha = 0.28f)),
        ) {
            Text(
                text = badge,
                color = badgeColor,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                ),
            )
        }
    }
}
