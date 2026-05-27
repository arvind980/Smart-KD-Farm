package com.smartkdfarm.app

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SmartKdFarmConsole(
    managerName: String = "Arvind Singh",
    farmName: String = "Smart KD Farm",
    currentRoleLabel: String = "ADMIN",
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        Text("Smart KD Farm Console - Deprecated. Use App() instead.")
    }
}
