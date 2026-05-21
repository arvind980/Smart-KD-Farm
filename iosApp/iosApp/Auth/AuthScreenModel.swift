import Foundation
import SharedLogic

@MainActor
final class AuthScreenModel: ObservableObject {
    @Published private(set) var state: AuthUiState

    private let viewModel: AuthViewModel
    private var handle: CloseableHandle?

    init(container: SharedContainer = SharedContainer()) {
        container.start()
        let viewModel = container.authViewModel()
        self.viewModel = viewModel
        self.state = viewModel.currentState()
        self.handle = viewModel.startObserving { [weak self] latestState in
            guard let latestState else { return }
            Task { @MainActor in
                self?.state = latestState
            }
        }
    }

    deinit {
        handle?.close()
        viewModel.dispose()
    }

    func login(mobileNumber: String, password: String) {
        let email = mobileNumber.contains("@") ? mobileNumber : "\(mobileNumber.filter { $0.isNumber || $0 == "+" })@mail.com"
        viewModel.login(mobileNumber: email, password: password)
    }

    func registerFarm(
        farmName: String,
        adminName: String,
        adminPassword: String,
        phoneNumber: String,
        village: String,
        district: String,
        stateName: String
    ) {
        let cleanPhone = phoneNumber.filter { $0.isNumber || $0 == "+" }
        let formattedEmail = "\(cleanPhone)@mail.com"

        let command = FarmRegistrationCommand(
            farmName: farmName,
            adminName: adminName,
            adminEmail: formattedEmail,
            adminPassword: adminPassword,
            adminPhoneNumber: phoneNumber,
            location: FarmLocation(
                village: village,
                district: district,
                state: stateName,
                country: "India",
                latitude: nil,
                longitude: nil
            ),
            landArea: AreaConfiguration(
                value: 12.0,
                unit: .bigha,
                localBighaSizeInSquareMeters: 2500.0
            ),
            notes: nil
        )
        viewModel.registerFarm(command: command)
    }

    func submitManagedUser(fullName: String, email: String, phoneNumber: String, role: UserRole) {
        let registration = ManagedUserRegistration(
            fullName: fullName,
            email: email,
            phoneNumber: phoneNumber,
            role: role
        )
        viewModel.addManagedUser(registration: registration)
    }

    func logout() {
        viewModel.logout()
    }
}

@MainActor
final class LivestockScreenModel: ObservableObject {
    @Published private(set) var state: LivestockUiState

    private let viewModel: LivestockViewModel
    private var handle: CloseableHandle?

    init(container: SharedContainer = SharedContainer()) {
        container.start()
        let viewModel = container.livestockViewModel()
        self.viewModel = viewModel
        self.state = viewModel.currentState()
        self.handle = viewModel.startObserving { [weak self] latestState in
            guard let latestState else { return }
            Task { @MainActor in
                self?.state = latestState
            }
        }
    }

    deinit {
        handle?.close()
        viewModel.dispose()
    }
}
