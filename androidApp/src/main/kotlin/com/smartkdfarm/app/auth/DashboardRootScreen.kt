package com.smartkdfarm.app.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun DashboardRootScreen(
    onBackToLogin: () -> Unit,
) {
    var selectedTab by remember { mutableStateOf(DashboardTab.HOME) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DashboardBg)
    ) {
        when (selectedTab) {
            DashboardTab.HOME -> DashboardHomeScreen(onBackToLogin = onBackToLogin)
            DashboardTab.LIVESTOCK -> LivestockMatrixScreen()
            DashboardTab.STAFF -> StaffControlScreen()
            DashboardTab.KHATA -> DashboardPlaceholderScreen(
                title = "Khata Book",
                subtitle = "Ledger preview",
                onBackToLogin = onBackToLogin,
            )
        }

        BottomDashboardNav(
            selectedTab = selectedTab,
            onSelectTab = { selectedTab = it },
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

@Composable
private fun DashboardPlaceholderScreen(
    title: String,
    subtitle: String,
    onBackToLogin: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .padding(top = 24.dp, bottom = 140.dp),
    ) {
        DashboardHeader(
            title = title,
            subtitle = subtitle,
            showSettings = false,
            onSettingsClick = onBackToLogin,
        )
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {}
    }
}
