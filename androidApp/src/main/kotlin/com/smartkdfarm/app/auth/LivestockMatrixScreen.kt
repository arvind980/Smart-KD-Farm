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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private enum class LivestockFilter(
    val title: String,
    val count: Int,
    val icon: ImageVector,
) {
    ALL("All", 8, Icons.Outlined.AutoAwesome),
    MILKING("Milking", 4, Icons.Filled.WaterDrop),
    PREGNANT("Pregnant", 2, Icons.Filled.Pets),
}

private enum class LivestockStatus(
    val label: String,
    val icon: ImageVector,
    val accent: Color,
) {
    MILKING("MILKING", Icons.Filled.WaterDrop, Mint),
    PREGNANT("PREGNANT", Icons.Filled.Pets, Purple),
    SICK("SICK", Icons.Outlined.FavoriteBorder, Red),
    DRY("DRY", Icons.Outlined.AutoAwesome, Yellow),
}

private data class LivestockCardModel(
    val tag: String,
    val breed: String,
    val age: String,
    val status: LivestockStatus,
    val health: Int,
    val footerLabel: String,
    val footerValue: String,
    val showArrow: Boolean = false,
)

@Composable
internal fun LivestockMatrixScreen() {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf(LivestockFilter.ALL) }
    val cards = remember {
        listOf(
            LivestockCardModel("Tag #01", "Murrah Buffalo", "5 yrs", LivestockStatus.MILKING, 95, "Last Yield", "12L"),
            LivestockCardModel("Tag #02", "HF Cross", "4 yrs", LivestockStatus.MILKING, 92, "Last Yield", "15L"),
            LivestockCardModel("Tag #03", "Sahiwal", "6 yrs", LivestockStatus.PREGNANT, 88, "Last Yield", "10L"),
            LivestockCardModel("Tag #04", "Murrah Buffalo", "3 yrs", LivestockStatus.SICK, 45, "Needs attention", "", showArrow = true),
            LivestockCardModel("Tag #05", "Jersey", "5 yrs", LivestockStatus.DRY, 78, "Last Yield", "0L"),
            LivestockCardModel("Tag #06", "HF Cross", "4 yrs", LivestockStatus.MILKING, 90, "Last Yield", "14L"),
            LivestockCardModel("Tag #07", "Gir", "7 yrs", LivestockStatus.PREGNANT, 86, "Last Yield", "8L"),
            LivestockCardModel("Tag #08", "Murrah Buffalo", "2 yrs", LivestockStatus.MILKING, 91, "Last Yield", "11L"),
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
            title = "Livestock Matrix",
            subtitle = "8 Animals",
            showSettings = false,
            onSettingsClick = {},
            leadingIcon = Icons.Filled.Pets,
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = GlassDark.copy(alpha = 0.95f),
                border = BorderStroke(1.dp, GlassStroke.copy(alpha = 0.85f)),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.44f),
                        modifier = Modifier.size(28.dp),
                    )
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.weight(1f),
                        placeholder = {
                            Text(
                                "Search by tag or breed...",
                                color = Color.White.copy(alpha = 0.42f),
                                style = MaterialTheme.typography.titleLarge.copy(fontSize = 16.sp),
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            cursorColor = Mint,
                        ),
                        textStyle = MaterialTheme.typography.titleMedium.copy(color = Color.White, fontSize = 16.sp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions.Default,
                    )
                    Surface(
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.06f),
                        modifier = Modifier.size(56.dp),
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Filled.FilterList,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.64f),
                                modifier = Modifier.size(28.dp),
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                LivestockFilter.entries.forEach { filter ->
                    FilterPill(
                        modifier = Modifier.weight(1f),
                        filter = filter,
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                    )
                }
            }

            cards.forEach { card ->
                LivestockCard(
                    modifier = Modifier.fillMaxWidth(),
                    card = card,
                )
            }
        }
    }
}

@Composable
private fun FilterPill(
    modifier: Modifier = Modifier,
    filter: LivestockFilter,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(26.dp),
        color = if (selected) Color.White.copy(alpha = 0.12f) else Color.White.copy(alpha = 0.04f),
        border = if (selected) BorderStroke(1.dp, Color.White.copy(alpha = 0.22f)) else null,
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = filter.icon,
                contentDescription = null,
                tint = if (selected) Color.White else Color.White.copy(alpha = 0.64f),
                modifier = Modifier.size(18.dp),
            )
            Text(
                text = filter.title,
                color = if (selected) Color.White else Color.White.copy(alpha = 0.64f),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                ),
                maxLines = 1,
            )
            Surface(
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.10f),
            ) {
                Text(
                    text = filter.count.toString(),
                    color = Color.White.copy(alpha = 0.88f),
                    modifier = Modifier.padding(horizontal = 11.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                    ),
                )
            }
        }
    }
}

@Composable
private fun LivestockCard(
    modifier: Modifier = Modifier,
    card: LivestockCardModel,
) {
    val accent = card.status.accent
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(32.dp),
        color = GlassDark.copy(alpha = 0.96f),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.35f)),
        shadowElevation = 10.dp,
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(accent.copy(alpha = 0.18f))
                    .padding(horizontal = 14.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = accent.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, accent.copy(alpha = 0.40f)),
                    modifier = Modifier.size(74.dp),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.Pets,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.88f),
                            modifier = Modifier.size(34.dp),
                        )
                    }
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(3.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top,
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(3.dp),
                        ) {
                            Text(
                                text = card.tag,
                                color = Color.White,
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 18.sp,
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Text(
                                text = card.breed,
                                color = Color.White.copy(alpha = 0.7f),
                                style = MaterialTheme.typography.titleMedium.copy(fontSize = 12.sp),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                        if (!card.showArrow) {
                            Column(
                                horizontalAlignment = Alignment.End,
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                Text(
                                    text = "Yield",
                                    color = Color.White.copy(alpha = 0.42f),
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp,
                                    ),
                                )
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = accent.copy(alpha = 0.10f),
                                    border = BorderStroke(1.dp, accent.copy(alpha = 0.22f)),
                                ) {
                                    Text(
                                        text = card.footerValue,
                                        color = accent,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 16.sp,
                                        ),
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Column(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Surface(
                        shape = RoundedCornerShape(22.dp),
                        color = card.status.accent.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, card.status.accent.copy(alpha = 0.40f)),
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = card.status.icon,
                                contentDescription = null,
                                tint = card.status.accent,
                                modifier = Modifier.size(18.dp),
                            )
                            Text(
                                text = card.status.label,
                                color = card.status.accent,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 12.sp,
                                ),
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White.copy(alpha = 0.06f),
                    ) {
                        Text(
                            text = card.age,
                            color = Color.White.copy(alpha = 0.6f),
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                            ),
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.FavoriteBorder,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.58f),
                            modifier = Modifier.size(24.dp),
                        )
                        Text(
                            "HEALTH",
                            color = Color.White.copy(alpha = 0.58f),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                            ),
                        )
                    }
                    Text(
                        "${card.health}%",
                        color = accent,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp,
                        ),
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(14.dp)
                        .background(Color.White.copy(alpha = 0.06f), RoundedCornerShape(10.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(card.health / 100f)
                            .height(14.dp)
                            .background(accent, RoundedCornerShape(10.dp))
                    )
                }

                HorizontalDivider(color = accent.copy(alpha = 0.14f))

                if (card.showArrow) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = card.footerLabel,
                            color = accent,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                            ),
                            maxLines = 1,
                        )
                        Surface(
                            shape = CircleShape,
                            color = accent.copy(alpha = 0.12f),
                            modifier = Modifier.size(46.dp),
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("›", color = accent, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
