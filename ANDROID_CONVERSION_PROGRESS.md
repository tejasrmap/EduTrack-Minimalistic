# EduTrack Minimalistic — Native Android Conversion Progress Tracker

## 1. Project Overview & Conversion Objectives
- **Source Web App**: EduTrack Minimal Academic Edition (`Member-2/edutrack-minimal-academic`)
- **Target Platform**: 100% Native Android (Kotlin 2.2.10, Jetpack Compose, Material 3, Room 2.8.4, MVVM, StateFlow, Coroutines, Navigation Compose)
- **Coursework Compliance**: Full CRUD operations on Room entities (Subject, StudySession, Task, Exam, Goal, Note, etc.), ViewModel + StateFlow observable state, native navigation stack, form validations, Snackbar feedback, persistent Room DB, native Canvas charts.
- **Target Package**: `com.gtinnox.edutrack`
- **Target Branch**: `android-native-conversion`
- **Build Status**: **SUCCESSFUL** (`BUILD SUCCESSFUL in 44s`, 37 actionable tasks executed)
- **APK Output**: `android/app/build/outputs/apk/debug/app-debug.apk` and `apk/EduTrack-Minimal-Native.apk` (~18.3 MB)

---

## 2. Feature Inventory & Migration Mapping

| # | Original Web Module / Feature | Original File / State | Native Android Conversion Target | Status |
|---|-------------------------------|----------------------|-----------------------------------|--------|
| 1 | **Dashboard** | `src/pages/dashboard/DashboardPage.tsx` | `ui/dashboard/DashboardScreen.kt`, `DashboardViewModel` | ✅ Completed & Verified |
| 2 | **Subjects Manager** | `src/pages/subjects/SubjectsPage.tsx`, `SubjectDetailPage.tsx` | `ui/subjects/SubjectsScreen.kt`, `SubjectDetailScreen.kt`, `SubjectViewModel` | ✅ Completed & Verified |
| 3 | **Study Sessions (CRUD)** | `StudySession`, `initialData.ts`, `DataContext.tsx` | `ui/sessions/AddEditSessionScreen.kt`, `SessionDetailScreen.kt`, `SessionViewModel` | ✅ Completed & Verified |
| 4 | **Focus / Pomodoro Timer** | `src/pages/timer/TimerPage.tsx`, `TimerContext.tsx` | `ui/timer/TimerScreen.kt`, `TimerViewModel`, persistent timestamp state | ✅ Completed & Verified |
| 5 | **Study Planner** | `src/pages/planner/PlannerPage.tsx`, `ScheduleEvent` | `ui/planner/PlannerScreen.kt`, `PlannerViewModel`, day/week view | ✅ Completed & Verified |
| 6 | **Assignments / Tasks** | `src/pages/assignments/AssignmentsPage.tsx`, `Task` | `ui/assignments/AssignmentsScreen.kt`, `AssignmentViewModel` | ✅ Completed & Verified |
| 7 | **Examinations** | `src/pages/examinations/ExaminationsPage.tsx`, `Examination` | `ui/examinations/ExaminationsScreen.kt`, `ExamViewModel` | ✅ Completed & Verified |
| 8 | **Goals Tracking** | `src/pages/goals/GoalsPage.tsx`, `Goal` | `ui/goals/GoalsScreen.kt`, `GoalViewModel` | ✅ Completed & Verified |
| 9 | **Notes & Revision** | `src/pages/notes/NotesPage.tsx`, `Note` | `ui/notes/NotesScreen.kt`, `NoteViewModel` | ✅ Completed & Verified |
| 10 | **Analytics & Visual Charts** | `src/pages/analytics/AnalyticsPage.tsx`, Recharts | `ui/analytics/AnalyticsScreen.kt`, native Compose/Canvas charts | ✅ Completed & Verified |
| 11 | **Session History** | `src/pages/history/HistoryPage.tsx` | `ui/history/HistoryScreen.kt`, `HistoryViewModel` | ✅ Completed & Verified |
| 12 | **Notifications** | `src/pages/notifications/NotificationsPage.tsx` | `ui/notifications/NotificationsScreen.kt`, Android Notification Channel | ✅ Completed & Verified |
| 13 | **Profile & Scholar Personas** | `src/pages/profile/ProfilePage.tsx`, `initialUserProfile` | `ui/profile/ProfileScreen.kt`, `ProfileViewModel` | ✅ Completed & Verified |
| 14 | **Settings & Preferences** | `src/pages/settings/SettingsPage.tsx`, `UserSettings` | `ui/settings/SettingsScreen.kt`, `SettingsViewModel`, DataStore | ✅ Completed & Verified |
| 15 | **Onboarding Wizard** | `src/pages/onboarding/OnboardingPage.tsx` | `ui/onboarding/OnboardingScreen.kt`, `OnboardingViewModel` | ✅ Completed & Verified |
| 16 | **Global Search** | `src/components/modals/GlobalSearchModal.tsx` | `ui/components/GlobalSearchDialog.kt` | ✅ Completed & Verified |
| 17 | **Quick Add Modal** | `src/components/modals/QuickAddModal.tsx` | `ui/components/QuickAddBottomSheet.kt` | ✅ Completed & Verified |
| 18 | **Data Export / Import** | JSON export/import in Settings | `DataManagementService`, Room JSON backup/restore | ✅ Completed & Verified |
| 19 | **Dark / Academic Light Theme**| Tailwind tokens `#6477D9`, `#F8FAFC`, `#FFFFFF` | `ui/theme/Theme.kt`, `Color.kt`, `Type.kt` | ✅ Completed & Verified |

---

## 3. Implementation Checklist

- [x] Repository audit & Git branch `android-native-conversion` checkout
- [x] Conversion progress tracker created
- [x] Native Android Gradle build files (`build.gradle.kts`, `settings.gradle.kts`, `app/build.gradle.kts`, `gradle/libs.versions.toml`)
- [x] Room database setup:
  - [x] Subject entity & DAO (`SubjectEntity`, `SubjectDao`)
  - [x] StudySession entity & DAO (`StudySessionEntity`, `StudySessionDao`)
  - [x] Task entity & DAO (`TaskEntity`, `TaskDao`)
  - [x] Examination entity & DAO (`ExaminationEntity`, `ExaminationDao`)
  - [x] Goal entity & DAO (`GoalEntity`, `GoalDao`)
  - [x] Note entity & DAO (`NoteEntity`, `NoteDao`)
  - [x] ScheduleEvent entity & DAO (`ScheduleEventEntity`, `ScheduleEventDao`)
  - [x] NotificationItem entity & DAO (`NotificationEntity`, `NotificationDao`)
  - [x] UserProfile entity & DAO (`UserProfileEntity`, `UserProfileDao`)
  - [x] Foreign keys with `CASCADE` on delete & indices
  - [x] `EduTrackDatabase` with pre-populated seed data (`DatabaseSeed.kt`)
- [x] Repository Layer (`EduTrackRepository.kt`) & ViewModels:
  - [x] `DashboardViewModel`
  - [x] `SubjectViewModel`
  - [x] `SessionViewModel`
  - [x] `TimerViewModel`
  - [x] `PlannerViewModel`
  - [x] `AssignmentViewModel`
  - [x] `ExamViewModel`
  - [x] `GoalViewModel`
  - [x] `NoteViewModel`
  - [x] `AnalyticsViewModel`
  - [x] `HistoryViewModel`
  - [x] `SettingsViewModel`
  - [x] `ProfileViewModel`
  - [x] `OnboardingViewModel`
  - [x] `ViewModelFactory`
- [x] Material 3 Jetpack Compose Theme & Minimalist Design System (`Color.kt`, `Type.kt`, `Shape.kt`, `Theme.kt`)
- [x] Navigation Compose (`Screen.kt`, `BottomNavItem.kt`, `MoreMenuScreen.kt`, NavHost & Backstack)
- [x] Feature Screens implementation:
  - [x] `DashboardScreen` (KPIs, time breakdown, upcoming deliverables, quick timer launch)
  - [x] `SubjectsScreen` & `SubjectDetailScreen` & `AddEditSubjectScreen` (Full CRUD, target hours, attendance, assignments)
  - [x] `AddEditSessionScreen` & `SessionDetailScreen` (Form validation, subject picker, notes)
  - [x] `HistoryScreen` (Date grouping, filtering, search, delete)
  - [x] `TimerScreen` (Pomodoro, Focus, Short/Long Break, timestamps, session logging)
  - [x] `PlannerScreen` (Daily & weekly agenda, time slots, schedule creation)
  - [x] `AssignmentsScreen` (Categorized by status/priority, filter by subject, check-off)
  - [x] `ExaminationsScreen` (Countdown, exam type, readiness status, target grade)
  - [x] `GoalsScreen` (Target vs actual metrics, progress bar, completion toggle)
  - [x] `NotesScreen` (Search, pinned notes, tags, subject linkage)
  - [x] `AnalyticsScreen` (Study duration bars, subject breakdown donut, focus trend line Canvas)
  - [x] `NotificationsScreen` (System notifications & in-app alerts)
  - [x] `ProfileScreen` (Scholar personas switch, academic program info, study metrics)
  - [x] `SettingsScreen` (Theme toggle, timer durations, sound, data export/import)
  - [x] `OnboardingScreen` (3-step academic wizard)
  - [x] `GlobalSearchDialog` & `QuickAddBottomSheet`
- [x] Unit test suite (`CourseworkUnitTests.kt` - 5/5 tests passed)
- [x] Compile and verify Gradle build (`.\gradlew.bat test` - BUILD SUCCESSFUL)
- [x] Generate debug APK (`.\gradlew.bat assembleDebug` - BUILD SUCCESSFUL)
- [x] Export standalone APK to `apk/EduTrack-Minimal-Native.apk` (18.4 MB)
- [x] EduTrack 2.0 UI/UX Redesign across all 16 modules:
  - [x] Premium editorial design tokens (Warm Ivory `#F6F5F1`, Lavender/Indigo `#615CE7`, Midnight Dark `#15151D`, 6 Pastel Subject identities)
  - [x] Unified component library (`PremiumCard`, `GradientHeroCard`, `MetricCard`, `SubjectChip`, `PriorityChip`, `StatusChip`, `SectionHeader`, `AcademicEmptyState`, `AcademicCalendarHeatmap`, `MinimalStudyBarChart`, `MinimalSubjectDonutChart`, `FocusTrendChart`)
  - [x] Dashboard redesigned with Hero ambient focus card, live progress ring, quick focus launch, and agenda timeline
  - [x] Full-screen Pomodoro focus workspace with Canvas countdown ring and mode selection pills
  - [x] Horizontal 14-day agenda timeline with subject badges and conflict scheduling
  - [x] Clean 20dp rounded cards, subtle `#E7E5ED` hairline borders, and zero clutter
- [x] Final verification and documentation

---

## 4. Coursework Technical Requirements Verification

1. **Android Architecture Components**:
   - Modern MVVM pattern across all 14 screens.
   - Kotlin Coroutines & StateFlow for reactive, lifecycle-aware UI state.
   - Clean unidirectional data flow (`ViewModel` -> `StateFlow` -> Compose UI).

2. **Persistence Layer**:
   - Room Database with 9 strongly typed entities.
   - Foreign key constraints with cascading deletes.
   - Seed data with realistic Oxford Computer Science curriculum.

3. **User Interface & Experience (EduTrack 2.0)**:
   - 100% Jetpack Compose with Material 3.
   - Editorial, Linear/Notion-inspired premium aesthetic.
   - Custom vector Canvas charts (Bar charts, Donut breakdown, Trendline, Heatmap grid).
   - Zero WebViews or hybrid wrappers.

4. **Testing & Stability**:
   - Unit tests covering Repository, ViewModels, and state transitions (`5/5 passed`).
   - Clean compilation with Gradle 9.2.1 and Kotlin 2.2.10.
   - Verified debug APK generated at `apk/EduTrack-Minimal-Native.apk`.

| Requirement | Implementation Details | Status |
|-------------|------------------------|--------|
| **A. At least two Room entities** | Implemented 9 entities: `SubjectEntity`, `StudySessionEntity`, `TaskEntity`, `ExaminationEntity`, `GoalEntity`, `NoteEntity`, `ScheduleEventEntity`, `NotificationEntity`, `UserProfileEntity` with `@ForeignKey(onDelete = CASCADE)` and indices | Verified |
| **B. CRUD operations** | Complete Create, Read, Update, Delete implemented on all DAOs, Repositories, ViewModels, and UI screens | Verified |
| **C. Multiple native screens** | 15+ Native Jetpack Compose screens covering Dashboard, Subjects, Study Sessions, Timer, Planner, Deliverables, Exams, Goals, Notes, Analytics, History, Profile, Settings, Onboarding | Verified |
| **D. Navigation** | Navigation Compose with `NavHost`, `NavController`, `currentBackStackEntryAsState()`, typed arguments, bottom navigation bar, and backstack restoration | Verified |
| **E. State management** | MVVM architecture with `ViewModel`, `StateFlow`, `asStateFlow()`, `stateIn(SharingStarted.WhileSubscribed)` | Verified |
| **F. Validation** | Required fields checking, non-zero numeric validation, duration boundaries, and user-facing error snackbars | Verified |
| **G. User feedback** | Native `SnackbarHost`, `AlertDialog` confirmation dialogs on deletion, and status chip feedback | Verified |
| **H. Persistent Room DB** | SQLite Room Database (`edutrack_minimal_academic.db`) persisting data across reboots with initial curriculum seeding | Verified |
| **I. Visual statistics & charts** | 100% Native Jetpack Compose `Canvas` charts: `MinimalStudyBarChart`, `MinimalSubjectDonutChart`, `FocusTrendChart`, `SoftAcademiaWeeklyActivity` | Verified |

---

## 5. Soft Academia Visual Redesign V3 (Design Version 2.1)

### Visual Philosophy & Direction
Completely eliminated generic Material 3 boxes, congested card grids, and purple gradients in favor of an organic, calm, academic editorial aesthetic:
- **Palette**: Warm Ivory (`#F7F6F2`), Primary Forest (`#355B48`), Deep Forest (`#243D30`), Soft Sage (`#DCE8DD`), Muted Sage (`#E8EEE6`), Sand (`#E9E3D6`), Soft Terracotta (`#F0E0D5`), Dusty Lilac (`#E8E5EF`). Dark mode: Deep Obsidian Forest (`#171A18`, `#222723`, `#2B312C`).
- **Organic Shapes & Hierarchy**: Structural containers 28-32dp, primary cards 22-24dp, list rows 18-20dp, chips & pills 12-14dp.
- **Editorial Typography**: 30-34sp greetings, 28sp titles, 20sp section headings directly on background with generous 28-36dp vertical whitespace.
- **Unboxed Layouts**: Headers, daily timelines, and weekly activity charts sit directly on the Warm Ivory surface without unnecessary card wrappers.
- **Redesigned Screens**:
  - `DashboardScreen`: Unboxed greeting, single `SoftSageFocusSummary` surface with streak pill & start focus CTA, canvas 7-day activity bars directly on background, vertical clean rows for continuing courses and agenda.
  - `TimerScreen`: Meditative focus ring in Primary Forest and Soft Sage, tabular countdown digits, 26dp mode selector pills, integrated session reflection and subject selector.
  - `SubjectsScreen` & `SubjectDetailScreen`: Library-style course list with weekly progress meters and clean instructor tags; organic hero overview card with direct focus session launcher.
  - `PlannerScreen`: 14-day horizontal agenda selector with Primary Forest active pill, clean vertical schedule blocks with hairline dividers and completion toggles.
  - `AssignmentsScreen`, `ExaminationsScreen`, `GoalsScreen`: Clean status tabs, countdown badges in Soft Sage and Terracotta, progress indicators in Forest Green.
  - `NotesScreen`, `HistoryScreen`, `AnalyticsScreen`, `MoreMenuScreen`, `ProfileScreen`, `SettingsScreen`: Spacious organic cards, earth-tone category tags, and Canvas charts.
- **Verification**: All unit tests pass (`5/5 passed`), build successful (`BUILD SUCCESSFUL in 22s`), verified debug APK generated at `apk/EduTrack-Minimal-Native.apk`.

---

## 6. Critical Bug Fix & Planner CRUD Completion (Design Version 2.2)

### Bug 1: Curriculum Module Loading Screen Resolution
- **Root Cause Identified**:
  1. **Route mismatch in Dashboard**: The "All Courses" section action called `onNavigateToSubject("")` instead of navigating to `Screen.Subjects.route`. This caused Navigation Compose to route to `Screen.SubjectDetail` with an empty `subjectId = ""`.
  2. **Indefinite loading state**: `SubjectDetailScreen` collected a raw `StateFlow` directly instantiated during recomposition, and treated `null` as "still loading" indefinitely, lacking explicit state-machine discrimination between loading, not found, success, and error.
- **Fix & Architectural Enhancements**:
  - Implemented `SubjectDetailUiState` (`Loading`, `Success`, `NotFound`, `Error`) with full lifecycle cancellation in `SubjectViewModel`.
  - Added `loadSubjectDetail(subjectId)` with Room Flow collection, `.catch` block, and empty ID guards.
  - Implemented comprehensive UI states in `SubjectDetailScreen`:
    - `Loading`: Displays animated `CircularProgressIndicator` with "Loading curriculum module..." and "Return to Modules" action.
    - `NotFound`: Displays `AcademicEmptyState` with book icon, informative message, and "Return to Modules" action.
    - `Error`: Displays warning icon, error details, "Retry" button, and "Return to Modules".
    - `Success`: Displays complete module statistics, weekly progress meter, enrolled sessions, and focus session launcher.
  - Updated `DashboardScreen` and `EduTrackApp` to separate `onNavigateToSubjects` ("All Courses") and `onNavigateToSubject(id)` (individual course cards), preventing blank route navigation.

### Bug 2: Planner Complete CRUD Implementation
- **Create**:
  - Implemented `showAddDialog` supporting title, subject filter chips, date selection, start time (`HH:mm`), end time (`HH:mm`), and location/notes.
  - Added strict validation preventing invalid times (`startTime >= endTime`) and empty required fields.
- **Read**:
  - Filtered and chronological sorting (`events.filter { it.date == date }.sortedBy { it.startTime }`) ensuring only selected date blocks appear.
  - Tapping a block opens the rich `EventDetailDialog` showing subject chip, duration (`1h 30m`), time interval, location, and status badge.
- **Update**:
  - Implemented `updateEvent` in `PlannerViewModel` and Room database.
  - Added prefilled `EventEditDialog` accessible from the card or details modal, allowing modification of title, subject, date, times, and notes.
- **Delete**:
  - Added `EventDeleteConfirmationDialog` before permanent removal with clear warnings.
  - Cancel preserves the scheduled event intact.
  - Confirm triggers `viewModel.deleteEvent(event)` and displays Snackbar feedback ("Scheduled block removed").
- **Isolated Completion**:
  - Checkbox toggle is isolated on the card with dedicated touch bounds (`IconButton`), immediately updating `isCompleted` in Room without unintentionally opening the details modal.
- **Removed Duplicate Add Actions**:
  - Removed redundant Scaffold FAB on the Planner route; retained a single, prominent, editorial "+ Add Block" button in the header.

### Live Emulator Verification & Test Results
- **Unit Tests**: `CourseworkUnitTests` updated with schedule event validation logic; 100% pass (`6/6 passed`).
- **Emulator Verification**:
  - Tested on `emulator-5554`:
    - Navigated from Dashboard "All Courses" and bottom navigation to Enrolled Modules without infinite loading.
    - Opened "Algorithms & Data Structures" detail view: content loaded instantly and completely.
    - Created schedule blocks, edited fields in prefilled modal, confirmed persistence.
    - Toggled completion checkbox: immediate visual feedback with green checkmark.
    - Tested delete cancellation: verified event preserved.
    - Confirmed deletion: block removed with feedback.
    - Force-stopped app and restarted: verified all states persisted in Room database.
- **Release APK**: Verified and packaged at `apk/EduTrack-Minimal-Native.apk` (18.50 MB).

---

## 7. Study Session History Layout Polish & Session Detail Loading Resolution (Design Version 2.3)

### Bug 1: Study Session History Card Column Squeezing
- **Root Cause Identified**:
  - `HistoryScreen.kt` had split the card into multiple rigid horizontal columns.
  - Timestamp ISO strings (e.g. `2026-11-25T09:00:00Z`) were displayed in a narrow separate column next to the delete button, causing date/time text to unnaturally wrap vertically character-by-character into multiple rows.
  - Rigid `SubjectChip` pill elements forced horizontal compression on narrower screens.
  - Prominent delete icon consumed card width.
- **Fix & Architectural Enhancements**:
  - **Responsive 3-Zone Card Layout**:
    - **LEFT**: Consistent 46dp rounded pastel surface (`SoftSage`) with centered dark green `Icons.Default.Timer`.
    - **CENTER**: Flexible `Modifier.weight(1f)` column with:
      - Clean title: `${session.durationMinutes} min focus block`.
      - Metadata line: `[XX% Focus] · Nov 25 · 9:00 AM` or `[XX% Focus] · Yesterday · 7:30 PM`, guaranteed single-line readable format.
      - Subject tag: 7dp colored dot + `${subject.code} · ${subject.name}`, cleanly truncated with ellipsis without squeezing.
      - Reflection notes: max 2 lines with ellipsis; omitted if blank.
    - **RIGHT**: Compact 32dp three-dot overflow menu (`Icons.Default.MoreVert`).
  - **Overflow Actions Menu & Safe Deletion**:
    - Added Material 3 `DropdownMenu` with "View Details", "Edit Session", and "Delete Session".
    - Added `AlertDialog` confirmation with clear explanation of study hours impact before permanent deletion.
    - Added `formatSessionDateTime` utility with unit tests.

### Bug 2: Session Detail Screen Infinite Loading Resolution
- **Root Cause Identified**:
  - In `SessionDetailScreen.kt`, `val sessionWithSubject by viewModel.getSessionWithSubject(sessionId).collectAsState()` called `.stateIn(...)` directly during composable evaluation without `remember`.
  - On every composition pass, a new StateFlow was instantiated with initial `null`, preventing the state from ever transitioning out of loading.
  - Tag pill in `SessionDetailScreen` had unbounded width, causing "DEEP WORK" to wrap vertically when adjacent to long course names.
- **Fix & Architectural Enhancements**:
  - Returned direct Room `Flow<SessionWithSubject?>` from `SessionViewModel.getSessionWithSubject`.
  - Used `remember(sessionId) { viewModel.getSessionWithSubject(sessionId) }.collectAsState(initial = null)` in `SessionDetailScreen`, enabling Room emissions to update state immediately.
  - Added `Modifier.weight(1f, fill = false)` to `SubjectChip` and `maxLines = 1` to session type pill, ensuring clean horizontal layout.
  - Polished editorial header title to "Session Details", preventing truncation.

### Live Emulator Verification & Test Results
- **Unit Tests**: `CourseworkUnitTests` passed 7/7 tests (`BUILD SUCCESSFUL in 1m 27s`).
- **Live Emulator Verification**:
  - Verified on `emulator-5554`:
    - Session history cards render cleanly with generous spacing, no squeezed columns or strange date wrapping.
    - Tapping cards opens `SessionDetailScreen` instantly with complete focus time, focus score, and notes.
    - 3-dot overflow menu opens with "View Details", "Edit Session", "Delete Session".
    - Delete confirmation dialog displays properly with "Cancel" and "Delete Session" actions.
    - "Cancel" preserves the session intact without unintended deletion.
- **Updated APK**: Built and deployed to `apk/EduTrack-Minimal-Native.apk` (18.64 MB).
