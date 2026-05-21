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
import com.smartkdfarm.app.core.domain.model.AreaConfiguration
import com.smartkdfarm.app.core.domain.model.AreaUnit
import com.smartkdfarm.app.core.domain.model.FarmLocation
import com.smartkdfarm.app.core.domain.model.FarmRegistrationCommand
import com.smartkdfarm.app.core.domain.model.ManagedUserRegistration
import com.smartkdfarm.app.core.domain.model.UserRole
import com.smartkdfarm.app.presentation.auth.AuthUiState
import com.smartkdfarm.app.presentation.auth.AuthViewModel

// Premium Color Palette
val PremiumGreen = Color(0xFF7EB55D)
val GlassWhite = Color(0xDDFFFFFF)
val TextDark = Color(0xFF1B2B1B)
val TextLight = Color(0xFF5A6A5A)
val SoftWhite = Color(0xFFF8FAF8)

@Composable
fun AuthAppScreen(viewModel: AuthViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var currentRoute by remember { mutableStateOf(AuthRoute.LOGIN) }

    AppBackground {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .safeContentPadding(), // Removed horizontal padding here to control it per-screen
            contentAlignment = Alignment.Center,
        ) {
            when {
                state.isLoading -> CircularProgressIndicator(color = Color.White, strokeWidth = 3.dp)
                state.currentUser == null && currentRoute == AuthRoute.LOGIN -> AuthEntryScreen(
                    onLogin = viewModel::login,
                    onOpenFarmRegistration = { currentRoute = AuthRoute.ADD_FARM },
                    errorMessage = state.errorMessage,
                )
                state.currentUser == null && currentRoute == AuthRoute.ADD_FARM -> FarmRegistrationScreen(
                    onRegisterFarm = viewModel::registerFarm,
                    onBack = { currentRoute = AuthRoute.LOGIN },
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
    onOpenFarmRegistration: () -> Unit,
    errorMessage: String?,
) {
    var mobileNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .widthIn(max = 420.dp)
            .padding(horizontal = 4.dp) // Set to 4dp padding
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp)) // Top Gap
        
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
                        placeholder = "e.g. 9876543210",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    )
                    DairyTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = "Password",
                        placeholder = "••••••••",
                        visualTransformation = PasswordVisualTransformation(),
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))

                PremiumButton(
                    text = "Login",
                    onClick = { onLogin(mobileNumber, password) },
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

        if (!errorMessage.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(16.dp))
            Surface(
                color = Color.Red.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color.Red.copy(alpha = 0.2f))
            ) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(48.dp)) // Bottom Gap
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FarmRegistrationScreen(
    onRegisterFarm: (FarmRegistrationCommand) -> Unit,
    onBack: () -> Unit,
    errorMessage: String?,
) {
    var farmName by remember { mutableStateOf("Smart KD Farm") }
    var adminName by remember { mutableStateOf("Farm Admin") }
    var adminEmail by remember { mutableStateOf("") }
    var adminPhone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var village by remember { mutableStateOf("") }
    var district by remember { mutableStateOf("") }
    var stateName by remember { mutableStateOf("Punjab") }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Standard Top Bar
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = "Join Us",
                    style = TextStyle(
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        shadow = Shadow(Color.Black.copy(alpha = 0.15f), offset = Offset(0f, 2f), blurRadius = 4f)
                    )
                )
            },
            navigationIcon = {
                PremiumIconButton(onClick = onBack)
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            ),
            modifier = Modifier.padding(horizontal = 4.dp).offset(y = (-16).dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = (-16).dp)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 4.dp), // Set to 4dp padding
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp)) // Reduced space from 16 to 8 (effectively less)

            DairyCard(modifier = Modifier.widthIn(max = 480.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        DairyTextField(value = farmName, onValueChange = { farmName = it }, label = "Farm Name", placeholder = "Enter farm name")
                    }
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(modifier = Modifier.weight(1f)) {
                            DairyTextField(value = village, onValueChange = { village = it }, label = "Village", placeholder = "Village name")
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            DairyTextField(value = district, onValueChange = { district = it }, label = "District", placeholder = "District name")
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        DairyTextField(value = stateName, onValueChange = { stateName = it }, label = "State", placeholder = "Enter state name")
                    }
                    
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        DairyTextField(value = adminName, onValueChange = { adminName = it }, label = "Admin Name", placeholder = "Enter admin name")
                        DairyTextField(value = adminPhone, onValueChange = { adminPhone = it }, label = "Mobile No.", placeholder = "Admin mobile number", keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone))
                        DairyTextField(value = password, onValueChange = { password = it }, label = "Password", placeholder = "Set a secure password", visualTransformation = PasswordVisualTransformation())
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Register button INSIDE the card
                    PremiumButton(
                        text = "Register Farm",
                        onClick = {
                            onRegisterFarm(
                                FarmRegistrationCommand(
                                    farmName = farmName,
                                    ownerName = adminName, // Use adminName as ownerName
                                    primaryPhoneNumber = adminPhone,
                                    managerName = adminName,
                                    managerEmail = adminEmail,
                                    managerPassword = password,
                                    managerPhoneNumber = adminPhone,
                                    location = FarmLocation(village = village, district = district, state = stateName),
                                    landArea = AreaConfiguration(value = 12.0, unit = AreaUnit.BIGHA)
                                )
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            
            if (!errorMessage.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp)) // Reduced from 16 to 12
                Surface(
                    color = Color.Red.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color.Red.copy(alpha = 0.2f))
                ) {
                    Text(
                        text = errorMessage,
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
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
    Column(
        modifier = Modifier.fillMaxSize()
            .padding(horizontal = 4.dp) // Set to 4dp padding
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        
        Column(
            modifier = Modifier.widthIn(max = 500.dp).fillMaxWidth()
        ) {
            Text(
                text = "Dashboard",
                style = TextStyle(
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    shadow = Shadow(Color.Black.copy(alpha = 0.2f), offset = Offset(0f, 4f), blurRadius = 6f)
                )
            )
            Text(
                text = "Management Control Center",
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.White.copy(alpha = 0.8f))
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        DairyCard(modifier = Modifier.widthIn(max = 500.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(PremiumGreen.copy(alpha = 0.1f), CircleShape)
                            .border(1.dp, PremiumGreen.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            state.currentUser?.fullName?.take(1).orEmpty(),
                            fontWeight = FontWeight.Bold,
                            color = PremiumGreen
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(state.currentUser?.fullName.orEmpty(), fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = TextDark)
                        Text(state.currentRole.toString(), color = PremiumGreen, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.labelMedium)
                    }
                }
                
                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Farm", color = TextLight)
                    Text(state.farmProfile?.farmName.orEmpty(), fontWeight = FontWeight.Bold, color = TextDark)
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                PremiumButton(
                    text = "Sign Out",
                    onClick = onLogout,
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = Color(0xFFF44336).copy(alpha = 0.9f)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(48.dp))
    }
}

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
