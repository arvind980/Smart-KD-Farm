import SwiftUI
import SharedLogic

struct ContentView: View {
    @StateObject private var model = AuthScreenModel()
    @State private var mobileNumber = ""
    @State private var password = ""
    @State private var showFarmRegistration = false
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
                    Text("Digital Dairy ERP")
                        .font(.largeTitle.bold())

                    if model.state.currentUser == nil {
                        GroupBox("Start Here") {
                            VStack(alignment: .leading, spacing: 12) {
                                Text("Login stays mobile-first for daily use. Farm setup opens in a separate form so the home screen remains clean.")
                                    .frame(maxWidth: .infinity, alignment: .leading)
                                Button("Add Farm") {
                                    showFarmRegistration = true
                                }
                                .buttonStyle(.borderedProminent)
                            }
                        }

                        GroupBox("Login") {
                            VStack(spacing: 12) {
                                TextField("Mobile Number", text: $mobileNumber)
                                    .keyboardType(.phonePad)
                                SecureField("Password", text: $password)
                                Button("Login") {
                                    model.login(mobileNumber: mobileNumber, password: password)
                                }
                                .buttonStyle(.borderedProminent)
                            }
                        }

                        if showFarmRegistration {
                        GroupBox("Create Farm") {
                            VStack(spacing: 12) {
                                TextField("Farm Name", text: $farmName)
                                TextField("Owner Name", text: $ownerName)
                                Text("Farm Location")
                                    .frame(maxWidth: .infinity, alignment: .leading)
                                TextField("Manager Name", text: $managerName)
                                TextField("Village", text: $village)
                                TextField("District", text: $district)
                                TextField("State", text: $stateName)
                                Text("Primary Manager")
                                    .frame(maxWidth: .infinity, alignment: .leading)
                                TextField("Manager Mobile Number", text: $phoneNumber)
                                    .keyboardType(.phonePad)
                                TextField("Manager Email", text: $managerEmail)
                                    .textInputAutocapitalization(.never)
                                SecureField("Manager Password", text: $password)
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
                                Button("Close") {
                                    showFarmRegistration = false
                                }
                                .buttonStyle(.bordered)
                            }
                        }
                        }
                    } else {
                        let role = model.state.currentRole
                        GroupBox("Role Snapshot") {
                            VStack(alignment: .leading, spacing: 8) {
                                Text(model.state.currentUser?.fullName ?? "")
                                    .font(.title3.bold())
                                Text("Role: \(role?.name ?? "Unknown")")
                                Text("Farm: \(model.state.farmProfile?.farmName ?? "-")")
                                Text(accessText(for: role))
                            }
                            .frame(maxWidth: .infinity, alignment: .leading)
                        }

                        if role == .manager || role == .dairyMan {
                            GroupBox("Create Staff / Farmer") {
                                VStack(spacing: 12) {
                                    TextField("Full Name", text: $staffName)
                                    TextField("Email", text: $staffEmail)
                                        .textInputAutocapitalization(.never)
                                    TextField("Phone", text: $staffPhone)
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
                                }
                            }
                        }

                        Button("Logout") {
                            model.logout()
                        }
                        .buttonStyle(.bordered)
                    }

                    if let error = model.state.errorMessage, !error.isEmpty {
                        Text(error)
                            .foregroundStyle(.red)
                    }
                }
            }
            .padding()
        }
        .overlay {
            if model.state.isLoading {
                ProgressView()
            }
        }
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
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
