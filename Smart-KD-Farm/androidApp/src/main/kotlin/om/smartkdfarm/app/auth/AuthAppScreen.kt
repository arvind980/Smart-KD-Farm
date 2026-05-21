package om.smartkdfarm.app.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import om.smartkdfarm.app.core.domain.model.AreaConfiguration
import om.smartkdfarm.app.core.domain.model.AreaUnit
import om.smartkdfarm.app.core.domain.model.FarmLocation
import om.smartkdfarm.app.core.domain.model.FarmRegistrationCommand
import om.smartkdfarm.app.core.domain.model.ManagedUserRegistration
import om.smartkdfarm.app.core.domain.model.UserRole
import om.smartkdfarm.app.presentation.auth.AuthUiState
import om.smartkdfarm.app.presentation.auth.AuthViewModel

@Composable
fun AuthAppScreen(viewModel: AuthViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Surface(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            contentAlignment = Alignment.Center,
        ) {
            when {
                state.isLoading -> CircularProgressIndicator()
                state.currentUser == null -> AuthEntryScreen(
                    onLogin = viewModel::login,
                    onRegisterFarm = viewModel::registerFarm,
                    errorMessage = state.errorMessage,
                )
                else -> RoleDashboard(
                    state = state,
                    onAddManagedUser = viewModel::addManagedUser,
                    onLogout = viewModel::logout,
                    onClearTransient = viewModel::clearTransientMessage,
                )
            }
        }
    }
}

@Composable
private fun AuthEntryScreen(
    onLogin: (String, String) -> Unit,
    onRegisterFarm: (FarmRegistrationCommand) -> Unit,
    errorMessage: String?,
) {
    var isFarmRegistrationOpen by remember { mutableStateOf(false) }
    var mobileNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var farmName by remember { mutableStateOf("Smart KD Farm") }
    var ownerName by remember { mutableStateOf("Arvind") }
    var managerName by remember { mutableStateOf("Farm Manager") }
    var managerEmail by remember { mutableStateOf("") }
    var managerPhone by remember { mutableStateOf("") }
    var village by remember { mutableStateOf("") }
    var district by remember { mutableStateOf("") }
    var stateName by remember { mutableStateOf("Punjab") }

    Column(
        modifier = Modifier
            .widthIn(max = 720.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("Digital Dairy ERP", style = MaterialTheme.typography.headlineMedium)
        Text(
            "Log in with mobile number and password, or add a new farm from a dedicated setup flow.",
            style = MaterialTheme.typography.bodyMedium,
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
            ),
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text("Welcome Back", style = MaterialTheme.typography.titleLarge)
                Text(
                    "Managers and staff sign in here. Farm setup is separated so the login screen stays fast and simple on mobile.",
                    style = MaterialTheme.typography.bodyMedium,
                )
                FilledTonalButton(
                    onClick = { isFarmRegistrationOpen = true },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Add Farm")
                }
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Login", style = MaterialTheme.typography.titleMedium)
                OutlinedTextField(
                    value = mobileNumber,
                    onValueChange = { mobileNumber = it },
                    label = { Text("Mobile Number") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                )
                Button(onClick = { onLogin(mobileNumber, password) }, modifier = Modifier.fillMaxWidth()) {
                    Text("Login")
                }
                Text(
                    "Use the manager mobile number that was registered with the farm.",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }

        if (isFarmRegistrationOpen) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text("Create Farm", style = MaterialTheme.typography.titleMedium)
                        OutlinedButton(onClick = { isFarmRegistrationOpen = false }) {
                            Text("Close")
                        }
                    }
                    Text(
                        "Set up the farm and create the first manager account. The location fields are grouped here because this is the natural place to register a new farm.",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    OutlinedTextField(
                        value = farmName,
                        onValueChange = { farmName = it },
                        label = { Text("Farm Name") },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    OutlinedTextField(
                        value = ownerName,
                        onValueChange = { ownerName = it },
                        label = { Text("Owner Name") },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Farm Location", style = MaterialTheme.typography.titleSmall)
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = village,
                            onValueChange = { village = it },
                            label = { Text("Village") },
                            modifier = Modifier.weight(1f),
                        )
                        OutlinedTextField(
                            value = district,
                            onValueChange = { district = it },
                            label = { Text("District") },
                            modifier = Modifier.weight(1f),
                        )
                    }
                    OutlinedTextField(
                        value = stateName,
                        onValueChange = { stateName = it },
                        label = { Text("State") },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Primary Manager", style = MaterialTheme.typography.titleSmall)
                    OutlinedTextField(
                        value = managerName,
                        onValueChange = { managerName = it },
                        label = { Text("Manager Full Name") },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    OutlinedTextField(
                        value = managerPhone,
                        onValueChange = { managerPhone = it },
                        label = { Text("Manager Mobile Number") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth(),
                    )
                    OutlinedTextField(
                        value = managerEmail,
                        onValueChange = { managerEmail = it },
                        label = { Text("Manager Email") },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Manager Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Text(
                        "Manager email is still required for secure Firebase account creation. Daily login stays mobile number + password only.",
                        style = MaterialTheme.typography.bodySmall,
                    )
                    Button(
                        onClick = {
                            onRegisterFarm(
                                FarmRegistrationCommand(
                                    farmName = farmName,
                                    ownerName = ownerName,
                                    primaryPhoneNumber = managerPhone,
                                    managerName = managerName,
                                    managerEmail = managerEmail,
                                    managerPassword = password,
                                    managerPhoneNumber = managerPhone,
                                    location = FarmLocation(
                                        village = village,
                                        district = district,
                                        state = stateName,
                                    ),
                                    landArea = AreaConfiguration(
                                        value = 12.0,
                                        unit = AreaUnit.BIGHA,
                                    ),
                                )
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("Register Farm")
                    }
                }
            }
        }

        if (!errorMessage.isNullOrBlank()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
            )
        }
    }
}

@Composable
private fun RoleDashboard(
    state: AuthUiState,
    onAddManagedUser: (ManagedUserRegistration) -> Unit,
    onLogout: () -> Unit,
    onClearTransient: () -> Unit,
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var role by remember { mutableStateOf(UserRole.LABOUR) }

    LaunchedEffect(state.provisioningRequest, state.errorMessage) {
        if (state.provisioningRequest != null || state.errorMessage != null) {
            onClearTransient()
        }
    }

    Column(
        modifier = Modifier.widthIn(max = 720.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "Welcome, ${state.currentUser?.fullName.orEmpty()}",
            style = MaterialTheme.typography.headlineMedium,
        )
        Text(
            text = "Role: ${state.currentRole} | Farm: ${state.farmProfile?.farmName.orEmpty()}",
            style = MaterialTheme.typography.bodyLarge,
        )

        state.farmProfile?.let { farm ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Farm Snapshot", style = MaterialTheme.typography.titleMedium)
                    Text("${farm.farmName} • ${farm.location.village}, ${farm.location.district}")
                    Text(
                        "Area: %.2f bigha / %.2f acres".format(
                            farm.areaIn(AreaUnit.BIGHA),
                            farm.areaIn(AreaUnit.ACRE),
                        )
                    )
                }
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Role-aware Access", style = MaterialTheme.typography.titleMedium)
                when (state.currentRole) {
                    UserRole.MANAGER -> Text("Full CRUD, staff control, and analytics are enabled.")
                    UserRole.DAIRY_MAN -> Text("Outer milk collection, farmer registration, and milking sheet are enabled.")
                    UserRole.LABOUR -> Text("Livestock profiles and inventory inputs are available with restricted edits.")
                    UserRole.FARMER -> Text("Read-only ledger, revenue, dues, and advances are available.")
                    null -> Text("No role available.")
                }
            }
        }

        if (state.currentRole == UserRole.MANAGER || state.currentRole == UserRole.DAIRY_MAN) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Create Staff / Farmer", style = MaterialTheme.typography.titleMedium)
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text("Full Name") },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone") },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    RoleSelector(
                        currentRole = role,
                        selectableRoles = if (state.currentRole == UserRole.MANAGER) {
                            listOf(UserRole.DAIRY_MAN, UserRole.LABOUR, UserRole.FARMER)
                        } else {
                            listOf(UserRole.FARMER)
                        },
                        onRoleSelected = { role = it },
                    )
                    Button(
                        onClick = {
                            onAddManagedUser(
                                ManagedUserRegistration(
                                    fullName = fullName,
                                    email = email,
                                    phoneNumber = phone,
                                    role = role,
                                )
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("Submit Provisioning Request")
                    }
                }
            }
        }

        Button(onClick = onLogout, modifier = Modifier.fillMaxWidth()) {
            Text("Logout")
        }
    }
}

@Composable
private fun RoleSelector(
    currentRole: UserRole,
    selectableRoles: List<UserRole>,
    onRoleSelected: (UserRole) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Target Role", style = MaterialTheme.typography.labelLarge)
        selectableRoles.forEach { candidate ->
            Button(
                onClick = { onRoleSelected(candidate) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(if (candidate == currentRole) "Selected: $candidate" else candidate.name)
            }
        }
    }
}
