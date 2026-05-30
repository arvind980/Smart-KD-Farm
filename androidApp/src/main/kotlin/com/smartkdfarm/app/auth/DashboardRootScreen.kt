package com.smartkdfarm.app.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

internal enum class DashboardModule {
    ACCESS,
    OUTSIDE_MILK,
    FARMER_PORTAL,
    LABOR_TASKS,
    ADMIN_CRUD,
    MILK,
    INVENTORY,
    HEALTH,
    EXPENSES,
    REPORTS,
}

internal enum class DashboardRole(
    val key: String,
    val label: String,
    val greeting: String,
) {
    ADMIN("ADMIN", "Admin", "Good Evening, Admin"),
    MILK_AGENT("MILK_AGENT", "Milk Agent", "Good Evening, Milk Agent"),
    LABOR("LABOR", "Labor", "Good Evening, Labor"),
    OUTSIDE_FARMER("OUTSIDE_FARMER", "Farmer", "Good Evening, Kisan"),
}

@Composable
internal fun DashboardRootScreen(
    initialRole: DashboardRole = DashboardRole.ADMIN,
    laborPermissions: LaborPermissions = LaborPermissions(
        canAddAnimalMilk = true,
        canMarkMilkingDone = true,
        canMarkFeedDone = true,
        canMarkCleaningDone = true,
        canAddHealthObservation = true,
    ),
    userAccounts: List<AppUserAccount> = emptyList(),
    onAddUser: (AppUserAccount) -> Unit = {},
    onBackToLogin: () -> Unit,
) {
    var selectedTab by remember(initialRole) {
        mutableStateOf(if (initialRole == DashboardRole.LABOR) DashboardTab.LIVESTOCK else DashboardTab.HOME)
    }
    var selectedModule by remember { mutableStateOf<DashboardModule?>(null) }
    var activeRole by remember(initialRole) { mutableStateOf(initialRole) }
    val directModule = activeRole.directLandingModule()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DashboardBg)
    ) {
        val module = selectedModule
        if (directModule != null && module == null) {
            DashboardModuleScreen(
                module = directModule,
                activeRole = activeRole,
                bottomPadding = 24,
                onBack = onBackToLogin,
            )
        } else if (module != null) {
            DashboardModuleScreen(
                module = module,
                activeRole = activeRole,
                onBack = { selectedModule = null },
            )
        } else {
            when (selectedTab) {
                DashboardTab.HOME -> DashboardHomeScreen(
                    activeRole = activeRole,
                    onRoleChange = { role ->
                        activeRole = role
                        selectedTab = DashboardTab.HOME
                        selectedModule = null
                    },
                    onBackToLogin = onBackToLogin,
                    onOpenModule = { selectedModule = it },
                )
                DashboardTab.LIVESTOCK -> LivestockMatrixScreen(
                    activeRole = activeRole,
                    laborPermissions = laborPermissions,
                )
                DashboardTab.STAFF -> StaffControlScreen(
                    userAccounts = userAccounts,
                    onAddUser = onAddUser,
                )
                DashboardTab.KHATA -> DashboardKhataScreen()
            }
        }

        if (directModule == null) {
            BottomDashboardNav(
                selectedTab = selectedTab,
                onSelectTab = {
                    if (activeRole.canOpenTab(it)) {
                        selectedModule = null
                        selectedTab = it
                    }
                },
                canOpenTab = activeRole::canOpenTab,
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }
    }
}

private fun DashboardRole.directLandingModule(): DashboardModule? = when (this) {
    DashboardRole.MILK_AGENT -> DashboardModule.OUTSIDE_MILK
    DashboardRole.OUTSIDE_FARMER -> DashboardModule.FARMER_PORTAL
    DashboardRole.ADMIN,
    DashboardRole.LABOR -> null
}

internal fun DashboardRole.canOpen(module: DashboardModule): Boolean = when (this) {
    DashboardRole.ADMIN -> true
    DashboardRole.MILK_AGENT -> module in setOf(
        DashboardModule.OUTSIDE_MILK,
        DashboardModule.FARMER_PORTAL,
        DashboardModule.ACCESS,
    )
    DashboardRole.LABOR -> module in setOf(
        DashboardModule.LABOR_TASKS,
        DashboardModule.HEALTH,
        DashboardModule.ACCESS,
    )
    DashboardRole.OUTSIDE_FARMER -> module in setOf(
        DashboardModule.FARMER_PORTAL,
        DashboardModule.ACCESS,
    )
}

internal fun DashboardRole.canOpenTab(tab: DashboardTab): Boolean = when (this) {
    DashboardRole.ADMIN -> true
    DashboardRole.MILK_AGENT -> tab == DashboardTab.HOME
    DashboardRole.LABOR -> tab == DashboardTab.LIVESTOCK
    DashboardRole.OUTSIDE_FARMER -> tab in setOf(DashboardTab.HOME, DashboardTab.KHATA)
}
