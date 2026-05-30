package com.smartkdfarm.app.auth

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.smartkdfarm.app.AppBackground
import com.smartkdfarm.app.presentation.admin.AdminViewModel
import com.smartkdfarm.app.presentation.auth.AuthViewModel
import com.smartkdfarm.app.presentation.livestock.LivestockViewModel
import com.smartkdfarm.app.presentation.operations.OperationsViewModel

private enum class AuthRoute {
    LOGIN,
    REGISTER,
    DASHBOARD,
}

internal data class AppUserAccount(
    val fullName: String,
    val mobileNumber: String,
    val password: String,
    val role: DashboardRole,
    val farmName: String,
    val canAddAnimalMilk: Boolean = false,
    val canMarkMilkingDone: Boolean = false,
    val canMarkFeedDone: Boolean = false,
    val canMarkCleaningDone: Boolean = false,
    val canAddHealthObservation: Boolean = false,
)

internal data class LaborPermissions(
    val canAddAnimalMilk: Boolean = false,
    val canMarkMilkingDone: Boolean = false,
    val canMarkFeedDone: Boolean = false,
    val canMarkCleaningDone: Boolean = false,
    val canAddHealthObservation: Boolean = false,
)

@Composable
@Suppress("UNUSED_PARAMETER")
fun AuthAppScreen(
    viewModel: AuthViewModel,
    livestockViewModel: LivestockViewModel,
    adminViewModel: AdminViewModel,
    operationsViewModel: OperationsViewModel,
) {
    var currentRoute by remember { mutableStateOf(AuthRoute.LOGIN) }
    var dashboardRole by remember { mutableStateOf(DashboardRole.ADMIN) }
    var laborPermissions by remember {
        mutableStateOf(
            LaborPermissions(
                canAddAnimalMilk = true,
                canMarkMilkingDone = true,
                canMarkFeedDone = true,
                canMarkCleaningDone = true,
                canAddHealthObservation = true,
            )
        )
    }
    val userAccounts = remember { mutableStateListOf<AppUserAccount>() }

    AppBackground {
        Scaffold(containerColor = androidx.compose.ui.graphics.Color.Transparent) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .navigationBarsPadding(),
                contentAlignment = Alignment.Center,
            ) {
                when (currentRoute) {
                    AuthRoute.LOGIN -> LoginScreen(
                        userAccounts = userAccounts,
                        onOpenDashboard = { account ->
                            dashboardRole = account.role
                            laborPermissions = LaborPermissions(
                                canAddAnimalMilk = account.canAddAnimalMilk || account.role == DashboardRole.ADMIN,
                                canMarkMilkingDone = account.canMarkMilkingDone || account.role == DashboardRole.ADMIN,
                                canMarkFeedDone = account.canMarkFeedDone || account.role == DashboardRole.ADMIN,
                                canMarkCleaningDone = account.canMarkCleaningDone || account.role == DashboardRole.ADMIN,
                                canAddHealthObservation = account.canAddHealthObservation || account.role == DashboardRole.ADMIN,
                            )
                            currentRoute = AuthRoute.DASHBOARD
                        },
                        onOpenRegister = { currentRoute = AuthRoute.REGISTER },
                    )
                    AuthRoute.REGISTER -> RegisterScreen(
                        onBack = { currentRoute = AuthRoute.LOGIN },
                        userAccounts = userAccounts,
                        onAdminRegistered = { account ->
                            userAccounts += account
                            dashboardRole = DashboardRole.ADMIN
                            laborPermissions = LaborPermissions(
                                canAddAnimalMilk = true,
                                canMarkMilkingDone = true,
                                canMarkFeedDone = true,
                                canMarkCleaningDone = true,
                                canAddHealthObservation = true,
                            )
                            currentRoute = AuthRoute.DASHBOARD
                        },
                    )
                    AuthRoute.DASHBOARD -> DashboardRootScreen(
                        initialRole = dashboardRole,
                        laborPermissions = laborPermissions,
                        userAccounts = userAccounts,
                        onAddUser = { account -> userAccounts += account },
                        onBackToLogin = { currentRoute = AuthRoute.LOGIN },
                    )
                }
            }
        }
    }
}
