import SwiftUI

/// Shown while the app checks for a persisted Firebase session.
/// Fades out automatically once AuthFlowStatus leaves IDLE / LOADING.
struct SplashView: View {

    @State private var logoScale:   CGFloat = 0.7
    @State private var logoOpacity: Double  = 0.0
    @State private var dotOffset:   CGFloat = 0.0

    var body: some View {
        ZStack {
            // ── Background — same gradient as the rest of the app ─────────────
            LinearGradient(
                colors: [
                    Color(red: 11/255,  green: 74/255,  blue: 58/255),
                    Color(red: 8/255,   green: 43/255,  blue: 43/255),
                ],
                startPoint: .topLeading,
                endPoint:   .bottomTrailing
            )
            .ignoresSafeArea()

            // Decorative ellipses (mirroring wholeScreenBackground)
            Ellipse()
                .fill(Color(red: 47/255, green: 107/255, blue: 89/255).opacity(0.45))
                .frame(width: 600, height: 180)
                .offset(x: 120, y: 200)
                .blur(radius: 30)

            Ellipse()
                .fill(Color(red: 116/255, green: 168/255, blue: 146/255).opacity(0.22))
                .frame(width: 380, height: 120)
                .offset(x: -80, y: -180)
                .blur(radius: 24)

            // ── Centre content ────────────────────────────────────────────────
            VStack(spacing: 28) {

                // Logo icon
                ZStack {
                    Circle()
                        .fill(Color(red: 0.07, green: 0.24, blue: 0.18))
                        .frame(width: 110, height: 110)
                        .shadow(color: Color(red: 0.18, green: 0.81, blue: 0.44).opacity(0.35),
                                radius: 28, x: 0, y: 10)

                    Text("🐄")
                        .font(.system(size: 52))
                }
                .scaleEffect(logoScale)
                .opacity(logoOpacity)

                // App name
                VStack(spacing: 6) {
                    Text("Smart KD Farm")
                        .font(.system(size: 30, weight: .heavy, design: .rounded))
                        .foregroundStyle(Color(red: 0.72, green: 0.96, blue: 0.80))
                        .opacity(logoOpacity)

                    Text("Digital Dairy ERP")
                        .font(.system(size: 14, weight: .medium))
                        .foregroundStyle(Color.white.opacity(0.55))
                        .opacity(logoOpacity)
                }

                // Animated dots loader
                HStack(spacing: 8) {
                    ForEach(0..<3, id: \.self) { i in
                        Circle()
                            .fill(Color(red: 0.24, green: 0.81, blue: 0.57))
                            .frame(width: 8, height: 8)
                            .offset(y: i == 1 ? dotOffset : 0)
                            .animation(
                                .easeInOut(duration: 0.55)
                                    .repeatForever(autoreverses: true)
                                    .delay(Double(i) * 0.18),
                                value: dotOffset
                            )
                    }
                }
                .opacity(logoOpacity)
            }
        }
        .onAppear {
            withAnimation(.spring(response: 0.65, dampingFraction: 0.62)) {
                logoScale   = 1.0
                logoOpacity = 1.0
            }
            dotOffset = -6
        }
    }
}

#Preview {
    SplashView()
}
