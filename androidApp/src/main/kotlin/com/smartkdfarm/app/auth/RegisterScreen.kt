package com.smartkdfarm.app.auth

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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun RegisterScreen(
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
            PremiumBackButton(onClick = onBack)
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

                    PremiumButton(
                        text = "Register Farm",
                        onClick = {},
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp),
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
