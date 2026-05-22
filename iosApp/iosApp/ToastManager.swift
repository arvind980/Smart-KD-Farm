import SwiftUI

// ─────────────────────────────────────────────────────────────────────────────
// MARK: – Toast model
// ─────────────────────────────────────────────────────────────────────────────

enum ToastType {
    case error
    case success
    case warning
    case info

    var icon: String {
        switch self {
        case .error:   return "xmark.circle.fill"
        case .success: return "checkmark.circle.fill"
        case .warning: return "exclamationmark.triangle.fill"
        case .info:    return "info.circle.fill"
        }
    }

    var tint: Color {
        switch self {
        case .error:   return Color(red: 0.90, green: 0.25, blue: 0.28)
        case .success: return Color(red: 0.18, green: 0.72, blue: 0.44)
        case .warning: return Color(red: 0.93, green: 0.62, blue: 0.14)
        case .info:    return Color(red: 0.22, green: 0.55, blue: 0.92)
        }
    }

    var background: Color {
        switch self {
        case .error:   return Color(red: 0.20, green: 0.04, blue: 0.04)
        case .success: return Color(red: 0.04, green: 0.17, blue: 0.10)
        case .warning: return Color(red: 0.20, green: 0.14, blue: 0.02)
        case .info:    return Color(red: 0.04, green: 0.10, blue: 0.22)
        }
    }
}

struct ToastMessage: Identifiable, Equatable {
    let id      = UUID()
    let text    : String
    let type    : ToastType

    static func == (lhs: ToastMessage, rhs: ToastMessage) -> Bool {
        lhs.id == rhs.id
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// MARK: – Manager
// ─────────────────────────────────────────────────────────────────────────────

@MainActor
final class ToastManager: ObservableObject {

    static let shared = ToastManager()
    private init() {}

    @Published var current: ToastMessage? = nil

    private var dismissTask: Task<Void, Never>? = nil

    /// Show a toast and auto-dismiss after `duration` seconds.
    func show(
        _ text: String,
        type: ToastType = .error,
        duration: TimeInterval = 3.5
    ) {
        guard !text.trimmingCharacters(in: .whitespaces).isEmpty else { return }
        dismissTask?.cancel()
        current = ToastMessage(text: text, type: type)
        dismissTask = Task {
            try? await Task.sleep(nanoseconds: UInt64(duration * 1_000_000_000))
            guard !Task.isCancelled else { return }
            withAnimation(.easeOut(duration: 0.25)) { current = nil }
        }
    }

    func dismiss() {
        dismissTask?.cancel()
        withAnimation(.easeOut(duration: 0.25)) { current = nil }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// MARK: – Toast banner view
// ─────────────────────────────────────────────────────────────────────────────

struct ToastBannerView: View {
    let toast  : ToastMessage
    let onDismiss: () -> Void

    var body: some View {
        HStack(spacing: 12) {
            Image(systemName: toast.type.icon)
                .font(.system(size: 20, weight: .semibold))
                .foregroundStyle(toast.type.tint)

            Text(toast.text)
                .font(.subheadline.weight(.medium))
                .foregroundStyle(.white)
                .multilineTextAlignment(.leading)
                .lineLimit(3)
                .frame(maxWidth: .infinity, alignment: .leading)

            Button(action: onDismiss) {
                Image(systemName: "xmark")
                    .font(.caption.bold())
                    .foregroundStyle(.white.opacity(0.6))
                    .padding(6)
                    .background(Color.white.opacity(0.12))
                    .clipShape(Circle())
            }
            .buttonStyle(.plain)
        }
        .padding(.horizontal, 16)
        .padding(.vertical, 14)
        .background(
            RoundedRectangle(cornerRadius: 18, style: .continuous)
                .fill(toast.type.background)
                .overlay(
                    RoundedRectangle(cornerRadius: 18, style: .continuous)
                        .stroke(toast.type.tint.opacity(0.35), lineWidth: 1)
                )
        )
        .shadow(color: Color.black.opacity(0.45), radius: 18, x: 0, y: 8)
        .padding(.horizontal, 16)
        .padding(.top, 8)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// MARK: – ViewModifier for easy attachment
// ─────────────────────────────────────────────────────────────────────────────

struct ToastOverlayModifier: ViewModifier {
    @ObservedObject private var manager = ToastManager.shared

    func body(content: Content) -> some View {
        content
            .overlay(alignment: .top) {
                if let toast = manager.current {
                    ToastBannerView(toast: toast) {
                        manager.dismiss()
                    }
                    .transition(
                        .asymmetric(
                            insertion : .move(edge: .top).combined(with: .opacity),
                            removal   : .move(edge: .top).combined(with: .opacity)
                        )
                    )
                    .zIndex(999)
                    .padding(.top, 52)   // clears the safe area / status bar
                }
            }
            .animation(.spring(response: 0.45, dampingFraction: 0.72), value: manager.current)
    }
}

extension View {
    /// Attach the floating toast overlay to any root view.
    func withToast() -> some View {
        modifier(ToastOverlayModifier())
    }
}
