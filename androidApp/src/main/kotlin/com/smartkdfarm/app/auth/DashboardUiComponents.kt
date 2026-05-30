package com.smartkdfarm.app.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Pets
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

internal enum class DashboardTab(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
) {
    HOME("Home", Icons.Filled.Home, Icons.Outlined.Home),
    LIVESTOCK("Livestock", Icons.Filled.Pets, Icons.Outlined.Pets),
    STAFF("Staff", Icons.Filled.People, Icons.Outlined.People),
    KHATA("Khata", Icons.Filled.Book, Icons.Outlined.Book),
}

@Composable
internal fun DashboardHeader(
    title: String,
    subtitle: String,
    showSettings: Boolean,
    onSettingsClick: () -> Unit,
    leadingIcon: ImageVector = Icons.Outlined.AutoAwesome,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MintBright.copy(alpha = 0.18f),
                border = BorderStroke(1.dp, MintBright.copy(alpha = 0.45f)),
                modifier = Modifier.size(80.dp),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(40.dp),
                    )
                }
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = title,
                    color = Color.White,
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 24.sp,
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = subtitle,
                    color = Mint,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
            DashboardIconChip(Icons.Outlined.Notifications, showDot = true)
            if (showSettings) {
                DashboardIconChip(Icons.Filled.Settings, onClick = onSettingsClick)
            }
        }
    }
}

@Composable
internal fun DashboardIconChip(
    icon: ImageVector,
    showDot: Boolean = false,
    onClick: (() -> Unit)? = null,
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color.White.copy(alpha = 0.06f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
        modifier = Modifier.size(70.dp),
        onClick = { onClick?.invoke() },
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.92f),
                modifier = Modifier.size(34.dp),
            )
            if (showDot) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 13.dp, end = 13.dp)
                        .size(12.dp)
                        .background(Color(0xFFFF3E3E), CircleShape)
                )
            }
        }
    }
}

@Composable
internal fun DashboardGlowIcon(
    icon: ImageVector,
    tint: Color,
    glow: Color,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = glow.copy(alpha = 0.08f),
        border = BorderStroke(1.dp, glow.copy(alpha = 0.22f)),
        modifier = modifier.size(66.dp),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(34.dp),
            )
        }
    }
}

@Composable
internal fun BottomDashboardNav(
    selectedTab: DashboardTab,
    onSelectTab: (DashboardTab) -> Unit,
    canOpenTab: (DashboardTab) -> Boolean = { true },
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxWidth()) {
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(32.dp),
            color = Color(0xFF0A3629),
            border = BorderStroke(1.dp, Color(0xFF0C6A51)),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 18.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                BottomNavItem(DashboardTab.HOME, selectedTab == DashboardTab.HOME, canOpenTab(DashboardTab.HOME)) { onSelectTab(DashboardTab.HOME) }
                BottomNavItem(DashboardTab.LIVESTOCK, selectedTab == DashboardTab.LIVESTOCK, canOpenTab(DashboardTab.LIVESTOCK)) { onSelectTab(DashboardTab.LIVESTOCK) }
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(56.dp))
                BottomNavItem(DashboardTab.STAFF, selectedTab == DashboardTab.STAFF, canOpenTab(DashboardTab.STAFF)) { onSelectTab(DashboardTab.STAFF) }
                BottomNavItem(DashboardTab.KHATA, selectedTab == DashboardTab.KHATA, canOpenTab(DashboardTab.KHATA)) { onSelectTab(DashboardTab.KHATA) }
            }
        }

        Surface(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .size(74.dp),
            shape = RoundedCornerShape(24.dp),
            color = MintBright,
            shadowElevation = 12.dp,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(36.dp),
                )
            }
        }
    }
}

@Composable
private fun BottomNavItem(
    tab: DashboardTab,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val contentColor = if (selected) Mint else Color.White.copy(alpha = 0.6f)
    val background = if (selected) Mint.copy(alpha = 0.12f) else Color.Transparent

    Surface(
        shape = RoundedCornerShape(24.dp),
        color = background,
        border = if (selected) BorderStroke(1.dp, Mint.copy(alpha = 0.25f)) else null,
        onClick = { if (enabled) onClick() },
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 11.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Icon(
                imageVector = if (enabled) {
                    if (selected) tab.selectedIcon else tab.unselectedIcon
                } else {
                    Icons.Filled.Lock
                },
                contentDescription = null,
                tint = if (enabled) contentColor else Color.White.copy(alpha = 0.28f),
                modifier = Modifier.size(26.dp),
            )
            Text(
                text = tab.label,
                color = if (enabled) contentColor else Color.White.copy(alpha = 0.28f),
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 12.sp,
                ),
            )
        }
    }
}
