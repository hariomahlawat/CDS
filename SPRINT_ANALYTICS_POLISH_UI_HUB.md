# Sprint “Analytics Polish + UI Hub”
Duration: 5 working days (Mon → Fri)
Release target: v1.4.0 (versionCode 38)

This sprint document aligns engineering, design, and QA on polishing analytics modules and introducing a unified navigation hub. It captures goals, tasks, and acceptance criteria for a focused one-week effort.


0 Macro-Goals
ID   Goal
G-1  Auto-land on Per-Quiz Analysis after the user submits a PYQ.
G-2  Navigation refactor to a hub-and-spokes IA (Home · Practice · Reports · Library).
G-3  Ghost-teaser preview for every locked analytics module with live CTA.
G-4  Finalise flag-colours, countdown strings, and add telemetry for unlock funnels.
G-5  CI green (unit, lint, screenshot, macrobench); WCAG AA & ≤ 10 MB APK.

1 Updated Navigation / UI Flow
```java
BottomBar / Rail (← RTL mirrors)
┌─────────────┬──────────┬─────────┬──────────┐
│  Home 🏠    │ Practice │ Reports │ Library  │
└─────────────┴──────────┴─────────┴──────────┘
```

Home:
  • Daily Discover
  • Quick tiles (Start PYQ, Mock, Bookmarks)
  • Streak teaser & last-quiz badge

Practice:
  • Previous PYQs (list)
  • Mock tests

Reports (VerticalPager):
  page0  Last-Quiz Report   (auto-opens post-submit)
  page1  Trend (10-week)    (locked preview until ≥3 quizzes)
  page2  Heat-map           (locked till 50 answers)
  page3  Time Mgmt Drill    (locked 24 h gate)
  page4  Peer Percentile    (placeholder)

Library:
  • Concepts search
  • Bookmarks filter

Routing keys

home, practice, reports, library as BottomBar destinations.

analysis/{sessionId} deep-link opens Reports pager page0.

2 Task Table (fully-detailed)
| ID | Owner (R) | Reviewer | Files / API | Steps | Tests | Est. |
|----|-----------|----------|-------------|-------|-------|------|
| NAV-01 | @fe-alice | @arch-dan | QuizViewModel.kt, MainNavGraph.kt | Replace `pop` with `nav.navigate("analysis/$id"){ popUpTo("practice"); launchSingleTop=true }` | Espresso QuizSubmitNavTest | 1 h |
| NAV-02 BottomBar swap | @fe-alice | @design-eve | MainNavGraph.kt, BottomBar.kt, NavigationRail.kt | 1. Add enum Reports.<br>2. Route to ReportsPagerScreen.<br>3. Feature-flag key `reports_tab_enabled` (RemoteConfig). | Paparazzi baseline (light/dark/RTL) | 2 h |
| UI-01 ReportsPager | @fe-bob | @arch-dan | ReportsPagerScreen.kt (accompanist-pager) | 1. VerticalPager 5 pages.<br>2. Locked pages recv ModuleStatus. | UI test ReportsSwipeTest | 3 h |
| UI-02 Ghost overlay | @fe-bob | @design-eve | GhostOverlay.kt, wrap each page | see GST-02 in previous brief. | GhostVisibilityTest | 4 h |
| DES-01 Skeleton assets | @design-eve | – | ui/skeleton/* | Provide TrendSkeleton/HeatmapSkeleton/etc. | Visual QA via Figma | 1 d |
| CLR-01 | @fe-bob | @design-eve | ColorSchemeExt.kt, QuestionStatePalette.kt | Add flaggedContainer/OnContainer. | FlagColorContrastTest | 0.5 h |
| COUNT-01 | @be-carol | @fe-bob | DurationExt.kt | Duration.toPretty() util. | Unit test | 0.5 h |
| UNLOCK-01 Progress & RC | @be-carol | @arch-dan | AnalyticsUnlocker.kt, AnalyticsUnlockConfig.kt | 1. Add `progress:Float`.<br>2. Load overrides from RC key `unlock_thresholds_v1`. | UnlockerUnitTest | 4 h |
| TEL-01 | @be-carol | @qa-cara | Telemetry.kt | Events `unlock_view`, `unlock_success`. | DebugView manual | 1 h |
| QA-01 | @qa-cara | – | new tests | Update all Espresso + Paparazzi; add Macrobench on ghost page. | CI green | 1 d |

Total dev effort ≈ 4.5 dev-days + 1 design-day + 1 QA-day

3 Telemetry Spec (TEL-01)
Event | Params | Fired when
---|---|---
unlock_view | module, remaining, progress | User lands on locked page.
unlock_success | module, total_quizzes, time_since_install | Ghost overlay fades out.

4 Colour Tokens (CLR-01)
```kotlin
// ColorSchemeExt.kt
val ColorScheme.flaggedContainer: Color
    @Composable get() =
        if (isSystemInDarkTheme()) primaryContainer.copy(alpha = .24f)
        else primary.copy(alpha = .20f)

val ColorScheme.flaggedOnContainer: Color
    @Composable get() =
        if (isSystemInDarkTheme()) Color(0xFFCAE5FF)
        else Color(0xFF00315D)
```
Only QuestionStatePalette consumes these; rest of theme untouched.

5 Acceptance Criteria
Navigation – Submit → Reports page0; back-stack has no stale Quiz.

BottomBar – Home / Practice / Reports / Library; Rail mirrors.

Ghost preview – Locked pages show skeleton chart, 30 % opacity, lock icon, CTA string with live countdown or remaining quizzes.

Unlock – finishing criteria removes overlay with 400 ms fade; telemetry logs unlock_success.

Flag palette – passes WCAG AA in both themes.

Perf – Macrobenchmark: Trend skeleton scroll jank ≤ 8 frames.

CI – `./gradlew detekt ktlintCheck lintDebug testDebugUnitTest connectedDebugAndroidTest verifyPaparazziDebug` all green.

APK ≤ 10 MB.

6 Release Checklist
Version bump (versionCode 38, versionName 1.4.0).

RemoteConfig:

unlock_thresholds_v1 JSON uploaded

reports_tab_enabled = true

Record new Paparazzi baselines.

Upload v1.4.0-rc1 to internal track; verify Play pre-launch.

Monitor Crashlytics & event funnel 24 h; then roll out 100 %.

Hand this single document to the dev, design, and QA teams.
Every task lists files, exact code steps, reviewers, tests, and definition of done—execution should be fool-proof.
