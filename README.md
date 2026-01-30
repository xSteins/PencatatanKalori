# PencatatanKalori

A modern Android application for tracking daily calorie intake and burned calories, helping users manage their fitness goals.

## Features

- 📊 Track daily calorie intake and burned calories
- 🎯 Set personalized fitness goals (lose weight, gain weight, or maintain weight)
- 📝 Log food items and workout activities
- 📈 View history of activities with detailed statistics
- 🧮 Calculate daily calorie needs using Mifflin-St Jeor equation
- 💪 Advanced exercise calorie management strategies
- 📸 Add photos to food and activity logs
- 🌟 Modern Material Design 3 UI with Jetpack Compose

## Development Requirements

### Prerequisites

- **Android Studio**: Arctic Fox or later (recommended: Latest stable version)
- **Operating System**: Windows (or Linux/macOS)
- **Debug Device/Emulator**: 
  - Minimum API Level: **29** (Android 10)
  - Target API Level: 35 (Android 15)
- **JDK**: Java 11 or higher
- **Kotlin**: 1.9.0 or higher

### Technology Stack

- Kotlin
- Jetpack Compose for UI
- Room Database for local storage
- Hilt for dependency injection
- Material Design 3
- Jetpack Navigation
- Coil for image loading
- KSP (Kotlin Symbol Processing)

## How to Compile

### 1. Clone the Repository

```bash
git clone https://github.com/xSteins/PencatatanKalori.git
cd PencatatanKalori
```

### 2. Open in Android Studio

1. Open Android Studio
2. Select **File > Open**
3. Navigate to the cloned repository folder
4. Click **OK**

### 3. Sync Gradle

1. Wait for Android Studio to load the project
2. Click **File > Sync Project with Gradle Files** (or use the sync button in the toolbar)
3. Wait for Gradle sync to complete
4. Resolve any dependency issues if prompted

### 4. Create Debug Configuration

1. Click on **Run > Edit Configurations**
2. Click the **+** button and select **Android App**
3. Name it "app" (or any preferred name)
4. Set **Module** to "app"
5. Click **OK**

### 5. Build and Run

1. Connect your Android device (API 29+) via USB with USB debugging enabled, or start an Android emulator
2. Select your device from the device dropdown
3. Click the **Run** button (green play icon) or press **Shift + F10**
4. The app will be built and installed on your device

## Installation for End Users

### Download Pre-built APK

1. Go to the [Releases](https://github.com/xSteins/PencatatanKalori/releases) tab on GitHub
2. Download the latest APK file
3. On your Android device:
   - Enable installation from unknown sources if prompted
   - Open the downloaded APK file
   - Follow the installation prompts
   - Grant necessary permissions when requested

## How to Use the App

### Initial Setup

1. **Launch the app** for the first time
2. **Enter your personal information**:
   - Body weight (kg)
   - Height (cm)
   - Age
   - Gender
   - Activity level (Sedentary to Super Active)
   - Fitness goal (Lose Weight, Maintain Weight, or Gain Weight)

### Main Features

#### Overview Screen
- View your daily calorie summary
- See net calories (intake - burned)
- Check remaining calories for the day
- View BMI (Body Mass Index)
- Quick access to recent activities

#### Logging Food Intake
1. Tap the **Add Food** button
2. Enter food name
3. Enter calorie amount
4. (Optional) Add notes
5. (Optional) Add a photo
6. Tap **Save**

#### Logging Workouts
1. Tap the **Add Workout** button
2. Enter activity name
3. Enter calories burned
4. (Optional) Add notes
5. (Optional) Add a photo
6. Tap **Save**

#### Viewing History
1. Navigate to the **History** tab
2. Select a date range to view activities
3. View detailed statistics for the selected period
4. Tap on any entry to edit or view details

#### Profile & Settings
1. Navigate to the **Profile** tab
2. Update your personal information
3. Change your fitness goal
4. Adjust activity level
5. Configure advanced calorie strategies
6. Access debugging features (if needed)

### Tips
- Log activities regularly for accurate tracking
- Update your weight periodically for better calorie calculations
- Use the date range selector in History to analyze trends
- Enable advanced exercise calorie management for more precise tracking

## Project Structure

```
app/src/main/java/com/ralvin/pencatatankalori/
├── model/              # Data models, database, and repositories
├── view/               # UI screens and components
├── viewmodel/          # ViewModels for managing UI state
└── PencatatanKalori.kt # Application class
```

## Building for Release

To create a release build:

```bash
./gradlew assembleRelease
```

The APK will be generated in `app/build/outputs/apk/release/`

## Contributing

Contributions are welcome! Please feel free to submit issues and pull requests.

## License

This project is available for educational and personal use.

## Support

For issues, questions, or suggestions, please open an issue on the GitHub repository.
