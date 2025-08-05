# Codebase Overview

This document provides an overview of the files and current functionality of the CDS Android application.
It is intended to be updated whenever new features are added or existing functionality is modified.

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
- `MainActivity.kt` – Entry activity hosting the navigation scaffold and bottom bar.
- `core/navigation/NavGraph.kt` – Defines navigation routes for English dashboard, concepts, detail, and quiz screens.

#### UI Screens
- `ui/english/dashboard/EnglishDashboardScreen.kt` – Dashboard showing PYQ summary and navigation.
- `ui/english/dashboard/EnglishDashboardViewModel.kt` – Supplies dashboard data.
- `ui/english/concepts/ConceptsHomeViewModel.kt` – Exposes English topics from the repository.
- `ui/english/concepts/ConceptsHomeScreen.kt` – Lists topics and navigates to their details.
- `ui/english/concepts/ConceptDetailViewModel.kt` – Loads a single topic based on navigation arguments.
- `ui/english/concepts/ConceptDetailScreen.kt` – Shows the selected topic’s name and overview.
- `ui/english/pyqp/PyqpPaperListScreen.kt` – Lists available previous year papers.
- `ui/english/pyqp/QuizScreen.kt` – Runs a quiz for a selected paper with a countdown timer (pause/resume on lifecycle events), question flagging and a palette for quick navigation, showing section intro pages and collapsible headers for passages or directions.
- `ui/english/pyqp/PyqpListViewModel.kt`, `QuizViewModel.kt` – View models for paper list and quiz screens with paging and section intro logic.
- `ui/english/quiz/QuizScreen.kt` – Placeholder quiz view for a topic.
- `ui/english/analysis/AnalysisScreen.kt` – Placeholder analysis screen.

#### Core Components & Theme
- `core/components/AppBar.kt` – Simple top app bar wrapper.
- `core/components/BottomNavBar.kt` – Bottom navigation bar linking dashboard, concepts, and quiz routes.
- `core/components/CdsCard.kt` – Convenience wrapper around Material `Card`.
- `core/model/Subject.kt` – Enum describing supported subjects and associated icons.
- `core/model/SubjectProgress.kt` – Model representing progress for a subject.
- `core/theme/Color.kt`, `core/theme/Type.kt`, `core/theme/Theme.kt` – Material3 theme definitions and typography.

#### Domain & Data Layer
- `domain/english/EnglishTopic.kt`, `EnglishQuestion.kt` – Domain models for topics and questions.
- `data/english/db/EnglishDatabase.kt` – Room database for English topics and questions.
- `data/english/db/EnglishTopicDao.kt`, `EnglishQuestionDao.kt` – DAO interfaces for topics and questions.
- `data/english/db/SeedUtil.kt` – Seeds the English database and sample PYQ data if empty.
- `data/english/model/EnglishTopicEntity.kt`, `EnglishQuestionEntity.kt` – Room entities with mappers to domain models.
- `data/english/model/PyqpQuestionEntity.kt`, `PyqpProgress.kt` – Entities for previous year questions and progress.
- `data/english/repo/EnglishRepository.kt` – Repository combining DAOs for higher-level operations.
- `data/english/repo/PyqpRepository.kt` – Repository exposing PYQ papers and questions.

#### Dependency Injection (`di/`)
- `data/english/db/EnglishDatabaseModule.kt` – Provides the English Room database, DAOs, and repository.

### Resources & Assets
- `src/main/AndroidManifest.xml` – Application manifest declaring permissions, application class and launcher activity.
- `src/main/res/values/` – String resources, colors and themes.
- `src/main/res/drawable` & `mipmap*` – Launcher icons and backgrounds.
- `src/main/res/xml/` – Backup and data extraction configuration.
- `src/main/assets/english_seed.json` – Seed data for populating the English database.
- `src/main/assets/CDS_II_2024_English_SetA.json` – PYQ sample exam data used by the quiz screen.

### Tests
- `src/test/java/.../ExampleUnitTest.kt` – Sample unit test.
- `src/test/java/.../data/english/db/EnglishDatabaseTest.kt` – Robolectric test ensuring the English database seeds two topics.
- `src/androidTest/java/.../ExampleInstrumentedTest.kt` – Sample instrumented test.

## Existing Functionality Summary
- Bottom navigation linking English dashboard, concepts, and quiz screens.
- English topics stored in a Room database with seed data and repository access.
- Concept listing and detail views backed by Hilt-injected view models.
- Quiz runtime supports timer, flagging and palette-based navigation.
- Basic dependency injection setup using Hilt.

This document should be updated whenever files are added, removed or significantly modified.

