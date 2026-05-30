package com.smartkdfarm.app.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun StaffControlScreen(
    userAccounts: List<AppUserAccount>,
    onAddUser: (AppUserAccount) -> Unit,
) {
    val nonAdminRoles = listOf(DashboardRole.MILK_AGENT, DashboardRole.LABOR, DashboardRole.OUTSIDE_FARMER)
    var fullName by remember { mutableStateOf("") }
    var mobileNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf(DashboardRole.MILK_AGENT) }
    var canAddAnimalMilk by remember { mutableStateOf(false) }
    var canMarkMilkingDone by remember { mutableStateOf(false) }
    var canMarkFeedDone by remember { mutableStateOf(false) }
    var canMarkCleaningDone by remember { mutableStateOf(false) }
    var canAddHealthObservation by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }
    var messageAccent by remember { mutableStateOf(Mint) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .padding(top = 24.dp, bottom = 140.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        DashboardHeader(
            title = "Staff Control",
            subtitle = "Admin user role management",
            showSettings = false,
            onSettingsClick = {},
            leadingIcon = Icons.Filled.AutoAwesome,
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            StaffSummaryCard(userAccounts)

            Surface(
                shape = RoundedCornerShape(34.dp),
                color = GlassDark.copy(alpha = 0.95f),
                border = BorderStroke(1.dp, Mint.copy(alpha = 0.24f)),
                shadowElevation = 12.dp,
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(15.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        DashboardGlowIcon(Icons.Filled.Add, Mint, Mint, modifier = Modifier.size(58.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Add User", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
                            Text("Admin can create only operational users", color = Mint, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }

                    DarkAuthField(fullName, { fullName = it; message = null }, "Full Name", "User name")
                    DarkAuthField(
                        value = mobileNumber,
                        onValueChange = { mobileNumber = it; message = null },
                        label = "Mobile Number",
                        placeholder = "Login mobile",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    )
                    DarkAuthField(
                        value = password,
                        onValueChange = { password = it; message = null },
                        label = "Password",
                        placeholder = "Minimum 8 characters",
                        visualTransformation = PasswordVisualTransformation(),
                    )

                    Text("Select Role", color = WhiteSoft, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        nonAdminRoles.forEach { role ->
                            StaffRoleChip(
                                role = role,
                                selected = role == selectedRole,
                                onClick = { selectedRole = role },
                            )
                        }
                    }

                    if (selectedRole == DashboardRole.LABOR) {
                        Surface(
                            shape = RoundedCornerShape(22.dp),
                            color = Yellow.copy(alpha = 0.08f),
                            border = BorderStroke(1.dp, Yellow.copy(alpha = 0.24f)),
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                Text("Labor Work Permissions", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                                Text("Jo kaam allow karoge, labor ko sirf wahi entry screen dikhegi.", color = WhiteSoft, fontSize = 12.sp)
                                PermissionCheckRow("Animal-wise milk entry", "Pashu tag ke hisab se litre entry", canAddAnimalMilk) { canAddAnimalMilk = it }
                                PermissionCheckRow("Milking done", "Subah/shaam milking complete mark", canMarkMilkingDone) { canMarkMilkingDone = it }
                                PermissionCheckRow("Chara / TMR done", "Feed work complete entry", canMarkFeedDone) { canMarkFeedDone = it }
                                PermissionCheckRow("Cleaning done", "Shed cleaning complete entry", canMarkCleaningDone) { canMarkCleaningDone = it }
                                PermissionCheckRow("Health observation", "Basic note only, no treatment cost", canAddHealthObservation) { canAddHealthObservation = it }
                            }
                        }
                    } else if (selectedRole == DashboardRole.MILK_AGENT) {
                        Surface(
                            shape = RoundedCornerShape(22.dp),
                            color = Blue.copy(alpha = 0.08f),
                            border = BorderStroke(1.dp, Blue.copy(alpha = 0.24f)),
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Text("Milk Agent Fixed Permissions", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                                Text("Login ke baad direct Outside Milk Collection screen khulega. Dashboard aur tabbar hidden rahenge.", color = WhiteSoft, fontSize = 12.sp)
                                Text("Can entry: farmer select, litre, FAT, SNF, rate, payment status.", color = Blue, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("Locked: profit, reports, inventory, staff, admin delete.", color = WhiteSoft, fontSize = 12.sp)
                            }
                        }
                    }

                    message?.let {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = messageAccent.copy(alpha = 0.10f),
                            border = BorderStroke(1.dp, messageAccent.copy(alpha = 0.25f)),
                        ) {
                            Text(
                                it,
                                color = WhiteSoft,
                                modifier = Modifier.padding(14.dp),
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            )
                        }
                    }

                    PremiumButton(
                        text = "Add ${selectedRole.label}",
                        onClick = {
                            val normalizedPhone = mobileNumber.trim()
                            val error = when {
                                fullName.isBlank() -> "User name required."
                                normalizedPhone.isBlank() -> "Mobile number required."
                                userAccounts.any { it.mobileNumber == normalizedPhone } -> "This mobile number is already registered."
                                password.length < 8 -> "Password must be at least 8 characters."
                                selectedRole == DashboardRole.ADMIN -> "Admin cannot be created from Staff Control."
                                else -> null
                            }
                            if (error != null) {
                                message = error
                                messageAccent = Red
                            } else {
                                onAddUser(
                                    AppUserAccount(
                                        fullName = fullName.trim(),
                                        mobileNumber = normalizedPhone,
                                        password = password,
                                        role = selectedRole,
                                        farmName = "Current Farm",
                                        canAddAnimalMilk = selectedRole == DashboardRole.LABOR && canAddAnimalMilk,
                                        canMarkMilkingDone = selectedRole == DashboardRole.LABOR && canMarkMilkingDone,
                                        canMarkFeedDone = selectedRole == DashboardRole.LABOR && canMarkFeedDone,
                                        canMarkCleaningDone = selectedRole == DashboardRole.LABOR && canMarkCleaningDone,
                                        canAddHealthObservation = selectedRole == DashboardRole.LABOR && canAddHealthObservation,
                                    )
                                )
                                message = "${selectedRole.label} user added. They can login with this mobile and password."
                                messageAccent = Mint
                                fullName = ""
                                mobileNumber = ""
                                password = ""
                                canAddAnimalMilk = false
                                canMarkMilkingDone = false
                                canMarkFeedDone = false
                                canMarkCleaningDone = false
                                canAddHealthObservation = false
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp),
                    )
                }
            }

            Text(
                text = "LOGIN USERS",
                color = Color.White.copy(alpha = 0.84f),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp,
                    fontSize = 17.sp,
                ),
            )
            userAccounts.forEach { account ->
                UserAccountCard(account)
            }
        }
    }
}

@Composable
private fun PermissionCheckRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(checkedColor = Yellow),
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
            Text(subtitle, color = WhiteSoft, fontSize = 12.sp)
        }
    }
}

@Composable
private fun StaffSummaryCard(userAccounts: List<AppUserAccount>) {
    val operationalUsers = userAccounts.count { it.role != DashboardRole.ADMIN }
    val admins = userAccounts.count { it.role == DashboardRole.ADMIN }
    Surface(
        shape = RoundedCornerShape(34.dp),
        color = GlassDark.copy(alpha = 0.95f),
        border = BorderStroke(1.dp, GlassStroke.copy(alpha = 0.9f)),
        shadowElevation = 12.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 18.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SummaryMetric("TOTAL", userAccounts.size.toString(), Icons.Filled.People, Mint, Modifier.weight(1f))
            SummaryMetric("ADMIN", admins.toString(), Icons.Filled.Shield, Blue, Modifier.weight(1f))
            SummaryMetric("USERS", operationalUsers.toString(), Icons.Filled.Lock, Yellow, Modifier.weight(1f))
        }
    }
}

@Composable
private fun SummaryMetric(
    label: String,
    value: String,
    icon: ImageVector,
    accent: Color,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        color = accent.copy(alpha = 0.08f),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.20f)),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(24.dp))
            Text(value, color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 28.sp)
            Text(label, color = accent, fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)
        }
    }
}

@Composable
private fun StaffRoleChip(
    role: DashboardRole,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val accent = roleAccent(role)
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(22.dp),
        color = if (selected) accent.copy(alpha = 0.16f) else Color.White.copy(alpha = 0.04f),
        border = BorderStroke(1.dp, accent.copy(alpha = if (selected) 0.48f else 0.18f)),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 13.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(roleIcon(role), contentDescription = null, tint = accent, modifier = Modifier.size(18.dp))
            Column {
                Text(role.label, color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                Text(role.key, color = accent, fontWeight = FontWeight.Bold, fontSize = 10.sp)
            }
        }
    }
}

@Composable
private fun UserAccountCard(account: AppUserAccount) {
    val accent = roleAccent(account.role)
    Surface(
        shape = RoundedCornerShape(26.dp),
        color = GlassDark.copy(alpha = 0.96f),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.22f)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            DashboardGlowIcon(roleIcon(account.role), accent, accent, modifier = Modifier.size(58.dp))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(
                    account.fullName,
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(7.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Call, contentDescription = null, tint = WhiteSoft, modifier = Modifier.size(14.dp))
                    Text(account.mobileNumber, color = WhiteSoft, fontSize = 12.sp)
                }
                Text(
                    "${account.role.key} login active",
                    color = accent,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 12.sp,
                )
            }
            Box(modifier = Modifier.size(12.dp).background(Mint, CircleShape))
        }
    }
}

private fun roleAccent(role: DashboardRole): Color = when (role) {
    DashboardRole.ADMIN -> Mint
    DashboardRole.MILK_AGENT -> Blue
    DashboardRole.LABOR -> Yellow
    DashboardRole.OUTSIDE_FARMER -> Purple
}

private fun roleIcon(role: DashboardRole): ImageVector = when (role) {
    DashboardRole.ADMIN -> Icons.Filled.Shield
    DashboardRole.MILK_AGENT -> Icons.Filled.WaterDrop
    DashboardRole.LABOR -> Icons.Filled.TaskAlt
    DashboardRole.OUTSIDE_FARMER -> Icons.Filled.Person
}
