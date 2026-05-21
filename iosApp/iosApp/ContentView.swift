import SwiftUI
import SharedLogic

struct ContentView: View {
    @StateObject private var model = AuthScreenModel()
    @State private var mobileNumber = ""
    @State private var password = ""
    @State private var farmName = "Smart KD Farm"
    @State private var ownerName = "Arvind"
    @State private var managerName = "Farm Manager"
    @State private var managerEmail = ""
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
                            Text("Use the manager mobile number that was registered with the farm.")
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

                        if role == .manager || role == .dairyMan {
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
                }
            }
            .padding()
        }
        .background(
            wholeScreenBackground
        )
        .overlay {
            if model.state.isLoading {
                ProgressView()
            }
        }
    }

    private var farmRegistrationView: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 20) {
                screenTitle(
                    title: "Create\nFarm",
                    subtitle: "Register the farm, location, and primary manager in one dedicated setup screen."
                )

                dairyCard(title: "Farm Setup") {
                    dairyField("Farm Name", text: $farmName)
                    dairyField("Owner Name", text: $ownerName)
                    Text("Farm Location")
                        .font(.headline)
                        .foregroundStyle(Color(red: 0.16, green: 0.29, blue: 0.15))
                    dairyField("Village", text: $village)
                    dairyField("District", text: $district)
                    dairyField("State", text: $stateName)
                    Text("Primary Manager")
                        .font(.headline)
                        .foregroundStyle(Color(red: 0.16, green: 0.29, blue: 0.15))
                    dairyField("Manager Name", text: $managerName)
                    dairyField("Manager Mobile Number", text: $phoneNumber)
                        .keyboardType(.phonePad)
                    dairyField("Manager Email", text: $managerEmail)
                    dairySecureField("Manager Password", text: $password)
                    Text("Manager email is required only for secure account creation. Normal login uses mobile number + password.")
                        .font(.footnote)
                        .foregroundStyle(.secondary)

                    Button("Register Farm") {
                        model.registerFarm(
                            farmName: farmName,
                            ownerName: ownerName,
                            managerName: managerName,
                            managerEmail: managerEmail,
                            managerPassword: password,
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
        case .manager:
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
        if actorRole == .manager {
            return [.dairyMan, .labour, .farmer]
        }
        return [.farmer]
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
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
