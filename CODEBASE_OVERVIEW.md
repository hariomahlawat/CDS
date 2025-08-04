# Codebase Overview

This document provides an overview of the files and current functionality of the CDS Android application. It is intended to be updated whenever new features are added or existing functionality is modified.

## Root Project Files

- `build.gradle.kts` – Top level Gradle build file enabling Android, Kotlin, Hilt and Google services plugins and resolving a JavaPoet version conflict.
- `settings.gradle.kts` – Configures repositories and includes the `app` module.
- `gradle.properties` – Gradle and Kotlin compiler settings used across the project.
- `gradlew`/`gradlew.bat`, `gradle/` – Gradle wrapper scripts and supporting files.

## App Module (`app/`)

### Build & Config
- `build.gradle.kts` – Module level build script configuring Compose, Hilt, Room, Firebase and other dependencies.
- `google-services.json` – Firebase project configuration.
- `proguard-rules.pro` – Placeholder for ProGuard/R8 rules.

### Source Code (`app/src/main/java/com/concepts_and_quizzes/cds`)

#### Application & Navigation
- `CdsApplication.kt` – Application class annotated with `@HiltAndroidApp` to bootstrap Hilt.
- `MainActivity.kt` – Entry activity hosting authentication flow and the main navigation host with bottom navigation.
- `core/navigation/NavGraph.kt` – Placeholder sealed class describing navigation destinations.

#### Authentication (`auth/`)
- `AuthRepository.kt` – Handles authentication with Firebase email/password and Google credentials using the Credential Manager API.
- `AuthViewModel.kt` – Holds login and registration UI state, performs validation and delegates auth actions to `AuthRepository`.
- `AuthScreens.kt` – Compose screens for login and registration.

#### UI Screens
- `ui/home/SubjectChooserViewModel.kt` – Provides subscription data to the subject chooser.
- `ui/home/SubjectChooserScreen.kt` – Grid of available subjects with access to dashboard and subject screens.
- `ui/home/SubjectCard.kt` – Card component displaying a subject and subscription status.
- `ui/dashboard/DashboardViewModel.kt` – Exposes progress for each subject via `ProgressRepository`.
- `ui/dashboard/GlobalDashboardScreen.kt` – Dashboard listing progress cards for all subjects.
- `ui/dashboard/ProgressCard.kt` – Visualizes completion percentage for a subject.
- `ui/concepts/ConceptsScreen.kt` – Placeholder screen for subject concepts.
- `ui/english/EnglishScreen.kt` – Placeholder English subject screen.
- `exam/ExamViewModel.kt` – Loads exam questions from `ExamRepository` (not yet used by a UI screen).

#### Core Components & Theme
- `core/components/AppBar.kt` – Simple top app bar wrapper.
- `core/components/BottomNavBar.kt` – Bottom navigation bar for major destinations.
- `core/components/CdsCard.kt` – Convenience wrapper around Material `Card`.
- `core/model/Subject.kt` – Enum describing supported subjects and associated icons.
- `core/model/SubjectProgress.kt` – Model representing progress for a subject.
- `core/theme/Color.kt`, `core/theme/Type.kt`, `core/theme/Theme.kt` – Material3 theme definitions and typography.

#### Data Layer
- `data/repository/ProgressRepository.kt` – Supplies placeholder `SubjectProgress` values.
- `data/repository/SubscriptionRepository.kt` – Persists premium subscription flags using DataStore.
- `data/repository/ExamRepository.kt` – Persists and queries exam, direction, passage and question data via DAOs.
- `data/import/ExamDataImporter.kt` – Parses exam JSON files from assets and inserts data through `ExamRepository`.
- `data/local/AppDatabase.kt` – Room database definition for exams, directions, passages and questions.
- `data/local/entities/*` – Room entity classes (`ExamEntity`, `DirectionEntity`, `PassageEntity`, `QuestionEntity`, `QuestionWithDirectionAndPassage`).
- `data/local/dao/*` – DAO interfaces for each entity (`ExamDao`, `DirectionDao`, `PassageDao`, `QuestionDao`).

#### Dependency Injection (`di/`)
- `SubscriptionModule.kt` – Provides `SubscriptionRepository`.
- `ProgressModule.kt` – Provides `ProgressRepository`.
- `DatabaseModule.kt` – Creates `AppDatabase`, DAO instances and an `ExamRepository`.

### Resources & Assets
- `src/main/AndroidManifest.xml` – Application manifest declaring permissions, application class and launcher activity.
- `src/main/res/values/` – String resources, colors and themes.
- `src/main/res/drawable` & `mipmap*` – Launcher icons and backgrounds.
- `src/main/res/xml/` – Backup and data extraction configuration.
- `src/main/assets/CDS_II_2024_English_SetA.json` – Sample exam data used by `ExamDataImporter`.

### Tests
- `src/test/java/.../ExampleUnitTest.kt` – Sample unit test.
- `src/test/java/.../data/ExamRepositoryTest.kt` – Unit test verifying question retrieval with multiple directions.
- `src/androidTest/java/.../ExampleInstrumentedTest.kt` – Sample instrumented test.

## Existing Functionality Summary
- Authentication via Firebase (email/password and Google sign-in).
- Subject subscription storage with DataStore.
- Dashboard showing per-subject progress (placeholder data).
- Subject chooser and navigation between major destinations.
- Room database schema for exams with import utility.
- Basic dependency injection setup using Hilt.

This document should be updated whenever files are added, removed or significantly modified.

