package com.smartkdfarm.app

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MainScreen() {
    var currentScreen by remember { mutableStateOf(Screen.Home) }

    Scaffold(
        bottomBar = {
            BottomNav(
                currentScreen = currentScreen,
                onScreenSelected = { currentScreen = it }
            )
        },
        floatingActionButton = {
            Surface(
                modifier = Modifier
                    .size(60.dp)
                    .offset(y = 50.dp),
                shape = RoundedCornerShape(20.dp),
                color = AppPrimary,
                shadowElevation = 8.dp
            ) {
                IconButton(onClick = { /* Add action */ }) {
                    Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.Black, modifier = Modifier.size(32.dp))
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        containerColor = AppDarkBg
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (currentScreen) {
                Screen.Home -> HomeScreen()
                Screen.Livestock -> LivestockScreen()
                Screen.Staff -> StaffScreen()
                Screen.Khata -> KhataScreen()
            }
        }
    }
}

@Composable
fun BottomNav(
    currentScreen: Screen,
    onScreenSelected: (Screen) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(84.dp),
        color = Color(0xFF0A1F1C).copy(alpha = 0.9f),
        shape = RoundedCornerShape(28.dp),
        border = BorderStroke(
            width = 1.dp,
            color = Color.White.copy(alpha = 0.08f)
        )
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(
                screen = Screen.Home,
                isSelected = currentScreen == Screen.Home,
                onClick = { onScreenSelected(Screen.Home) },
                icon = Icons.Default.Home,
                outlinedIcon = Icons.Outlined.Home
            )
            BottomNavItem(
                screen = Screen.Livestock,
                isSelected = currentScreen == Screen.Livestock,
                onClick = { onScreenSelected(Screen.Livestock) },
                icon = Icons.Default.Pets,
                outlinedIcon = Icons.Outlined.Pets
            )
            
            Spacer(modifier = Modifier.width(48.dp)) // Space for FAB

            BottomNavItem(
                screen = Screen.Staff,
                isSelected = currentScreen == Screen.Staff,
                onClick = { onScreenSelected(Screen.Staff) },
                icon = Icons.Default.People,
                outlinedIcon = Icons.Outlined.People
            )
            BottomNavItem(
                screen = Screen.Khata,
                isSelected = currentScreen == Screen.Khata,
                onClick = { onScreenSelected(Screen.Khata) },
                icon = Icons.Default.Book,
                outlinedIcon = Icons.Outlined.Book
            )
        }
    }
}

@Composable
fun BottomNavItem(
    screen: Screen,
    isSelected: Boolean,
    onClick: () -> Unit,
    icon: ImageVector,
    outlinedIcon: ImageVector
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .then(if (isSelected) Modifier.background(AppPrimary.copy(alpha = 0.1f)) else Modifier)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = if (isSelected) icon else outlinedIcon,
            contentDescription = screen.title,
            tint = if (isSelected) AppPrimary else TextGray,
            modifier = Modifier.size(26.dp)
        )
        Text(
            text = screen.title,
            style = MaterialTheme.typography.labelLarge,
            color = if (isSelected) AppPrimary else TextGray,
            fontSize = 10.sp
        )
    }
}

enum class Screen(val title: String) {
    Home("Home"),
    Livestock("Livestock"),
    Staff("Staff"),
    Khata("Khata")
}
