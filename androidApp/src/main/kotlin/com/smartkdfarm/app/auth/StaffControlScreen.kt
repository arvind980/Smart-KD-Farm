package com.smartkdfarm.app.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private enum class StaffRole(
    val label: String,
    val accent: Color,
) {
    DAIRY_MAN("DAIRY MAN", Mint),
    LABOUR("LABOUR", Yellow),
    SUPERVISOR("SUPERVISOR", Purple),
}

private data class StaffMemberModel(
    val name: String,
    val role: StaffRole,
    val since: String,
    val phone: String,
    val dbAccessEnabled: Boolean,
)

@Composable
internal fun StaffControlScreen() {
    val members = remember {
        listOf(
            StaffMemberModel("Raj Kumar", StaffRole.DAIRY_MAN, "Jan 2022", "+91 98765 43210", true),
            StaffMemberModel("Mohan Singh", StaffRole.LABOUR, "Mar 2023", "+91 87654 32109", false),
            StaffMemberModel("Suresh Yadav", StaffRole.DAIRY_MAN, "Feb 2021", "+91 76543 21098", true),
            StaffMemberModel("Ramesh Verma", StaffRole.SUPERVISOR, "Aug 2020", "+91 65432 10987", true),
            StaffMemberModel("Amit Sharma", StaffRole.LABOUR, "Jun 2023", "+91 54321 09876", false),
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .padding(top = 24.dp, bottom = 140.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        DashboardHeader(
            title = "Staff Control",
            subtitle = "Team Management",
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
            StaffSummaryCard()
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "TEAM MEMBERS",
                        color = Color.White.copy(alpha = 0.84f),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp,
                            fontSize = 17.sp,
                        ),
                    )
                    Icon(
                        imageVector = Icons.Filled.AutoAwesome,
                        contentDescription = null,
                        tint = Mint,
                        modifier = Modifier.size(18.dp),
                    )
                }
                Text(
                    text = "Toggle for DB access",
                    color = Color.White.copy(alpha = 0.38f),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp,
                    ),
                    maxLines = 1,
                )
            }

            members.forEach { member ->
                StaffMemberCard(member = member)
            }
        }
    }
}

@Composable
private fun StaffSummaryCard() {
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
            verticalAlignment = Alignment.CenterVertically,
        ) {
            StaffSummaryMetric(
                value = "5",
                label = "TOTAL",
                accent = Color.White,
                icon = Icons.Filled.People,
                modifier = Modifier.weight(1f),
            )
            SummaryDivider()
            StaffSummaryMetric(
                value = "4",
                label = "ACTIVE",
                accent = Mint,
                dotOnly = true,
                modifier = Modifier.weight(1f),
            )
            SummaryDivider()
            StaffSummaryMetric(
                value = "3",
                label = "DB ACCESS",
                accent = Purple,
                icon = Icons.Outlined.Shield,
                modifier = Modifier.weight(1f),
            )
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White.copy(alpha = 0.04f),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.10f)),
                modifier = Modifier.size(78.dp),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Outlined.MoreVert,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.52f),
                        modifier = Modifier.size(30.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun StaffSummaryMetric(
    value: String,
    label: String,
    accent: Color,
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    dotOnly: Boolean = false,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        when {
            dotOnly -> Box(
                modifier = Modifier
                    .size(22.dp)
                    .background(accent, CircleShape),
            )

            icon != null -> Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accent.copy(alpha = 0.95f),
                modifier = Modifier.size(24.dp),
            )
        }
        Text(
            text = value,
            color = accent,
            style = MaterialTheme.typography.displaySmall.copy(
                fontWeight = FontWeight.ExtraBold,
                fontSize = 38.sp,
            ),
            maxLines = 1,
        )
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.46f),
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                fontSize = 13.sp,
            ),
            maxLines = 1,
        )
    }
}

@Composable
private fun SummaryDivider() {
    Box(
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .width(1.dp)
            .height(92.dp)
            .background(Color.White.copy(alpha = 0.10f))
    )
}

@Composable
private fun StaffMemberCard(
    member: StaffMemberModel,
) {
    val accent = member.role.accent
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = GlassDark.copy(alpha = 0.96f),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.18f)),
        shadowElevation = 8.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(topStart = 24.dp, bottomStart = 24.dp))
                    .background(accent)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box {
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = accent.copy(alpha = 0.10f),
                            border = BorderStroke(1.dp, accent.copy(alpha = 0.40f)),
                            modifier = Modifier.size(64.dp),
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.Person,
                                    contentDescription = null,
                                    tint = accent,
                                    modifier = Modifier.size(28.dp),
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .offset(x = 2.dp, y = 2.dp)
                                .size(20.dp)
                                .background(DashboardBg, CircleShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .background(MintBright, CircleShape)
                            )
                        }
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Text(
                            text = member.name,
                            color = Color.White,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp,
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = accent.copy(alpha = 0.12f),
                                border = BorderStroke(1.dp, accent.copy(alpha = 0.30f)),
                            ) {
                                Text(
                                    text = member.role.label,
                                    color = accent,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 10.sp,
                                    ),
                                    maxLines = 1,
                                )
                            }
                            Text(
                                text = "Since ${member.since}",
                                color = Color.White.copy(alpha = 0.34f),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 12.sp,
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Call,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.34f),
                                modifier = Modifier.size(14.dp),
                            )
                            Text(
                                text = member.phone,
                                color = Color.White.copy(alpha = 0.36f),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 12.sp,
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier.width(72.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    DbAccessToggle(
                        enabled = member.dbAccessEnabled,
                    )
                    Text(
                        text = if (member.dbAccessEnabled) "ENABLED" else "DISABLED",
                        color = if (member.dbAccessEnabled) Mint else Color.White.copy(alpha = 0.22f),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 10.sp,
                        ),
                    )
                }
            }
        }
    }
}

@Composable
private fun DbAccessToggle(
    enabled: Boolean,
) {
    val trackColor = if (enabled) MintBright else Color.White.copy(alpha = 0.08f)
    val knobTint = if (enabled) Mint else Color.White.copy(alpha = 0.56f)
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = trackColor,
        modifier = Modifier.size(width = 54.dp, height = 30.dp),
        shadowElevation = if (enabled) 6.dp else 0.dp,
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            Box(
                modifier = Modifier
                    .align(if (enabled) Alignment.CenterEnd else Alignment.CenterStart)
                    .padding(horizontal = 4.dp)
                    .size(22.dp)
                    .background(Color.White, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = if (enabled) Icons.Filled.Shield else Icons.Outlined.Shield,
                    contentDescription = null,
                    tint = knobTint,
                    modifier = Modifier.size(14.dp),
                )
            }
        }
    }
}
