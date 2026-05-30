package com.smartkdfarm.app.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun RegisterScreen(
    onBack: () -> Unit,
    userAccounts: SnapshotStateList<AppUserAccount>,
    onAdminRegistered: (AppUserAccount) -> Unit,
) {
    var farmName by remember { mutableStateOf("Smart KD Farm") }
    var adminName by remember { mutableStateOf("") }
    var adminPhone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var village by remember { mutableStateOf("") }
    var district by remember { mutableStateOf("") }
    var stateName by remember { mutableStateOf("Punjab") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 18.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        Spacer(modifier = Modifier.height(18.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 480.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PremiumBackButton(onClick = onBack)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Register Farm",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                )
                Text(
                    "Only Admin can create a new farm",
                    color = Mint,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 13.sp),
                )
            }
        }

        Surface(
            modifier = Modifier.widthIn(max = 480.dp),
            shape = RoundedCornerShape(36.dp),
            color = Color.Transparent,
            border = BorderStroke(1.dp, Mint.copy(alpha = 0.28f)),
            shadowElevation = 14.dp,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            listOf(
                                Mint.copy(alpha = 0.20f),
                                GlassDark.copy(alpha = 0.96f),
                                DashboardBg,
                            )
                        )
                    )
                    .padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                DashboardGlowIcon(Icons.Filled.Store, Mint, Mint, modifier = Modifier.size(72.dp))
                Text(
                    "One admin, one farm",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.ExtraBold, fontSize = 28.sp),
                )
                Text(
                    "New admin registration creates a separate farm account. Milk Agent, Labor and Farmer cannot register from outside.",
                    color = WhiteSoft,
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp, lineHeight = 21.sp),
                )
            }
        }

        Surface(
            modifier = Modifier.widthIn(max = 480.dp),
            shape = RoundedCornerShape(34.dp),
            color = GlassDark.copy(alpha = 0.95f),
            border = BorderStroke(1.dp, GlassStroke.copy(alpha = 0.86f)),
            shadowElevation = 14.dp,
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    DashboardGlowIcon(Icons.Filled.Shield, Mint, Mint, modifier = Modifier.size(58.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Admin Farm Setup", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 21.sp)
                        Text("Role key: ADMIN", color = Mint, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }

                DarkAuthField(farmName, { farmName = it }, "Farm Name", "Farm Name")
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        DarkAuthField(village, { village = it }, "Village", "Village")
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        DarkAuthField(district, { district = it }, "District", "District")
                    }
                }
                DarkAuthField(stateName, { stateName = it }, "State", "State")
                DarkAuthField(adminName, { adminName = it }, "Admin Name", "Admin")
                DarkAuthField(
                    value = adminPhone,
                    onValueChange = {
                        adminPhone = it
                        errorMessage = null
                    },
                    label = "Admin Mobile Number",
                    placeholder = "Unique mobile number",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                )
                DarkAuthField(
                    value = password,
                    onValueChange = { password = it },
                    label = "Password",
                    placeholder = "Minimum 8 characters",
                    visualTransformation = PasswordVisualTransformation(),
                )

                errorMessage?.let { message ->
                    StatusBanner(message = message, accent = Red, icon = Icons.Filled.ErrorOutline)
                }

                PremiumButton(
                    text = "Create Admin Farm",
                    onClick = {
                        val normalizedPhone = adminPhone.trim()
                        errorMessage = when {
                            normalizedPhone.isBlank() -> "Admin mobile number required."
                            userAccounts.any { it.mobileNumber == normalizedPhone } -> "This mobile number is already registered. Please login or use another number."
                            adminName.isBlank() -> "Admin name required."
                            farmName.isBlank() -> "Farm name required."
                            password.length < 8 -> "Password must be at least 8 characters."
                            else -> null
                        }
                        if (errorMessage == null) {
                            onAdminRegistered(
                                AppUserAccount(
                                    fullName = adminName.trim(),
                                    mobileNumber = normalizedPhone,
                                    password = password,
                                    role = DashboardRole.ADMIN,
                                    farmName = farmName.trim(),
                                    canAddAnimalMilk = true,
                                    canMarkMilkingDone = true,
                                    canMarkFeedDone = true,
                                    canMarkCleaningDone = true,
                                    canAddHealthObservation = true,
                                )
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp),
                )

                RuleCard(
                    title = "Registration Policy",
                    lines = listOf(
                        "Public signup allows ADMIN only.",
                        "After login, Admin can add Milk Agent, Labor and Outside Farmer inside the app.",
                        "Each new admin mobile creates a separate farm.",
                        "Same mobile number cannot register again.",
                    ),
                    accent = Mint,
                )

                StatusBanner(
                    message = "Other roles are invited by Admin from Staff/User Access, not from this screen.",
                    accent = Yellow,
                    icon = Icons.Filled.People,
                )
            }
        }
        Spacer(modifier = Modifier.height(28.dp))
    }
}

@Composable
private fun StatusBanner(
    message: String,
    accent: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
) {
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = accent.copy(alpha = 0.10f),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.25f)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(22.dp))
            Text(
                message,
                color = WhiteSoft,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, lineHeight = 19.sp),
                modifier = Modifier.weight(1f),
            )
        }
    }
}
