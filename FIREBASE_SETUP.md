# Firebase Setup

This project is wired for Firebase on both mobile targets, but you still need to place the real Firebase app config files downloaded from the Firebase console.

## Android

1. Register the Android app with package name `com.smartkdfarm.app`.
2. Download `google-services.json`.
3. Place it at `/Users/arvind/Desktop/Smart-KD-Farm/androidApp/google-services.json`.
4. The Android app already applies the `com.google.gms.google-services` plugin.

## iOS

1. Register the iOS app with bundle id `com.smartkdfarm.app.ios`.
2. Download `GoogleService-Info.plist`.
3. Place it at `/Users/arvind/Desktop/Smart-KD-Farm/iosApp/iosApp/GoogleService-Info.plist`.
4. Firebase startup is handled in `/Users/arvind/Desktop/Smart-KD-Farm/iosApp/iosApp/FirebaseAppDelegate.swift`.
5. Add `FirebaseCore`, `FirebaseAuth`, and `FirebaseFirestore` to the `iosApp` Xcode target.

## Notes

- The `.template` files are placeholders only.
- The real Firebase config files are project-specific and are ignored by git.
- Firebase Auth and Firestore must also be enabled in the Firebase console.
