package com.smartkdfarm.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LivestockScreen() {
    AppBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            LivestockTopBar()
            LivestockSearchBar()
            LivestockFilters()
            LivestockGrid()
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun LivestockTopBar() {
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
                Icon(Icons.Default.Pets, contentDescription = null, tint = Color.Black)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    "Livestock Matrix",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = TextWhite
                )
                Text(
                    "8 Animals",
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
fun LivestockSearchBar() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.White.copy(alpha = 0.05f),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Search, contentDescription = "Search", tint = TextGray, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text("Search by tag or breed...", color = TextGray, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.weight(1f))
            Icon(Icons.Default.FilterList, contentDescription = "Filter", tint = TextGray, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
fun LivestockFilters() {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        FilterChipCustom("All", "8", true)
        FilterChipCustom("Milking", "4", false)
        FilterChipCustom("Pregnant", "2", false)
    }
}

@Composable
fun FilterChipCustom(label: String, count: String, isSelected: Boolean) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) Color.White.copy(alpha = 0.1f) else Color.Transparent,
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = if (isSelected) Color.White.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.05f)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, color = if (isSelected) TextWhite else TextGray, style = MaterialTheme.typography.labelLarge)
            Spacer(modifier = Modifier.width(8.dp))
            Text(count, color = if (isSelected) TextGray else TextGray.copy(alpha = 0.5f), style = MaterialTheme.typography.labelLarge, fontSize = 10.sp)
        }
    }
}

@Composable
fun LivestockGrid() {
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
            AnimalCard("Tag #01", "Murrah", "Buffalo", "MILKING", "5 yrs", "95%", "12L", AppPrimary, Modifier.weight(1f))
            AnimalCard("Tag #02", "HF Cross", "Cow", "MILKING", "4 yrs", "92%", "15L", AppPrimary, Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
            AnimalCard("Tag #03", "Sahiwal", "Cow", "PREGNANT", "6 yrs", "88%", "N/A", CardPurple, Modifier.weight(1f))
            AnimalCard("Tag #04", "Murrah", "Buffalo", "SICK", "3 yrs", "45%", "N/A", CardRed, Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
            AnimalCard("Tag #05", "Jersey", "Cow", "DRY", "5 yrs", "78%", "N/A", CardYellow, Modifier.weight(1f))
            AnimalCard("Tag #06", "HF Cross", "Cow", "MILKING", "4 yrs", "90%", "14L", AppPrimary, Modifier.weight(1f))
        }
    }
}

@Composable
fun AnimalCard(
    tag: String,
    breed: String,
    type: String,
    status: String,
    age: String,
    health: String,
    yield: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(color.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Pets, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(tag, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                    Text(breed, style = MaterialTheme.typography.bodyMedium)
                    Text(type, style = MaterialTheme.typography.bodyMedium, color = TextGray)
                }
            }
            Icon(Icons.Default.MoreVert, contentDescription = null, tint = TextGray, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.height(20.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = color.copy(alpha = 0.1f),
                border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f))
            ) {
                Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.WaterDrop, contentDescription = null, tint = color, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(status, color = color, style = MaterialTheme.typography.labelLarge, fontSize = 9.sp)
                }
            }
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White.copy(alpha = 0.05f)
            ) {
                Text(age, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), style = MaterialTheme.typography.labelLarge, fontSize = 9.sp)
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.FavoriteBorder, contentDescription = null, tint = TextGray, modifier = Modifier.size(12.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("HEALTH", style = MaterialTheme.typography.labelLarge, fontSize = 9.sp)
            }
            Text(health, style = MaterialTheme.typography.titleLarge, color = color, fontWeight = FontWeight.Black)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.05f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(if (health.endsWith("%")) health.removeSuffix("%").toFloat() / 100f else 0.5f)
                    .fillMaxHeight()
                    .background(color)
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Last Yield", style = MaterialTheme.typography.bodyMedium)
            Text(yield, style = MaterialTheme.typography.titleLarge, color = AppPrimary, fontWeight = FontWeight.Black)
        }
    }
}
