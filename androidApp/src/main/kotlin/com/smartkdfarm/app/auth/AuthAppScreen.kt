package com.smartkdfarm.app.auth

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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

@Composable
@Suppress("UNUSED_PARAMETER")
fun AuthAppScreen(
    viewModel: AuthViewModel,
    livestockViewModel: LivestockViewModel,
    adminViewModel: AdminViewModel,
    operationsViewModel: OperationsViewModel,
) {
    var currentRoute by remember { mutableStateOf(AuthRoute.LOGIN) }

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
                        onOpenDashboard = { currentRoute = AuthRoute.DASHBOARD },
                        onOpenRegister = { currentRoute = AuthRoute.REGISTER },
                    )
                    AuthRoute.REGISTER -> RegisterScreen(
                        onBack = { currentRoute = AuthRoute.LOGIN },
                    )
                    AuthRoute.DASHBOARD -> DashboardRootScreen(
                        onBackToLogin = { currentRoute = AuthRoute.LOGIN },
                    )
                }
            }
        }
    }
}
