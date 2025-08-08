# Codebase Overview

This document provides an overview of the files and current functionality of the CDS Android application.
It is intended to be updated whenever new features are added or existing functionality is modified.

## Architecture
The project uses a single `app` module structured into clear layers:
- **UI** – Jetpack Compose screens and navigation logic.
- **Domain** – Kotlin models representing topics, questions and progress.
- **Data** – Room databases, repositories and DataStore for persistence.
- **Dependency Injection** – Hilt modules wiring repositories and view models.

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
- `MainActivity.kt` – Entry activity hosting the navigation scaffold and bottom bar with first-run onboarding.
- `core/navigation/NavGraph.kt` – Defines navigation routes for English dashboard, concepts, detail, and quiz screens.
- `AppViewModel.kt` – Root view model exposing onboarding state.
- `ui/onboarding/OnboardingScreen.kt` – Simple carousel shown on first launch.

#### UI Screens
- `ui/english/dashboard/EnglishDashboardScreen.kt` – Hero landing with greeting, progress ring, action chips, live stats and a discover carousel.
- `ui/english/dashboard/EnglishDashboardViewModel.kt` – Supplies dashboard data including questions practised and daily tips with bookmarking.
- `ui/english/dashboard/DiscoverComponents.kt` – Card and carousel composables for daily tips with pre-cached items to reduce jank.
- `ui/english/discover/DiscoverConceptDetailScreen.kt` – Full-screen detail view for a tip with bookmarking and a tip icon.
- `ui/english/discover/DiscoverConceptViewModel.kt` – Loads a single tip and exposes bookmark state.
- `ui/english/concepts/ConceptsHomeViewModel.kt` – Exposes English topics and bookmarked tips.
- `ui/english/concepts/ConceptsHomeScreen.kt` – Lists topics with a Bookmarks tab for saved tips.
- `ui/english/concepts/ConceptDetailViewModel.kt` – Loads a single topic based on navigation arguments.
- `ui/english/concepts/ConceptDetailScreen.kt` – Shows the selected topic’s name and overview.
- `ui/english/pyqp/PyqpPaperListScreen.kt` – Lists available previous year papers.
- `ui/english/pyqp/QuizScreen.kt` – Runs a quiz for a selected paper with a countdown timer (pause/resume on lifecycle events), question flagging and a palette for quick navigation, showing section intro pages and collapsible headers for passages or directions.
- `ui/english/pyqp/PyqpListViewModel.kt`, `QuizViewModel.kt` – View models for paper list and quiz screens with paging and section intro logic.
- `ui/english/quiz/QuizHubScreen.kt`, `QuizHubViewModel.kt` – Hub for starting or resuming previous year question practice and accessing analytics with a snackbar to resume the last test.
- `ui/english/quiz/QuizScreen.kt` – Placeholder quiz view for a topic.
- `ui/english/analysis/AnalysisScreen.kt` – Placeholder analysis screen.

#### Core Components & Theme
- `core/components/AppBar.kt` – Simple top app bar wrapper.
- `core/components/BottomNavBar.kt` – Bottom navigation bar linking dashboard, concepts, and quiz routes.
- `core/components/CdsCard.kt` – Convenience wrapper around Material `Card`.
- `core/model/Subject.kt` – Enum describing supported subjects and associated icons.
- `core/model/SubjectProgress.kt` – Model representing progress for a subject.
- `core/theme/Color.kt`, `core/theme/Type.kt`, `core/theme/Theme.kt` – Material3 theme definitions and typography with custom light/dark color tokens and boosted dark text contrast.

#### Domain & Data Layer
- `domain/english/EnglishTopic.kt`, `EnglishQuestion.kt` – Domain models for topics and questions.
- `data/english/db/EnglishDatabase.kt` – Room database for English topics, quizzes and daily concepts.
- `data/discover/model/*` – Entities for concepts, daily tips and bookmarks.
- `data/discover/db/ConceptDao.kt` – DAO for concepts, rotation and bookmarks.
- `data/discover/DiscoverRepository.kt` – Repository handling tip rotation and bookmarking.
- `data/english/db/EnglishTopicDao.kt`, `EnglishQuestionDao.kt` – DAO interfaces for topics and questions.
- `data/english/db/SeedUtil.kt` – Seeds the English database, sample PYQ data and concepts if empty.
- `data/english/model/EnglishTopicEntity.kt`, `EnglishQuestionEntity.kt` – Room entities with mappers to domain models.
- `data/english/model/PyqpQuestionEntity.kt`, `PyqpProgress.kt` – Entities for previous year questions and progress.
- `data/english/repo/EnglishRepository.kt` – Repository combining DAOs for higher-level operations.
- `data/english/repo/PyqpRepository.kt` – Repository exposing PYQ papers and questions.
- `data/quiz/QuizResumeStore.kt` – DataStore-backed persistence for resuming quizzes.
- `data/settings/UserPreferences.kt` – Stores onboarding completion flag.

#### Dependency Injection (`di/`)
- `data/english/db/EnglishDatabaseModule.kt` – Provides the Room database, DAOs, and repositories including discover.

### Resources & Assets
- `src/main/AndroidManifest.xml` – Application manifest declaring permissions, application class and launcher activity.
- `src/main/res/values/` – String resources, colors and themes.
- `src/main/res/drawable` & `mipmap*` – Launcher icons and backgrounds.
- `src/main/res/xml/` – Backup and data extraction configuration.
- `src/main/assets/english_seed.json` – Seed data for populating the English database.
- `src/main/assets/CDS_II_2024_English_SetA.json` – PYQ sample exam data used by the quiz screen.
- `src/main/assets/concepts_of_the_day.json` – Concept tips seeded into the database.

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

## Development Workflow
1. Branch from `main` and keep commits focused.
2. Run lint and unit tests before pushing: `./gradlew detekt ktlintCheck lintDebug :app:testDebugUnitTest`
3. Update documentation and this overview when files change.
4. Use the PR template to describe changes and manual testing.

This document should be updated whenever files are added, removed or significantly modified.

