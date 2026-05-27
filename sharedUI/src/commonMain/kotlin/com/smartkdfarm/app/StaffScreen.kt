package com.smartkdfarm.app

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Shield
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
fun StaffScreen() {
    AppBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            StaffTopBar()
            StaffSummaryCard()
            TeamMembersSection()
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun StaffTopBar() {
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
                Icon(Icons.Default.Group, contentDescription = null, tint = Color.Black)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    "Staff Control",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = TextWhite
                )
                Text(
                    "Team Management",
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
fun StaffSummaryCard() {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StaffSummaryItem("5", "TOTAL")
            StaffSummaryItem("4", "ACTIVE", color = AppPrimary, showDot = true)
            StaffSummaryItem("3", "DB ACCESS", color = CardPurple, icon = Icons.Default.Shield)
        }
    }
}

@Composable
fun StaffSummaryItem(value: String, label: String, color: Color = TextWhite, showDot: Boolean = false, icon: androidx.compose.ui.graphics.vector.ImageVector? = null) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (showDot) {
                Box(modifier = Modifier.size(8.dp).background(color, CircleShape))
                Spacer(modifier = Modifier.width(8.dp))
            }
            if (icon != null) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(value, style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black), color = color)
        }
        Text(label, style = MaterialTheme.typography.labelLarge, color = TextGray)
    }
}

@Composable
fun TeamMembersSection() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("TEAM MEMBERS", style = MaterialTheme.typography.labelLarge)
                Spacer(modifier = Modifier.width(6.dp))
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = AppPrimary, modifier = Modifier.size(14.dp))
            }
            Text("Toggle for DB access", style = MaterialTheme.typography.labelLarge, fontSize = 10.sp)
        }
        StaffMemberCard("Raj Kumar", "DAIRY MAN", "Jan 2022", "+91 98765 43210", true, AppPrimary)
        StaffMemberCard("Mohan Singh", "LABOUR", "Mar 2023", "+91 87654 32109", false, CardYellow)
        StaffMemberCard("Suresh Yadav", "DAIRY MAN", "Feb 2021", "+91 76543 21098", true, AppPrimary)
        StaffMemberCard("Ramesh Verma", "SUPERVISOR", "Aug 2020", "+91 65432 10987", true, CardPurple)
        StaffMemberCard("Amit Sharma", "LABOUR", "Jun 2023", "+91 54321 09876", false, CardYellow)
    }
}

@Composable
fun StaffMemberCard(
    name: String,
    role: String,
    since: String,
    phone: String,
    dbAccess: Boolean,
    roleColor: Color
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(roleColor.copy(alpha = 0.05f))
                        .border(1.dp, roleColor.copy(alpha = 0.2f), RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Box(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = roleColor, modifier = Modifier.fillMaxSize())
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(AppPrimary, CircleShape)
                                .border(2.dp, AppDarkBg, CircleShape)
                                .align(Alignment.BottomEnd)
                                .offset(x = 4.dp, y = 4.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = roleColor.copy(alpha = 0.1f)
                        ) {
                            Text(
                                role,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                color = roleColor,
                                style = MaterialTheme.typography.labelLarge,
                                fontSize = 9.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Since $since", style = MaterialTheme.typography.bodyMedium)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Call, contentDescription = null, tint = TextGray, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(phone, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Switch(
                    checked = dbAccess,
                    onCheckedChange = { },
                    thumbContent = {
                        Icon(
                            imageVector = if (dbAccess) Icons.Default.Shield else Icons.Outlined.Shield,
                            contentDescription = null,
                            modifier = Modifier.size(SwitchDefaults.IconSize),
                        )
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = AppPrimary,
                        uncheckedThumbColor = TextGray,
                        uncheckedTrackColor = Color.White.copy(alpha = 0.05f),
                        uncheckedBorderColor = Color.White.copy(alpha = 0.1f)
                    )
                )
                Text(
                    if (dbAccess) "ENABLED" else "DISABLED",
                    style = MaterialTheme.typography.labelLarge,
                    color = if (dbAccess) AppPrimary else TextGray,
                    fontSize = 8.sp
                )
            }
        }
    }
}
