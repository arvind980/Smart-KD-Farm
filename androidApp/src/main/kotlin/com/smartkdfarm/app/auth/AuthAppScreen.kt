package com.smartkdfarm.app.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartkdfarm.app.AppBackground
import com.smartkdfarm.app.SmartKdFarmConsole
import com.smartkdfarm.app.core.domain.model.AreaConfiguration
import com.smartkdfarm.app.core.domain.model.AreaUnit
import com.smartkdfarm.app.core.domain.model.FarmLocation
import com.smartkdfarm.app.core.domain.model.FarmRegistrationCommand
import com.smartkdfarm.app.core.domain.model.LandPartition
import com.smartkdfarm.app.core.domain.model.ManagedUserRegistration
import com.smartkdfarm.app.core.domain.model.MilkSession
import com.smartkdfarm.app.core.domain.model.PermissionLevel
import com.smartkdfarm.app.core.domain.model.StaffAccessProfile
import com.smartkdfarm.app.core.domain.model.StaffModule
import com.smartkdfarm.app.core.domain.model.StaffPermission
import com.smartkdfarm.app.core.domain.model.UserRole
import com.smartkdfarm.app.core.domain.model.AutoRateBreakdown
import com.smartkdfarm.app.core.domain.model.InventoryCategory
import com.smartkdfarm.app.core.domain.model.KhataEntryKind
import com.smartkdfarm.app.core.domain.model.MilkCollectionEntry
import com.smartkdfarm.app.core.domain.model.NotificationStatus
import com.smartkdfarm.app.presentation.admin.AdminUiState
import com.smartkdfarm.app.presentation.admin.AdminViewModel
import com.smartkdfarm.app.presentation.auth.AuthUiState
import com.smartkdfarm.app.presentation.auth.AuthViewModel
import com.smartkdfarm.app.presentation.livestock.AnimalCardUiModel
import com.smartkdfarm.app.presentation.livestock.LivestockStatusSummary
import com.smartkdfarm.app.presentation.livestock.LivestockViewModel
import com.smartkdfarm.app.presentation.operations.OperationsUiState
import com.smartkdfarm.app.presentation.operations.OperationsViewModel

// Premium Color Palette
val PremiumGreen = Color(0xFF7EB55D)
val GlassWhite = Color(0xDDFFFFFF)
val TextDark = Color(0xFF1B2B1B)
val TextLight = Color(0xFF5A6A5A)
val SoftWhite = Color(0xFFF8FAF8)

@Composable
fun AuthAppScreen(
    viewModel: AuthViewModel,
    livestockViewModel: LivestockViewModel,
    adminViewModel: AdminViewModel,
    operationsViewModel: OperationsViewModel,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val livestockState by livestockViewModel.uiState.collectAsStateWithLifecycle()
    val adminState by adminViewModel.uiState.collectAsStateWithLifecycle()
    val operationsState by operationsViewModel.uiState.collectAsStateWithLifecycle()
    var currentRoute by remember { mutableStateOf(AuthRoute.LOGIN) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Show error as a Snackbar (Toast-like behavior)
    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let {
            if (it.isNotBlank()) {
                snackbarHostState.showSnackbar(
                    message = it,
                    duration = SnackbarDuration.Short
                )
                viewModel.clearTransientMessage()
            }
        }
    }

    LaunchedEffect(livestockState.errorMessage) {
        livestockState.errorMessage?.let {
            if (it.isNotBlank()) {
                snackbarHostState.showSnackbar(
                    message = it,
                    duration = SnackbarDuration.Short
                )
                livestockViewModel.clearTransientMessage()
            }
        }
    }

    LaunchedEffect(adminState.errorMessage) {
        adminState.errorMessage?.let {
            if (it.isNotBlank()) {
                snackbarHostState.showSnackbar(
                    message = it,
                    duration = SnackbarDuration.Short
                )
                adminViewModel.clearTransientMessage()
            }
        }
    }

    LaunchedEffect(operationsState.errorMessage) {
        operationsState.errorMessage?.let {
            if (it.isNotBlank()) {
                snackbarHostState.showSnackbar(
                    message = it,
                    duration = SnackbarDuration.Short
                )
                operationsViewModel.clearTransientMessage()
            }
        }
    }

    AppBackground {
        Scaffold(
            containerColor = Color.Transparent,
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .navigationBarsPadding(),
                contentAlignment = Alignment.Center,
            ) {
                when {
                    state.isLoading -> CircularProgressIndicator(color = Color.White, strokeWidth = 3.dp)
                    state.currentUser == null && currentRoute == AuthRoute.LOGIN -> AuthEntryScreen(
                        onLogin = viewModel::login,
                        onOpenFarmRegistration = { currentRoute = AuthRoute.ADD_FARM }
                    )
                    state.currentUser == null && currentRoute == AuthRoute.ADD_FARM -> FarmRegistrationScreen(
                        onRegisterFarm = viewModel::registerFarm,
                        onBack = { currentRoute = AuthRoute.LOGIN }
                    )
                    else -> RoleDashboard(
                        state = state,
                        livestockCards = livestockState.animalCards,
                        livestockSummary = livestockState.statusSummary,
                        adminState = adminState,
                        operationsState = operationsState,
                        onAddManagedUser = viewModel::addManagedUser,
                        onSaveStaffProfile = adminViewModel::saveStaffProfile,
                        onDeactivateStaffProfile = adminViewModel::deactivateStaffProfile,
                        onSaveFarmSetup = adminViewModel::saveFarmSetup,
                        onRecordMilkCollection = operationsViewModel::recordMilkCollection,
                        onSaveInventoryItem = operationsViewModel::saveInventoryItem,
                        onProduceFeedBatch = operationsViewModel::produceSimpleFeedBatch,
                        onRecordManualLedger = operationsViewModel::recordManualLedgerEntry,
                        onGeneratePredictions = operationsViewModel::generatePredictions,
                        onUpdateNotificationStatus = operationsViewModel::updateNotificationStatus,
                        onLogout = viewModel::logout,
                    )
                }
            }
        }
    }
}

@Composable
private fun AuthEntryScreen(
    onLogin: (String, String) -> Unit,
    onOpenFarmRegistration: () -> Unit,
) {
    var mobileNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .widthIn(max = 420.dp)
            .statusBarsPadding()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Welcome Back!",
                style = TextStyle(
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = 0.5.sp,
                    shadow = Shadow(
                        color = Color.Black.copy(alpha = 0.2f),
                        offset = Offset(0f, 4f),
                        blurRadius = 8f
                    )
                )
            )
            Text(
                text = "Securely access your farm operations",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color.White.copy(alpha = 0.9f),
                    fontWeight = FontWeight.Medium
                )
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        DairyCard {
            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        "Sign In",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                    )
                    HorizontalDivider(thickness = 2.dp, color = PremiumGreen.copy(alpha = 0.3f), modifier = Modifier.width(40.dp))
                }

                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    DairyTextField(
                        value = mobileNumber,
                        onValueChange = { mobileNumber = it },
                        label = "Mobile Number",
                        placeholder = "Mobile Number",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    )
                    DairyTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = "Password",
                        placeholder = "Password",
                        visualTransformation = PasswordVisualTransformation(),
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))

                PremiumButton(
                    text = "Login",
                    onClick = { 
                        val email = if (mobileNumber.contains("@")) mobileNumber else "${mobileNumber.filter { it.isDigit() || it == '+' }}@mail.com"
                        onLogin(email, password) 
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("New here?", color = TextLight, style = MaterialTheme.typography.bodyMedium)
                    TextButton(onClick = onOpenFarmRegistration) {
                        Text(
                            "Create an Account",
                            color = PremiumGreen,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(48.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FarmRegistrationScreen(
    onRegisterFarm: (FarmRegistrationCommand) -> Unit,
    onBack: () -> Unit,
) {
    var farmName by remember { mutableStateOf("Smart KD Farm") }
    var adminName by remember { mutableStateOf("") }
    var adminPhone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var village by remember { mutableStateOf("") }
    var district by remember { mutableStateOf("") }
    var stateName by remember { mutableStateOf("Punjab") }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Absolute top positioning for standard navigation feel
        Spacer(modifier = Modifier.height(11.dp))

        // Navigation Header Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PremiumIconButton(onClick = onBack)
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Join Us",
                style = TextStyle(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    shadow = Shadow(Color.Black.copy(alpha = 0.15f), offset = Offset(0f, 2f), blurRadius = 4f)
                )
            )
            Spacer(modifier = Modifier.weight(1f))
            // Placeholder to keep the title perfectly centered
            Box(modifier = Modifier.size(44.dp)) 
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Scrollable content area
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DairyCard(modifier = Modifier.widthIn(max = 480.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        DairyTextField(value = farmName, onValueChange = { farmName = it }, label = "Farm Name", placeholder = "Farm Name")
                    }
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(modifier = Modifier.weight(1f)) {
                            DairyTextField(value = village, onValueChange = { village = it }, label = "Village", placeholder = "Village")
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            DairyTextField(value = district, onValueChange = { district = it }, label = "District", placeholder = "District")
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        DairyTextField(value = stateName, onValueChange = { stateName = it }, label = "State", placeholder = "State")
                    }
                    
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        DairyTextField(value = adminName, onValueChange = { adminName = it }, label = "Admin Name", placeholder = "Admin")
                        DairyTextField(value = adminPhone, onValueChange = { adminPhone = it }, label = "Mobile No.", placeholder = "Mobile No.", keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone))
                        DairyTextField(value = password, onValueChange = { password = it }, label = "Password", placeholder = "Password", visualTransformation = PasswordVisualTransformation())
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    PremiumButton(
                        text = "Register Farm",
                        onClick = {
                            val formattedEmail = "${adminPhone.filter { it.isDigit() || it == '+' }}@mail.com"
                            onRegisterFarm(
                                FarmRegistrationCommand(
                                    farmName = farmName,
                                    adminName = adminName,
                                    adminEmail = formattedEmail,
                                    adminPassword = password,
                                    adminPhoneNumber = adminPhone,
                                    location = FarmLocation(village = village, district = district, state = stateName),
                                    landArea = AreaConfiguration(value = 12.0, unit = AreaUnit.BIGHA)
                                )
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Precise 8dp gap from bottom
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun RoleDashboard(
    state: AuthUiState,
    livestockCards: List<AnimalCardUiModel>,
    livestockSummary: List<LivestockStatusSummary>,
    adminState: AdminUiState,
    operationsState: OperationsUiState,
    onAddManagedUser: (ManagedUserRegistration) -> Unit,
    onSaveStaffProfile: (StaffAccessProfile) -> Unit,
    onDeactivateStaffProfile: (String) -> Unit,
    onSaveFarmSetup: (List<LandPartition>, String?) -> Unit,
    onRecordMilkCollection: (MilkCollectionEntry) -> Unit,
    onSaveInventoryItem: (String?, String, InventoryCategory, String, Double, Double, String?) -> Unit,
    onProduceFeedBatch: (String, Double, String, Double) -> Unit,
    onRecordManualLedger: (String, Double, KhataEntryKind, String?) -> Unit,
    onGeneratePredictions: () -> Unit,
    onUpdateNotificationStatus: (com.smartkdfarm.app.core.domain.model.FarmerNotification, NotificationStatus) -> Unit,
    onLogout: () -> Unit,
) {
    val isAdmin = state.currentRole == UserRole.ADMIN
    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            SmartKdFarmConsole(
                managerName = state.currentUser?.fullName.orEmpty(),
                farmName = state.farmProfile?.farmName ?: "Smart KD Farm",
                currentRoleLabel = state.currentRole?.name ?: "ADMIN",
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (livestockSummary.isNotEmpty()) {
                    LivestockStatusRow(summaries = livestockSummary)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                PremiumButton(
                    text = "Sign Out",
                    onClick = onLogout,
                    containerColor = Color(0xFFD24D57),
                    modifier = Modifier.widthIn(max = 240.dp),
                )
            }
        }
        if (isAdmin) {
            AdminControlPanel(
                farmId = state.currentUser?.farmId.orEmpty(),
                adminState = adminState,
                onAddManagedUser = onAddManagedUser,
                onSaveStaffProfile = onSaveStaffProfile,
                onDeactivateStaffProfile = onDeactivateStaffProfile,
                onSaveFarmSetup = onSaveFarmSetup,
            )
        }
        OperationsControlPanel(
            farmId = state.currentUser?.farmId.orEmpty(),
            operationsState = operationsState,
            livestockSummary = livestockSummary,
            canManage = state.currentRole != UserRole.FARMER,
            onRecordMilkCollection = onRecordMilkCollection,
            onSaveInventoryItem = onSaveInventoryItem,
            onProduceFeedBatch = onProduceFeedBatch,
            onRecordManualLedger = onRecordManualLedger,
            onGeneratePredictions = onGeneratePredictions,
            onUpdateNotificationStatus = onUpdateNotificationStatus,
        )
    }
}

@Composable
private fun AdminControlPanel(
    farmId: String,
    adminState: AdminUiState,
    onAddManagedUser: (ManagedUserRegistration) -> Unit,
    onSaveStaffProfile: (StaffAccessProfile) -> Unit,
    onDeactivateStaffProfile: (String) -> Unit,
    onSaveFarmSetup: (List<LandPartition>, String?) -> Unit,
) {
    var staffName by remember { mutableStateOf("") }
    var staffPhone by remember { mutableStateOf("") }
    var role by remember { mutableStateOf(UserRole.DAIRY_MAN) }
    var permissionLevel by remember { mutableStateOf(PermissionLevel.MANAGE) }
    var note by remember { mutableStateOf("") }
    var partitionName by remember { mutableStateOf("") }
    var partitionArea by remember { mutableStateOf("") }
    var partitionUse by remember { mutableStateOf("") }
    var editingStaffId by remember { mutableStateOf("") }
    val draftPartitions = remember { mutableStateListOf<LandPartition>() }
    val selectedModules = remember {
        mutableStateListOf(
            StaffModule.DASHBOARD,
            StaffModule.OUTER_CENTER,
            StaffModule.KHATA,
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        DairyCard {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    "Admin Control Panel",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = TextDark,
                    )
                )
                Text(
                    "Add labour or dairy man, assign access, and validate farm land setup from one place.",
                    color = TextLight,
                    style = MaterialTheme.typography.bodyMedium,
                )
                if (adminState.isLoading) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        color = PremiumGreen,
                        trackColor = PremiumGreen.copy(alpha = 0.15f),
                    )
                }
            }
        }

        DairyCard {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SectionTitle("Staff Access Configurator")
                DairyTextField(
                    value = staffName,
                    onValueChange = { staffName = it },
                    label = "Staff Name",
                    placeholder = "Raju Dairy Man",
                )
                DairyTextField(
                    value = staffPhone,
                    onValueChange = { staffPhone = it },
                    label = "Phone Number",
                    placeholder = "9876543210",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                )
                Text("Role", color = TextLight, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    RoleChip(role = UserRole.DAIRY_MAN, selectedRole = role) {
                        role = it
                        selectedModules.clear()
                        selectedModules.addAll(listOf(StaffModule.DASHBOARD, StaffModule.OUTER_CENTER, StaffModule.KHATA))
                    }
                    RoleChip(role = UserRole.LABOUR, selectedRole = role) {
                        role = it
                        selectedModules.clear()
                        selectedModules.addAll(listOf(StaffModule.DASHBOARD, StaffModule.INVENTORY, StaffModule.LIVESTOCK))
                    }
                }
                Text("Modules", color = TextLight, fontWeight = FontWeight.Bold)
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    StaffModule.entries.chunked(3).forEach { rowModules ->
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            rowModules.forEach { module ->
                                FilterChip(
                                    selected = selectedModules.contains(module),
                                    onClick = {
                                        if (selectedModules.contains(module)) {
                                            selectedModules.remove(module)
                                        } else {
                                            selectedModules.add(module)
                                        }
                                    },
                                    label = { Text(module.name.replace("_", " ")) },
                                )
                            }
                        }
                    }
                }
                Text("Access Level", color = TextLight, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PermissionChip(PermissionLevel.VIEW, permissionLevel) { permissionLevel = it }
                    PermissionChip(PermissionLevel.MANAGE, permissionLevel) { permissionLevel = it }
                    PermissionChip(PermissionLevel.APPROVE, permissionLevel) { permissionLevel = it }
                }
                PremiumButton(
                    text = if (editingStaffId.isBlank()) "Add Staff" else "Update Staff",
                    onClick = {
                        val normalizedPhone = staffPhone.filter { it.isDigit() || it == '+' }
                        val email = "$normalizedPhone@mail.com"
                        onAddManagedUser(
                            ManagedUserRegistration(
                                fullName = staffName,
                                email = email,
                                phoneNumber = staffPhone,
                                role = role,
                            )
                        )
                        onSaveStaffProfile(
                            {
                                val now = System.currentTimeMillis()
                                StaffAccessProfile(
                                    id = editingStaffId,
                                    farmId = farmId,
                                    userId = "",
                                    fullName = staffName,
                                    role = role,
                                    phoneNumber = staffPhone,
                                    permissions = selectedModules.distinct().map { StaffPermission(it, permissionLevel) },
                                    shiftLabel = if (role == UserRole.DAIRY_MAN) "Milk Collection" else "Farm Operations",
                                    createdAtEpochMillis = now,
                                    updatedAtEpochMillis = now,
                                )
                            }()
                        )
                        staffName = ""
                        staffPhone = ""
                        role = UserRole.DAIRY_MAN
                        editingStaffId = ""
                        permissionLevel = PermissionLevel.MANAGE
                        selectedModules.clear()
                        selectedModules.addAll(listOf(StaffModule.DASHBOARD, StaffModule.OUTER_CENTER, StaffModule.KHATA))
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
                adminState.staffProfiles.forEach { staff ->
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = SoftWhite,
                        tonalElevation = 1.dp,
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(staff.fullName, color = TextDark, fontWeight = FontWeight.Bold)
                                    Text(
                                        "${staff.role.name.replace("_", " ")} • ${staff.phoneNumber}",
                                        color = TextLight,
                                        style = MaterialTheme.typography.bodySmall,
                                    )
                                }
                                Row {
                                    TextButton(
                                        onClick = {
                                            editingStaffId = staff.id
                                            staffName = staff.fullName
                                            staffPhone = staff.phoneNumber
                                            role = staff.role
                                            permissionLevel = staff.permissions.firstOrNull()?.level ?: PermissionLevel.MANAGE
                                            selectedModules.clear()
                                            selectedModules.addAll(staff.permissions.map { it.module })
                                        },
                                    ) {
                                        Text("Edit", color = PremiumGreen, fontWeight = FontWeight.Bold)
                                    }
                                    TextButton(
                                        onClick = { onDeactivateStaffProfile(staff.id) },
                                        enabled = staff.isActive,
                                    ) {
                                        Text(
                                            if (staff.isActive) "Deactivate" else "Inactive",
                                            color = if (staff.isActive) Color(0xFFD24D57) else TextLight,
                                            fontWeight = FontWeight.Bold,
                                        )
                                    }
                                }
                            }
                            Text(
                                staff.permissions.joinToString("  ") {
                                    "${it.module.name.lowercase()}=${it.level.name.lowercase()}"
                                },
                                color = TextLight,
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                    }
                }
            }
        }

        DairyCard {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SectionTitle("Farm Setup Validation")
                DairyTextField(
                    value = partitionName,
                    onValueChange = { partitionName = it },
                    label = "Partition Name",
                    placeholder = "Green Fodder",
                )
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        DairyTextField(
                            value = partitionArea,
                            onValueChange = { partitionArea = it },
                            label = "Area (Acre)",
                            placeholder = "2.5",
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        DairyTextField(
                            value = partitionUse,
                            onValueChange = { partitionUse = it },
                            label = "Use / Crop",
                            placeholder = "Napier",
                        )
                    }
                }
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Setup Note") },
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SoftWhite,
                        unfocusedContainerColor = SoftWhite,
                        focusedBorderColor = PremiumGreen,
                        unfocusedBorderColor = Color.Transparent,
                    ),
                )
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    PremiumButton(
                        text = "Add Partition",
                        onClick = {
                            val area = partitionArea.toDoubleOrNull() ?: 0.0
                            if (partitionName.isNotBlank() && partitionUse.isNotBlank() && area > 0.0) {
                                draftPartitions += LandPartition(
                                    id = "",
                                    farmId = farmId,
                                    name = partitionName,
                                    area = AreaConfiguration(value = area, unit = AreaUnit.ACRE),
                                    cropOrUse = partitionUse,
                                    colorHex = if (draftPartitions.size % 2 == 0) "#7EB55D" else "#134E5E",
                                )
                                partitionName = ""
                                partitionArea = ""
                                partitionUse = ""
                            }
                        },
                        modifier = Modifier.weight(1f),
                    )
                    PremiumButton(
                        text = "Save Setup",
                        onClick = {
                            val allPartitions = (adminState.landPartitions + draftPartitions).distinctBy {
                                "${it.name}_${it.area.value}_${it.cropOrUse}"
                            }
                            onSaveFarmSetup(allPartitions, note)
                            draftPartitions.clear()
                        },
                        modifier = Modifier.weight(1f),
                        containerColor = Color(0xFF1F8A70),
                    )
                }
                val combinedPartitions = adminState.landPartitions + draftPartitions
                combinedPartitions.forEach { partition ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(partition.name, color = TextDark, fontWeight = FontWeight.Bold)
                            Text(
                                "${partition.cropOrUse} • ${partition.area.value} ${partition.area.unit.name.lowercase()}",
                                color = TextLight,
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                    }
                }
                adminState.farmSetupConfig?.let { setup ->
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = PremiumGreen.copy(alpha = 0.12f),
                    ) {
                        Text(
                            text = "Saved setup: ${setup.partitionAreaTotalInAcres} acres allocated • ${setup.validationStatus}",
                            modifier = Modifier.padding(12.dp),
                            color = TextDark,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RoleChip(
    role: UserRole,
    selectedRole: UserRole,
    onSelect: (UserRole) -> Unit,
) {
    FilterChip(
        selected = role == selectedRole,
        onClick = { onSelect(role) },
        label = {
            Text(role.name.replace("_", " "))
        },
    )
}

@Composable
private fun PermissionChip(
    level: PermissionLevel,
    selectedLevel: PermissionLevel,
    onSelect: (PermissionLevel) -> Unit,
) {
    FilterChip(
        selected = level == selectedLevel,
        onClick = { onSelect(level) },
        label = { Text(level.name) },
    )
}

@Composable
private fun DashboardRecordRow(
    title: String,
    subtitle: String,
    trailing: String,
    trailingColor: Color = TextDark,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = TextDark, fontWeight = FontWeight.Bold)
            Text(subtitle, color = TextLight, style = MaterialTheme.typography.bodySmall)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(trailing, color = trailingColor, fontWeight = FontWeight.ExtraBold)
    }
}

@Composable
private fun OperationsControlPanel(
    farmId: String,
    operationsState: OperationsUiState,
    livestockSummary: List<LivestockStatusSummary>,
    canManage: Boolean,
    onRecordMilkCollection: (MilkCollectionEntry) -> Unit,
    onSaveInventoryItem: (String?, String, InventoryCategory, String, Double, Double, String?) -> Unit,
    onProduceFeedBatch: (String, Double, String, Double) -> Unit,
    onRecordManualLedger: (String, Double, KhataEntryKind, String?) -> Unit,
    onGeneratePredictions: () -> Unit,
    onUpdateNotificationStatus: (com.smartkdfarm.app.core.domain.model.FarmerNotification, NotificationStatus) -> Unit,
) {
    var farmerName by remember { mutableStateOf("") }
    var farmerSearchQuery by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var fat by remember { mutableStateOf("6.0") }
    var snf by remember { mutableStateOf("8.6") }
    var editingInventoryId by remember { mutableStateOf("") }
    var inventoryName by remember { mutableStateOf("") }
    var inventoryUnit by remember { mutableStateOf("kg") }
    var inventoryStock by remember { mutableStateOf("") }
    var reorderLevel by remember { mutableStateOf("") }
    var inventoryVendor by remember { mutableStateOf("") }
    var inventoryCategory by remember { mutableStateOf(InventoryCategory.FEED) }
    var batchName by remember { mutableStateOf("") }
    var batchOutput by remember { mutableStateOf("") }
    var batchIngredientQty by remember { mutableStateOf("") }
    var selectedInventoryId by remember { mutableStateOf("") }
    var ledgerTitle by remember { mutableStateOf("") }
    var ledgerAmount by remember { mutableStateOf("") }
    var ledgerKind by remember { mutableStateOf(KhataEntryKind.EXPENSE) }
    val totalIncome = operationsState.ledgerEntries.filter { it.kind == KhataEntryKind.INCOME }.sumOf { it.amount }
    val totalExpense = operationsState.ledgerEntries.filter { it.kind == KhataEntryKind.EXPENSE }.sumOf { it.amount }
    val netPosition = totalIncome - totalExpense
    val activeAnimals = livestockSummary.sumOf { it.count }
    val topFarmer = operationsState.milkCollections
        .groupBy { it.farmerName }
        .mapValues { (_, list) -> list.sumOf { it.rateBreakdown.totalAmount } }
        .maxByOrNull { it.value }
    val farmerSummaries = operationsState.milkCollections
        .groupBy { it.farmerName }
        .map { (name, records) ->
            Triple(
                name,
                records.sumOf { it.rateBreakdown.quantityLiters },
                records.sumOf { it.rateBreakdown.totalAmount }
            )
        }
        .filter { it.first.contains(farmerSearchQuery, ignoreCase = true) }
        .sortedByDescending { it.third }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        DairyCard {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "Operations Panel",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = TextDark,
                    )
                )
                Text(
                    "Live outer center, inventory, khata, and prediction controls.",
                    color = TextLight,
                )
                if (operationsState.isLoading) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        color = PremiumGreen,
                        trackColor = PremiumGreen.copy(alpha = 0.15f),
                    )
                }
            }
        }

        DairyCard {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SectionTitle("Live Dashboard Cards")
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    MiniMetricCard("Total Milk", "${operationsState.milkCollections.sumOf { it.rateBreakdown.quantityLiters }.toInt()}L", Modifier.weight(1f))
                    MiniMetricCard("Animals", activeAnimals.toString(), Modifier.weight(1f))
                    MiniMetricCard("Net", "Rs ${netPosition.toInt()}", Modifier.weight(1f), highlight = true)
                }
                if (operationsState.predictions.isNotEmpty()) {
                    Text("Live Supply Chart", color = TextDark, fontWeight = FontWeight.Bold)
                    operationsState.predictions.forEach { point ->
                        val maxLiters = operationsState.predictions.maxOf { it.projectedMilkLiters }.takeIf { it > 0 } ?: 1.0
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(point.label, color = TextDark)
                                Text("${point.projectedMilkLiters}L", color = TextLight)
                            }
                            LinearProgressIndicator(
                                progress = { (point.projectedMilkLiters / maxLiters).toFloat() },
                                modifier = Modifier.fillMaxWidth(),
                                color = PremiumGreen,
                                trackColor = PremiumGreen.copy(alpha = 0.12f),
                            )
                        }
                    }
                }
            }
        }

        if (canManage) {
            DairyCard {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SectionTitle("Outer Center")
                    DairyTextField(farmerName, { farmerName = it }, label = "Farmer Name", placeholder = "Kisan Mohan")
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(Modifier.weight(1f)) {
                            DairyTextField(quantity, { quantity = it }, label = "Milk (L)", placeholder = "10.5", keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                        }
                        Box(Modifier.weight(1f)) {
                            DairyTextField(fat, { fat = it }, label = "FAT", placeholder = "6.0", keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                        }
                        Box(Modifier.weight(1f)) {
                            DairyTextField(snf, { snf = it }, label = "SNF", placeholder = "8.6", keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                        }
                    }
                    val qty = quantity.toDoubleOrNull() ?: 0.0
                    val fatValue = fat.toDoubleOrNull() ?: 0.0
                    val snfValue = snf.toDoubleOrNull() ?: 0.0
                    val rate = (42 + fatValue + (snfValue / 2.0))
                    val total = qty * rate
                    Text(
                        "Auto Rate Preview: Rs ${"%.2f".format(rate)}/L • Total Rs ${"%.2f".format(total)}",
                        color = TextDark,
                        fontWeight = FontWeight.Bold,
                    )
                    PremiumButton(
                        text = "Record Milk Collection",
                        onClick = {
                            onRecordMilkCollection(
                                MilkCollectionEntry(
                                    id = "",
                                    farmId = farmId,
                                    farmerId = farmerName.lowercase().replace(" ", "_"),
                                    farmerName = farmerName,
                                    session = MilkSession.MORNING,
                                    collectedAtEpochMillis = System.currentTimeMillis(),
                                    rateBreakdown = AutoRateBreakdown(
                                        fat = fatValue,
                                        snf = snfValue,
                                        quantityLiters = qty,
                                        baseRatePerLiter = rate,
                                        totalAmount = total,
                                    ),
                                    createdByUserId = "",
                                )
                            )
                            farmerName = ""
                            quantity = ""
                        },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    operationsState.milkCollections.take(3).forEach { record ->
                        DashboardRecordRow(
                            title = "${record.farmerName} • ${record.rateBreakdown.quantityLiters}L",
                            subtitle = "FAT ${record.rateBreakdown.fat} • SNF ${record.rateBreakdown.snf}",
                            trailing = "Rs ${record.rateBreakdown.totalAmount.toInt()}",
                        )
                    }
                    operationsState.notifications.take(2).forEach { alert ->
                        Text(alert.message, color = TextLight, style = MaterialTheme.typography.bodySmall)
                    }
                    if (operationsState.notifications.isNotEmpty()) {
                        Text("SMS Delivery Tracking", color = TextDark, fontWeight = FontWeight.Bold)
                        operationsState.notifications.take(4).forEach { notification ->
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = SoftWhite,
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    Text(notification.message, color = TextDark, style = MaterialTheme.typography.bodySmall)
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        NotificationStatus.entries.forEach { status ->
                                            FilterChip(
                                                selected = notification.status == status,
                                                onClick = { onUpdateNotificationStatus(notification, status) },
                                                label = { Text(status.name) },
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            DairyCard {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SectionTitle("Inventory & Feed")
                    DairyTextField(inventoryName, { inventoryName = it }, label = "Item Name", placeholder = "Makka")
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(Modifier.weight(1f)) {
                            DairyTextField(inventoryStock, { inventoryStock = it }, label = "Stock", placeholder = "120", keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                        }
                        Box(Modifier.weight(1f)) {
                            DairyTextField(reorderLevel, { reorderLevel = it }, label = "Reorder", placeholder = "40", keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(Modifier.weight(1f)) {
                            DairyTextField(inventoryUnit, { inventoryUnit = it }, label = "Unit", placeholder = "kg")
                        }
                        Box(Modifier.weight(1f)) {
                            DairyTextField(inventoryVendor, { inventoryVendor = it }, label = "Vendor", placeholder = "Arora Feed")
                        }
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        InventoryCategory.entries.chunked(3).forEach { row ->
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                row.forEach { category ->
                                    FilterChip(
                                        selected = inventoryCategory == category,
                                        onClick = { inventoryCategory = category },
                                        label = { Text(category.name) },
                                    )
                                }
                            }
                        }
                    }
                    PremiumButton(
                        text = if (editingInventoryId.isBlank()) "Save Inventory Item" else "Update Inventory Item",
                        onClick = {
                            onSaveInventoryItem(
                                editingInventoryId.ifBlank { null },
                                inventoryName,
                                inventoryCategory,
                                inventoryUnit,
                                inventoryStock.toDoubleOrNull() ?: 0.0,
                                reorderLevel.toDoubleOrNull() ?: 0.0,
                                inventoryVendor.ifBlank { null },
                            )
                            editingInventoryId = ""
                            inventoryName = ""
                            inventoryStock = ""
                            reorderLevel = ""
                        },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    if (operationsState.inventoryItems.isNotEmpty()) {
                        Text("Select ingredient for batch", color = TextLight, fontWeight = FontWeight.Bold)
                        operationsState.inventoryItems.take(4).forEach { item ->
                            FilterChip(
                                selected = selectedInventoryId == item.id,
                                onClick = { selectedInventoryId = item.id },
                                label = { Text("${item.name} (${item.currentStock} ${item.unit})") },
                            )
                        }
                        DairyTextField(batchName, { batchName = it }, label = "Batch Name", placeholder = "Morning Mix")
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Box(Modifier.weight(1f)) {
                                DairyTextField(batchOutput, { batchOutput = it }, label = "Output Kg", placeholder = "125", keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                            }
                            Box(Modifier.weight(1f)) {
                                DairyTextField(batchIngredientQty, { batchIngredientQty = it }, label = "Ingredient Qty", placeholder = "40", keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                            }
                        }
                        PremiumButton(
                            text = "Produce Feed Batch",
                            onClick = {
                                if (selectedInventoryId.isNotBlank()) {
                                    onProduceFeedBatch(
                                        batchName,
                                        batchOutput.toDoubleOrNull() ?: 0.0,
                                        selectedInventoryId,
                                        batchIngredientQty.toDoubleOrNull() ?: 0.0,
                                    )
                                    batchName = ""
                                    batchOutput = ""
                                    batchIngredientQty = ""
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            containerColor = Color(0xFF1F8A70),
                        )
                    }
                    operationsState.inventoryItems.take(4).forEach { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            DashboardRecordRow(
                                title = item.name,
                                subtitle = "${item.category.name} • reorder ${item.reorderLevel} ${item.unit}",
                                trailing = "${item.currentStock} ${item.unit}",
                                trailingColor = if (item.currentStock <= item.reorderLevel) Color(0xFFD24D57) else TextDark,
                            )
                            TextButton(
                                onClick = {
                                    editingInventoryId = item.id
                                    inventoryName = item.name
                                    inventoryUnit = item.unit
                                    inventoryStock = item.currentStock.toString()
                                    reorderLevel = item.reorderLevel.toString()
                                    inventoryVendor = item.vendorName.orEmpty()
                                    inventoryCategory = item.category
                                }
                            ) {
                                Text("Edit", color = PremiumGreen, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            DairyCard {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SectionTitle("Khata Book")
                    DairyTextField(ledgerTitle, { ledgerTitle = it }, label = "Entry Title", placeholder = "Diesel Expense")
                    DairyTextField(ledgerAmount, { ledgerAmount = it }, label = "Amount", placeholder = "1500", keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(selected = ledgerKind == KhataEntryKind.INCOME, onClick = { ledgerKind = KhataEntryKind.INCOME }, label = { Text("Income") })
                        FilterChip(selected = ledgerKind == KhataEntryKind.EXPENSE, onClick = { ledgerKind = KhataEntryKind.EXPENSE }, label = { Text("Expense") })
                    }
                    PremiumButton(
                        text = "Post Ledger Entry",
                        onClick = {
                            onRecordManualLedger(
                                ledgerTitle,
                                ledgerAmount.toDoubleOrNull() ?: 0.0,
                                ledgerKind,
                                "Manual entry from dashboard",
                            )
                            ledgerTitle = ""
                            ledgerAmount = ""
                        },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    operationsState.ledgerEntries.take(5).forEach { entry ->
                        DashboardRecordRow(
                            title = entry.title,
                            subtitle = entry.sourceModule,
                            trailing = "${if (entry.kind == KhataEntryKind.INCOME) "+" else "-"} Rs ${entry.amount.toInt()}",
                            trailingColor = if (entry.kind == KhataEntryKind.INCOME) PremiumGreen else Color(0xFFD24D57),
                        )
                    }
                }
            }
        }

        DairyCard {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SectionTitle("Farmer Master & Search")
                DairyTextField(
                    value = farmerSearchQuery,
                    onValueChange = { farmerSearchQuery = it },
                    label = "Search Farmer",
                    placeholder = "Search by farmer name",
                )
                if (farmerSummaries.isEmpty()) {
                    Text("No farmer records yet.", color = TextLight)
                } else {
                    farmerSummaries.take(8).forEach { farmer ->
                        DashboardRecordRow(
                            title = farmer.first,
                            subtitle = "${"%.1f".format(farmer.second)}L total collection",
                            trailing = "Rs ${farmer.third.toInt()}",
                        )
                    }
                }
            }
        }

        DairyCard {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SectionTitle("Predictions & Alerts")
                PremiumButton(
                    text = "Refresh 3-Month Prediction",
                    onClick = onGeneratePredictions,
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = Color(0xFF2E7D68),
                )
                operationsState.predictions.forEach { point ->
                    DashboardRecordRow(
                        title = point.label,
                        subtitle = "Projected milk ${point.projectedMilkLiters}L",
                        trailing = "Rs ${point.projectedRevenue.toInt()}",
                    )
                }
                operationsState.alerts.take(3).forEach { alert ->
                    Text("${alert.title}: ${alert.message}", color = TextLight, style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        DairyCard {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SectionTitle("Reports UI")
                MiniMetricCard("Income", "Rs ${totalIncome.toInt()}", Modifier.fillMaxWidth())
                MiniMetricCard("Expense", "Rs ${totalExpense.toInt()}", Modifier.fillMaxWidth())
                MiniMetricCard("Net P&L", "Rs ${netPosition.toInt()}", Modifier.fillMaxWidth(), highlight = true)
                topFarmer?.let {
                    Text("Top Farmer: ${it.key} • Rs ${it.value.toInt()}", color = TextDark, fontWeight = FontWeight.Bold)
                }
                val lowStockItems = operationsState.inventoryItems.filter { it.currentStock <= it.reorderLevel }
                if (lowStockItems.isNotEmpty()) {
                    Text("Low Stock Alerts", color = Color(0xFFD24D57), fontWeight = FontWeight.ExtraBold)
                    lowStockItems.forEach { item ->
                        Text("${item.name}: ${item.currentStock} ${item.unit}", color = TextLight)
                    }
                }
            }
        }
    }
}

@Composable
private fun MiniMetricCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    highlight: Boolean = false,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = if (highlight) Color(0xFF133C38) else SoftWhite,
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(label, color = if (highlight) Color.White.copy(alpha = 0.75f) else TextLight)
            Text(
                value,
                color = if (highlight) Color.White else TextDark,
                fontWeight = FontWeight.ExtraBold,
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }
}

@Composable
private fun LivestockStatusRow(summaries: List<LivestockStatusSummary>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        summaries.forEach { summary ->
            Surface(
                color = colorFromHex(summary.colorHex).copy(alpha = 0.16f),
                contentColor = colorFromHex(summary.colorHex),
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, colorFromHex(summary.colorHex).copy(alpha = 0.35f)),
            ) {
                Text(
                    text = "${summary.label} ${summary.count}",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                )
            }
        }
    }
}

@Composable
private fun LivestockProfileCard(card: AnimalCardUiModel) {
    val accent = colorFromHex(card.statusColorHex)
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = GlassWhite,
        border = BorderStroke(1.dp, accent.copy(alpha = 0.32f)),
        shadowElevation = 10.dp,
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = card.tagNumber,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = TextDark,
                        )
                    )
                    Text(
                        text = card.breed,
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextLight),
                    )
                }
                Surface(
                    color = accent.copy(alpha = 0.16f),
                    contentColor = accent,
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Text(
                        text = card.statusLabel,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    )
                }
            }

            Text(card.ageLabel, color = TextDark, fontWeight = FontWeight.SemiBold)
            Text(card.purchasePriceLabel, color = TextDark, fontWeight = FontWeight.Bold)
            Text(card.breedingTimelineLabel, color = TextLight, style = MaterialTheme.typography.bodySmall)
            Text(card.healthHeadline, color = TextLight, style = MaterialTheme.typography.bodySmall)
        }
    }
}

private fun colorFromHex(hex: String): Color =
    Color(hex.removePrefix("#").toLong(16) or 0xFF000000L)

@Composable
private fun PremiumIconButton(onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = Color.White.copy(alpha = 0.2f),
        modifier = Modifier.size(44.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = ImageVector.Builder(
                    name = "back",
                    defaultWidth = 24.dp,
                    defaultHeight = 24.dp,
                    viewportWidth = 24f,
                    viewportHeight = 24f
                ).path(
                    fill = androidx.compose.ui.graphics.SolidColor(Color.White),
                    stroke = androidx.compose.ui.graphics.SolidColor(Color.White),
                    strokeLineWidth = 0.5f
                ) {
                    moveTo(20f, 11f)
                    horizontalLineTo(7.83f)
                    lineTo(13.42f, 5.41f)
                    lineTo(12f, 4f)
                    lineTo(4f, 12f)
                    lineTo(12f, 20f)
                    lineTo(13.41f, 18.59f)
                    lineTo(7.83f, 13f)
                    horizontalLineTo(20f)
                    verticalLineTo(11f)
                    close()
                }.build(),
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun PremiumButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = PremiumGreen
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(58.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = Color.White
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 6.dp,
            pressedElevation = 2.dp
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.5.sp
            )
        )
    }
}

@Composable
private fun DairyCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        color = GlassWhite,
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f)),
        shadowElevation = 12.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp), // Reduced from 28dp
            content = content
        )
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge.copy(
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.5.sp,
            color = PremiumGreen
        ),
        modifier = Modifier.padding(top = 8.dp, bottom = 2.dp) // Reduced padding
    )
}

@Composable
private fun DairyTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth(),
    label: String,
    placeholder: String = "",
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: androidx.compose.ui.text.input.VisualTransformation = androidx.compose.ui.text.input.VisualTransformation.None,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) { // Reduced from 6dp to 4dp (approx 2dp reduction)
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            color = TextLight,
            modifier = Modifier.padding(start = 2.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextLight.copy(alpha = 0.5f)
                )
            },
            keyboardOptions = keyboardOptions,
            visualTransformation = visualTransformation,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = SoftWhite,
                unfocusedContainerColor = SoftWhite,
                focusedBorderColor = PremiumGreen,
                unfocusedBorderColor = Color.Transparent,
            ),
            singleLine = true
        )
    }
}

private enum class AuthRoute {
    LOGIN,
    ADD_FARM,
}
