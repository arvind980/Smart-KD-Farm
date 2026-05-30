package com.smartkdfarm.app.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun LoginScreen(
    userAccounts: List<AppUserAccount>,
    onOpenDashboard: (AppUserAccount) -> Unit,
    onOpenRegister: () -> Unit,
) {
    var mobileNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .widthIn(max = 460.dp)
            .statusBarsPadding()
            .padding(horizontal = 18.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        Spacer(modifier = Modifier.height(18.dp))
        AuthHero(
            title = "Smart KD Farm",
            subtitle = "Secure dairy dashboard login",
            icon = Icons.AutoMirrored.Filled.Login,
            accent = Mint,
            body = "Mobile number local users mein mile to saved role open hoga. Abhi demo mode mein new typed login temporary Admin dashboard open karega.",
        )

        Surface(
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
                    DashboardGlowIcon(Icons.Filled.Lock, Mint, Mint, modifier = Modifier.size(58.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Login",
                            color = Color.White,
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                        )
                        Text(
                            "Role based access enabled",
                            color = Mint,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 13.sp),
                        )
                    }
                }

                DarkAuthField(
                    value = mobileNumber,
                    onValueChange = {
                        mobileNumber = it
                        errorMessage = null
                    },
                    label = "Mobile Number",
                    placeholder = "Registered mobile",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                )
                DarkAuthField(
                    value = password,
                    onValueChange = {
                        password = it
                        errorMessage = null
                    },
                    label = "Password",
                    placeholder = "Password",
                    visualTransformation = PasswordVisualTransformation(),
                )

                errorMessage?.let { message ->
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Red.copy(alpha = 0.10f),
                        border = BorderStroke(1.dp, Red.copy(alpha = 0.25f)),
                    ) {
                        Text(
                            message,
                            color = WhiteSoft,
                            modifier = Modifier.padding(14.dp),
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        )
                    }
                }

                PremiumButton(
                    text = "Login",
                    onClick = {
                        val normalizedPhone = mobileNumber.trim()
                        val account = userAccounts.firstOrNull { it.mobileNumber == normalizedPhone }
                        when {
                            normalizedPhone.isBlank() -> errorMessage = "Mobile number required."
                            password.isBlank() -> errorMessage = "Password required."
                            account == null -> onOpenDashboard(
                                AppUserAccount(
                                    fullName = "Demo Admin",
                                    mobileNumber = normalizedPhone,
                                    password = password,
                                    role = DashboardRole.ADMIN,
                                    farmName = "Demo Farm",
                                    canAddAnimalMilk = true,
                                    canMarkMilkingDone = true,
                                    canMarkFeedDone = true,
                                    canMarkCleaningDone = true,
                                    canAddHealthObservation = true,
                                )
                            )
                            account.password != password -> errorMessage = "Password does not match this user."
                            else -> onOpenDashboard(account)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp),
                )

                RuleCard(
                    title = "Access Rule",
                    lines = listOf(
                        "Admin sees full farm dashboard.",
                        "Local added user role opens automatically.",
                        "Unknown typed login opens temporary Admin for now.",
                        "Database auth can replace this local check later.",
                    ),
                    accent = Mint,
                )
            }
        }

        Surface(
            shape = RoundedCornerShape(26.dp),
            color = Color.White.copy(alpha = 0.06f),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("New farm?", color = WhiteSoft, style = MaterialTheme.typography.bodyMedium)
                TextButton(onClick = onOpenRegister) {
                    Text("Register Admin Farm", color = Mint, fontWeight = FontWeight.ExtraBold)
                }
            }
        }
        Spacer(modifier = Modifier.height(28.dp))
    }
}

@Composable
private fun AuthHero(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accent: Color,
    body: String,
) {
    Surface(
        shape = RoundedCornerShape(36.dp),
        color = Color.Transparent,
        border = BorderStroke(1.dp, accent.copy(alpha = 0.26f)),
        shadowElevation = 14.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        listOf(
                            accent.copy(alpha = 0.22f),
                            GlassDark.copy(alpha = 0.96f),
                            DashboardBg,
                        )
                    )
                )
                .padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            DashboardGlowIcon(icon, accent, accent, modifier = Modifier.size(72.dp))
            Text(
                title,
                color = Color.White,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 30.sp,
                ),
            )
            Text(
                subtitle,
                color = accent,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold, fontSize = 16.sp),
            )
            Text(
                body,
                color = WhiteSoft,
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp, lineHeight = 21.sp),
            )
        }
    }
}

@Composable
internal fun DarkAuthField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: androidx.compose.ui.text.input.VisualTransformation = androidx.compose.ui.text.input.VisualTransformation.None,
) {
    Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
        Text(
            label,
            color = WhiteSoft,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.ExtraBold),
        )
        androidx.compose.material3.OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder, color = WhiteSoft.copy(alpha = 0.52f)) },
            keyboardOptions = keyboardOptions,
            visualTransformation = visualTransformation,
            singleLine = true,
            shape = RoundedCornerShape(18.dp),
            colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedContainerColor = Color.White.copy(alpha = 0.05f),
                unfocusedContainerColor = Color.White.copy(alpha = 0.04f),
                focusedBorderColor = Mint,
                unfocusedBorderColor = Color.White.copy(alpha = 0.14f),
                focusedLabelColor = Mint,
                unfocusedLabelColor = WhiteSoft,
                cursorColor = Mint,
            ),
        )
    }
}

@Composable
internal fun RuleCard(
    title: String,
    lines: List<String>,
    accent: Color,
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = accent.copy(alpha = 0.08f),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.22f)),
    ) {
        Column(
            modifier = Modifier.padding(15.dp),
            verticalArrangement = Arrangement.spacedBy(9.dp),
        ) {
            Text(title, color = accent, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
            lines.forEach { line ->
                Row(horizontalArrangement = Arrangement.spacedBy(9.dp), verticalAlignment = Alignment.Top) {
                    Box(modifier = Modifier.padding(top = 6.dp).size(7.dp).background(accent, CircleShape))
                    Text(line, color = WhiteSoft, fontSize = 13.sp, lineHeight = 18.sp, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}
