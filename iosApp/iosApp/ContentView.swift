import SwiftUI
import SharedLogic

// ─── Root View ────────────────────────────────────────────────────────────────
struct ContentView: View {
    @StateObject private var authModel       = AuthScreenModel()
    @StateObject private var livestockModel  = LivestockScreenModel()
    @StateObject private var operationsModel = OperationsScreenModel()

    var body: some View {
        Group {
            switch authModel.state.status {

            // ── IDLE: Firebase session check not yet complete → Splash ────────
            case .idle:
                SplashView()
                    .transition(.opacity)

            // ── Authenticated: show main app ──────────────────────────────────
            case .authenticated:
                MainTabView(
                    authModel:       authModel,
                    livestockModel:  livestockModel,
                    operationsModel: operationsModel
                )
                .background(wholeScreenBackground.ignoresSafeArea())
                .overlay {
                    // Global loading spinner over tabs (data fetching)
                    if livestockModel.state.isLoading || operationsModel.state.isLoading {
                        Color.black.opacity(0.18).ignoresSafeArea()
                        ProgressView()
                            .scaleEffect(1.5)
                            .tint(.white)
                    }
                }
                .transition(.opacity)

            // ── loading / signedOut / failed → Auth form
            // (LOADING keeps the form visible so the user sees their inputs
            //  while login is in progress; isLoading drives the button spinner)
            default:
                AuthFlowView(model: authModel)
                    .background(wholeScreenBackground.ignoresSafeArea())
                    .transition(.opacity)
            }
        }
        .animation(.easeInOut(duration: 0.35), value: authModel.state.status)
        // ── Floating toast overlay ────────────────────────────────────────────
        .withToast()
        // ── Route model errors → toast ────────────────────────────────────────
        .onChange(of: authModel.state.errorMessage) { _, msg in
            if let msg, !msg.isEmpty {
                ToastManager.shared.show(msg, type: .error)
                authModel.clearError()
            }
        }
        .onChange(of: livestockModel.state.errorMessage) { _, msg in
            if let msg, !msg.isEmpty {
                ToastManager.shared.show(msg, type: .error)
                livestockModel.clearError()
            }
        }
        .onChange(of: operationsModel.state.errorMessage) { _, msg in
            if let msg, !msg.isEmpty {
                ToastManager.shared.show(msg, type: .error)
                operationsModel.clearError()
            }
        }
    }
}

// ─── Auth flow ────────────────────────────────────────────────────────────────
struct AuthFlowView: View {
    @ObservedObject var model: AuthScreenModel
    @State private var mobileNumber = ""
    @State private var password     = ""

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(alignment: .leading, spacing: 20) {
                    screenTitle(
                        title: "Digital\nDairy ERP",
                        subtitle: "Green, clean, mobile-first operations for your dairy farm."
                    )

                    dairyCard(title: "Welcome Back") {
                        Text("Login stays mobile-first for daily use. Farm setup opens in a separate screen.")
                            .foregroundStyle(Color(red: 0.36, green: 0.42, blue: 0.36))
                        NavigationLink {
                            FarmRegistrationView(model: model)
                        } label: {
                            Text("Add Farm")
                                .fontWeight(.semibold)
                                .frame(maxWidth: .infinity)
                        }
                        .buttonStyle(.borderedProminent)
                        .tint(Color(red: 0.46, green: 0.70, blue: 0.42))
                    }

                    dairyCard(title: "Login") {
                        dairyField("Mobile Number", text: $mobileNumber)
                            .keyboardType(.phonePad)
                        dairySecureField("Password", text: $password)

                        Button {
                            model.login(mobileNumber: mobileNumber, password: password)
                        } label: {
                            HStack(spacing: 10) {
                                if model.state.isLoading {
                                    ProgressView()
                                        .progressViewStyle(.circular)
                                        .tint(.white)
                                        .scaleEffect(0.85)
                                }
                                Text(model.state.isLoading ? "Logging in…" : "Login")
                                    .fontWeight(.semibold)
                            }
                            .frame(maxWidth: .infinity)
                        }
                        .buttonStyle(.borderedProminent)
                        .tint(Color(red: 0.29, green: 0.53, blue: 0.26))
                        .disabled(model.state.isLoading)
                    }

                    // errors shown as toast — no inline text needed
                }
                .padding()
            }
        }
    }
}

// ─── Farm Registration ────────────────────────────────────────────────────────
struct FarmRegistrationView: View {
    @ObservedObject var model: AuthScreenModel
    @State private var farmName   = "Smart KD Farm"
    @State private var adminName  = ""
    @State private var phoneNumber = ""
    @State private var password   = ""
    @State private var village    = ""
    @State private var district   = ""
    @State private var stateName  = "Punjab"

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 20) {
                screenTitle(title: "Create\nFarm", subtitle: "Register farm, location, and primary admin.")

                dairyCard(title: "Farm Setup") {
                    dairyField("Farm Name", text: $farmName)
                    Text("Farm Location").font(.headline).foregroundStyle(Color(red: 0.16, green: 0.29, blue: 0.15))
                    dairyField("Village", text: $village)
                    dairyField("District", text: $district)
                    dairyField("State", text: $stateName)
                    Text("Primary Admin").font(.headline).foregroundStyle(Color(red: 0.16, green: 0.29, blue: 0.15))
                    dairyField("Admin Name", text: $adminName)
                    dairyField("Mobile Number", text: $phoneNumber).keyboardType(.phonePad)
                    dairySecureField("Admin Password", text: $password)
                    Button("Register Farm") {
                        model.registerFarm(
                            farmName:     farmName,
                            adminName:    adminName,
                            adminPassword: password,
                            phoneNumber:  phoneNumber,
                            village:      village,
                            district:     district,
                            stateName:    stateName
                        )
                    }
                    .buttonStyle(.borderedProminent)
                    .tint(Color(red: 0.29, green: 0.53, blue: 0.26))
                }
            }
            .padding()
        }
        .navigationTitle("Add Farm").navigationBarTitleDisplayMode(.inline)
        .background(wholeScreenBackground.ignoresSafeArea())
    }
}

// ─── Main tab view ────────────────────────────────────────────────────────────
struct MainTabView: View {
    @ObservedObject var authModel:       AuthScreenModel
    @ObservedObject var livestockModel:  LivestockScreenModel
    @ObservedObject var operationsModel: OperationsScreenModel

    @State private var selectedRole: UserRole = .labour
    @State private var staffName  = ""
    @State private var staffEmail = ""
    @State private var staffPhone = ""

    /// Convenience: the currently logged-in user's role.
    private var role: UserRole? { authModel.state.currentRole }

    var body: some View {
        TabView {
            // ── Always visible ────────────────────────────────────────────────
            DashboardTab(authModel: authModel, operationsModel: operationsModel)
                .tabItem { Label("Dashboard", systemImage: "chart.bar.fill") }

            // LIVESTOCK – ADMIN & DAIRY_MAN can manage; LABOUR sees read-only
            if role == .admin || role == .dairyMan || role == .labour {
                LivestockTab(authModel: authModel, model: livestockModel)
                    .tabItem { Label("Livestock", systemImage: "hare.fill") }
            }

            // OUTER CENTER – ADMIN & DAIRY_MAN can record; FARMER read-only
            if role == .admin || role == .dairyMan || role == .farmer {
                OuterCenterTab(authModel: authModel, operationsModel: operationsModel)
                    .tabItem { Label("Outer Center", systemImage: "drop.fill") }
            }

            // KHATA – ADMIN & DAIRY_MAN & FARMER can view; only ADMIN edits
            if role == .admin || role == .dairyMan || role == .farmer {
                KhataTab(authModel: authModel, operationsModel: operationsModel)
                    .tabItem { Label("Khata", systemImage: "indianrupeesign.circle.fill") }
            }

            // INVENTORY – ADMIN & LABOUR & DAIRY_MAN
            if role == .admin || role == .dairyMan || role == .labour {
                InventoryTab(operationsModel: operationsModel)
                    .tabItem { Label("Inventory", systemImage: "shippingbox.fill") }
            }

            // ── ADMIN-only tabs ───────────────────────────────────────────────
            if role == .admin {
                FarmConfigTab(authModel: authModel)
                    .tabItem { Label("Farm Config", systemImage: "leaf.fill") }

                StaffTab(
                    authModel:    authModel,
                    staffName:    $staffName,
                    staffEmail:   $staffEmail,
                    staffPhone:   $staffPhone,
                    selectedRole: $selectedRole
                )
                .tabItem { Label("Staff", systemImage: "person.2.fill") }
            }
        }
        .accentColor(Color(red: 0.24, green: 0.81, blue: 0.57))
    }
}

// ─── Tab 1: Dashboard ─────────────────────────────────────────────────────────
struct DashboardTab: View {
    @ObservedObject var authModel:       AuthScreenModel
    @ObservedObject var operationsModel: OperationsScreenModel

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(alignment: .leading, spacing: 16) {
                    screenTitle(
                        title: "My\nDashboard",
                        subtitle: "\(authModel.state.currentUser?.fullName ?? "") • \(authModel.state.farmProfile?.farmName ?? "-")"
                    )

                    // KPI strip
                    ScrollView(.horizontal, showsIndicators: false) {
                        HStack(spacing: 12) {
                            kpiCard("Milk Today", "575 L",   "Morning + Evening")
                            kpiCard("Animals",   "19",       "Milking + Pregnant")
                            kpiCard("Net P&L",   "₹ 60,977", "Month to date", highlight: true)
                        }
                    }

                    // Role snapshot
                    dairyCard(title: "Role Snapshot") {
                        Text(authModel.state.currentUser?.fullName ?? "").font(.title3.bold())
                        Text("Role: \(authModel.state.currentRole?.name ?? "-")")
                        Text("Farm: \(authModel.state.farmProfile?.farmName ?? "-")")
                        Text(accessText(for: authModel.state.currentRole))
                            .foregroundStyle(Color(red: 0.36, green: 0.42, blue: 0.36))
                    }

                    // Alerts
                    dairyCard(title: "⚠️ Critical Alerts") {
                        alertRow("Low stock: Makka (72 kg remaining)")
                        alertRow("#04 Deworming overdue")
                        alertRow("Supply spike projected in 45 days")
                    }

                    // Quick actions
                    dairyCard(title: "Quick Actions") {
                        HStack(spacing: 12) {
                            quickActionButton("+ Doodh Entry",   Color(red: 0.24, green: 0.81, blue: 0.57))
                            quickActionButton("+ Expense",       Color(red: 0.82, green: 0.30, blue: 0.34))
                        }
                        HStack(spacing: 12) {
                            quickActionButton("Batch Feed",      Color(red: 0.12, green: 0.23, blue: 0.37))
                            quickActionButton("Log Health",      Color(red: 0.49, green: 0.44, blue: 0.09))
                        }
                    }

                    Button("Logout") { authModel.logout() }
                        .buttonStyle(.borderedProminent)
                        .tint(Color(red: 0.20, green: 0.38, blue: 0.20))
                }
                .padding()
            }
            .navigationTitle("Dashboard").navigationBarTitleDisplayMode(.inline)
        }
    }

    private func kpiCard(_ title: String, _ value: String, _ footer: String, highlight: Bool = false) -> some View {
        VStack(alignment: .leading, spacing: 6) {
            Text(title).font(.caption).foregroundStyle(Color(red: 0.36, green: 0.42, blue: 0.36))
            Text(value).font(.title2.bold()).foregroundStyle(highlight ? Color(red: 0.24, green: 0.81, blue: 0.57) : Color(red: 0.16, green: 0.29, blue: 0.15))
            Text(footer).font(.caption2).foregroundStyle(.secondary)
        }
        .padding(14)
        .background(.white)
        .clipShape(RoundedRectangle(cornerRadius: 20, style: .continuous))
        .shadow(color: Color.black.opacity(0.07), radius: 8, x: 0, y: 4)
        .frame(minWidth: 130)
    }

    private func alertRow(_ text: String) -> some View {
        HStack(spacing: 8) {
            Circle().fill(Color(red: 0.82, green: 0.30, blue: 0.34)).frame(width: 8, height: 8)
            Text(text).font(.subheadline).foregroundStyle(Color(red: 0.16, green: 0.29, blue: 0.15))
        }
    }

    private func quickActionButton(_ label: String, _ color: Color) -> some View {
        Button(label) {}
            .font(.subheadline.bold())
            .frame(maxWidth: .infinity)
            .padding(.vertical, 12)
            .background(color)
            .foregroundStyle(.white)
            .clipShape(RoundedRectangle(cornerRadius: 14, style: .continuous))
    }

    private func accessText(for role: UserRole?) -> String {
        switch role {
        case .admin:    return "Full CRUD, staff control, and financial analytics are enabled."
        case .dairyMan: return "Outer milk collection, farmer registration, and milking sheet are enabled."
        case .labour:   return "Livestock and inventory inputs are available with restricted edits."
        case .farmer:   return "Read-only personal milk ledger, dues, and payment summaries are enabled."
        default:        return "Role-specific access will appear after login."
        }
    }
}

// ─── Tab 2: Livestock ─────────────────────────────────────────────────────────
struct LivestockTab: View {
    @ObservedObject var authModel: AuthScreenModel
    @ObservedObject var model: LivestockScreenModel

    private var role: UserRole? { authModel.state.currentRole }
    private var canManage: Bool  { role == .admin || role == .dairyMan }
    private var canArchive: Bool { role == .admin }

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(alignment: .leading, spacing: 16) {

                    // ── Permission banner for LABOUR (read-only on Livestock) ──
                    if role == .labour {
                        permissionBanner("You have VIEW access to Livestock. Contact ADMIN for edit rights.", .yellow)
                    }

                    // Status pills
                    ScrollView(.horizontal, showsIndicators: false) {
                        HStack(spacing: 8) {
                            ForEach(model.state.statusSummary, id: \.label) { summary in
                                statusPill(summary: summary)
                            }
                        }
                    }

                    if model.state.animalCards.isEmpty {
                        dairyCard(title: "Pashu Profile") {
                            Text("No animal profiles yet. Add animals under this farm to see them here.")
                                .foregroundStyle(Color(red: 0.36, green: 0.42, blue: 0.36))
                        }
                    } else {
                        LazyVGrid(
                            columns: [GridItem(.flexible(), spacing: 12), GridItem(.flexible(), spacing: 12)],
                            spacing: 12
                        ) {
                            ForEach(model.state.animalCards, id: \.id) { card in
                                livestockCard(card)
                            }
                        }
                    }

                    // ── Actions: only for ADMIN / DAIRY_MAN ───────────────────
                    if canManage {
                        dairyCard(title: "Livestock Actions") {
                            HStack(spacing: 10) {
                                livestockActionBtn("🔬 Log AI",     Color(red: 0.12, green: 0.49, blue: 0.42))
                                livestockActionBtn("🩺 Log Health", Color(red: 0.49, green: 0.44, blue: 0.09))
                            }
                            // Archive is ADMIN-only
                            if canArchive {
                                livestockActionBtn("📦 Archive Animal", Color(red: 0.82, green: 0.30, blue: 0.34))
                                    .frame(maxWidth: .infinity)
                            }
                        }
                    }
                }
                .padding()
            }
            .navigationTitle("Livestock").navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button(action: { model.refresh() }) {
                        Image(systemName: "arrow.clockwise")
                    }
                }
            }
        }
    }

    private func livestockActionBtn(_ label: String, _ tint: Color) -> some View {
        Button(label) {}
            .font(.subheadline.bold())
            .padding(.horizontal, 14).padding(.vertical, 10)
            .background(tint.opacity(0.12))
            .foregroundStyle(tint)
            .clipShape(RoundedRectangle(cornerRadius: 12, style: .continuous))
            .overlay(RoundedRectangle(cornerRadius: 12).stroke(tint.opacity(0.35), lineWidth: 1))
    }

    private func statusPill(summary: LivestockStatusSummary) -> some View {
        let accent = color(from: summary.colorHex)
        return Text("\(summary.label)  \(summary.count)")
            .font(.caption.bold())
            .foregroundStyle(accent)
            .padding(.horizontal, 12).padding(.vertical, 8)
            .background(accent.opacity(0.14))
            .overlay(Capsule().stroke(accent.opacity(0.3), lineWidth: 1))
            .clipShape(Capsule())
    }

    private func livestockCard(_ card: AnimalCardUiModel) -> some View {
        let accent = color(from: card.statusColorHex)
        return VStack(alignment: .leading, spacing: 10) {
            HStack(alignment: .top) {
                VStack(alignment: .leading, spacing: 4) {
                    Text(card.tagNumber).font(.headline.bold()).foregroundStyle(Color(red: 0.16, green: 0.29, blue: 0.15))
                    Text(card.breed).font(.subheadline).foregroundStyle(Color(red: 0.36, green: 0.42, blue: 0.36))
                }
                Spacer(minLength: 8)
                Text(card.statusLabel).font(.caption.bold()).foregroundStyle(accent)
                    .padding(.horizontal, 10).padding(.vertical, 6)
                    .background(accent.opacity(0.14)).clipShape(Capsule())
            }
            Text(card.ageLabel).font(.subheadline.weight(.semibold)).foregroundStyle(Color(red: 0.16, green: 0.29, blue: 0.15))
            Text(card.purchasePriceLabel).font(.subheadline.weight(.bold)).foregroundStyle(Color(red: 0.16, green: 0.29, blue: 0.15))
            Text(card.breedingTimelineLabel).font(.caption).foregroundStyle(Color(red: 0.36, green: 0.42, blue: 0.36))
            Text(card.healthHeadline).font(.caption).foregroundStyle(Color(red: 0.36, green: 0.42, blue: 0.36))
        }
        .frame(maxWidth: .infinity, minHeight: 170, alignment: .topLeading)
        .padding(16)
        .background(.white.opacity(0.96))
        .clipShape(RoundedRectangle(cornerRadius: 26, style: .continuous))
        .overlay(RoundedRectangle(cornerRadius: 26, style: .continuous).stroke(accent.opacity(0.28), lineWidth: 1))
        .shadow(color: Color.black.opacity(0.06), radius: 10, x: 0, y: 6)
    }
}

// ─── Tab 3: Outer Center ──────────────────────────────────────────────────────
struct OuterCenterTab: View {
    @ObservedObject var authModel:       AuthScreenModel
    @ObservedObject var operationsModel: OperationsScreenModel

    @State private var farmerName   = ""
    @State private var quantityText = ""
    @State private var fatText      = ""
    @State private var snfText      = ""
    @State private var searchQuery  = ""

    private var filteredCollections: [MilkCollectionEntry] {
        let all = operationsModel.state.milkCollections
        guard !searchQuery.isEmpty else { return all }
        return all.filter { $0.farmerName.localizedCaseInsensitiveContains(searchQuery) }
    }

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(alignment: .leading, spacing: 16) {
                    // Search
                    dairyField("🔍 Search farmer…", text: $searchQuery)

                    // Quick entry form
                    dairyCard(title: "Doodh Entry") {
                        dairyField("Farmer Name", text: $farmerName)
                        HStack(spacing: 12) {
                            dairyField("Qty (L)", text: $quantityText).keyboardType(.decimalPad)
                            dairyField("FAT %",   text: $fatText).keyboardType(.decimalPad)
                            dairyField("SNF %",   text: $snfText).keyboardType(.decimalPad)
                        }
                        // Auto rate preview
                        if let qty = Double(quantityText), let fat = Double(fatText), qty > 0, fat > 0 {
                            let rate   = 30.0 + (fat - 5.0) * 4.0
                            let amount = qty * rate
                            HStack {
                                Text("Auto Rate: ₹\(String(format: "%.2f", rate))/L")
                                Spacer()
                                Text("Total: ₹\(String(format: "%.0f", amount))").bold()
                            }
                            .font(.subheadline)
                            .foregroundStyle(Color(red: 0.16, green: 0.29, blue: 0.15))
                            .padding(.vertical, 4)
                        }
                        Button("Record & Send SMS") {}
                            .buttonStyle(.borderedProminent)
                            .tint(Color(red: 0.29, green: 0.53, blue: 0.26))
                            .frame(maxWidth: .infinity)
                    }

                    // Collections list
                    if !filteredCollections.isEmpty {
                        dairyCard(title: "Today's Collections") {
                            ForEach(filteredCollections, id: \.id) { entry in
                                collectionRow(entry)
                                if entry.id != filteredCollections.last?.id {
                                    Divider()
                                }
                            }
                        }
                    }

                    // Notifications
                    let pending = operationsModel.state.notifications.filter { $0.status == .queued || $0.status == .failed }
                    if !pending.isEmpty {
                        dairyCard(title: "Pending SMS Notifications (\(pending.count))") {
                            ForEach(pending, id: \.id) { notif in
                                VStack(alignment: .leading, spacing: 6) {
                                    Text("To: \(notif.farmerId)").font(.caption).foregroundStyle(.secondary)
                                    Text(notif.message).font(.subheadline)
                                    Text("Status: \(notif.status.name)").font(.caption).foregroundStyle(
                                        notif.status == .failed ? .red : .orange
                                    )
                                }
                                .padding(.vertical, 4)
                                Divider()
                            }
                        }
                    }
                }
                .padding()
            }
            .navigationTitle("Outer Center").navigationBarTitleDisplayMode(.inline)
        }
    }

    private func collectionRow(_ entry: MilkCollectionEntry) -> some View {
        HStack {
            VStack(alignment: .leading, spacing: 4) {
                Text(entry.farmerName).font(.headline).foregroundStyle(Color(red: 0.16, green: 0.29, blue: 0.15))
                Text("\(entry.rateBreakdown.quantityLiters, specifier: "%.1f") L  •  FAT \(entry.rateBreakdown.fat, specifier: "%.1f")")
                    .font(.caption).foregroundStyle(.secondary)
            }
            Spacer()
            VStack(alignment: .trailing, spacing: 4) {
                Text("₹\(entry.rateBreakdown.totalAmount, specifier: "%.0f")").font(.headline.bold())
                    .foregroundStyle(Color(red: 0.24, green: 0.81, blue: 0.57))
                paymentBadge(for: entry.paymentStatus)
            }
        }
        .padding(.vertical, 6)
    }

    private func paymentBadge(for status: CollectionPaymentStatus) -> some View {
        let (label, color): (String, Color) = switch status {
            case .pending:  ("Pending", .orange)
            case .approved: ("Approved", Color(red: 0.24, green: 0.81, blue: 0.57))
            case .paid:     ("Paid ✓",   .green)
        }
        return Text(label).font(.caption.bold()).foregroundStyle(color)
            .padding(.horizontal, 8).padding(.vertical, 4)
            .background(color.opacity(0.12))
            .clipShape(Capsule())
    }
}

// ─── Tab 4: Khata ─────────────────────────────────────────────────────────────
struct KhataTab: View {
    @ObservedObject var authModel: AuthScreenModel
    @ObservedObject var operationsModel: OperationsScreenModel
    @State private var entryTitle  = ""
    @State private var amountText  = ""
    @State private var isIncome    = true

    private var role: UserRole? { authModel.state.currentRole }
    private var income:  Double { operationsModel.state.ledgerEntries.filter { $0.kind == .income  }.reduce(0) { $0 + $1.amount } }
    private var expense: Double { operationsModel.state.ledgerEntries.filter { $0.kind == .expense }.reduce(0) { $0 + $1.amount } }

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(alignment: .leading, spacing: 16) {
                    // Summary cards (visible to all)
                    HStack(spacing: 12) {
                        financeSummaryCard("Income",  "₹\(Int(income))",    Color(red: 0.24, green: 0.81, blue: 0.57))
                        financeSummaryCard("Expense", "₹\(Int(expense))",   Color(red: 0.82, green: 0.30, blue: 0.34))
                        financeSummaryCard("Net",     "₹\(Int(income - expense))", Color(red: 0.12, green: 0.23, blue: 0.37))
                    }

                    // ── Add entry form: ADMIN only ────────────────────────────
                    if role == .admin {
                        dairyCard(title: "Add Manual Entry") {
                            Picker("Type", selection: $isIncome) {
                                Text("Income").tag(true)
                                Text("Expense").tag(false)
                            }
                            .pickerStyle(.segmented)

                            dairyField("Title (e.g. Milk payment)", text: $entryTitle)
                            dairyField("Amount (₹)",                text: $amountText).keyboardType(.decimalPad)

                            Button("Add to Khata") {}
                                .buttonStyle(.borderedProminent)
                                .tint(isIncome ? Color(red: 0.29, green: 0.53, blue: 0.26) : Color(red: 0.69, green: 0.17, blue: 0.22))
                                .frame(maxWidth: .infinity)
                        }
                    } else {
                        permissionBanner("Khata entries are auto-posted. Only ADMIN can add manual entries.", .blue)
                    }

                    // Ledger entries
                    if !operationsModel.state.ledgerEntries.isEmpty {
                        dairyCard(title: "Ledger") {
                            ForEach(operationsModel.state.ledgerEntries, id: \.id) { entry in
                                ledgerRow(entry)
                                Divider()
                            }
                        }
                    } else {
                        dairyCard(title: "Ledger") {
                            Text("No ledger entries yet.").foregroundStyle(.secondary)
                        }
                    }
                }
                .padding()
            }
            .navigationTitle("Khata").navigationBarTitleDisplayMode(.inline)
        }
    }

    private func financeSummaryCard(_ title: String, _ value: String, _ color: Color) -> some View {
        VStack(spacing: 4) {
            Text(title).font(.caption).foregroundStyle(.secondary)
            Text(value).font(.headline.bold()).foregroundStyle(color)
        }
        .frame(maxWidth: .infinity)
        .padding(12)
        .background(.white)
        .clipShape(RoundedRectangle(cornerRadius: 16, style: .continuous))
        .shadow(color: Color.black.opacity(0.06), radius: 6, x: 0, y: 3)
    }

    private func ledgerRow(_ entry: KhataLedgerEntry) -> some View {
        let isInc = entry.kind == .income
        return HStack {
            VStack(alignment: .leading, spacing: 4) {
                Text(entry.title).font(.subheadline.bold()).foregroundStyle(Color(red: 0.16, green: 0.29, blue: 0.15))
                Text(entry.sourceModule).font(.caption).foregroundStyle(.secondary)
            }
            Spacer()
            Text("\(isInc ? "+" : "-") ₹\(Int(entry.amount))")
                .font(.headline.bold())
                .foregroundStyle(isInc ? Color(red: 0.24, green: 0.81, blue: 0.57) : Color(red: 0.82, green: 0.30, blue: 0.34))
        }
        .padding(.vertical, 6)
    }
}

// ─── Tab 5: Inventory ─────────────────────────────────────────────────────────
struct InventoryTab: View {
    @ObservedObject var operationsModel: OperationsScreenModel
    @State private var selectedCategory = 0
    private let categories = ["All", "Feed", "Fodder", "Medicine", "Equipment"]

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(alignment: .leading, spacing: 16) {
                    Picker("Category", selection: $selectedCategory) {
                        ForEach(0..<categories.count, id: \.self) { i in
                            Text(categories[i]).tag(i)
                        }
                    }
                    .pickerStyle(.segmented)

                    let items = operationsModel.state.inventoryItems
                    if items.isEmpty {
                        dairyCard(title: "Inventory Godown") {
                            Text("No items yet.").foregroundStyle(.secondary)
                        }
                    } else {
                        dairyCard(title: "Stock Levels") {
                            ForEach(items, id: \.id) { item in
                                inventoryRow(item)
                                Divider()
                            }
                        }
                    }

                    // Feed batches
                    if !operationsModel.state.feedBatches.isEmpty {
                        dairyCard(title: "Feed Batches") {
                            ForEach(operationsModel.state.feedBatches, id: \.id) { batch in
                                VStack(alignment: .leading, spacing: 4) {
                                    HStack {
                                        Text(batch.batchName).font(.subheadline.bold())
                                        Spacer()
                                        Text("\(batch.totalOutputKg, specifier: "%.0f") kg").font(.subheadline.bold())
                                            .foregroundStyle(Color(red: 0.24, green: 0.81, blue: 0.57))
                                    }
                                    Text("\(batch.ingredients.count) ingredients  •  \(batch.autoStockDeducted ? "Auto deducted ✓" : "Pending deduction")")
                                        .font(.caption).foregroundStyle(.secondary)
                                }
                                .padding(.vertical, 6)
                                Divider()
                            }
                        }
                    }

                    // Biogas summary
                    dairyCard(title: "Biogas & Waste") {
                        HStack(spacing: 16) {
                            biogasStat("2.8 m³",  "Biogas",  Color(red: 0.18, green: 0.49, blue: 0.41))
                            biogasStat("420 kg",  "Dung",    Color(red: 0.42, green: 0.36, blue: 0.24))
                            biogasStat("140 kg",  "Compost", Color(red: 0.49, green: 0.35, blue: 0.18))
                        }
                        Button("+ Log Waste / Biogas") {}
                            .buttonStyle(.bordered)
                            .tint(Color(red: 0.18, green: 0.49, blue: 0.41))
                            .frame(maxWidth: .infinity)
                    }
                }
                .padding()
            }
            .navigationTitle("Inventory").navigationBarTitleDisplayMode(.inline)
        }
    }

    private func inventoryRow(_ item: InventoryItem) -> some View {
        let isLow = item.currentStock <= item.reorderLevel
        return HStack {
            VStack(alignment: .leading, spacing: 4) {
                Text(item.name).font(.subheadline.bold()).foregroundStyle(Color(red: 0.16, green: 0.29, blue: 0.15))
                Text("\(item.category.name.lowercased()) • reorder \(item.reorderLevel, specifier: "%.0f") \(item.unit)")
                    .font(.caption).foregroundStyle(.secondary)
            }
            Spacer()
            VStack(alignment: .trailing, spacing: 4) {
                Text("\(item.currentStock, specifier: "%.0f") \(item.unit)").font(.headline.bold())
                    .foregroundStyle(isLow ? .red : Color(red: 0.16, green: 0.29, blue: 0.15))
                if isLow { Text("LOW STOCK").font(.caption2.bold()).foregroundStyle(.red) }
            }
        }
        .padding(.vertical, 6)
    }

    private func biogasStat(_ value: String, _ label: String, _ color: Color) -> some View {
        VStack(spacing: 4) {
            Text(value).font(.headline.bold()).foregroundStyle(color)
            Text(label).font(.caption).foregroundStyle(.secondary)
        }
        .frame(maxWidth: .infinity)
        .padding(10)
        .background(color.opacity(0.10))
        .clipShape(RoundedRectangle(cornerRadius: 12, style: .continuous))
    }
}

// ─── Tab 6: Farm Config ───────────────────────────────────────────────────────
struct FarmConfigTab: View {
    @ObservedObject var authModel: AuthScreenModel
    @State private var smsProvider = "MSG91"
    @State private var apiKey      = ""
    @State private var senderId    = ""
    @State private var smsActive   = true

    private let providers = ["MSG91", "FAST2SMS", "TWILIO", "CUSTOM_HTTP"]
    private var isAdmin: Bool { authModel.state.currentRole == .admin }

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(alignment: .leading, spacing: 16) {
                    // Farm info (read-only for all)
                    if let farm = authModel.state.farmProfile {
                        dairyCard(title: "Farm Profile") {
                            recordRow(label: "Farm Name", value: farm.farmName)
                            recordRow(label: "Phone",     value: farm.primaryPhoneNumber)
                        }
                    }

                    // Land partitions — ADMIN may edit
                    dairyCard(title: "Zameen / Land Setup") {
                        landRow("Green Fodder Block", "4.5 acres", "Napier Grass", Color(red: 0.24, green: 0.81, blue: 0.57))
                        landRow("Dry Storage",        "1.8 acres", "Hay & Feed",   Color(red: 0.91, green: 0.78, blue: 0.42))
                        landRow("Livestock Zone",     "2.2 acres", "Shed & Yard",  Color(red: 0.08, green: 0.31, blue: 0.37))
                        if isAdmin {
                            Button("+ Add Land Partition") {}
                                .font(.subheadline.bold())
                                .foregroundStyle(Color(red: 0.24, green: 0.81, blue: 0.57))
                        }
                    }

                    // SMS Gateway — ADMIN only
                    if isAdmin {
                        dairyCard(title: "SMS Gateway Config") {
                            Toggle("SMS Active", isOn: $smsActive)
                                .tint(Color(red: 0.24, green: 0.81, blue: 0.57))

                            Picker("Provider", selection: $smsProvider) {
                                ForEach(providers, id: \.self) { p in
                                    Text(p).tag(p)
                                }
                            }
                            .pickerStyle(.menu)

                            dairyField("API Key", text: $apiKey)
                            dairyField("Sender ID", text: $senderId)

                            Button("Save Gateway Config") {}
                                .buttonStyle(.borderedProminent)
                                .tint(Color(red: 0.29, green: 0.53, blue: 0.26))
                                .frame(maxWidth: .infinity)
                        }
                    } else {
                        permissionBanner("SMS Gateway and land edits require ADMIN access.", .orange)
                    }
                }
                .padding()
            }
            .navigationTitle("Farm Config").navigationBarTitleDisplayMode(.inline)
        }
    }

    private func recordRow(label: String, value: String) -> some View {
        HStack {
            Text(label).foregroundStyle(.secondary)
            Spacer()
            Text(value).bold().foregroundStyle(Color(red: 0.16, green: 0.29, blue: 0.15))
        }
        .padding(.vertical, 4)
    }

    private func landRow(_ name: String, _ area: String, _ use: String, _ color: Color) -> some View {
        HStack {
            Circle().fill(color).frame(width: 12, height: 12)
            VStack(alignment: .leading, spacing: 2) {
                Text(name).font(.subheadline.bold())
                Text(use).font(.caption).foregroundStyle(.secondary)
            }
            Spacer()
            Text(area).font(.subheadline.bold()).foregroundStyle(color)
        }
        .padding(.vertical, 6)
    }
}

// ─── Tab 7: Staff ─────────────────────────────────────────────────────────────
struct StaffTab: View {
    @ObservedObject var authModel: AuthScreenModel
    @Binding var staffName:   String
    @Binding var staffEmail:  String
    @Binding var staffPhone:  String
    @Binding var selectedRole: UserRole

    private var actorRole: UserRole? { authModel.state.currentRole }

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(alignment: .leading, spacing: 16) {
                    // Staff list (placeholder)
                    dairyCard(title: "Active Staff") {
                        staffRow("Raju Dairy Man",  "Dairy Man",  "+91 98765 43210", Color(red: 0.24, green: 0.81, blue: 0.57))
                        Divider()
                        staffRow("Shyam Labour",    "Labour",     "+91 91234 12345", Color(red: 0.91, green: 0.78, blue: 0.42))
                    }

                    // Add staff form
                    if actorRole == .admin || actorRole == .dairyMan {
                        dairyCard(title: "Add Staff / Farmer") {
                            dairyField("Full Name",   text: $staffName)
                            dairyField("Email",       text: $staffEmail)
                                .keyboardType(.emailAddress)
                                .textInputAutocapitalization(.never)
                            dairyField("Phone",       text: $staffPhone)
                                .keyboardType(.phonePad)

                            Picker("Role", selection: $selectedRole) {
                                ForEach(allowedRoles(for: actorRole), id: \.self) { role in
                                    Text(role.name).tag(role)
                                }
                            }
                            .pickerStyle(.segmented)

                            Button("Submit Provisioning Request") {
                                authModel.submitManagedUser(
                                    fullName:    staffName,
                                    email:       staffEmail,
                                    phoneNumber: staffPhone,
                                    role:        selectedRole
                                )
                            }
                            .buttonStyle(.borderedProminent)
                            .tint(Color(red: 0.29, green: 0.53, blue: 0.26))
                        }
                    }

                    // Live permission matrix for the logged-in role
                    dairyCard(title: "My Permissions — \(actorRole?.name.uppercased() ?? "UNKNOWN")") {
                        ForEach(permissionMatrix(for: actorRole), id: \.0) { item in
                            permissionRow(item.0, item.1, item.2)
                        }
                    }
                }
                .padding()
            }
            .navigationTitle("Staff Control").navigationBarTitleDisplayMode(.inline)
        }
    }

    private func staffRow(_ name: String, _ role: String, _ phone: String, _ color: Color) -> some View {
        HStack {
            Circle().fill(color.opacity(0.2)).frame(width: 42, height: 42).overlay(
                Text(name.prefix(2).uppercased()).font(.caption.bold()).foregroundStyle(color)
            )
            VStack(alignment: .leading, spacing: 2) {
                Text(name).font(.subheadline.bold()).foregroundStyle(Color(red: 0.16, green: 0.29, blue: 0.15))
                Text("\(role)  •  \(phone)").font(.caption).foregroundStyle(.secondary)
            }
            Spacer()
            Image(systemName: "chevron.right").foregroundStyle(.secondary)
        }
        .padding(.vertical, 4)
    }

    private func permissionRow(_ module: String, _ level: String, _ color: Color) -> some View {
        HStack {
            Text(module).font(.subheadline).foregroundStyle(Color(red: 0.16, green: 0.29, blue: 0.15))
            Spacer()
            Text(level).font(.caption.bold()).foregroundStyle(color)
                .padding(.horizontal, 10).padding(.vertical, 5)
                .background(color.opacity(0.12))
                .clipShape(Capsule())
        }
        .padding(.vertical, 4)
    }

    private func allowedRoles(for actorRole: UserRole?) -> [UserRole] {
        actorRole == .admin ? [.dairyMan, .labour, .farmer] : [.farmer]
    }

    /// Returns (module name, level label, tint color) for the permission matrix.
    private func permissionMatrix(for role: UserRole?) -> [(String, String, Color)] {
        let green  = Color(red: 0.24, green: 0.81, blue: 0.57)
        let amber  = Color(red: 0.91, green: 0.78, blue: 0.42)
        let blue   = Color(red: 0.12, green: 0.23, blue: 0.37)
        let red    = Color(red: 0.82, green: 0.30, blue: 0.34)
        let muted  = Color(red: 0.60, green: 0.60, blue: 0.60)
        let purple = Color(red: 0.49, green: 0.10, blue: 0.60)

        switch role {
        case .admin:
            return [
                ("Dashboard",    "APPROVE", purple),
                ("Livestock",    "APPROVE", purple),
                ("Outer Center", "APPROVE", purple),
                ("Khata",        "APPROVE", purple),
                ("Inventory",    "APPROVE", purple),
                ("Farm Config",  "APPROVE", purple),
                ("Staff",        "APPROVE", purple),
                ("Reports",      "APPROVE", purple),
            ]
        case .dairyMan:
            return [
                ("Dashboard",    "VIEW",   green),
                ("Livestock",    "MANAGE", blue),
                ("Outer Center", "MANAGE", blue),
                ("Khata",        "VIEW",   green),
                ("Inventory",    "VIEW",   green),
                ("Farm Config",  "NONE",   muted),
                ("Staff",        "NONE",   muted),
                ("Reports",      "VIEW",   green),
            ]
        case .labour:
            return [
                ("Dashboard",    "VIEW",   green),
                ("Livestock",    "VIEW",   green),
                ("Outer Center", "VIEW",   green),
                ("Khata",        "NONE",   muted),
                ("Inventory",    "MANAGE", blue),
                ("Farm Config",  "NONE",   muted),
                ("Staff",        "NONE",   muted),
                ("Reports",      "NONE",   muted),
            ]
        case .farmer:
            return [
                ("Dashboard",    "VIEW",   green),
                ("Livestock",    "NONE",   muted),
                ("Outer Center", "VIEW",   green),
                ("Khata",        "VIEW",   green),
                ("Inventory",    "NONE",   muted),
                ("Farm Config",  "NONE",   muted),
                ("Staff",        "NONE",   muted),
                ("Reports",      "VIEW",   green),
            ]
        default:
            return [("(No role assigned)", "NONE", muted)]
        }
    }
}

// ─── Shared helper views ──────────────────────────────────────────────────────

@ViewBuilder
func screenTitle(title: String, subtitle: String) -> some View {
    VStack(alignment: .leading, spacing: 10) {
        Text(title)
            .font(.system(size: 38, weight: .heavy, design: .rounded))
            .foregroundStyle(.white)
        Text(subtitle)
            .font(.subheadline)
            .foregroundStyle(Color.white.opacity(0.85))
    }
    .frame(maxWidth: .infinity, alignment: .leading)
    .frame(height: 200)
}

@ViewBuilder
func dairyCard<Content: View>(title: String, @ViewBuilder content: () -> Content) -> some View {
    VStack(alignment: .leading, spacing: 14) {
        Text(title)
            .font(.title3.weight(.bold))
            .foregroundStyle(Color(red: 0.16, green: 0.29, blue: 0.15))
        content()
    }
    .frame(maxWidth: .infinity, alignment: .leading)
    .padding(18)
    .background(.white)
    .clipShape(RoundedRectangle(cornerRadius: 30, style: .continuous))
    .overlay(RoundedRectangle(cornerRadius: 30, style: .continuous).stroke(.white, lineWidth: 6))
    .shadow(color: Color.black.opacity(0.08), radius: 14, x: 0, y: 8)
}

func dairyField(_ title: String, text: Binding<String>) -> some View {
    TextField(title, text: text)
        .padding(.horizontal, 14)
        .frame(height: 52)
        .background(Color.white)
        .overlay(RoundedRectangle(cornerRadius: 16, style: .continuous).stroke(Color(red: 0.84, green: 0.90, blue: 0.82), lineWidth: 1))
        .clipShape(RoundedRectangle(cornerRadius: 16, style: .continuous))
}

func dairySecureField(_ title: String, text: Binding<String>) -> some View {
    SecureField(title, text: text)
        .padding(.horizontal, 14)
        .frame(height: 52)
        .background(Color.white)
        .overlay(RoundedRectangle(cornerRadius: 16, style: .continuous).stroke(Color(red: 0.84, green: 0.90, blue: 0.82), lineWidth: 1))
        .clipShape(RoundedRectangle(cornerRadius: 16, style: .continuous))
}

var wholeScreenBackground: some View {
    ZStack {
        LinearGradient(
            colors: [
                Color(red: 11/255, green: 74/255, blue: 58/255),
                Color(red: 232/255, green: 245/255, blue: 233/255)
            ],
            startPoint: .leading,
            endPoint:   .trailing
        )
        Ellipse()
            .fill(Color(red: 47/255, green: 107/255, blue: 89/255).opacity(0.72))
            .frame(width: 900, height: 220)
            .offset(x: 180, y: 170)
        Ellipse()
            .fill(Color(red: 116/255, green: 168/255, blue: 146/255).opacity(0.34))
            .frame(width: 520, height: 150)
            .offset(x: -90, y: 205)
    }
}

/// Inline banner shown when a feature is restricted for the current role.
@ViewBuilder
func permissionBanner(_ message: String, _ variant: PermissionBannerVariant) -> some View {
    let (bg, fg, icon): (Color, Color, String) = {
        switch variant {
        case .yellow:  return (Color(red: 0.98, green: 0.96, blue: 0.87), Color(red: 0.65, green: 0.50, blue: 0.00), "lock.fill")
        case .blue:    return (Color(red: 0.87, green: 0.93, blue: 0.98), Color(red: 0.10, green: 0.30, blue: 0.60), "eye.fill")
        case .orange:  return (Color(red: 0.99, green: 0.94, blue: 0.86), Color(red: 0.65, green: 0.35, blue: 0.00), "exclamationmark.triangle.fill")
        }
    }()
    HStack(spacing: 10) {
        Image(systemName: icon).font(.subheadline).foregroundStyle(fg)
        Text(message).font(.caption).foregroundStyle(fg)
    }
    .padding(12)
    .frame(maxWidth: .infinity, alignment: .leading)
    .background(bg)
    .clipShape(RoundedRectangle(cornerRadius: 12, style: .continuous))
    .overlay(RoundedRectangle(cornerRadius: 12).stroke(fg.opacity(0.2), lineWidth: 1))
}

enum PermissionBannerVariant { case yellow, blue, orange }

func color(from hex: String) -> Color {
    let clean = hex.replacingOccurrences(of: "#", with: "")
    guard let value = UInt(clean, radix: 16) else {
        return Color(red: 0.49, green: 0.71, blue: 0.36)
    }
    return Color(
        red:   Double((value >> 16) & 0xFF) / 255.0,
        green: Double((value >>  8) & 0xFF) / 255.0,
        blue:  Double( value        & 0xFF) / 255.0
    )
}

// ─── Preview ──────────────────────────────────────────────────────────────────
struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
