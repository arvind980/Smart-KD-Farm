package com.smartkdfarm.app

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartkdfarm.app.core.domain.model.AreaConfiguration
import com.smartkdfarm.app.core.domain.model.AreaUnit
import com.smartkdfarm.app.core.domain.model.AutoRateBreakdown
import com.smartkdfarm.app.core.domain.model.CollectionPaymentStatus
import com.smartkdfarm.app.core.domain.model.DashboardAlert
import com.smartkdfarm.app.core.domain.model.DashboardAlertSeverity
import com.smartkdfarm.app.core.domain.model.FeedBatchLog
import com.smartkdfarm.app.core.domain.model.FeedFormulaIngredient
import com.smartkdfarm.app.core.domain.model.InventoryCategory
import com.smartkdfarm.app.core.domain.model.InventoryItem
import com.smartkdfarm.app.core.domain.model.KhataEntryKind
import com.smartkdfarm.app.core.domain.model.KhataLedgerEntry
import com.smartkdfarm.app.core.domain.model.KpiSnapshot
import com.smartkdfarm.app.core.domain.model.LandPartition
import com.smartkdfarm.app.core.domain.model.MilkCollectionEntry
import com.smartkdfarm.app.core.domain.model.MilkSession
import com.smartkdfarm.app.core.domain.model.PermissionLevel
import com.smartkdfarm.app.core.domain.model.PredictionPoint
import com.smartkdfarm.app.core.domain.model.PredictionSource
import com.smartkdfarm.app.core.domain.model.StaffAccessProfile
import com.smartkdfarm.app.core.domain.model.SmsGatewayConfig
import com.smartkdfarm.app.core.domain.model.SmsProvider
import com.smartkdfarm.app.core.domain.model.StaffModule
import com.smartkdfarm.app.core.domain.model.StaffPermission
import com.smartkdfarm.app.core.domain.model.UserRole
import com.smartkdfarm.app.core.domain.model.WasteCategory
import kotlin.math.abs

// ── Colour palette ────────────────────────────────────────────────────────────
private val Forest     = Color(0xFF0D3C3B)
private val DeepForest = Color(0xFF082B2B)
private val Mint       = Color(0xFFB9F4CC)
private val Accent     = Color(0xFF3CCF91)
private val AccentSoft = Color(0xFF8BE4BC)
private val Cream      = Color(0xFFF7F2E8)
private val Card       = Color(0xFFF8FBF7)
private val Ink        = Color(0xFF16312D)
private val Muted      = Color(0xFF6B7E77)
private val Danger     = Color(0xFFD24D57)
private val DangerSoft = Color(0xFFF7C7CB)
private val Gold       = Color(0xFFE7C76A)

// ── Root composable ───────────────────────────────────────────────────────────
@Composable
fun SmartKdFarmConsole(
    managerName: String = "Arvind Singh",
    farmName: String = "Smart KD Farm",
    currentRoleLabel: String = "ADMIN",
    modifier: Modifier = Modifier,
) {
    var selectedSection   by remember { mutableStateOf(ConsoleSection.Dashboard) }
    var selectedAnimalTag by remember { mutableStateOf<String?>(null) }
    var fabExpanded       by remember { mutableStateOf(false) }
    var kisanSearch       by remember { mutableStateOf("") }
    var showHealthForm    by remember { mutableStateOf(false) }
    val data = remember { ConsolePreviewData.sample(farmName = farmName) }
    val scroll = rememberScrollState()

    Box(modifier = modifier.fillMaxSize()) {
        AppBackground {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scroll)
                    .padding(horizontal = 16.dp, vertical = 20.dp)
                    .padding(bottom = 88.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp),
            ) {
                if (selectedAnimalTag != null) {
                    val animal = data.livestockCards.find { it.tag == selectedAnimalTag }
                    if (animal != null) {
                        AnimalDetailScreen(animal = animal, onBack = { selectedAnimalTag = null })
                    }
                } else {
                    HeroHeader(managerName = managerName, farmName = farmName, currentRoleLabel = currentRoleLabel)
                    DashboardHero(selectedSection = selectedSection, onSelectSection = { selectedSection = it }, data = data)
                    when (selectedSection) {
                        ConsoleSection.Dashboard   -> DashboardOverview(data = data)
                        ConsoleSection.Livestock   -> LivestockSection(
                            data = data,
                            onAnimalClick = { selectedAnimalTag = it },
                            showHealthForm = showHealthForm,
                            onToggleHealthForm = { showHealthForm = !showHealthForm },
                        )
                        ConsoleSection.OuterCenter -> OuterCenterSection(
                            data = data,
                            searchQuery = kisanSearch,
                            onSearchChange = { kisanSearch = it },
                        )
                        ConsoleSection.Khata       -> KhataSection(data = data)
                        ConsoleSection.Inventory   -> InventorySection(data = data)
                        ConsoleSection.Config      -> FarmConfigSection(data = data)
                        ConsoleSection.Staff       -> StaffSection(data = data)
                    }
                }
            }
        }

        // Floating animated FAB
        if (selectedAnimalTag == null) {
            AnimatedFab(
                expanded = fabExpanded,
                onToggle = { fabExpanded = !fabExpanded },
                modifier = Modifier.align(Alignment.BottomEnd).padding(20.dp),
            )
        }
    }
}

// ── Hero header ───────────────────────────────────────────────────────────────
@Composable
private fun HeroHeader(managerName: String, farmName: String, currentRoleLabel: String) {
    Surface(
        shape = RoundedCornerShape(30.dp),
        color = Color.White.copy(alpha = 0.08f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(Forest, DeepForest, Color(0xFF145B54))))
                .padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Chip(text = "MANAGER / $currentRoleLabel", color = Mint, textColor = DeepForest)
                    Text(farmName, style = MaterialTheme.typography.headlineMedium.copy(color = Cream, fontWeight = FontWeight.Black, fontSize = 34.sp))
                    Text("Central command for milk forecasting, staff control, khata automation, and inventory trust loop.", color = Cream.copy(alpha = 0.82f), style = MaterialTheme.typography.bodyMedium)
                }
                Box(
                    modifier = Modifier.size(64.dp).background(Color.White.copy(alpha = 0.14f), CircleShape).border(1.dp, Color.White.copy(alpha = 0.14f), CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(managerName.take(2).uppercase(), color = Cream, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatPill(title = "Auto Rate",      value = "Live FAT/SNF")
                StatPill(title = "3 Month Supply", value = "Projected")
                StatPill(title = "SMS Gateway",    value = "Trigger Ready")
            }
        }
    }
}

// ── Dashboard hero (KPIs + charts + tabs) ────────────────────────────────────
@Composable
private fun DashboardHero(
    selectedSection: ConsoleSection,
    onSelectSection: (ConsoleSection) -> Unit,
    data: ConsolePreviewData,
) {
    Surface(shape = RoundedCornerShape(32.dp), color = Color.White.copy(alpha = 0.92f), shadowElevation = 18.dp) {
        Column(modifier = Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                data.kpiCards.forEach { kpi ->
                    Surface(modifier = Modifier.weight(1f), shape = RoundedCornerShape(24.dp), color = if (kpi.highlight) Color(0xFF102F2D) else Card) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(kpi.label, color = if (kpi.highlight) Cream.copy(alpha = 0.8f) else Muted, style = MaterialTheme.typography.labelLarge)
                            Text(kpi.value, color = if (kpi.highlight) Cream else Ink, fontWeight = FontWeight.Black, fontSize = 24.sp)
                            Text(kpi.footer, color = if (kpi.highlight) Mint else Muted, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
            AlertBanner(data.alert)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                AnalyticsCard(modifier = Modifier.weight(1f), title = "Lactation Curve", subtitle = "Tap chart to inspect values", points = data.lactationCurve, primary = Accent, secondary = AccentSoft)
                AnalyticsCard(modifier = Modifier.weight(1f), title = "3-Month Supply Deal", subtitle = "Revenue and supply estimate", points = data.supplyPrediction, primary = Gold, secondary = Accent)
            }
            SectionTabs(selected = selectedSection, onSelectSection = onSelectSection)
        }
    }
}

// ── Section: Dashboard ────────────────────────────────────────────────────────
@Composable
private fun DashboardOverview(data: ConsolePreviewData) {
    TwoColumnGrid(
        left  = { QuickFabHint(); SmartCenterLoop(data); NotificationGatewayCard(data) },
        right = { KhataSnapshotCard(data); InventorySnapshotCard(data); FinancialReportsCard(data) },
    )
}

// ── Section: Livestock ────────────────────────────────────────────────────────
@Composable
private fun LivestockSection(
    data: ConsolePreviewData,
    onAnimalClick: (String) -> Unit,
    showHealthForm: Boolean,
    onToggleHealthForm: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Status summary pills
        Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            data.livestockCards.groupBy { it.status }.forEach { (status, animals) ->
                StatusSummaryPill(label = status, count = animals.size, color = animals.first().statusColor)
            }
        }
        TwoColumnGrid(
            left = {
                DetailCard(title = "Livestock Grid", subtitle = "Tap any card to open full detail, breeding timeline, and health logs.") {
                    data.livestockCards.chunked(2).forEach { row ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            row.forEach { item ->
                                Surface(
                                    modifier = Modifier.weight(1f).clickable { onAnimalClick(item.tag) },
                                    shape = RoundedCornerShape(22.dp),
                                    color = Card,
                                    border = BorderStroke(1.dp, item.statusColor.copy(alpha = 0.35f)),
                                ) {
                                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text(item.tag, fontWeight = FontWeight.ExtraBold, color = Ink)
                                        Chip(text = item.status, color = item.statusColor.copy(alpha = 0.14f), textColor = item.statusColor)
                                        Text(item.meta, color = Muted, style = MaterialTheme.typography.bodySmall)
                                        Text(item.breeding, color = Ink, style = MaterialTheme.typography.bodySmall)
                                        Text("View Detail →", color = Accent, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                            if (row.size == 1) Spacer(modifier = Modifier.weight(1f))
                        }
                        Spacer(Modifier.height(10.dp))
                    }
                }
            },
            right = {
                DetailCard(title = "Breeding Lifecycle Tracker", subtitle = "AI, pregnancy diagnosis, and expected calving flow.") {
                    data.breedingTimeline.forEachIndexed { i, item -> TimelineRow(index = i + 1, text = item) }
                }
                DetailCard(title = "Health Logs & Deworming", subtitle = "Tap below to log a new health event for any animal.") {
                    data.healthAlerts.forEach { item ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(item.title, color = Ink, fontWeight = FontWeight.SemiBold)
                            Text(item.value, color = item.color, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.height(8.dp))
                    }
                    Surface(
                        modifier = Modifier.fillMaxWidth().clickable { onToggleHealthForm() },
                        shape = RoundedCornerShape(14.dp),
                        color = if (showHealthForm) DangerSoft else Accent.copy(alpha = 0.1f),
                    ) {
                        Text(
                            text = if (showHealthForm) "✕  Cancel" else "+  Log Health Event",
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp),
                            color = if (showHealthForm) Danger else Accent,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center,
                        )
                    }
                    AnimatedVisibility(visible = showHealthForm, enter = fadeIn() + expandVertically(), exit = fadeOut() + shrinkVertically()) {
                        HealthLogForm()
                    }
                }
            },
        )
    }
}

// ── Animal detail drilldown ───────────────────────────────────────────────────
@Composable
private fun AnimalDetailScreen(animal: LivestockCardUi, onBack: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Button(onClick = onBack, shape = RoundedCornerShape(18.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.2f), contentColor = Cream)) {
                Text("← Back to Herd", fontWeight = FontWeight.Bold)
            }
            Chip(text = "ANIMAL DETAIL", color = Mint.copy(alpha = 0.2f), textColor = Mint)
        }

        // Profile card
        Surface(shape = RoundedCornerShape(28.dp), color = Color.White.copy(alpha = 0.96f), shadowElevation = 14.dp) {
            Column(modifier = Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text(animal.tag, color = Ink, fontWeight = FontWeight.Black, fontSize = 28.sp)
                        Text(animal.meta, color = Muted)
                    }
                    Chip(text = animal.status, color = animal.statusColor.copy(alpha = 0.14f), textColor = animal.statusColor)
                }
                Surface(color = Color(0xFFEFF8F3), shape = RoundedCornerShape(14.dp)) {
                    Text(text = animal.breeding, modifier = Modifier.fillMaxWidth().padding(12.dp), color = Ink, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        // Breeding lifecycle visual
        BreedingTimelineCard()

        // Action buttons
        Surface(shape = RoundedCornerShape(28.dp), color = Color.White.copy(alpha = 0.96f), shadowElevation = 14.dp) {
            Column(modifier = Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Quick Actions", color = Ink, fontWeight = FontWeight.Black, fontSize = 18.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ActionButton("Log AI", Accent, Modifier.weight(1f))
                    ActionButton("Health Log", Gold, Modifier.weight(1f))
                    ActionButton("Archive", Danger, Modifier.weight(1f))
                }
            }
        }
    }
}

// ── Breeding lifecycle visual card ───────────────────────────────────────────
@Composable
private fun BreedingTimelineCard() {
    DetailCard(title = "Breeding Lifecycle Timeline", subtitle = "Visual phase tracker: AI → Pregnancy Diagnosis → Calving.") {
        val phases = listOf(
            Triple("Inseminated", Accent, 0.33f),
            Triple("PD Due",      Gold,   0.33f),
            Triple("Calving",     Color(0xFF2E7D68), 0.34f),
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            phases.forEach { (label, color, fraction) ->
                Surface(
                    modifier = Modifier.weight(fraction).height(38.dp),
                    shape = RoundedCornerShape(10.dp),
                    color = color.copy(alpha = 0.18f),
                    border = BorderStroke(1.dp, color.copy(alpha = 0.4f)),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(label, color = color, fontWeight = FontWeight.ExtraBold, fontSize = 11.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 4.dp))
                    }
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        Canvas(modifier = Modifier.fillMaxWidth().height(20.dp)) {
            val y = size.height / 2f
            drawLine(color = Muted.copy(alpha = 0.2f), start = Offset(0f, y), end = Offset(size.width, y), strokeWidth = 3f)
            drawLine(color = Accent, start = Offset(0f, y), end = Offset(size.width * 0.35f, y), strokeWidth = 4f)
            listOf(0f, 0.35f, 0.68f, 1f).forEachIndexed { idx, fraction ->
                val x = fraction * size.width
                val c = if (idx <= 1) Accent else if (idx == 2) Gold else Muted
                drawCircle(color = c, radius = 8f, center = Offset(x, y))
                drawCircle(color = Color.White, radius = 4f, center = Offset(x, y))
            }
        }
        Spacer(Modifier.height(6.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("AI Date",  color = Accent,              style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            Text("PD Due",   color = Gold,                style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            Text("Calving",  color = Color(0xFF2E7D68),   style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
        }
    }
}

// ── Section: Outer Center ─────────────────────────────────────────────────────
@Composable
private fun OuterCenterSection(data: ConsolePreviewData, searchQuery: String, onSearchChange: (String) -> Unit) {
    val filtered = remember(searchQuery) {
        if (searchQuery.isBlank()) data.milkCollections
        else data.milkCollections.filter { it.farmerName.contains(searchQuery, ignoreCase = true) }
    }
    TwoColumnGrid(
        left = {
            DetailCard(title = "Outer Milk Collection", subtitle = "Search kisan by name, quick entry with FAT/SNF, auto rate.") {
                Box(modifier = Modifier.fillMaxWidth().background(Color(0xFFF2F7F4), RoundedCornerShape(18.dp)).padding(12.dp)) {
                    SearchBarReal(value = searchQuery, onValueChange = onSearchChange, hint = "Search kisan name / phone…")
                }
                Spacer(Modifier.height(12.dp))
                if (filtered.isEmpty()) {
                    Surface(color = Color(0xFFF2F7F4), shape = RoundedCornerShape(14.dp)) {
                        Text("No results for \"$searchQuery\"", modifier = Modifier.fillMaxWidth().padding(14.dp), color = Muted, textAlign = TextAlign.Center)
                    }
                } else {
                    filtered.forEach { entry ->
                        RecordRow(
                            title    = "${entry.farmerName}  ${entry.rateBreakdown.quantityLiters}L",
                            subtitle = "FAT ${entry.rateBreakdown.fat}  SNF ${entry.rateBreakdown.snf}",
                            trailing = "Rs ${entry.rateBreakdown.totalAmount.toInt()}",
                        )
                    }
                }
            }
            FarmerPayoutCard(data)
        },
        right = {
            DetailCard(title = "Auto-Rate Calculator", subtitle = "Instant settlement: quantity × FAT/SNF + bonus − deductions.") {
                val rate = data.milkCollections.first().rateBreakdown
                RateMetric("Base Rate",  "Rs ${rate.baseRatePerLiter}/L")
                RateMetric("Bonus",      "Rs ${rate.bonusAmount}")
                RateMetric("Deductions", "Rs ${rate.deductionAmount}")
                RateMetric("Payable",    "Rs ${rate.totalAmount}", emphasize = true)
            }
            NotificationGatewayCard(data)
        },
    )
}

// ── Farmer payout board ───────────────────────────────────────────────────────
@Composable
private fun FarmerPayoutCard(data: ConsolePreviewData) {
    DetailCard(title = "Farmer Payout Board", subtitle = "Approve and clear pending cycle payments for each kisan.") {
        data.milkCollections.groupBy { it.farmerId }.forEach { (_, entries) ->
            val totalLiters = entries.sumOf { it.rateBreakdown.quantityLiters }
            val totalAmount = entries.sumOf { it.rateBreakdown.totalAmount }
            val isPending   = entries.any { it.paymentStatus == CollectionPaymentStatus.PENDING }
            val farmer      = entries.first()
            Surface(
                shape  = RoundedCornerShape(18.dp),
                color  = if (isPending) Gold.copy(alpha = 0.1f) else Accent.copy(alpha = 0.1f),
                border = BorderStroke(1.dp, if (isPending) Gold.copy(alpha = 0.4f) else Accent.copy(alpha = 0.4f)),
            ) {
                Row(modifier = Modifier.fillMaxWidth().padding(14.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(farmer.farmerName, color = Ink, fontWeight = FontWeight.ExtraBold)
                        Text("${totalLiters}L total  •  ${entries.size} entries", color = Muted, style = MaterialTheme.typography.bodySmall)
                    }
                    Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Rs ${totalAmount.toInt()}", color = Ink, fontWeight = FontWeight.Black)
                        Surface(shape = RoundedCornerShape(12.dp), color = if (isPending) Gold else Accent) {
                            Text(
                                text     = if (isPending) "Pay Now" else "Paid ✓",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                color    = if (isPending) Ink else DeepForest,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 12.sp,
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(10.dp))
        }
    }
}

// ── Section: Khata ────────────────────────────────────────────────────────────
@Composable
private fun KhataSection(data: ConsolePreviewData) {
    TwoColumnGrid(
        left  = { KhataSnapshotCard(data) },
        right = {
            DetailCard(title = "Automation Flow", subtitle = "Income, expense, milk payment, and feed production all push into one ledger.") {
                data.khataFlow.forEachIndexed { i, item -> TimelineRow(index = i + 1, text = item) }
            }
            FinancialReportsCard(data)
        },
    )
}

// ── Section: Inventory ────────────────────────────────────────────────────────
@Composable
private fun InventorySection(data: ConsolePreviewData) {
    TwoColumnGrid(
        left  = {
            InventorySnapshotCard(data)
            BiogasWasteCard(data.wasteLogs)
        },
        right = {
            DetailCard(title = "Batch Feed Production", subtitle = "Smart formulation deducts stock automatically from linked items.") {
                data.feedBatches.forEach { batch ->
                    RecordRow(
                        title    = batch.batchName,
                        subtitle = "${batch.totalOutputKg} kg output  •  ${batch.ingredients.size} ingredients",
                        trailing = if (batch.autoStockDeducted) "Auto Deducted" else "Pending",
                    )
                }
            }
            FodderYieldCard(data.fodderYields)
        },
    )
}

// ── Fodder yield tracker ──────────────────────────────────────────────────────
@Composable
private fun FodderYieldCard(yields: List<FodderYieldUi>) {
    DetailCard(title = "Fodder Yield Tracker", subtitle = "Land plot-linked harvest records and current availability.") {
        yields.forEach { y ->
            Surface(shape = RoundedCornerShape(16.dp), color = Color(0xFFEFF8F3), border = BorderStroke(1.dp, Accent.copy(alpha = 0.2f))) {
                Column(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text(y.partitionName, color = Ink, fontWeight = FontWeight.ExtraBold)
                            Text("${y.cropType}  •  ${y.harvestDate}", color = Muted, style = MaterialTheme.typography.labelSmall)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("${y.yieldKg.toInt()} kg", color = Accent, fontWeight = FontWeight.Black)
                            Text("harvested", color = Muted, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                    // Stock bar
                    Box(modifier = Modifier.fillMaxWidth().height(6.dp)) {
                        Surface(modifier = Modifier.fillMaxSize(), color = Accent.copy(alpha = 0.15f), shape = RoundedCornerShape(3.dp)) {}
                        Surface(modifier = Modifier.fillMaxWidth(y.stockFraction).fillMaxHeight(), color = Accent, shape = RoundedCornerShape(3.dp)) {}
                    }
                    Text("${(y.stockFraction * 100).toInt()}% remaining in store", color = Muted, style = MaterialTheme.typography.labelSmall)
                }
            }
            Spacer(Modifier.height(10.dp))
        }
    }
}

// ── Section: Farm Config ──────────────────────────────────────────────────────
@Composable
private fun FarmConfigSection(data: ConsolePreviewData) {
    TwoColumnGrid(
        left = {
            DetailCard(title = "Zameen & Setup Validation", subtitle = "Sub-divisions must never exceed total farm area.") {
                DonutChart(partitions = data.landPartitions)
                Spacer(Modifier.height(12.dp))
                data.landPartitions.forEach { p ->
                    RecordRow(title = p.name, subtitle = p.cropOrUse, trailing = "${p.area.value} ${p.area.unit.name.lowercase()}")
                }
            }
        },
        right = {
            DetailCard(title = "Validation Rule", subtitle = "Critical architectural logic connected to farm config.") {
                Surface(shape = RoundedCornerShape(20.dp), color = Danger.copy(alpha = 0.14f)) {
                    Text("SUM OF PARTS <= TOTAL AREA", modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp), color = Danger, fontWeight = FontWeight.ExtraBold)
                }
                Spacer(Modifier.height(12.dp))
                data.configRules.forEachIndexed { i, item -> TimelineRow(index = i + 1, text = item) }
            }
            SmsGatewayConfigCard(data.smsConfig)
        },
    )
}

// ── Biogas & Waste card ───────────────────────────────────────────────────────
@Composable
private fun BiogasWasteCard(logs: List<WasteLogUi>) {
    val biogasColor  = Color(0xFF2E7D68)
    val compostColor = Color(0xFF7E5B2E)
    val dungColor    = Color(0xFF6B5A3E)

    val totalBiogas  = logs.mapNotNull { it.biogasM3 }.sum()
    val totalCompost = logs.mapNotNull { it.compositeKg }.sum()
    val totalDung    = logs.filter { it.category == "DUNG" }.sumOf { it.quantityKg }

    DetailCard(title = "Biogas & Waste Management", subtitle = "Dung → Biogas → Compost closed resource loop for the farm.") {
        // Summary row
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            WasteSummaryBadge("${totalDung.toInt()} kg",    "Dung",    dungColor,   Modifier.weight(1f))
            WasteSummaryBadge("${totalBiogas} m³",          "Biogas",  biogasColor, Modifier.weight(1f))
            WasteSummaryBadge("${totalCompost.toInt()} kg", "Compost", compostColor,Modifier.weight(1f))
        }
        Spacer(Modifier.height(14.dp))

        // Log rows
        logs.forEach { log ->
            val accent = when (log.category) {
                "BIOGAS_OUTPUT" -> biogasColor
                "COMPOST"       -> compostColor
                else            -> dungColor
            }
            Surface(shape = RoundedCornerShape(14.dp), color = accent.copy(alpha = 0.08f), border = BorderStroke(1.dp, accent.copy(alpha = 0.25f))) {
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text(log.category.replace("_", " "), color = accent, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                        Text(log.date, color = Muted, style = MaterialTheme.typography.labelSmall)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        if (log.quantityKg > 0) Text("${log.quantityKg.toInt()} kg dung", color = Ink, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        log.biogasM3?.let { Text("${it} m³ gas", color = biogasColor, fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                        log.compositeKg?.let { Text("${it.toInt()} kg compost", color = compostColor, fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
        }
        ActionButton("+ Log Waste / Biogas Output", biogasColor, Modifier.fillMaxWidth())
    }
}

@Composable
private fun WasteSummaryBadge(value: String, label: String, color: Color, modifier: Modifier = Modifier) {
    Surface(modifier = modifier, shape = RoundedCornerShape(14.dp), color = color.copy(alpha = 0.10f)) {
        Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, color = color, fontWeight = FontWeight.Black, fontSize = 14.sp, textAlign = TextAlign.Center)
            Text(label, color = Muted, style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center)
        }
    }
}

// ── SMS Gateway Config card ───────────────────────────────────────────────────
@Composable
private fun SmsGatewayConfigCard(config: SmsGatewayConfig?) {
    val active = config?.isActive == true
    val statusColor = if (active) Accent else Danger

    DetailCard(title = "SMS Gateway Config", subtitle = "Configure which provider sends Kisan receipt SMS automatically.") {
        // Status banner
        Surface(shape = RoundedCornerShape(14.dp), color = statusColor.copy(alpha = 0.10f), border = BorderStroke(1.dp, statusColor.copy(alpha = 0.3f))) {
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Text(if (active) "● SMS Active" else "○ SMS Disabled", color = statusColor, fontWeight = FontWeight.ExtraBold)
                if (config != null) Text(config.provider.name, color = Muted, style = MaterialTheme.typography.labelSmall)
            }
        }
        Spacer(Modifier.height(12.dp))

        if (config != null) {
            RateMetric("Provider",  config.provider.name)
            RateMetric("Sender ID", config.senderId)
            config.routeId?.let { RateMetric("Route",  it) }
            Spacer(Modifier.height(4.dp))
            Surface(shape = RoundedCornerShape(10.dp), color = Color(0xFFF7F2E8)) {
                Text("API Key: ${config.apiKey}", modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), color = Muted, style = MaterialTheme.typography.labelSmall)
            }
            Spacer(Modifier.height(10.dp))
        } else {
            Text("No SMS gateway configured yet. Tap below to set one up.", color = Muted, style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(10.dp))
        }

        // Provider chips
        Text("Available Providers", color = Ink, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
        Spacer(Modifier.height(8.dp))
        Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SmsProvider.entries.forEach { provider ->
                val sel = config?.provider == provider
                Surface(
                    shape    = RoundedCornerShape(999.dp),
                    color    = if (sel) Accent else Color(0xFFEAF6EF),
                    border   = BorderStroke(1.dp, if (sel) Accent else Color(0xFFCDE8D8)),
                ) {
                    Text(provider.name, modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp), color = if (sel) DeepForest else Ink, fontWeight = if (sel) FontWeight.ExtraBold else FontWeight.Normal, fontSize = 12.sp)
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        ActionButton(if (config != null) "Update Gateway Config" else "Setup SMS Gateway", Accent, Modifier.fillMaxWidth())
    }
}

// ── Section: Staff ────────────────────────────────────────────────────────────
@Composable
private fun StaffSection(data: ConsolePreviewData) {
    TwoColumnGrid(
        left = {
            DetailCard(title = "Staff Control Panel", subtitle = "Admin can add/delete labour and dairy man, then configure access.") {
                data.staffProfiles.forEach { staff ->
                    Surface(shape = RoundedCornerShape(20.dp), color = Card) {
                        Column(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column {
                                    Text(staff.fullName, color = Ink, fontWeight = FontWeight.ExtraBold)
                                    Text(staff.role.name.replace("_", " "), color = Muted)
                                }
                                Chip(text = if (staff.isActive) "Active" else "Disabled", color = if (staff.isActive) Accent.copy(alpha = 0.14f) else DangerSoft, textColor = if (staff.isActive) Accent else Danger)
                            }
                            Text(staff.phoneNumber, color = Ink)
                            Text(staff.permissions.joinToString("  ") { "${it.module.name.lowercase()}=${it.level.name.lowercase()}" }, color = Muted, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                }
            }
        },
        right = {
            DetailCard(title = "Admin Only Actions", subtitle = "Approval matrix for sensitive operational controls.") {
                data.adminActions.forEachIndexed { i, item -> TimelineRow(index = i + 1, text = item) }
            }
            DetailCard(title = "Configurator Matrix", subtitle = "Modules are assignable with view, manage, and approve control levels.") {
                data.permissionMatrix.forEach { item -> RecordRow(title = item.first, subtitle = item.second, trailing = item.third) }
            }
        },
    )
}

// ── Shared cards ──────────────────────────────────────────────────────────────
@Composable
private fun QuickFabHint() {
    DetailCard(title = "Global + FAB", subtitle = "Tap the floating + button at the bottom-right for instant actions.") {
        listOf("Quick Doodh Entry", "Quick Kharcha / Income", "Batch Feed Production").forEach { label ->
            Surface(shape = RoundedCornerShape(18.dp), color = Color(0xFFF0F7F3)) {
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(label, color = Ink, fontWeight = FontWeight.SemiBold)
                    Text("→", color = Accent, fontWeight = FontWeight.ExtraBold)
                }
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun SmartCenterLoop(data: ConsolePreviewData) {
    DetailCard(title = "Smart Kisan Center", subtitle = "Outer center, auto-rate, SMS gateway, and farmer ledger clearance work as one trust loop.") {
        data.smartLoop.forEachIndexed { i, item -> TimelineRow(index = i + 1, text = item) }
    }
}

@Composable
private fun NotificationGatewayCard(data: ConsolePreviewData) {
    DetailCard(title = "Kisan Alerts Gateway", subtitle = "Automated customer trust message after milk receive event.") {
        Surface(shape = RoundedCornerShape(20.dp), color = Color(0xFFEFF8F3)) {
            Text(data.smsPreview, modifier = Modifier.padding(16.dp), color = Ink, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun KhataSnapshotCard(data: ConsolePreviewData) {
    DetailCard(title = "Khata Book Ledger", subtitle = "Green income and red expense with automated entries from operations.") {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ActionButton(text = "+ Income",  color = Accent, modifier = Modifier.weight(1f))
            ActionButton(text = "- Expense", color = Danger, modifier = Modifier.weight(1f))
        }
        Spacer(Modifier.height(12.dp))
        data.khataEntries.forEach { item ->
            val color = if (item.kind == KhataEntryKind.INCOME) Accent else Danger
            RecordRow(
                title        = item.title,
                subtitle     = item.sourceModule,
                trailing     = "${if (item.kind == KhataEntryKind.INCOME) "+" else "-"} Rs ${item.amount.toInt()}",
                trailingColor = color,
            )
        }
    }
}

@Composable
private fun InventorySnapshotCard(data: ConsolePreviewData) {
    DetailCard(title = "Inventory Godown", subtitle = "Livestock, fodder, and feed tabs connected with auto stock validation.") {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Chip(text = "Livestock", color = Color(0xFFEAF6EF), textColor = Ink)
            Chip(text = "Fodder",    color = Color(0xFFEAF6EF), textColor = Ink)
            Chip(text = "Inventory", color = Color(0xFFEAF6EF), textColor = Ink)
        }
        Spacer(Modifier.height(12.dp))
        data.inventoryItems.forEach { item ->
            val isLow = item.currentStock <= item.reorderLevel
            RecordRow(
                title        = item.name,
                subtitle     = "${item.category.name.lowercase()} • reorder ${item.reorderLevel} ${item.unit}",
                trailing     = "${item.currentStock} ${item.unit}",
                trailingColor = if (isLow) Danger else Ink,
            )
        }
    }
}

@Composable
private fun FinancialReportsCard(data: ConsolePreviewData) {
    DetailCard(title = "Financial Reports", subtitle = "P&L breakdown and income vs expense distribution.") {
        PieChartCanvas(income = data.incomeTotal, expense = data.expenseTotal)
        Spacer(Modifier.height(14.dp))
        RateMetric("Total Income",  "Rs ${data.incomeTotal.toInt()}")
        RateMetric("Total Expense", "Rs ${data.expenseTotal.toInt()}")
        RateMetric("Net Position",  "Rs ${(data.incomeTotal - data.expenseTotal).toInt()}", emphasize = true)
    }
}

// ── Pie chart ─────────────────────────────────────────────────────────────────
@Composable
private fun PieChartCanvas(income: Double, expense: Double) {
    val total        = (income + expense).coerceAtLeast(1.0)
    val incomeSweep  = (income / total * 360f).toFloat()
    val incomePercent = ((income / total) * 100).toInt()

    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Box(modifier = Modifier.size(100.dp), contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.size(100.dp)) {
                drawArc(color = Danger.copy(alpha = 0.85f), startAngle = -90f + incomeSweep, sweepAngle = 360f - incomeSweep, useCenter = true, topLeft = Offset.Zero, size = Size(size.width, size.height))
                drawArc(color = Accent, startAngle = -90f, sweepAngle = incomeSweep, useCenter = true, topLeft = Offset.Zero, size = Size(size.width, size.height))
                drawCircle(color = Color.White, radius = size.minDimension * 0.30f)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("$incomePercent%", color = Ink, fontWeight = FontWeight.Black, fontSize = 16.sp)
                Text("profit", color = Muted, fontSize = 10.sp)
            }
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            PieLegendItem(Accent,           "Income",  "Rs ${income.toInt()}")
            PieLegendItem(Danger,           "Expense", "Rs ${expense.toInt()}")
            PieLegendItem(Color(0xFF1E40AF),"Net P&L", "Rs ${(income - expense).toInt()}", emphasize = true)
        }
    }
}

@Composable
private fun PieLegendItem(color: Color, label: String, amount: String, emphasize: Boolean = false) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(modifier = Modifier.size(10.dp).background(color, CircleShape))
        Text(label, color = Muted, style = MaterialTheme.typography.bodySmall)
        Spacer(Modifier.weight(1f))
        Text(amount, color = if (emphasize) Ink else color, fontWeight = if (emphasize) FontWeight.Black else FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
    }
}

// ── Animated floating FAB ─────────────────────────────────────────────────────
@Composable
private fun AnimatedFab(expanded: Boolean, onToggle: () -> Unit, modifier: Modifier = Modifier) {
    val rotation by animateFloatAsState(targetValue = if (expanded) 45f else 0f, animationSpec = tween(280), label = "fab_rot")
    Column(modifier = modifier, horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        AnimatedVisibility(
            visible = expanded,
            enter   = fadeIn(tween(180)) + expandVertically(expandFrom = Alignment.Bottom),
            exit    = fadeOut(tween(180)) + shrinkVertically(shrinkTowards = Alignment.Bottom),
        ) {
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                FabMiniButton("Batch Feed Production",  Color(0xFF1E3A5F))
                FabMiniButton("Quick Kharcha / Income", Color(0xFF4A1942))
                FabMiniButton("Quick Doodh Entry",      Forest)
            }
        }
        Box(
            modifier = Modifier
                .size(58.dp)
                .background(if (expanded) Danger else Accent, CircleShape)
                .clickable { onToggle() },
            contentAlignment = Alignment.Center,
        ) {
            Text("+", color = if (expanded) Color.White else DeepForest, fontWeight = FontWeight.Black, fontSize = 30.sp, modifier = Modifier.rotate(rotation))
        }
    }
}

@Composable
private fun FabMiniButton(label: String, color: Color) {
    Surface(shape = RoundedCornerShape(22.dp), color = color, shadowElevation = 6.dp) {
        Text(label, modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
    }
}

// ── Health log inline form ────────────────────────────────────────────────────
@Composable
private fun HealthLogForm() {
    var selectedType by remember { mutableStateOf("Deworming") }
    var notes        by remember { mutableStateOf("") }
    Column(modifier = Modifier.padding(top = 12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Event Type", color = Ink, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
        Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Deworming", "Vaccination", "Mastitis", "General").forEach { type ->
                val sel = type == selectedType
                Surface(shape = RoundedCornerShape(999.dp), color = if (sel) Accent else Color(0xFFEAF6EF), modifier = Modifier.clickable { selectedType = type }) {
                    Text(type, modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp), color = if (sel) DeepForest else Ink, fontWeight = if (sel) FontWeight.ExtraBold else FontWeight.Normal)
                }
            }
        }
        SearchBarReal(value = notes, onValueChange = { notes = it }, hint = "Health notes, observations…")
        ActionButton("Save Health Log", Accent, Modifier.fillMaxWidth())
    }
}

// ── Status summary pills ──────────────────────────────────────────────────────
@Composable
private fun StatusSummaryPill(label: String, count: Int, color: Color) {
    Surface(shape = RoundedCornerShape(999.dp), color = color.copy(alpha = 0.12f), border = BorderStroke(1.dp, color.copy(alpha = 0.3f))) {
        Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(8.dp).background(color, CircleShape))
            Text(label, color = color, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Box(modifier = Modifier.size(20.dp).background(color, CircleShape), contentAlignment = Alignment.Center) {
                Text("$count", color = Color.White, fontWeight = FontWeight.Black, fontSize = 10.sp)
            }
        }
    }
}

// ── Real search bar ───────────────────────────────────────────────────────────
@Composable
private fun SearchBarReal(value: String, onValueChange: (String) -> Unit, hint: String = "Search…") {
    Surface(
        shape  = RoundedCornerShape(16.dp),
        color  = Color.White,
        border = BorderStroke(1.5.dp, if (value.isNotEmpty()) Accent else Color(0xFFDDE8E4)),
    ) {
        BasicTextField(
            value       = value,
            onValueChange = onValueChange,
            modifier    = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp),
            textStyle   = MaterialTheme.typography.bodyMedium.copy(color = Ink),
            singleLine  = true,
            decorationBox = { inner ->
                Box {
                    if (value.isEmpty()) Text(hint, color = Muted, style = MaterialTheme.typography.bodyMedium)
                    inner()
                }
            },
        )
    }
}

// ── Analytics card (interactive) ─────────────────────────────────────────────
@Composable
private fun AnalyticsCard(modifier: Modifier, title: String, subtitle: String, points: List<PredictionPoint>, primary: Color, secondary: Color) {
    var touchedIndex by remember { mutableStateOf<Int?>(null) }
    Surface(modifier = modifier, shape = RoundedCornerShape(24.dp), color = Card) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(title,    color = Ink,  fontWeight = FontWeight.ExtraBold)
            Text(subtitle, color = Muted, style = MaterialTheme.typography.bodySmall)
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .pointerInput(points) {
                        detectTapGestures { tap ->
                            if (points.size < 2) return@detectTapGestures
                            val stepX = size.width.toFloat() / (points.size - 1)
                            val nearest = points.indices.minByOrNull { abs(tap.x - stepX * it) }
                            touchedIndex = if (touchedIndex == nearest) null else nearest
                        }
                    },
            ) {
                if (points.isEmpty()) return@Canvas
                val maxV  = points.maxOf { it.projectedMilkLiters }.toFloat()
                val minV  = points.minOf { it.projectedMilkLiters }.toFloat()
                val range = (maxV - minV).takeIf { it > 0f } ?: 1f
                val stepX = size.width / (points.size - 1).coerceAtLeast(1)

                drawLine(color = Muted.copy(alpha = 0.2f), start = Offset(0f, size.height - 10f), end = Offset(size.width, size.height - 10f), strokeWidth = 2f)

                val fill = Path(); val line = Path()
                val pts  = mutableListOf<Offset>()
                points.forEachIndexed { i, p ->
                    val x = stepX * i
                    val y = size.height - 18f - ((p.projectedMilkLiters.toFloat() - minV) / range) * (size.height - 36f)
                    pts.add(Offset(x, y))
                    if (i == 0) { line.moveTo(x, y); fill.moveTo(x, size.height); fill.lineTo(x, y) }
                    else        { line.lineTo(x, y); fill.lineTo(x, y) }
                }
                fill.lineTo(size.width, size.height); fill.close()
                drawPath(path = fill, brush = Brush.verticalGradient(listOf(primary.copy(alpha = 0.28f), Color.Transparent)))
                drawPath(path = line, color = primary, style = Stroke(width = 6f, cap = StrokeCap.Round))

                pts.forEachIndexed { i, o ->
                    val hit = i == touchedIndex
                    drawCircle(color = if (hit) primary else secondary, radius = if (hit) 11f else 6f, center = o)
                    if (hit) drawCircle(color = Color.White, radius = 5f, center = o)
                }
                touchedIndex?.let { idx ->
                    drawLine(
                        color       = primary.copy(alpha = 0.35f),
                        start       = Offset(pts[idx].x, 0f),
                        end         = Offset(pts[idx].x, size.height),
                        strokeWidth = 2f,
                        pathEffect  = PathEffect.dashPathEffect(floatArrayOf(8f, 4f)),
                    )
                }
            }
            // Tooltip
            touchedIndex?.let { idx ->
                val p = points[idx]
                Surface(color = Ink.copy(alpha = 0.07f), shape = RoundedCornerShape(10.dp)) {
                    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(p.label, color = Ink, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                        Text("${p.projectedMilkLiters.toInt()}L", color = primary, fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.bodySmall)
                        if (p.projectedRevenue > 0) Text("Rs ${p.projectedRevenue.toInt()}", color = Muted, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                    }
                }
            } ?: Text("Tap chart to inspect values", color = Muted.copy(alpha = 0.6f), style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                points.forEach { Text(it.label, color = Muted, style = MaterialTheme.typography.labelSmall) }
            }
        }
    }
}

// ── Alert banner ──────────────────────────────────────────────────────────────
@Composable
private fun AlertBanner(alert: DashboardAlert) {
    val colors = when (alert.severity) {
        DashboardAlertSeverity.INFO     -> listOf(Color(0xFF2C8E78), Color(0xFF4CB899))
        DashboardAlertSeverity.WARNING  -> listOf(Color(0xFFD18B2E), Color(0xFFE0AE55))
        DashboardAlertSeverity.CRITICAL -> listOf(Color(0xFFB93D47), Color(0xFFD65A63))
    }
    Surface(shape = RoundedCornerShape(24.dp), color = Color.Transparent) {
        Row(
            modifier = Modifier.fillMaxWidth().background(Brush.horizontalGradient(colors), RoundedCornerShape(24.dp)).padding(horizontal = 18.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically,
        ) {
            Column {
                Text(alert.title,   color = Color.White, fontWeight = FontWeight.Black)
                Text(alert.message, color = Color.White.copy(alpha = 0.9f))
            }
            alert.actionLabel?.let { Chip(text = it, color = Color.White.copy(alpha = 0.18f), textColor = Color.White) }
        }
    }
}

// ── Section tabs ──────────────────────────────────────────────────────────────
@Composable
private fun SectionTabs(selected: ConsoleSection, onSelectSection: (ConsoleSection) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        ConsoleSection.entries.forEach { section ->
            val active = section == selected
            Button(
                onClick = { onSelectSection(section) },
                shape   = RoundedCornerShape(18.dp),
                colors  = ButtonDefaults.buttonColors(containerColor = if (active) Accent else Color(0xFFEAF3EE), contentColor = if (active) DeepForest else Ink),
            ) { Text(section.label, fontWeight = FontWeight.Bold) }
        }
    }
}

// ── Two-column layout ─────────────────────────────────────────────────────────
@Composable
private fun TwoColumnGrid(left: @Composable () -> Unit, right: @Composable () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.Top) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(16.dp)) { left() }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(16.dp)) { right() }
    }
}

// ── Donut chart (land partitions) ─────────────────────────────────────────────
@Composable
private fun DonutChart(partitions: List<LandPartition>) {
    val total = partitions.sumOf { it.area.convertTo(AreaUnit.ACRE) }.toFloat().takeIf { it > 0f } ?: 1f
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(180.dp)) {
            var start = -90f
            partitions.forEach { p ->
                val sweep = (p.area.convertTo(AreaUnit.ACRE).toFloat() / total) * 360f
                drawArc(color = colorFromHex(p.colorHex), startAngle = start, sweepAngle = sweep, useCenter = false, topLeft = Offset.Zero, size = Size(size.width, size.height), style = Stroke(width = 34f))
                start += sweep
            }
        }
        Text("Land Split", color = Ink, fontWeight = FontWeight.ExtraBold)
    }
}

// ── Shared small components ───────────────────────────────────────────────────
@Composable
private fun DetailCard(title: String, subtitle: String, content: @Composable () -> Unit) {
    Surface(shape = RoundedCornerShape(28.dp), color = Color.White.copy(alpha = 0.96f), shadowElevation = 14.dp) {
        Column(modifier = Modifier.fillMaxWidth().padding(18.dp)) {
            Text(title,    color = Ink,  fontWeight = FontWeight.Black, fontSize = 20.sp)
            Spacer(Modifier.height(4.dp))
            Text(subtitle, color = Muted, style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(14.dp))
            content()
        }
    }
}

@Composable
private fun TimelineRow(index: Int, text: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Top) {
        Box(modifier = Modifier.size(28.dp).background(Accent.copy(alpha = 0.14f), CircleShape), contentAlignment = Alignment.Center) {
            Text(index.toString(), color = Accent, fontWeight = FontWeight.ExtraBold)
        }
        Text(text, color = Ink, style = MaterialTheme.typography.bodyMedium)
    }
    Spacer(Modifier.height(10.dp))
}

@Composable
private fun RecordRow(title: String, subtitle: String, trailing: String, trailingColor: Color = Ink) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title,    color = Ink,  fontWeight = FontWeight.Bold)
            Text(subtitle, color = Muted, style = MaterialTheme.typography.bodySmall)
        }
        Spacer(Modifier.width(12.dp))
        Text(trailing, color = trailingColor, fontWeight = FontWeight.ExtraBold)
    }
    Spacer(Modifier.height(10.dp))
}

@Composable
private fun RateMetric(label: String, value: String, emphasize: Boolean = false) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Muted)
        Text(value, color = if (emphasize) Ink else Accent, fontWeight = if (emphasize) FontWeight.Black else FontWeight.Bold)
    }
    Spacer(Modifier.height(10.dp))
}

@Composable
private fun ActionButton(text: String, color: Color, modifier: Modifier = Modifier) {
    Button(
        onClick  = {},
        modifier = modifier,
        shape    = RoundedCornerShape(18.dp),
        colors   = ButtonDefaults.buttonColors(containerColor = color, contentColor = if (color == Danger) Color.White else DeepForest),
    ) { Text(text, fontWeight = FontWeight.ExtraBold) }
}

@Composable
private fun Chip(text: String, color: Color, textColor: Color) {
    Surface(shape = RoundedCornerShape(999.dp), color = color) {
        Text(text, modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp), color = textColor, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
private fun StatPill(title: String, value: String) {
    Surface(shape = RoundedCornerShape(18.dp), color = Color.White.copy(alpha = 0.08f), border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f))) {
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
            Text(title, color = Cream.copy(alpha = 0.72f), style = MaterialTheme.typography.labelMedium)
            Text(value, color = Cream, fontWeight = FontWeight.Bold)
        }
    }
}

// ── Utility ───────────────────────────────────────────────────────────────────
private fun colorFromHex(hex: String): Color = Color(hex.removePrefix("#").toLong(16) or 0xFF000000L)

// ── Enums & UI models ─────────────────────────────────────────────────────────
private enum class ConsoleSection(val label: String) {
    Dashboard("Dashboard"), Livestock("Livestock"), OuterCenter("Outer Center"),
    Khata("Khata"), Inventory("Inventory"), Config("Farm Config"), Staff("Staff"),
}

private data class KpiCardUi(val label: String, val value: String, val footer: String, val highlight: Boolean = false)
private data class LivestockCardUi(val tag: String, val status: String, val statusColor: Color, val meta: String, val breeding: String)
private data class HealthAlertUi(val title: String, val value: String, val color: Color)
private data class FodderYieldUi(val partitionName: String, val cropType: String, val yieldKg: Double, val harvestDate: String, val stockFraction: Float)
private data class WasteLogUi(val category: String, val quantityKg: Double, val biogasM3: Double?, val compositeKg: Double?, val date: String)

// ── Preview data ──────────────────────────────────────────────────────────────
private data class ConsolePreviewData(
    val kpiCards: List<KpiCardUi>,
    val alert: DashboardAlert,
    val lactationCurve: List<PredictionPoint>,
    val supplyPrediction: List<PredictionPoint>,
    val livestockCards: List<LivestockCardUi>,
    val breedingTimeline: List<String>,
    val healthAlerts: List<HealthAlertUi>,
    val milkCollections: List<MilkCollectionEntry>,
    val smsPreview: String,
    val khataEntries: List<KhataLedgerEntry>,
    val inventoryItems: List<InventoryItem>,
    val feedBatches: List<FeedBatchLog>,
    val landPartitions: List<LandPartition>,
    val staffProfiles: List<StaffAccessProfile>,
    val smartLoop: List<String>,
    val khataFlow: List<String>,
    val inventoryLinks: List<String>,
    val configRules: List<String>,
    val adminActions: List<String>,
    val permissionMatrix: List<Triple<String, String, String>>,
    val incomeTotal: Double,
    val expenseTotal: Double,
    val fodderYields: List<FodderYieldUi>,
    val wasteLogs: List<WasteLogUi>,
    val smsConfig: SmsGatewayConfig?,
) {
    companion object {
        fun sample(farmName: String): ConsolePreviewData {
            val kpi = KpiSnapshot("kpi-1", "farm-1", 0L, 575.0, 19, 60977.0, "Makka", 72.0)
            val khataEntries = listOf(
                KhataLedgerEntry("kh-1", "farm-1", KhataEntryKind.INCOME,  "Kisan milk receipt",    504.0,   "Outer Center", "mc-1", 0L, "u-1"),
                KhataLedgerEntry("kh-2", "farm-1", KhataEntryKind.INCOME,  "Animal sale advance",   12000.0, "Livestock",    "lv-2", 0L, "u-1"),
                KhataLedgerEntry("kh-3", "farm-1", KhataEntryKind.EXPENSE, "Makka purchase",        3600.0,  "Inventory",    "in-1", 0L, "u-1"),
                KhataLedgerEntry("kh-4", "farm-1", KhataEntryKind.EXPENSE, "Deworming medicine",    950.0,   "Health",       "hl-1", 0L, "u-1"),
            )
            return ConsolePreviewData(
                kpiCards = listOf(
                    KpiCardUi("Total Milk",      "${kpi.totalMilkLiters.toInt()}L", "Today vs evening"),
                    KpiCardUi("Active Animals",  "${kpi.activeAnimals}",            "Milking + pregnant"),
                    KpiCardUi("Net Profit/Loss", "Rs ${kpi.netProfitLoss.toInt()}",  "Month to date", highlight = true),
                ),
                alert = DashboardAlert("alert-1", "farm-1", "Critical Alerts",
                    "Low stock: Makka  •  #04 Deworming due  •  Supply spike expected in 45 days",
                    DashboardAlertSeverity.CRITICAL, "Resolve Today"),
                lactationCurve = listOf(
                    PredictionPoint(0, "Now", 510.0, 0.0,  PredictionSource.LACTATION_CURVE),
                    PredictionPoint(1, "M+1", 560.0, 0.0,  PredictionSource.LACTATION_CURVE),
                    PredictionPoint(2, "M+2", 605.0, 0.0,  PredictionSource.LACTATION_CURVE),
                    PredictionPoint(3, "M+3", 640.0, 0.0,  PredictionSource.LACTATION_CURVE),
                ),
                supplyPrediction = listOf(
                    PredictionPoint(0, "Now", 510.0, 35500.0, PredictionSource.SUPPLY_DEAL),
                    PredictionPoint(1, "M+1", 590.0, 42100.0, PredictionSource.SUPPLY_DEAL),
                    PredictionPoint(2, "M+2", 635.0, 46800.0, PredictionSource.SUPPLY_DEAL),
                    PredictionPoint(3, "M+3", 690.0, 51200.0, PredictionSource.SUPPLY_DEAL),
                ),
                livestockCards = listOf(
                    LivestockCardUi("Tag 01", "Milking",      Accent,           "HF • 3 yr 4 mo",     "AI done • calving due in 97 days"),
                    LivestockCardUi("Tag 04", "Pregnant",     Gold,             "Sahiwal • 4 yr",      "PD due tomorrow"),
                    LivestockCardUi("Tag 08", "Stable",       Color(0xFF2E7D68),"Jersey • 2 yr 8 mo",  "No repeat required"),
                    LivestockCardUi("Tag 12", "Health Watch", Danger,           "Murrah • 5 yr",       "Deworming due"),
                ),
                breedingTimeline = listOf(
                    "Log insemination from the livestock card and auto-calculate diagnosis + calving dates.",
                    "Use lactation curve projection to estimate future milk supply from breeding status.",
                    "Push high-confidence supply months into deal planning for the next 3 months.",
                ),
                healthAlerts = listOf(
                    HealthAlertUi("Tag 12 deworming",    "Due",       Danger),
                    HealthAlertUi("Tag 07 mastitis check","Observe",   Gold),
                    HealthAlertUi("Vaccination drive",   "Scheduled", Accent),
                ),
                milkCollections = listOf(
                    MilkCollectionEntry("mc-1","farm-1","f-1","Kisan Mohan", MilkSession.MORNING, 0L,
                        AutoRateBreakdown(6.0, 8.6, 10.5, 48.0, 6.0, 6.0, 504.0), CollectionPaymentStatus.APPROVED, true, "u-2"),
                    MilkCollectionEntry("mc-2","farm-1","f-2","Kisan Kalu",  MilkSession.MORNING, 0L,
                        AutoRateBreakdown(5.4, 8.2, 8.0,  45.0, 0.0, 0.0, 360.0), CollectionPaymentStatus.PENDING,  false,"u-2"),
                ),
                smsPreview = "$farmName: Aaj subah aapka 10.5L doodh receive hua. FAT 6.0, SNF 8.6, Total Rs 504.00.",
                khataEntries = khataEntries,
                inventoryItems = listOf(
                    InventoryItem("inv-1","farm-1","Makka",      InventoryCategory.FEED,     "kg",     72.0, 100.0,"Arora Feed"),
                    InventoryItem("inv-2","farm-1","Dry Fodder", InventoryCategory.FODDER,   "bundle", 48.0, 20.0, "Village Supplier"),
                    InventoryItem("inv-3","farm-1","Mineral Mix",InventoryCategory.MEDICINE, "pack",   11.0, 8.0,  "Dairy Hub"),
                ),
                feedBatches = listOf(
                    FeedBatchLog("fb-1","farm-1","Morning Dairy Mix", 0L, 125.0,
                        listOf(FeedFormulaIngredient("inv-1","Makka",40.0,"kg"), FeedFormulaIngredient("inv-2","Dry Fodder",25.0,"kg")),
                        true, null, "u-3"),
                ),
                landPartitions = listOf(
                    LandPartition("lp-1","farm-1","Green Fodder",  AreaConfiguration(4.5, AreaUnit.ACRE), "Napier block",        "#3CCF91"),
                    LandPartition("lp-2","farm-1","Dry Storage",   AreaConfiguration(1.8, AreaUnit.ACRE), "Hay and feed store",  "#E7C76A"),
                    LandPartition("lp-3","farm-1","Livestock Zone", AreaConfiguration(2.2, AreaUnit.ACRE),"Shed and movement yard","#134E5E"),
                ),
                staffProfiles = listOf(
                    StaffAccessProfile("st-1","farm-1","u-2","Raju Dairy Man", UserRole.DAIRY_MAN,"+91 98765 43210",
                        listOf(StaffPermission(StaffModule.OUTER_CENTER, PermissionLevel.MANAGE), StaffPermission(StaffModule.KHATA, PermissionLevel.VIEW), StaffPermission(StaffModule.DASHBOARD, PermissionLevel.VIEW))),
                    StaffAccessProfile("st-2","farm-1","u-3","Shyam Labour",   UserRole.LABOUR,   "+91 91234 12345",
                        listOf(StaffPermission(StaffModule.INVENTORY, PermissionLevel.MANAGE), StaffPermission(StaffModule.LIVESTOCK, PermissionLevel.VIEW))),
                ),
                smartLoop = listOf(
                    "Milk receive entry starts at Outer Center with search + quick quantity input.",
                    "FAT/SNF logic calculates rate instantly and updates payable balance.",
                    "Kisan SMS gateway sends confirmation automatically for each receive event.",
                    "Farmer ledger then moves to payout approval and khata posting.",
                ),
                khataFlow = listOf(
                    "Kisan receipts create income-ready log items when collection is approved.",
                    "Inventory purchases and feed production create expense entries automatically.",
                    "Asset sale and livestock movement can also push direct ledger events.",
                ),
                inventoryLinks = listOf(
                    "Batch feed production deducts input stock through smart formulation logic.",
                    "Fodder yield tracker maps land usage to availability planning.",
                    "Biogas and waste management close the resource loop for the farm.",
                ),
                configRules = listOf(
                    "Admin allocates total farm area at setup.",
                    "Every sub-division is stored with unit-aware area conversion.",
                    "Validation rejects any total where sum of parts exceeds farm land.",
                ),
                adminActions = listOf(
                    "Add or delete dairy man and labour accounts only from admin panel.",
                    "Grant module-wise access using view, manage, or approve level.",
                    "Restrict reports, khata approval, and staff settings to admin-only paths.",
                ),
                permissionMatrix = listOf(
                    Triple("Dashboard",    "View overall KPIs and alerts",              "VIEW"),
                    Triple("Outer Center", "Create milk entries and trigger SMS",        "MANAGE"),
                    Triple("Khata",        "Approve payout and expense posting",         "APPROVE"),
                ),
                incomeTotal  = khataEntries.filter { it.kind == KhataEntryKind.INCOME  }.sumOf { it.amount },
                expenseTotal = khataEntries.filter { it.kind == KhataEntryKind.EXPENSE }.sumOf { it.amount },
                fodderYields = listOf(
                    FodderYieldUi("Green Fodder Block", "Napier Grass",  480.0, "May 15, 2026", 0.72f),
                    FodderYieldUi("Kharif Plot",        "Maize Silage",  320.0, "May 10, 2026", 0.45f),
                    FodderYieldUi("Berseem Patch",      "Berseem Clover",210.0, "May 08, 2026", 0.88f),
                ),
                wasteLogs = listOf(
                    WasteLogUi("DUNG",         420.0, 2.8,  140.0, "May 21, 2026"),
                    WasteLogUi("BIOGAS_OUTPUT", 0.0,  5.1,  null,  "May 20, 2026"),
                    WasteLogUi("COMPOST",       180.0, null, 160.0, "May 18, 2026"),
                ),
                smsConfig = SmsGatewayConfig(
                    provider  = SmsProvider.MSG91,
                    apiKey    = "••••••••••••••",
                    senderId  = "KDFRM",
                    routeId   = "4",
                    isActive  = true,
                ),
            )
        }
    }
}
