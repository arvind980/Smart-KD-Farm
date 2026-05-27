package com.smartkdfarm.app.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ShowChart
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartkdfarm.app.AppBackground
import com.smartkdfarm.app.presentation.admin.AdminViewModel
import com.smartkdfarm.app.presentation.auth.AuthViewModel
import com.smartkdfarm.app.presentation.livestock.LivestockViewModel
import com.smartkdfarm.app.presentation.operations.OperationsViewModel

private val PremiumGreen = Color(0xFF7EB55D)
private val GlassWhite = Color(0xDDFFFFFF)
private val TextDark = Color(0xFF1B2B1B)
private val TextLight = Color(0xFF5A6A5A)
private val SoftWhite = Color(0xFFF8FAF8)
private val DashboardBg = Color(0xFF032E23)
private val GlassDark = Color(0xFF17352E)
private val GlassStroke = Color(0xFF295446)
private val Mint = Color(0xFF08D6A0)
private val MintBright = Color(0xFF11C989)
private val Blue = Color(0xFF4D91FF)
private val Purple = Color(0xFFAF62FF)
private val Red = Color(0xFFFF5C5C)
private val Yellow = Color(0xFFE2B321)
private val WhiteSoft = Color(0xFFB4BDB9)

private enum class DashboardTab(
    val label: String,
    val icon: ImageVector,
) {
    HOME("Home", Icons.Outlined.Home),
    LIVESTOCK("Livestock", Icons.Outlined.Pets),
    STAFF("Staff", Icons.Outlined.Groups),
    KHATA("Khata", Icons.Outlined.Book),
}

@Composable
@Suppress("UNUSED_PARAMETER")
fun AuthAppScreen(
    viewModel: AuthViewModel,
    livestockViewModel: LivestockViewModel,
    adminViewModel: AdminViewModel,
    operationsViewModel: OperationsViewModel,
) {
    var currentRoute by remember { mutableStateOf(AuthRoute.LOGIN) }

    AppBackground {
        Scaffold(containerColor = Color.Transparent) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .navigationBarsPadding(),
                contentAlignment = Alignment.Center,
            ) {
                when (currentRoute) {
                    AuthRoute.LOGIN -> AuthEntryScreen(
                        onOpenDashboard = { currentRoute = AuthRoute.DASHBOARD },
                        onOpenFarmRegistration = { currentRoute = AuthRoute.ADD_FARM },
                    )
                    AuthRoute.ADD_FARM -> FarmRegistrationScreen(
                        onBack = { currentRoute = AuthRoute.LOGIN },
                    )
                    AuthRoute.DASHBOARD -> DashboardScreen(
                        onBackToLogin = { currentRoute = AuthRoute.LOGIN },
                    )
                }
            }
        }
    }
}

@Composable
private fun AuthEntryScreen(
    onOpenDashboard: () -> Unit,
    onOpenFarmRegistration: () -> Unit,
) {
    var mobileNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .widthIn(max = 420.dp)
            .statusBarsPadding()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "Welcome Back!",
                style = TextStyle(
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = 0.5.sp,
                    shadow = Shadow(
                        color = Color.Black.copy(alpha = 0.2f),
                        offset = Offset(0f, 4f),
                        blurRadius = 8f,
                    ),
                ),
            )
            Text(
                text = "Sign in screen UI only",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color.White.copy(alpha = 0.9f),
                    fontWeight = FontWeight.Medium,
                ),
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        DairyCard {
            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Sign In",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextDark,
                        ),
                    )
                    HorizontalDivider(
                        thickness = 2.dp,
                        color = PremiumGreen.copy(alpha = 0.3f),
                        modifier = Modifier.width(40.dp),
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    DairyTextField(
                        value = mobileNumber,
                        onValueChange = { mobileNumber = it },
                        label = "Mobile Number",
                        placeholder = "Mobile Number",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    )
                    DairyTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = "Password",
                        placeholder = "Password",
                        visualTransformation = PasswordVisualTransformation(),
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                PremiumButton(
                    text = "Login",
                    onClick = onOpenDashboard,
                    modifier = Modifier.fillMaxWidth(),
                )

                Text(
                    text = "Login functionality removed.",
                    color = TextLight,
                    style = MaterialTheme.typography.bodySmall,
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("New here?", color = TextLight, style = MaterialTheme.typography.bodyMedium)
                    TextButton(onClick = onOpenFarmRegistration) {
                        Text(
                            "Create an Account",
                            color = PremiumGreen,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(48.dp))
    }
}

@Composable
private fun DashboardScreen(
    onBackToLogin: () -> Unit,
) {
    var selectedTab by remember { mutableStateOf(DashboardTab.HOME) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DashboardBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 24.dp, bottom = 140.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            DashboardHeader(onBackToLogin = onBackToLogin)
            when (selectedTab) {
                DashboardTab.HOME -> {
                    GreetingBanner()
                    SectionHeader(
                        title = "OVERVIEW",
                        trailing = "Live Data",
                        trailingColor = Mint,
                    )
                    OverviewRow()
                    SectionHeader(
                        title = "ALERTS",
                        trailing = "2 Active",
                        trailingColor = Color(0xFFFF6464),
                    )
                    AlertsColumn()
                    ProductionCard()
                    SectionHeader(
                        title = "QUICK ACTIONS",
                        trailing = "View All",
                        trailingColor = Mint,
                    )
                    QuickActionsRow()
                    SectionHeader(
                        title = "RECENT ACTIVITY",
                        trailing = "Today",
                        trailingColor = WhiteSoft,
                    )
                    RecentActivityCard()
                }
                DashboardTab.LIVESTOCK -> DashboardPlaceholderPanel(
                    title = "Livestock",
                    subtitle = "Pashu module preview",
                    accent = Purple,
                    icon = Icons.Outlined.Pets,
                )
                DashboardTab.STAFF -> DashboardPlaceholderPanel(
                    title = "Staff",
                    subtitle = "Team access preview",
                    accent = Mint,
                    icon = Icons.Outlined.Groups,
                )
                DashboardTab.KHATA -> DashboardPlaceholderPanel(
                    title = "Khata",
                    subtitle = "Ledger preview",
                    accent = Blue,
                    icon = Icons.Outlined.Book,
                )
            }
        }

        BottomDashboardNav(
            selectedTab = selectedTab,
            onSelectTab = { selectedTab = it },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun FarmRegistrationScreen(
    onBack: () -> Unit,
) {
    var farmName by remember { mutableStateOf("Smart KD Farm") }
    var adminName by remember { mutableStateOf("") }
    var adminPhone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var village by remember { mutableStateOf("") }
    var district by remember { mutableStateOf("") }
    var stateName by remember { mutableStateOf("Punjab") }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(11.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PremiumIconButton(onClick = onBack)
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Join Us",
                style = TextStyle(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    shadow = Shadow(
                        Color.Black.copy(alpha = 0.15f),
                        offset = Offset(0f, 2f),
                        blurRadius = 4f,
                    ),
                ),
            )
            Spacer(modifier = Modifier.weight(1f))
            Box(modifier = Modifier.size(44.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            DairyCard(modifier = Modifier.widthIn(max = 480.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    DairyTextField(
                        value = farmName,
                        onValueChange = { farmName = it },
                        label = "Farm Name",
                        placeholder = "Farm Name",
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(modifier = Modifier.weight(1f)) {
                            DairyTextField(
                                value = village,
                                onValueChange = { village = it },
                                label = "Village",
                                placeholder = "Village",
                            )
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            DairyTextField(
                                value = district,
                                onValueChange = { district = it },
                                label = "District",
                                placeholder = "District",
                            )
                        }
                    }

                    DairyTextField(
                        value = stateName,
                        onValueChange = { stateName = it },
                        label = "State",
                        placeholder = "State",
                    )

                    DairyTextField(
                        value = adminName,
                        onValueChange = { adminName = it },
                        label = "Admin Name",
                        placeholder = "Admin",
                    )
                    DairyTextField(
                        value = adminPhone,
                        onValueChange = { adminPhone = it },
                        label = "Mobile No.",
                        placeholder = "Mobile No.",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    )
                    DairyTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = "Password",
                        placeholder = "Password",
                        visualTransformation = PasswordVisualTransformation(),
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    PremiumButton(
                        text = "Register Farm",
                        onClick = {},
                        modifier = Modifier.fillMaxWidth(),
                    )

                    Text(
                        text = "Registration functionality removed.",
                        color = TextLight,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun DashboardHeader(
    onBackToLogin: () -> Unit,
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
                shape = RoundedCornerShape(22.dp),
                color = MintBright.copy(alpha = 0.18f),
                border = BorderStroke(1.dp, MintBright.copy(alpha = 0.45f)),
                modifier = Modifier.size(74.dp),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Outlined.AutoAwesome,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(38.dp),
                    )
                }
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                    Text(
                        text = "Smart KD Farm",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 24.sp,
                        ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "Dairy Management System",
                    color = Mint,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            DashboardIconChip(Icons.Outlined.NotificationsNone, showDot = true)
            DashboardIconChip(Icons.Outlined.Settings, onClick = onBackToLogin)
        }
    }
}

@Composable
private fun DashboardIconChip(
    icon: ImageVector,
    showDot: Boolean = false,
    onClick: (() -> Unit)? = null,
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = Color.White.copy(alpha = 0.06f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
        modifier = Modifier.size(64.dp),
        onClick = { onClick?.invoke() },
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.92f),
                modifier = Modifier.size(32.dp),
            )
            if (showDot) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 13.dp, end = 13.dp)
                        .size(11.dp)
                        .background(Color(0xFFFF3E3E), CircleShape)
                )
            }
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
                icon = Icons.Outlined.AutoAwesome,
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
                        fontSize = 22.sp,
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    "All systems running smoothly",
                    color = Mint,
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.Transparent,
                border = BorderStroke(1.dp, Mint.copy(alpha = 0.45f)),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.AutoAwesome,
                            contentDescription = null,
                            tint = Mint,
                            modifier = Modifier.size(16.dp),
                        )
                        Text(
                            "Pro",
                            color = Mint,
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                            ),
                        )
                    }
                }
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
        OverviewCard(
            accent = Blue,
            icon = Icons.Outlined.WaterDrop,
            badge = "12%",
            value = "250L",
            title = "Total Milk",
            footer = "Today vs Evening",
        )
        OverviewCard(
            accent = Purple,
            icon = Icons.Outlined.Pets,
            badge = "0",
            value = "15",
            title = "Active Herd",
            footer = "All Healthy",
        )
        OverviewCard(
            accent = Mint,
            icon = Icons.Outlined.TrendingUp,
            badge = "+₹809",
            value = "₹8.5K",
            title = "Net Profit",
            footer = "This Month",
        )
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
                        maxLines = 1,
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
                maxLines = 1,
            )
            Text(
                text = title,
                color = WhiteSoft,
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 17.sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
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
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
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
        AlertCard(
            accent = Red,
            icon = Icons.Outlined.WarningAmber,
            title = "Deworming Due",
            badge = "URGENT",
            message = "Animal #04 requires immediate attention",
            time = "2h ago",
        )
        AlertCard(
            accent = Yellow,
            icon = Icons.Outlined.WarningAmber,
            title = "Low Stock Alert",
            badge = "",
            message = "Makka feed - only 50kg remaining",
            time = "5h ago",
        )
    }
}

@Composable
private fun AlertCard(
    accent: Color,
    icon: ImageVector,
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
            DashboardGlowIcon(icon = icon, tint = accent, glow = accent)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = title,
                        color = accent,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
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
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = time,
                    color = Color.White.copy(alpha = 0.38f),
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MiniActionBubble(symbol = "›", tint = accent)
                MiniActionBubble(symbol = "×", tint = Color.White.copy(alpha = 0.45f))
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
                    DashboardGlowIcon(icon = Icons.Outlined.ShowChart, tint = Mint, glow = Mint)
                    Column {
                        Text(
                            "Milk Production",
                            color = Color.White,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 22.sp,
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            "3-Month Lactation Forecast",
                            color = WhiteSoft,
                            style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
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

            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                listOf("280L", "210L", "140L", "70L", "0L").forEach { label ->
                    Text(
                        text = label,
                        color = Color.White.copy(alpha = 0.35f),
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                    )
                }
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
private fun LegendDot(
    color: Color,
    label: String,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color, CircleShape)
        )
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.72f),
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
        )
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
                maxLines = 1,
            )
            Text(
                text = value,
                color = valueColor,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 19.sp,
                ),
                maxLines = 1,
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
        QuickActionCard("Quick Doodh\nEntry", "Log milk collection", Blue, Icons.Outlined.WaterDrop)
        QuickActionCard("Quick Kharcha", "Add expense", Color(0xFFE29A16), Icons.Outlined.ReceiptLong)
        QuickActionCard("Batch Feed", "Production log", Purple, Icons.Outlined.Inventory2)
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
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
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
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(dotColor, CircleShape)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
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
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun BottomDashboardNav(
    selectedTab: DashboardTab,
    onSelectTab: (DashboardTab) -> Unit,
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
                BottomNavItem(
                    label = DashboardTab.HOME.label,
                    icon = DashboardTab.HOME.icon,
                    selected = selectedTab == DashboardTab.HOME,
                    onClick = { onSelectTab(DashboardTab.HOME) },
                )
                BottomNavItem(
                    label = DashboardTab.LIVESTOCK.label,
                    icon = DashboardTab.LIVESTOCK.icon,
                    selected = selectedTab == DashboardTab.LIVESTOCK,
                    onClick = { onSelectTab(DashboardTab.LIVESTOCK) },
                )
                Spacer(modifier = Modifier.width(56.dp))
                BottomNavItem(
                    label = DashboardTab.STAFF.label,
                    icon = DashboardTab.STAFF.icon,
                    selected = selectedTab == DashboardTab.STAFF,
                    onClick = { onSelectTab(DashboardTab.STAFF) },
                )
                BottomNavItem(
                    label = DashboardTab.KHATA.label,
                    icon = DashboardTab.KHATA.icon,
                    selected = selectedTab == DashboardTab.KHATA,
                    onClick = { onSelectTab(DashboardTab.KHATA) },
                )
            }
        }

        Surface(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .size(72.dp),
            shape = RoundedCornerShape(24.dp),
            color = MintBright,
            shadowElevation = 12.dp,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(34.dp),
                )
            }
        }
    }
}

@Composable
private fun BottomNavItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val contentColor = if (selected) Mint else Color.White.copy(alpha = 0.58f)
    val background = if (selected) Mint.copy(alpha = 0.12f) else Color.Transparent
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = background,
        border = if (selected) BorderStroke(1.dp, Mint.copy(alpha = 0.25f)) else null,
        onClick = onClick,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 11.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(24.dp),
            )
            Text(
                text = label,
                color = contentColor,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 12.sp,
                ),
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun DashboardPlaceholderPanel(
    title: String,
    subtitle: String,
    accent: Color,
    icon: ImageVector,
) {
    Surface(
        shape = RoundedCornerShape(32.dp),
        color = GlassDark.copy(alpha = 0.95f),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.24f)),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            DashboardGlowIcon(icon = icon, tint = accent, glow = accent)
            Text(
                text = title,
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 24.sp,
                ),
            )
            Text(
                text = subtitle,
                color = Color.White.copy(alpha = 0.62f),
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp),
            )
            Text(
                text = "Bottom tabs are now clickable. Share the next reference screen and I’ll match this tab too.",
                color = accent,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
            )
        }
    }
}

@Composable
private fun DashboardGlowIcon(
    icon: ImageVector,
    tint: Color,
    glow: Color,
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = glow.copy(alpha = 0.08f),
        border = BorderStroke(1.dp, glow.copy(alpha = 0.20f)),
        modifier = Modifier.size(64.dp),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(32.dp),
            )
        }
    }
}

@Composable
private fun MiniActionBubble(
    symbol: String,
    tint: Color,
) {
    Surface(
        shape = CircleShape,
        color = tint.copy(alpha = 0.08f),
        modifier = Modifier.size(38.dp),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = symbol,
                color = tint,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                ),
            )
        }
    }
}

@Composable
private fun PremiumIconButton(onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = Color.White.copy(alpha = 0.2f),
        modifier = Modifier.size(44.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
    ) {
        Box(contentAlignment = Alignment.Center) {
            androidx.compose.material3.Icon(
                imageVector = ImageVector.Builder(
                    name = "back",
                    defaultWidth = 24.dp,
                    defaultHeight = 24.dp,
                    viewportWidth = 24f,
                    viewportHeight = 24f,
                ).path(
                    fill = androidx.compose.ui.graphics.SolidColor(Color.White),
                    stroke = androidx.compose.ui.graphics.SolidColor(Color.White),
                    strokeLineWidth = 0.5f,
                ) {
                    moveTo(20f, 11f)
                    horizontalLineTo(7.83f)
                    lineTo(13.42f, 5.41f)
                    lineTo(12f, 4f)
                    lineTo(4f, 12f)
                    lineTo(12f, 20f)
                    lineTo(13.41f, 18.59f)
                    lineTo(7.83f, 13f)
                    horizontalLineTo(20f)
                    verticalLineTo(11f)
                    close()
                }.build(),
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier.size(24.dp),
            )
        }
    }
}

@Composable
private fun PremiumButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(58.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = PremiumGreen,
            contentColor = Color.White,
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 6.dp,
            pressedElevation = 2.dp,
        ),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.5.sp,
            ),
        )
    }
}

@Composable
private fun DairyCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        color = GlassWhite,
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f)),
        shadowElevation = 12.dp,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp),
        ) {
            content()
        }
    }
}

@Composable
private fun DairyTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth(),
    label: String,
    placeholder: String = "",
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: androidx.compose.ui.text.input.VisualTransformation = androidx.compose.ui.text.input.VisualTransformation.None,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            color = TextLight,
            modifier = Modifier.padding(start = 2.dp),
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextLight.copy(alpha = 0.5f),
                )
            },
            keyboardOptions = keyboardOptions,
            visualTransformation = visualTransformation,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = SoftWhite,
                unfocusedContainerColor = SoftWhite,
                focusedBorderColor = PremiumGreen,
                unfocusedBorderColor = Color.Transparent,
            ),
            singleLine = true,
        )
    }
}

private enum class AuthRoute {
    LOGIN,
    ADD_FARM,
    DASHBOARD,
}
