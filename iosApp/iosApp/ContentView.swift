import SwiftUI
import SharedLogic

struct ContentView: View {
    @StateObject private var model = AuthScreenModel()
    @StateObject private var livestockModel = LivestockScreenModel()
    @State private var mobileNumber = ""
    @State private var password = ""
    @State private var farmName = "Smart KD Farm"
    @State private var adminName = ""
    @State private var phoneNumber = ""
    @State private var village = ""
    @State private var district = ""
    @State private var stateName = "Punjab"
    @State private var staffName = ""
    @State private var staffEmail = ""
    @State private var staffPhone = ""
    @State private var selectedRole: UserRole = .labour

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(alignment: .leading, spacing: 20) {
                    if model.state.currentUser == nil {
                        screenTitle(
                            title: "Digital\nDairy ERP",
                            subtitle: "Green, clean, mobile-first operations for your dairy farm."
                        )

                        dairyCard(title: "Welcome Back") {
                            Text("Login stays mobile-first for daily use. Farm setup opens in a separate screen so the home page stays crisp and easy to use.")
                                .foregroundStyle(Color(red: 0.36, green: 0.42, blue: 0.36))
                            NavigationLink {
                                    farmRegistrationView
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
                            Button("Login") {
                                    model.login(mobileNumber: mobileNumber, password: password)
                                }
                                .buttonStyle(.borderedProminent)
                                .tint(Color(red: 0.29, green: 0.53, blue: 0.26))
                                .frame(maxWidth: .infinity, alignment: .center)
                            Text("Use the admin mobile number that was registered with the farm.")
                                .font(.footnote)
                                .foregroundStyle(.secondary)
                        }
                    } else {
                        let role = model.state.currentRole
                        screenTitle(
                            title: "My\nDashboard",
                            subtitle: "\(model.state.currentUser?.fullName ?? "") • \(role?.name ?? "Unknown") • \(model.state.farmProfile?.farmName ?? "-")"
                        )

                        dairyCard(title: "Role Snapshot") {
                                Text(model.state.currentUser?.fullName ?? "")
                                    .font(.title3.bold())
                                Text("Role: \(role?.name ?? "Unknown")")
                                Text("Farm: \(model.state.farmProfile?.farmName ?? "-")")
                                Text(accessText(for: role))
                        }

                        dairyCard(title: "Pashu Profile") {
                            livestockSummaryRow
                            if livestockModel.state.animalCards.isEmpty {
                                Text("No animal profiles yet. Livestock cards will appear here once profiles are added under this farm.")
                                    .foregroundStyle(Color(red: 0.36, green: 0.42, blue: 0.36))
                            } else {
                                LazyVGrid(
                                    columns: [
                                        GridItem(.flexible(), spacing: 12),
                                        GridItem(.flexible(), spacing: 12)
                                    ],
                                    spacing: 12
                                ) {
                                    ForEach(livestockModel.state.animalCards, id: \.id) { card in
                                        livestockCard(card)
                                    }
                                }
                            }
                        }

                        if role == .admin || role == .dairyMan {
                            dairyCard(title: "Create Staff / Farmer") {
                                    dairyField("Full Name", text: $staffName)
                                    dairyField("Email", text: $staffEmail)
                                    dairyField("Phone", text: $staffPhone)
                                    Picker("Role", selection: $selectedRole) {
                                        ForEach(allowedRoles(for: role), id: \.self) { role in
                                            Text(role.name).tag(role)
                                        }
                                    }
                                    .pickerStyle(.segmented)
                                    Button("Submit Provisioning Request") {
                                        model.submitManagedUser(
                                            fullName: staffName,
                                            email: staffEmail,
                                            phoneNumber: staffPhone,
                                            role: selectedRole
                                        )
                                    }
                                    .buttonStyle(.borderedProminent)
                                    .tint(Color(red: 0.29, green: 0.53, blue: 0.26))
                                }
                        }

                        Button("Logout") {
                            model.logout()
                        }
                        .buttonStyle(.borderedProminent)
                        .tint(Color(red: 0.20, green: 0.38, blue: 0.20))
                    }

                    if let error = model.state.errorMessage, !error.isEmpty {
                        Text(error)
                            .foregroundStyle(.red)
                    }

                    if let error = livestockModel.state.errorMessage, !error.isEmpty {
                        Text(error)
                            .foregroundStyle(.red)
                    }
                }
            }
            .padding()
        }
        .background(
            wholeScreenBackground
        )
        .overlay {
            if model.state.isLoading || livestockModel.state.isLoading {
                ProgressView()
            }
        }
    }

    private var farmRegistrationView: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 20) {
                screenTitle(
                    title: "Create\nFarm",
                    subtitle: "Register the farm, location, and primary admin in one dedicated setup screen."
                )

                dairyCard(title: "Farm Setup") {
                    dairyField("Farm Name", text: $farmName)
                    Text("Farm Location")
                        .font(.headline)
                        .foregroundStyle(Color(red: 0.16, green: 0.29, blue: 0.15))
                    dairyField("Village", text: $village)
                    dairyField("District", text: $district)
                    dairyField("State", text: $stateName)
                    Text("Primary Admin")
                        .font(.headline)
                        .foregroundStyle(Color(red: 0.16, green: 0.29, blue: 0.15))
                    dairyField("Admin Name", text: $adminName)
                    dairyField("Admin Mobile Number", text: $phoneNumber)
                        .keyboardType(.phonePad)
                    dairySecureField("Admin Password", text: $password)
                    Text("Admin email is required only for secure account creation. Normal login uses mobile number + password.")
                        .font(.footnote)
                        .foregroundStyle(.secondary)

                    Button("Register Farm") {
                        model.registerFarm(
                            farmName: farmName,
                            adminName: adminName,
                            adminPassword: password,
                            phoneNumber: phoneNumber,
                            village: village,
                            district: district,
                            stateName: stateName
                        )
                    }
                    .buttonStyle(.borderedProminent)
                    .tint(Color(red: 0.29, green: 0.53, blue: 0.26))
                }
            }
            .padding()
        }
        .navigationTitle("Add Farm")
        .navigationBarTitleDisplayMode(.inline)
        .background(
            wholeScreenBackground
        )
    }

    private func accessText(for role: UserRole?) -> String {
        switch role {
        case .admin:
            return "Full CRUD, staff control, and financial analytics are enabled."
        case .dairyMan:
            return "Outer milk collection, farmer registration, and milking sheet are enabled."
        case .labour:
            return "Livestock and inventory inputs are available with restricted edits."
        case .farmer:
            return "Read-only personal milk ledger, dues, and payment summaries are enabled."
        default:
            return "Role-specific access will appear after login."
        }
    }

    private func allowedRoles(for actorRole: UserRole?) -> [UserRole] {
        if actorRole == .admin {
            return [.dairyMan, .labour, .farmer]
        }
        return [.farmer]
    }

    @ViewBuilder
    private var livestockSummaryRow: some View {
        ScrollView(.horizontal, showsIndicators: false) {
            HStack(spacing: 8) {
                ForEach(livestockModel.state.statusSummary, id: \.label) { summary in
                    Text("\(summary.label) \(summary.count)")
                        .font(.caption.bold())
                        .foregroundStyle(color(from: summary.colorHex))
                        .padding(.horizontal, 12)
                        .padding(.vertical, 8)
                        .background(color(from: summary.colorHex).opacity(0.14))
                        .overlay(
                            Capsule()
                                .stroke(color(from: summary.colorHex).opacity(0.3), lineWidth: 1)
                        )
                        .clipShape(Capsule())
                }
            }
        }
    }

    @ViewBuilder
    private func livestockCard(_ card: AnimalCardUiModel) -> some View {
        let accent = color(from: card.statusColorHex)
        VStack(alignment: .leading, spacing: 10) {
            HStack(alignment: .top) {
                VStack(alignment: .leading, spacing: 4) {
                    Text(card.tagNumber)
                        .font(.headline.bold())
                        .foregroundStyle(Color(red: 0.16, green: 0.29, blue: 0.15))
                    Text(card.breed)
                        .font(.subheadline)
                        .foregroundStyle(Color(red: 0.36, green: 0.42, blue: 0.36))
                }
                Spacer(minLength: 8)
                Text(card.statusLabel)
                    .font(.caption.bold())
                    .foregroundStyle(accent)
                    .padding(.horizontal, 10)
                    .padding(.vertical, 6)
                    .background(accent.opacity(0.14))
                    .clipShape(Capsule())
            }

            Text(card.ageLabel)
                .font(.subheadline.weight(.semibold))
                .foregroundStyle(Color(red: 0.16, green: 0.29, blue: 0.15))
            Text(card.purchasePriceLabel)
                .font(.subheadline.weight(.bold))
                .foregroundStyle(Color(red: 0.16, green: 0.29, blue: 0.15))
            Text(card.breedingTimelineLabel)
                .font(.caption)
                .foregroundStyle(Color(red: 0.36, green: 0.42, blue: 0.36))
            Text(card.healthHeadline)
                .font(.caption)
                .foregroundStyle(Color(red: 0.36, green: 0.42, blue: 0.36))
        }
        .frame(maxWidth: .infinity, minHeight: 170, alignment: .topLeading)
        .padding(16)
        .background(.white.opacity(0.96))
        .clipShape(RoundedRectangle(cornerRadius: 26, style: .continuous))
        .overlay(
            RoundedRectangle(cornerRadius: 26, style: .continuous)
                .stroke(accent.opacity(0.28), lineWidth: 1)
        )
        .shadow(color: Color.black.opacity(0.06), radius: 10, x: 0, y: 6)
    }

    @ViewBuilder
    private func screenTitle(title: String, subtitle: String) -> some View {
        VStack(alignment: .leading, spacing: 10) {
            Text(title)
                .font(.system(size: 38, weight: .heavy, design: .rounded))
                .foregroundStyle(.white)
            Text(subtitle)
                .font(.subheadline)
                .foregroundStyle(Color.white.opacity(0.85))
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .frame(height: 220)
    }

    @ViewBuilder
    private func dairyCard<Content: View>(title: String, @ViewBuilder content: () -> Content) -> some View {
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
        .overlay(
            RoundedRectangle(cornerRadius: 30, style: .continuous)
                .stroke(.white, lineWidth: 6)
        )
        .shadow(color: Color.black.opacity(0.08), radius: 14, x: 0, y: 8)
    }

    private func dairyField(_ title: String, text: Binding<String>) -> some View {
        TextField(title, text: text)
            .padding(.horizontal, 14)
            .frame(height: 52)
            .background(Color.white)
            .overlay(
                RoundedRectangle(cornerRadius: 16, style: .continuous)
                    .stroke(Color(red: 0.84, green: 0.90, blue: 0.82), lineWidth: 1)
            )
            .clipShape(RoundedRectangle(cornerRadius: 16, style: .continuous))
    }

    private func dairySecureField(_ title: String, text: Binding<String>) -> some View {
        SecureField(title, text: text)
            .padding(.horizontal, 14)
            .frame(height: 52)
            .background(Color.white)
            .overlay(
                RoundedRectangle(cornerRadius: 16, style: .continuous)
                    .stroke(Color(red: 0.84, green: 0.90, blue: 0.82), lineWidth: 1)
            )
            .clipShape(RoundedRectangle(cornerRadius: 16, style: .continuous))
    }

    private var wholeScreenBackground: some View {
        ZStack {
            LinearGradient(
                colors: [
                    Color(red: 11/255, green: 74/255, blue: 58/255),
                    Color(red: 232/255, green: 245/255, blue: 233/255)
                ],
                startPoint: .leading,
                endPoint: .trailing
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
        .ignoresSafeArea()
    }

    private func color(from hex: String) -> Color {
        let clean = hex.replacingOccurrences(of: "#", with: "")
        guard let value = UInt(clean, radix: 16) else {
            return Color(red: 0.49, green: 0.71, blue: 0.36)
        }
        let red = Double((value >> 16) & 0xFF) / 255.0
        let green = Double((value >> 8) & 0xFF) / 255.0
        let blue = Double(value & 0xFF) / 255.0
        return Color(red: red, green: green, blue: blue)
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
