import SwiftUI
import SharedLogic

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(FirebaseAppDelegate.self) var firebaseDelegate

    init() {
        SharedContainer().start()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
