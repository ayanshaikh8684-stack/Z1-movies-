# Z1 Movies — Android Production Application

**Z1 Movies** ("Watch. Discover. Enjoy.") is a modern, high-performance Android movie and video streaming application built entirely with **Kotlin**, **Jetpack Compose**, and **Material Design 3**. Designed with a dark cinema aesthetic, it provides an end-to-end streaming client experience for licensed, owned, and public-domain cinematic content.

---

## Technical Specifications

| Specification | Value |
|---|---|
| **App Name** | Z1 Movies |
| **Package Name / Application ID** | `com.z1movies.app` |
| **Target SDK** | Android 16 (API Level 36) |
| **Minimum SDK** | Android 7.0 (API Level 24) |
| **Language** | Kotlin 2.2+ |
| **UI Framework** | Jetpack Compose with Material 3 (M3) |
| **Architecture** | MVVM (Model-View-ViewModel) with Kotlin Flow & StateFlow |
| **Persistence** | Android Room Database (SQLite) + Repository Pattern |
| **Image Loading** | Coil 2.7 (Async Image caching & transitions) |
| **Design System** | Cinema Dark Palette (`#0A0D14` canvas, `#00E5FF` electric blue accent) |

---

## App Screens & Features

1. **Splash Screen**: Animated cinematic Z1 logo, tagline, and instant launch transition.
2. **Home Screen**:
   - Dynamic Hero Banner with auto-advancing carousel, high-res backdrops, and quick actions ("Watch Now", "Add to My List").
   - Continue Watching row with video thumbnails, elapsed time progress bar, and 1-tap resume.
   - Trending Now carousel with `#1` to `#10` rank badges.
   - Popular, New Releases, and Curated Recommendations shelves.
3. **Movies Screen**:
   - Comprehensive grid view with instant search filter.
   - Interactive Filter Bottom Sheet (Genre, Audio Language, Year, Quality: 720p, 1080p FHD, 4K Ultra HD).
   - Dynamic Sorting Modal (Popularity, Newest, Highest Rated, Alphabetical A-Z).
4. **Categories Screen**:
   - Visual genre discovery tiles (Action, Sci-Fi, Drama, Islamic, Indian, Animation, Documentary, etc.).
   - Category-specific listing screen with custom headers.
5. **Full-Featured Video Player**:
   - Custom fullscreen Compose player with tap-to-reveal controls and auto-hide overlay.
   - Play/Pause, ±10-second seek skips, scrubbable progress bar, and current/total duration timer.
   - Playback speed switcher (0.5x, 0.75x, 1x, 1.25x, 1.5x, 2x).
   - Video resolution selector (Auto, 360p, 480p, 720p, 1080p FHD, 4K Ultra HD).
   - Multi-language Subtitles and Audio Tracks selector (Dolby Atmos, 5.1, Stereo).
   - Touch lock toggle to prevent accidental touches while watching.
6. **Downloads Screen**:
   - Offline management for licensed movies with storage status meter (`2.4 GB / 10 GB used`).
   - Download controls: Pause, Resume, Delete, and instant offline play.
   - Settings for Wi-Fi only downloads, automatic next episode, and default quality.
7. **Search & Discovery**:
   - Real-time search by movie title, director, actors, or genres.
   - Voice search dialog simulation.
   - Recent query history with one-tap clear and trending search tags.
8. **My List**:
   - Personal library with tabs for **Watchlist**, **Favorites**, and **Watch History**.
9. **Profile & Settings**:
   - User account info, streaming preferences, data saver mode, cache management, and terms/privacy.
10. **Admin Content Manager**:
    - Manage catalog titles: add new licensed titles (metadata, URLs, cast, genres) and delete entries.
11. **Authentication**:
    - Sign In, Sign Up, Forgot Password, mock 4-digit OTP verification, and Google Sign-in UI.

---

## Building in Android Studio

### Prerequisites
- **Android Studio**: Android Studio Ladybug (2024.2.1+) or newer.
- **JDK**: Java 17 or Java 21 (bundled automatically with modern Android Studio).
- **Android SDK**: API Level 36 (Android 16 SDK platform and build tools installed via SDK Manager).

### Opening the Project
1. Open Android Studio.
2. Select **File > Open...** (or **Open an Existing Project** from the Welcome screen).
3. Select the root directory of this project.
4. Android Studio will automatically recognize the Gradle files and initiate project sync.
5. Once Gradle sync completes, select `app` from the run configurations dropdown.

---

## Building APK and Android App Bundle (.aab)

### 1. Build Debug APK (For Testing on Physical Device or Emulator)

From the command line in the project root:
```bash
./gradlew assembleDebug
```
(On Windows Command Prompt: `gradlew.bat assembleDebug`)

The generated debug APK will be located at:
```
app/build/outputs/apk/debug/app-debug.apk
```

You can install it directly onto a connected device via:
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

---

### 2. Prepare Release Signing Key (Keystore)

For production distribution, generate an upload signing key using `keytool` (included with JDK):

```bash
keytool -genkey -v -keystore release.keystore -alias upload -keyalg RSA -keysize 2048 -validity 10000
```
Follow the prompts to enter your password and certificate information.

> **Security Rule**: Keep your keystore file and passwords **OUTSIDE** source code control. Never commit `.keystore` or `.jks` files or credentials to Git repositories.

---

### 3. Provide Signing Credentials

You can supply credentials using either **Environment Variables** or **Gradle Properties**:

#### Option A: Environment Variables (Recommended for CI/CD like GitHub Actions)
```bash
export KEYSTORE_PATH="/path/to/release.keystore"
export STORE_PASSWORD="your_keystore_password"
export KEY_ALIAS="upload"
export KEY_PASSWORD="your_key_password"
```

#### Option B: `~/.gradle/gradle.properties` (User-level, outside project root)
Add the following lines to `~/.gradle/gradle.properties`:
```properties
RELEASE_STORE_FILE=/path/to/release.keystore
RELEASE_STORE_PASSWORD=your_keystore_password
RELEASE_KEY_ALIAS=upload
RELEASE_KEY_PASSWORD=your_key_password
```

#### Option C: Android Studio GUI (No terminal required)
1. In Android Studio, go to **Build > Generate Signed Bundle / APK...**
2. Choose **Android App Bundle** (for Google Play Store) or **APK**.
3. Point to your `release.keystore`, enter passwords and key alias.
4. Select `release` build variant and click **Finish**.

---

### 4. Build Production Android App Bundle (.aab)

Google Play Store requires the `.aab` format for publishing:
```bash
./gradlew bundleRelease
```
The resulting production bundle will be created at:
```
app/build/outputs/bundle/release/app-release.aab
```

---

### 5. Build Production Signed Release APK

To create a standalone release APK:
```bash
./gradlew assembleRelease
```
The signed APK will be created at:
```
app/build/outputs/apk/release/app-release.apk
```

---

## Running Unit & Screenshot Tests

To execute JVM unit and Robolectric tests:
```bash
./gradlew :app:testDebugUnitTest
```

To verify Compose screenshot tests with Roborazzi:
```bash
./gradlew :app:verifyRoborazziDebug
```

---

## Directory Structure

```
.
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/
│   │   │   │   ├── MainActivity.kt               # Main Activity with edge-to-edge Compose setup
│   │   │   │   ├── data/
│   │   │   │   │   ├── model/                     # Movie, Category, Download, Notification models
│   │   │   │   │   ├── repository/                # MovieRepository with local demo datasets
│   │   │   │   ├── ui/
│   │   │   │   │   ├── Z1MoviesApp.kt             # Navigation orchestrator & backstack coordinator
│   │   │   │   │   ├── components/                # Hero banner, cards, posters, navigation bar
│   │   │   │   │   ├── screens/                   # Home, Movies, Categories, Player, Downloads, Profile, Search, MyList, Admin, Auth
│   │   │   │   │   ├── theme/                     # Cinema dark color scheme, typography, shapes
│   │   │   │   │   └── viewmodel/                 # Z1MoviesViewModel state management
│   │   │   ├── res/
│   │   │   │   ├── drawable/                      # Vector art, icons, adaptive icon layers
│   │   │   │   ├── mipmap-*/                      # Custom adaptive launcher icon
│   │   │   │   └── values/                        # strings.xml ("Z1 Movies"), colors, themes
│   │   │   └── AndroidManifest.xml                # Permissions and Activity configurations
│   │   └── test/                                  # Robolectric CUJ and Roborazzi visual tests
│   ├── build.gradle.kts                           # App module Gradle configuration (SDK 36, dependencies)
│   └── proguard-rules.pro                         # Production R8/ProGuard rules
├── gradle/
│   ├── libs.versions.toml                         # Centralized Gradle version catalog
│   └── wrapper/                                   # Gradle wrapper configuration
├── build.gradle.kts                               # Root build script
├── settings.gradle.kts                            # Project settings (project name: "Z1 Movies")
├── keystore.properties.example                    # Reference template for release signing
└── README.md                                      # Comprehensive production build guide
```

---

## License & Compliance
This application is designed for streaming licensed, owned, or public-domain cinematic content. It complies with standard Google Play Developer distribution policies, enforces edge-to-edge display on modern Android versions, and safeguards user credentials and signing certificates outside the source repository.
