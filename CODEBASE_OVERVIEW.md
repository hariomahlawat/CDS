# Codebase Overview

This document provides an overview of the files and current functionality of the CDS Android application. It is intended to be updated whenever new features are added or existing functionality is modified.

## Root Project Files

- `build.gradle.kts` – Top level Gradle build file enabling Android, Kotlin, Hilt and Google services plugins and resolving a JavaPoet version conflict.
- `settings.gradle.kts` – Configures repositories and includes the `app` module.
- `gradle.properties` – Gradle and Kotlin compiler settings used across the project.
- `gradlew`/`gradlew.bat`, `gradle/` – Gradle wrapper scripts and supporting files.

## App Module (`app/`)

### Build & Config
- `build.gradle.kts` – Module level build script configuring Compose, Hilt, Room, Firebase Auth (sign-in flow disabled) and other dependencies.
- `google-services.json` – Firebase project configuration.
- `proguard-rules.pro` – Placeholder for ProGuard/R8 rules.

### Source Code (`app/src/main/java/com/concepts_and_quizzes/cds`)

#### Application & Navigation
- `CdsApplication.kt` – Application class annotated with `@HiltAndroidApp` to bootstrap Hilt.
- `MainActivity.kt` – Entry activity hosting the main navigation with bottom navigation.
- `core/navigation/NavGraph.kt` – Placeholder sealed class describing navigation destinations.

#### UI Screens
- `ui/home/SubjectChooserViewModel.kt` – Provides subscription data to the subject chooser.
- `ui/home/SubjectChooserScreen.kt` – Grid of available subjects with access to dashboard and subject screens.
- `ui/home/SubjectCard.kt` – Card component displaying a subject and subscription status.
- `ui/dashboard/DashboardViewModel.kt` – Exposes progress for each subject via `ProgressRepository`.
- `ui/dashboard/GlobalDashboardScreen.kt` – Dashboard listing progress cards for all subjects.
- `ui/dashboard/ProgressCard.kt` – Visualizes completion percentage for a subject.
- `ui/concepts/ConceptsScreen.kt` – Placeholder screen for subject concepts.
- `ui/english/EnglishScreen.kt` – Placeholder English subject screen.

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
- `data/english/db/EnglishDatabase.kt` – Room database for English topics and questions.
- `data/english/db/EnglishTopicDao.kt`, `EnglishQuestionDao.kt` – DAO interfaces for topics and questions.
- `data/english/db/SeedUtil.kt` – Seeds the English database from `english_seed.json` if empty.
- `data/english/repo/EnglishRepository.kt` – Repository combining DAOs for higher-level operations.

#### Dependency Injection (`di/`)
- `SubscriptionModule.kt` – Provides `SubscriptionRepository`.
- `ProgressModule.kt` – Provides `ProgressRepository`.
- `data/english/db/EnglishDatabaseModule.kt` – Provides the English Room database and DAOs.

### Resources & Assets
- `src/main/AndroidManifest.xml` – Application manifest declaring permissions, application class and launcher activity.
- `src/main/res/values/` – String resources, colors and themes.
- `src/main/res/drawable` & `mipmap*` – Launcher icons and backgrounds.
- `src/main/res/xml/` – Backup and data extraction configuration.
- `src/main/assets/CDS_II_2024_English_SetA.json` – PYQ sample exam data that will be presented to the user in quiz format.

### Tests
- `src/test/java/.../ExampleUnitTest.kt` – Sample unit test.
- `src/test/java/.../data/english/db/EnglishDatabaseTest.kt` – Robolectric test ensuring the English database seeds two topics.
- `src/androidTest/java/.../ExampleInstrumentedTest.kt` – Sample instrumented test.

## Existing Functionality Summary
- Subject subscription storage with DataStore.
- Dashboard showing per-subject progress (placeholder data).
- Subject chooser and navigation between major destinations.
- Room database for English topics and questions with seeding support.
- Basic dependency injection setup using Hilt.

This document should be updated whenever files are added, removed or significantly modified.

