# CDS

An Android application for concept-driven study built with Kotlin and Jetpack Compose. It currently focuses on English practice and quiz analytics while laying the groundwork for additional subjects and modules.

## Overview

CDS exposes a dashboard of daily tips, concept browsing, and previous year question practice. Quizzes capture timing and flagging data that will feed upcoming analytics modules.

## Requirements
- Android Studio Giraffe (or newer)
- JDK 17
- Android SDK Platform 34 with Google APIs

## Developer Setup
1. Clone the repository and open the project in Android Studio.
2. Sync Gradle when prompted.
3. Build the debug APK and run unit tests:
   ```bash
   ./gradlew :app:assembleDebug :app:testDebugUnitTest
   ```
4. Install the app on a connected device or emulator:
   ```bash
   ./gradlew :app:installDebug
   ```

## Running Checks
### Unit Tests
```bash
./gradlew :app:testDebugUnitTest
```

### Lint and Static Analysis
```bash
./gradlew detekt ktlintCheck lintDebug
```

## Project Structure
See [CODEBASE_OVERVIEW.md](CODEBASE_OVERVIEW.md) for a detailed breakdown of modules and key files.

## Contributing
- Follow Kotlin and Jetpack Compose best practices.
- Write tests for new features and keep documentation up to date.
- Run the commands in [Running Checks](#running-checks) before submitting a pull request.

## License
This project is distributed under the MIT License; see the `LICENSE` file if present or consult the repository owner.
