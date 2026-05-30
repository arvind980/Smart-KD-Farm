package com.smartkdfarm.app.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.filled.Vaccines
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun DashboardModuleScreen(
    module: DashboardModule,
    activeRole: DashboardRole,
    bottomPadding: Int = 140,
    onBack: () -> Unit,
) {
    if (module == DashboardModule.ACCESS) {
        RoleAccessScreen(onBack = onBack)
        return
    }

    if (!activeRole.canOpen(module)) {
        LockedModuleScreen(module = module, activeRole = activeRole, onBack = onBack)
        return
    }

    when (module) {
        DashboardModule.OUTSIDE_MILK -> {
            OutsideMilkCollectionScreen(activeRole = activeRole, bottomPadding = bottomPadding, onBack = onBack)
            return
        }
        DashboardModule.FARMER_PORTAL -> {
            OutsideFarmerPortalScreen(bottomPadding = bottomPadding, onBack = onBack)
            return
        }
        DashboardModule.LABOR_TASKS -> {
            LaborDailyTaskScreen(onBack = onBack)
            return
        }
        DashboardModule.ADMIN_CRUD -> {
            AdminCrudScreen(onBack = onBack)
            return
        }
        DashboardModule.REPORTS -> {
            ReportsDetailScreen(onBack = onBack)
            return
        }
        DashboardModule.ACCESS,
        DashboardModule.MILK,
        DashboardModule.INVENTORY,
        DashboardModule.HEALTH,
        DashboardModule.EXPENSES -> Unit
    }

    val spec = module.spec()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .padding(top = 24.dp, bottom = bottomPadding.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        ModuleHeader(spec = spec, onBack = onBack)
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            ModuleHeroCard(spec)
            ModuleMetricStrip(spec.metrics)
            ModuleActionGrid(spec.actions)
            ModuleSection("ADMIN WORKFLOW", spec.workflow)
            ModuleSection("ALERTS & CONTROL", spec.controls)
        }
    }
}

@Composable
private fun ModuleHeader(
    spec: ModuleSpec,
    onBack: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White.copy(alpha = 0.07f),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
            modifier = Modifier.size(58.dp),
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp),
                )
            }
        }
        Spacer(modifier = Modifier.width(14.dp))
        DashboardGlowIcon(icon = spec.icon, tint = spec.accent, glow = spec.accent, modifier = Modifier.size(70.dp))
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = spec.title,
                color = Color.White,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 25.sp,
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = spec.subtitle,
                color = spec.accent,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun ModuleHeroCard(spec: ModuleSpec) {
    Surface(
        shape = RoundedCornerShape(34.dp),
        color = Color.Transparent,
        border = BorderStroke(1.dp, spec.accent.copy(alpha = 0.28f)),
        shadowElevation = 12.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        listOf(
                            spec.accent.copy(alpha = 0.20f),
                            GlassDark.copy(alpha = 0.96f),
                            Color(0xFF06251D),
                        )
                    )
                )
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = spec.heroTitle,
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 23.sp,
                ),
            )
            Text(
                text = spec.heroBody,
                color = WhiteSoft,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp,
                    lineHeight = 21.sp,
                ),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                spec.heroBadges.forEach { badge ->
                    ModuleBadge(text = badge, accent = spec.accent)
                }
            }
        }
    }
}

@Composable
private fun ModuleMetricStrip(metrics: List<ModuleMetric>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        metrics.forEach { metric ->
            Surface(
                modifier = Modifier.width(154.dp),
                shape = RoundedCornerShape(26.dp),
                color = GlassDark.copy(alpha = 0.95f),
                border = BorderStroke(1.dp, metric.accent.copy(alpha = 0.22f)),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Icon(metric.icon, contentDescription = null, tint = metric.accent, modifier = Modifier.size(25.dp))
                    Text(
                        text = metric.value,
                        color = Color.White,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 25.sp,
                        ),
                        maxLines = 1,
                    )
                    Text(
                        text = metric.label,
                        color = WhiteSoft,
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 13.sp),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@Composable
private fun ModuleActionGrid(actions: List<ModuleAction>) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        actions.chunked(2).forEach { rowActions ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                rowActions.forEach { action ->
                    ModuleActionCard(action = action, modifier = Modifier.weight(1f))
                }
                if (rowActions.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun ModuleActionCard(
    action: ModuleAction,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.height(118.dp),
        shape = RoundedCornerShape(26.dp),
        color = action.accent.copy(alpha = 0.09f),
        border = BorderStroke(1.dp, action.accent.copy(alpha = 0.28f)),
    ) {
        Column(
            modifier = Modifier.padding(15.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Icon(action.icon, contentDescription = null, tint = action.accent, modifier = Modifier.size(27.dp))
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = action.title,
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = action.subtitle,
                    color = WhiteSoft,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun ModuleSection(
    title: String,
    items: List<String>,
) {
    Surface(
        shape = RoundedCornerShape(30.dp),
        color = GlassDark.copy(alpha = 0.95f),
        border = BorderStroke(1.dp, GlassStroke.copy(alpha = 0.82f)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(
                text = title,
                color = Color.White.copy(alpha = 0.86f),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    letterSpacing = 1.sp,
                ),
            )
            items.forEach { item ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    Box(
                        modifier = Modifier
                            .padding(top = 7.dp)
                            .size(8.dp)
                            .background(Mint, CircleShape),
                    )
                    Text(
                        text = item,
                        modifier = Modifier.weight(1f),
                        color = WhiteSoft,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                        ),
                    )
                }
            }
        }
    }
}

@Composable
private fun ModuleBadge(text: String, accent: Color) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = accent.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.28f)),
    ) {
        Text(
            text = text,
            color = accent,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                fontSize = 12.sp,
            ),
            maxLines = 1,
        )
    }
}

@Composable
private fun RoleAccessScreen(
    onBack: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .padding(top = 24.dp, bottom = 140.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        ModuleHeader(
            spec = roleAccessHeaderSpec,
            onBack = onBack,
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            RoleAccessHero()
            RoleMatrixStrip()
            roleCards.forEach { role ->
                RoleCard(role)
            }
            ModuleSection(
                title = "DATABASE READY NOTES",
                items = listOf(
                    "UI role names are stable: ADMIN, MILK_AGENT, LABOR, OUTSIDE_FARMER.",
                    "Each role card separates see, entry/edit, delete and locked modules for easy permission mapping later.",
                    "No database is created now; this is static UI only for finalizing the app flow first.",
                ),
            )
        }
    }
}

@Composable
private fun RoleAccessHero() {
    Surface(
        shape = RoundedCornerShape(34.dp),
        color = Color.Transparent,
        border = BorderStroke(1.dp, Mint.copy(alpha = 0.28f)),
        shadowElevation = 12.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        listOf(
                            Mint.copy(alpha = 0.20f),
                            GlassDark.copy(alpha = 0.96f),
                            Color(0xFF06251D),
                        )
                    )
                )
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "Role based access control",
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 23.sp,
                ),
            )
            Text(
                text = "Admin sab kuch manage karega. Agent sirf outside milk entry karega. Labor sirf daily task complete karega. Kisan apna milk hisaab dekhega.",
                color = WhiteSoft,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp,
                    lineHeight = 21.sp,
                ),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ModuleBadge("4 Roles", Mint)
                ModuleBadge("Static UI", Blue)
                ModuleBadge("DB Ready", Yellow)
            }
        }
    }
}

@Composable
private fun RoleMatrixStrip() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        roleCards.forEach { role ->
            Surface(
                modifier = Modifier.width(180.dp),
                shape = RoundedCornerShape(26.dp),
                color = GlassDark.copy(alpha = 0.95f),
                border = BorderStroke(1.dp, role.accent.copy(alpha = 0.24f)),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Icon(role.icon, contentDescription = null, tint = role.accent, modifier = Modifier.size(28.dp))
                    Text(
                        text = role.shortName,
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 17.sp,
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = role.accessLevel,
                        color = role.accent,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp,
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@Composable
private fun RoleCard(role: RoleAccessCard) {
    Surface(
        shape = RoundedCornerShape(30.dp),
        color = GlassDark.copy(alpha = 0.95f),
        border = BorderStroke(1.dp, role.accent.copy(alpha = 0.25f)),
        shadowElevation = 8.dp,
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                DashboardGlowIcon(role.icon, role.accent, role.accent, modifier = Modifier.size(64.dp))
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = role.title,
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp,
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = role.responsibility,
                        color = WhiteSoft,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            RolePermissionGroup("CAN SEE", role.canSee, Mint)
            RolePermissionGroup("CAN ENTRY / EDIT", role.canEdit, Blue)
            RolePermissionGroup("CAN DELETE", role.canDelete, Red)
            RolePermissionGroup("LOCKED", role.locked, WhiteSoft)
        }
    }
}

@Composable
private fun RolePermissionGroup(
    title: String,
    items: List<String>,
    accent: Color,
) {
    Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
        Text(
            text = title,
            color = accent,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                fontSize = 12.sp,
                letterSpacing = 1.sp,
            ),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items.forEach { item ->
                ModuleBadge(text = item, accent = accent)
            }
        }
    }
}

@Composable
private fun LockedModuleScreen(
    module: DashboardModule,
    activeRole: DashboardRole,
    onBack: () -> Unit,
) {
    val spec = module.specOrFallback()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .padding(top = 24.dp, bottom = 140.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        ModuleHeader(spec = spec, onBack = onBack)
        Surface(
            shape = RoundedCornerShape(34.dp),
            color = GlassDark.copy(alpha = 0.96f),
            border = BorderStroke(1.dp, Red.copy(alpha = 0.32f)),
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                DashboardGlowIcon(Icons.Filled.Lock, Red, Red, modifier = Modifier.size(76.dp))
                Text(
                    text = "Locked for ${activeRole.label}",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                )
                Text(
                    text = "${activeRole.key} role ko is screen ka access nahi hai. Admin role se login/switch karne par ye module open hoga.",
                    color = WhiteSoft,
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp, lineHeight = 21.sp),
                )
            }
        }
    }
}

@Composable
private fun OutsideMilkCollectionScreen(
    activeRole: DashboardRole,
    bottomPadding: Int = 140,
    onBack: () -> Unit,
) {
    var farmer by remember { mutableStateOf("Ramesh Gurjar") }
    var litre by remember { mutableStateOf("42.5") }
    var fat by remember { mutableStateOf("6.4") }
    var snf by remember { mutableStateOf("8.8") }
    var rate by remember { mutableStateOf("58") }
    var paid by remember { mutableStateOf(false) }
    val canDelete = activeRole == DashboardRole.ADMIN

    ManagedModuleShell(spec = outsideMilkSpec, bottomPadding = bottomPadding, onBack = onBack) {
        ModuleHeroCard(outsideMilkSpec)
        ModuleMetricStrip(outsideMilkSpec.metrics)
        FormPanel("MILK COLLECTION ENTRY", outsideMilkSpec.accent) {
            DemoField("Farmer Select", farmer) { farmer = it }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                DemoField("Litre", litre, Modifier.weight(1f)) { litre = it }
                DemoField("FAT", fat, Modifier.weight(1f)) { fat = it }
                DemoField("SNF", snf, Modifier.weight(1f)) { snf = it }
            }
            DemoField("Rate per Litre", rate) { rate = it }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = paid,
                    onCheckedChange = { paid = it },
                    colors = CheckboxDefaults.colors(checkedColor = Mint),
                )
                Text("Payment ${if (paid) "Paid" else "Pending"}", color = WhiteSoft, fontWeight = FontWeight.Bold)
            }
            ButtonRow(
                primary = "Save Entry",
                secondary = "Clear",
                accent = outsideMilkSpec.accent,
                secondaryEnabled = canDelete,
            )
        }
        DataListPanel(
            title = "TODAY COLLECTION LIST",
            rows = listOf(
                Triple("Ramesh Gurjar", "42.5L  FAT 6.4  SNF 8.8", "₹2,465 Pending"),
                Triple("Sita Devi", "31.0L  FAT 5.9  SNF 8.6", "₹1,736 Paid"),
                Triple("Mahesh Jat", "38.2L  FAT 6.1  SNF 8.7", "₹2,178 Pending"),
            ),
            accent = Blue,
        )
    }
}

@Composable
private fun OutsideFarmerPortalScreen(
    bottomPadding: Int = 140,
    onBack: () -> Unit,
) {
    ManagedModuleShell(spec = farmerPortalSpec, bottomPadding = bottomPadding, onBack = onBack) {
        ModuleHeroCard(farmerPortalSpec)
        ModuleMetricStrip(farmerPortalSpec.metrics)
        DataListPanel(
            title = "MY MILK LEDGER",
            rows = listOf(
                Triple("Today Morning", "18.5L  FAT 6.3  Rate ₹58", "₹1,073 Pending"),
                Triple("Today Evening", "16.0L  FAT 6.1  Rate ₹57", "₹912 Pending"),
                Triple("May Total", "824L  Avg FAT 6.2", "₹47,430"),
            ),
            accent = Purple,
        )
        ModuleSection(
            title = "MONTHLY HISAAB",
            items = listOf(
                "Total milk, average FAT/SNF, rate aur pending amount kisan ko read-only dikhega.",
                "Paid/Pending status admin ya milk agent ke approval ke baad update hoga.",
                "Kisan dusre farmers, staff, inventory, profit/loss ya delete actions nahi dekh sakta.",
            ),
        )
    }
}

@Composable
private fun LaborDailyTaskScreen(onBack: () -> Unit) {
    val tasks = listOf(
        "Chara done",
        "TMR done",
        "Milking support",
        "Shed cleaning",
        "Health observation",
    )
    var checked by remember { mutableStateOf(setOf("Chara done", "Shed cleaning")) }
    ManagedModuleShell(spec = laborTaskSpec, onBack = onBack) {
        ModuleHeroCard(laborTaskSpec)
        FormPanel("DAILY TASK CHECKLIST", laborTaskSpec.accent) {
            tasks.forEach { task ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = task in checked,
                        onCheckedChange = { isChecked ->
                            checked = if (isChecked) checked + task else checked - task
                        },
                        colors = CheckboxDefaults.colors(checkedColor = Yellow),
                    )
                    Text(task, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
            DemoField("Health observation", "Cow #04 ate less feed today") {}
            ButtonRow("Submit Tasks", "No Financial Access", laborTaskSpec.accent, secondaryEnabled = false)
        }
        DataListPanel(
            title = "SHIFT SUMMARY",
            rows = listOf(
                Triple("Morning", "3/5 tasks complete", "Supervisor review"),
                Triple("Evening", "Pending TMR and milking support", "Due 6:00 PM"),
            ),
            accent = Yellow,
        )
    }
}

@Composable
private fun AdminCrudScreen(onBack: () -> Unit) {
    var selected by remember { mutableStateOf(adminCrudSections.first()) }
    ManagedModuleShell(spec = adminCrudSpec, onBack = onBack) {
        ModuleHeroCard(adminCrudSpec)
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            adminCrudSections.forEach { section ->
                Surface(
                    onClick = { selected = section },
                    shape = RoundedCornerShape(18.dp),
                    color = if (section == selected) Red.copy(alpha = 0.16f) else GlassDark.copy(alpha = 0.90f),
                    border = BorderStroke(1.dp, Red.copy(alpha = if (section == selected) 0.45f else 0.16f)),
                ) {
                    Text(
                        section,
                        color = if (section == selected) Red else WhiteSoft,
                        modifier = Modifier.padding(horizontal = 13.dp, vertical = 9.dp),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 12.sp,
                    )
                }
            }
        }
        FormPanel("MANAGE $selected", Red) {
            DemoField("$selected Name", sampleNameFor(selected)) {}
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                DemoField("Code / Tag", "KD-001", Modifier.weight(1f)) {}
                DemoField("Status", "Active", Modifier.weight(1f)) {}
            }
            DemoField("Notes", "Ready for add/edit/delete workflow") {}
            ButtonRow("Add / Save", "Delete", Red)
        }
        DataListPanel(
            title = "$selected RECORDS",
            rows = listOf(
                Triple(sampleNameFor(selected), "Active record", "Edit"),
                Triple("Demo $selected 02", "Needs review", "Delete"),
            ),
            accent = Red,
        )
    }
}

@Composable
private fun ReportsDetailScreen(onBack: () -> Unit) {
    ManagedModuleShell(spec = detailedReportsSpec, onBack = onBack) {
        ModuleHeroCard(detailedReportsSpec)
        ModuleMetricStrip(detailedReportsSpec.metrics)
        DataListPanel(
            title = "PROFIT / LOSS BREAKUP",
            rows = listOf(
                Triple("Milk income", "₹7,20,000", "+"),
                Triple("Feed cost", "₹1,84,000", "-"),
                Triple("Salary", "₹86,000", "-"),
                Triple("Medicine + health", "₹24,500", "-"),
                Triple("EMI", "₹72,000", "Due 05 Jun"),
                Triple("Subsidy", "₹1,50,000", "File tracking"),
            ),
            accent = Mint,
        )
        ModuleSection(
            title = "REPORT CONTROLS",
            items = listOf(
                "Monthly P&L, EMI, subsidy, salary aur feed cost separate cards mein dikh rahe hain.",
                "Reports screen admin-only locked hai because yahan full financial summary hai.",
                "Future wiring: Khata ledger, milk collection, staff salary aur inventory expenses source banenge.",
            ),
        )
    }
}

@Composable
private fun ManagedModuleShell(
    spec: ModuleSpec,
    bottomPadding: Int = 140,
    onBack: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .padding(top = 24.dp, bottom = bottomPadding.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        ModuleHeader(spec = spec, onBack = onBack)
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(18.dp),
            content = content,
        )
    }
}

@Composable
private fun FormPanel(
    title: String,
    accent: Color,
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(30.dp),
        color = GlassDark.copy(alpha = 0.95f),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.24f)),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(title, color = accent, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, letterSpacing = 1.sp)
            content()
        }
    }
}

@Composable
private fun DemoField(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit,
) {
    var fieldValue by remember(value) { mutableStateOf(value) }
    OutlinedTextField(
        value = fieldValue,
        onValueChange = {
            fieldValue = it
            onValueChange(it)
        },
        label = { Text(label) },
        modifier = modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedBorderColor = Mint,
            unfocusedBorderColor = Color.White.copy(alpha = 0.18f),
            focusedLabelColor = Mint,
            unfocusedLabelColor = WhiteSoft,
            cursorColor = Mint,
        ),
        singleLine = true,
    )
}

@Composable
private fun ButtonRow(
    primary: String,
    secondary: String,
    accent: Color,
    secondaryEnabled: Boolean = true,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Button(
            onClick = {},
            colors = ButtonDefaults.buttonColors(containerColor = accent),
            modifier = Modifier.weight(1f),
        ) {
            Icon(Icons.Filled.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text(primary, fontWeight = FontWeight.ExtraBold)
        }
        TextButton(
            onClick = {},
            enabled = secondaryEnabled,
            modifier = Modifier.weight(1f),
        ) {
            Text(secondary, color = if (secondaryEnabled) Red else WhiteSoft, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
private fun DataListPanel(
    title: String,
    rows: List<Triple<String, String, String>>,
    accent: Color,
) {
    Surface(
        shape = RoundedCornerShape(30.dp),
        color = GlassDark.copy(alpha = 0.95f),
        border = BorderStroke(1.dp, GlassStroke.copy(alpha = 0.82f)),
    ) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(title, color = Color.White.copy(alpha = 0.86f), fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, letterSpacing = 1.sp)
            rows.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(modifier = Modifier.size(10.dp).background(accent, CircleShape))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(row.first, color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                        Text(row.second, color = WhiteSoft, fontSize = 13.sp)
                    }
                    Text(row.third, color = accent, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
                }
            }
        }
    }
}

private fun sampleNameFor(section: String): String = when (section) {
    "Animal" -> "Cow #04"
    "Milk Entry" -> "Morning Milk"
    "Inventory" -> "Makka Feed"
    "Health Treatment" -> "Deworming"
    "Expenses" -> "Diesel Expense"
    "Staff" -> "Raju Labor"
    else -> "Ramesh Farmer"
}

@Immutable
private data class ModuleSpec(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val accent: Color,
    val heroTitle: String,
    val heroBody: String,
    val heroBadges: List<String>,
    val metrics: List<ModuleMetric>,
    val actions: List<ModuleAction>,
    val workflow: List<String>,
    val controls: List<String>,
)

@Immutable
private data class ModuleMetric(
    val label: String,
    val value: String,
    val icon: ImageVector,
    val accent: Color,
)

@Immutable
private data class ModuleAction(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val accent: Color,
)

@Immutable
private data class RoleAccessCard(
    val title: String,
    val shortName: String,
    val responsibility: String,
    val accessLevel: String,
    val icon: ImageVector,
    val accent: Color,
    val canSee: List<String>,
    val canEdit: List<String>,
    val canDelete: List<String>,
    val locked: List<String>,
)

private val roleAccessHeaderSpec = ModuleSpec(
    title = "User Access",
    subtitle = "Roles and permissions",
    icon = Icons.Filled.Shield,
    accent = Mint,
    heroTitle = "",
    heroBody = "",
    heroBadges = emptyList(),
    metrics = emptyList(),
    actions = emptyList(),
    workflow = emptyList(),
    controls = emptyList(),
)

private val roleCards = listOf(
    RoleAccessCard(
        title = "Admin - Arvind Bhai",
        shortName = "Admin",
        responsibility = "Poore farm, staff, milk, inventory, Khata aur reports ka full control.",
        accessLevel = "Full 100% Access",
        icon = Icons.Filled.Shield,
        accent = Mint,
        canSee = listOf("All Dashboard", "All Animals", "All Milk", "All Khata", "Reports"),
        canEdit = listOf("Animals", "Staff", "Inventory", "Health", "Expenses", "Roles"),
        canDelete = listOf("Wrong Entries", "Inactive Users", "Old Alerts", "Duplicate Records"),
        locked = listOf("None"),
    ),
    RoleAccessCard(
        title = "Milk Collection Agent",
        shortName = "Milk Agent",
        responsibility = "Outside farmers se milk weight, FAT/SNF aur entry manage karega.",
        accessLevel = "Only Outside Milk Entry",
        icon = Icons.Filled.WaterDrop,
        accent = Blue,
        canSee = listOf("Own Entries", "Farmer List", "Today Collection", "Payment Status"),
        canEdit = listOf("Milk Weight", "FAT/SNF", "Farmer Entry"),
        canDelete = listOf("Own Draft Entry"),
        locked = listOf("Profit", "Khata Delete", "Staff", "Inventory", "Reports"),
    ),
    RoleAccessCard(
        title = "Labor - Shed Staff",
        shortName = "Labor",
        responsibility = "TMR, chara, cleaning aur daily task done mark karega.",
        accessLevel = "Daily Tasks Only",
        icon = Icons.Filled.People,
        accent = Yellow,
        canSee = listOf("Task List", "Animal Tags", "Feed Schedule", "Health Notes"),
        canEdit = listOf("Task Done", "Feed Done", "Basic Observation"),
        canDelete = listOf("None"),
        locked = listOf("Financials", "Milk Rate", "Khata", "Reports", "Role Access"),
    ),
    RoleAccessCard(
        title = "Outside Farmer - Kisan",
        shortName = "Farmer",
        responsibility = "Apna milk weight, FAT, rate aur monthly hisaab live dekhega.",
        accessLevel = "Self Account View",
        icon = Icons.Filled.People,
        accent = Purple,
        canSee = listOf("Own Milk", "Own FAT/SNF", "Own Rate", "Monthly Hisaab"),
        canEdit = listOf("Profile Details", "Bank/UPI Later"),
        canDelete = listOf("None"),
        locked = listOf("Other Farmers", "Farm Data", "Staff", "Inventory", "Admin Reports"),
    ),
)

private val outsideMilkSpec = ModuleSpec(
    title = "Outside Milk",
    subtitle = "Agent collection form",
    icon = Icons.Filled.WaterDrop,
    accent = Blue,
    heroTitle = "Farmer se direct milk entry",
    heroBody = "Milk Collection Agent farmer select karke litre, FAT, SNF, rate aur payment status manage karega. Delete/financial access admin ke paas rahega.",
    heroBadges = listOf("Farmer", "FAT/SNF", "Payment"),
    metrics = listOf(
        ModuleMetric("Today", "111.7L", Icons.Filled.WaterDrop, Blue),
        ModuleMetric("Pending", "₹5.7K", Icons.Filled.Payments, Yellow),
        ModuleMetric("Farmers", "3", Icons.Filled.People, Mint),
    ),
    actions = emptyList(),
    workflow = emptyList(),
    controls = emptyList(),
)

private val farmerPortalSpec = ModuleSpec(
    title = "Farmer Portal",
    subtitle = "Self milk and payment view",
    icon = Icons.Filled.Person,
    accent = Purple,
    heroTitle = "Kisan ka apna milk hisaab",
    heroBody = "Outside Farmer sirf apna milk weight, FAT, SNF, rate, paid/pending status aur monthly total dekhega.",
    heroBadges = listOf("Read Only", "Own Data", "Monthly"),
    metrics = listOf(
        ModuleMetric("Month Milk", "824L", Icons.Filled.WaterDrop, Blue),
        ModuleMetric("Avg FAT", "6.2", Icons.Filled.Science, Mint),
        ModuleMetric("Pending", "₹8.4K", Icons.Filled.Payments, Yellow),
    ),
    actions = emptyList(),
    workflow = emptyList(),
    controls = emptyList(),
)

private val laborTaskSpec = ModuleSpec(
    title = "Labor Tasks",
    subtitle = "Daily work checklist",
    icon = Icons.Filled.TaskAlt,
    accent = Yellow,
    heroTitle = "Shed ka daily kaam simple checklist mein",
    heroBody = "Labor chara, TMR, milking support, cleaning aur health observation mark karega. Financial screens hidden rahenge.",
    heroBadges = listOf("No Financials", "Checklist", "Observation"),
    metrics = listOf(
        ModuleMetric("Done", "2/5", Icons.Filled.CheckCircle, Mint),
        ModuleMetric("Pending", "3", Icons.Filled.TaskAlt, Yellow),
        ModuleMetric("Notes", "1", Icons.Filled.HealthAndSafety, Red),
    ),
    actions = emptyList(),
    workflow = emptyList(),
    controls = emptyList(),
)

private val adminCrudSpec = ModuleSpec(
    title = "Admin CRUD",
    subtitle = "Add edit delete manager",
    icon = Icons.Filled.Shield,
    accent = Red,
    heroTitle = "Admin ke real manage screens",
    heroBody = "Animal, milk entry, inventory, health treatment, expenses, staff aur farmer ke add/edit/delete controls ek jagah.",
    heroBadges = listOf("Add", "Edit", "Delete"),
    metrics = listOf(
        ModuleMetric("Modules", "7", Icons.Filled.BarChart, Mint),
        ModuleMetric("Drafts", "4", Icons.Filled.Edit, Blue),
        ModuleMetric("Admin Only", "Yes", Icons.Filled.Lock, Red),
    ),
    actions = emptyList(),
    workflow = emptyList(),
    controls = emptyList(),
)

private val detailedReportsSpec = ModuleSpec(
    title = "Reports",
    subtitle = "Profit EMI subsidy salary feed",
    icon = Icons.Filled.BarChart,
    accent = Mint,
    heroTitle = "Detailed monthly farm report",
    heroBody = "Profit/loss, EMI, subsidy, salary, feed cost aur medicine expense ko clear breakdown mein dekhein.",
    heroBadges = listOf("P&L", "EMI", "Feed Cost"),
    metrics = listOf(
        ModuleMetric("Net Profit", "₹3.54L", Icons.AutoMirrored.Filled.TrendingUp, Mint),
        ModuleMetric("Feed Cost", "₹1.84L", Icons.Filled.Inventory2, Yellow),
        ModuleMetric("EMI Due", "₹72K", Icons.Filled.Payments, Red),
    ),
    actions = emptyList(),
    workflow = emptyList(),
    controls = emptyList(),
)

private val adminCrudSections = listOf(
    "Animal",
    "Milk Entry",
    "Inventory",
    "Health Treatment",
    "Expenses",
    "Staff",
    "Farmer",
)

private fun DashboardModule.specOrFallback(): ModuleSpec = when (this) {
    DashboardModule.OUTSIDE_MILK -> outsideMilkSpec
    DashboardModule.FARMER_PORTAL -> farmerPortalSpec
    DashboardModule.LABOR_TASKS -> laborTaskSpec
    DashboardModule.ADMIN_CRUD -> adminCrudSpec
    DashboardModule.REPORTS -> detailedReportsSpec
    else -> spec()
}

private fun DashboardModule.spec(): ModuleSpec = when (this) {
    DashboardModule.ACCESS -> error("Access module uses RoleAccessScreen.")
    DashboardModule.OUTSIDE_MILK -> outsideMilkSpec
    DashboardModule.FARMER_PORTAL -> farmerPortalSpec
    DashboardModule.LABOR_TASKS -> laborTaskSpec
    DashboardModule.ADMIN_CRUD -> adminCrudSpec

    DashboardModule.MILK -> ModuleSpec(
        title = "Doodh Ganit",
        subtitle = "Production and sales",
        icon = Icons.Filled.WaterDrop,
        accent = Blue,
        heroTitle = "Subah-shaam milk control",
        heroBody = "Roz ka total milk, per-animal yield, FAT/SNF, rate aur dairy payment ko ek jagah manage karein.",
        heroBadges = listOf("FAT/SNF", "BMC", "Payment"),
        metrics = listOf(
            ModuleMetric("Today Milk", "250L", Icons.Filled.WaterDrop, Blue),
            ModuleMetric("Avg FAT", "6.4", Icons.Filled.Science, Mint),
            ModuleMetric("Pending Pay", "₹38K", Icons.Filled.Payments, Yellow),
        ),
        actions = listOf(
            ModuleAction("Add Entry", "Morning/evening milk", Icons.Filled.Add, Blue),
            ModuleAction("Correct Entry", "Rate or litre edit", Icons.Filled.Edit, Mint),
            ModuleAction("Delete Duplicate", "Wrong milk record", Icons.Filled.DeleteOutline, Red),
            ModuleAction("Sale Payment", "Dairy/mandi settlement", Icons.Filled.Payments, Yellow),
        ),
        workflow = listOf(
            "Admin daily milk entry, FAT/SNF aur payment status verify karega.",
            "Galat litre/rate ko edit karke monthly profit calculation clean rakhega.",
            "Duplicate milk entry delete karke Khata aur reports ko accurate rakhega.",
        ),
        controls = listOf(
            "Low-yield animal alert ko Livestock health screen se connect karna hai.",
            "Bulk Milk Cooler aur pipeline machine data ko future integration point banaya gaya hai.",
        ),
    )

    DashboardModule.INVENTORY -> ModuleSpec(
        title = "Feed Stock",
        subtitle = "Ration and inventory",
        icon = Icons.Filled.Inventory2,
        accent = Purple,
        heroTitle = "Godam aur feed plant control",
        heroBody = "Gehun, khal, chokar, bhoosa, medicine aur TMR batch ko reorder level ke saath track karein.",
        heroBadges = listOf("Reorder", "TMR", "Waste"),
        metrics = listOf(
            ModuleMetric("Bhoosa", "18T", Icons.Filled.Inventory2, Yellow),
            ModuleMetric("Khal", "6Q", Icons.Filled.ShoppingCart, Purple),
            ModuleMetric("TMR Today", "420kg", Icons.Filled.Science, Mint),
        ),
        actions = listOf(
            ModuleAction("Add Stock", "New raw material", Icons.Filled.Add, Purple),
            ModuleAction("Edit Formula", "Seasonal feed recipe", Icons.Filled.Edit, Mint),
            ModuleAction("Spoiled Stock", "Waste/remove entry", Icons.Filled.DeleteOutline, Red),
            ModuleAction("Reorder List", "5 din pehle alert", Icons.Filled.ShoppingCart, Yellow),
        ),
        workflow = listOf(
            "Market se material aate hi stock balance update karein.",
            "Garmiyo/sardiyo ke hisab se feed formula edit karein.",
            "Kharab feed stock ko waste log mein daal kar inventory clean rakhein.",
        ),
        controls = listOf(
            "Reorder level cross hote hi Home alerts par low-stock warning dikhani hai.",
            "Feed batch expense automatic Khata expense mein post hona chahiye.",
        ),
    )

    DashboardModule.HEALTH -> ModuleSpec(
        title = "Health Alerts",
        subtitle = "Veterinary and vaccines",
        icon = Icons.Filled.HealthAndSafety,
        accent = Red,
        heroTitle = "Pashu swasthya command center",
        heroBody = "Vaccine, deworming, treatment, body temperature aur doctor visit ko animal profile se connect karein.",
        heroBadges = listOf("Vaccine", "Deworming", "Doctor"),
        metrics = listOf(
            ModuleMetric("Due Vaccine", "4", Icons.Filled.Vaccines, Red),
            ModuleMetric("Deworming", "7", Icons.Filled.HealthAndSafety, Yellow),
            ModuleMetric("Critical", "1", Icons.Filled.HealthAndSafety, Purple),
        ),
        actions = listOf(
            ModuleAction("Add Treatment", "Dose and doctor note", Icons.Filled.Add, Red),
            ModuleAction("Edit Schedule", "Next vaccine/deworming", Icons.Filled.Edit, Mint),
            ModuleAction("Clear Alert", "Completed medical task", Icons.Filled.DeleteOutline, Yellow),
            ModuleAction("Medical Cost", "Post to Khata", Icons.AutoMirrored.Filled.ReceiptLong, Purple),
        ),
        workflow = listOf(
            "Doctor visit ke baad treatment, dose aur medical kharcha update karein.",
            "Animal detail screen mein vaccine/deworming timeline dikhani hogi.",
            "Doodh kam hone ya temperature badhne par admin ko immediate alert mile.",
        ),
        controls = listOf(
            "Old completed alerts clear karne ka control admin-only rahe.",
            "Critical health alert Home dashboard par top priority mein dikhe.",
        ),
    )

    DashboardModule.EXPENSES -> ModuleSpec(
        title = "Kharcha Control",
        subtitle = "Labor and daily expenses",
        icon = Icons.AutoMirrored.Filled.ReceiptLong,
        accent = Yellow,
        heroTitle = "Labor, diesel, dawa aur maintenance",
        heroBody = "Worker attendance, advance/uhaar, salary balance aur daily expenses ko Khata se sync karein.",
        heroBadges = listOf("Attendance", "Advance", "Salary"),
        metrics = listOf(
            ModuleMetric("Workers", "4", Icons.Filled.Payments, Yellow),
            ModuleMetric("Advance", "₹12K", Icons.AutoMirrored.Filled.ReceiptLong, Red),
            ModuleMetric("This Month", "₹86K", Icons.Filled.Savings, Mint),
        ),
        actions = listOf(
            ModuleAction("Mark Attendance", "Daily staff presence", Icons.Filled.Add, Yellow),
            ModuleAction("Add Advance", "Uhaar/advance entry", Icons.Filled.Edit, Red),
            ModuleAction("Daily Expense", "Diesel, dawa, repair", Icons.AutoMirrored.Filled.ReceiptLong, Purple),
            ModuleAction("Delete Wrong", "Remove bad expense", Icons.Filled.DeleteOutline, Red),
        ),
        workflow = listOf(
            "Labor ki chutti, attendance aur advance salary record Staff module se link hogi.",
            "Diesel, dawa, maintenance expense direct Khata expense mein jaayega.",
            "Wrong expense delete karne ka permission admin-only hona chahiye.",
        ),
        controls = listOf(
            "Labor ko sirf limited see/edit access mile, financial delete admin ke paas rahe.",
            "Salary pending aur advance balance monthly reports mein dikhna chahiye.",
        ),
    )

    DashboardModule.REPORTS -> ModuleSpec(
        title = "Report Card",
        subtitle = "Profit and analytics",
        icon = Icons.Filled.BarChart,
        accent = Mint,
        heroTitle = "Shuddh munafe ka dashboard",
        heroBody = "Milk income, feed cost, labor, medicine, EMI aur subsidy mila kar real monthly net profit dekhein.",
        heroBadges = listOf("Profit", "EMI", "Subsidy"),
        metrics = listOf(
            ModuleMetric("Net Profit", "₹5.0L", Icons.AutoMirrored.Filled.TrendingUp, Mint),
            ModuleMetric("EMI Due", "₹72K", Icons.Filled.Payments, Yellow),
            ModuleMetric("Subsidy", "Track", Icons.Filled.Savings, Blue),
        ),
        actions = listOf(
            ModuleAction("Monthly P&L", "Income vs expense", Icons.Filled.BarChart, Mint),
            ModuleAction("EMI Status", "Loan payment tracker", Icons.Filled.Payments, Yellow),
            ModuleAction("Subsidy File", "Document progress", Icons.Filled.Savings, Blue),
            ModuleAction("Export Report", "PDF/share later", Icons.AutoMirrored.Filled.ReceiptLong, Purple),
        ),
        workflow = listOf(
            "Mahine ke end par real net profit calculate hoga: milk sale minus feed, staff, health, diesel aur EMI.",
            "Bank loan EMI status aur subsidy follow-up admin dashboard mein visible rahe.",
            "Reports admin-only module hona chahiye kyunki yahan financial summary hoti hai.",
        ),
        controls = listOf(
            "Labor users ko Reports ka access VIEW bhi na mile jab tak admin allow na kare.",
            "Khata ledger ko reports ka source of truth banana chahiye.",
        ),
    )
}
