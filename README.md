# EduTrack – Minimal Academic Edition (Master Suite)

> **Member 2 Submission** · EduTrack UI Architecture Competition
> A production-grade, distraction-free university academic workspace and study productivity companion.

---

## 1. Product Vision & Design Philosophy

**EduTrack: Minimal Academic Edition** is engineered for university and college scholars who require a rigorous, uncluttered, and low-cognitive-load environment to organize their academic lives.

### Design System Tokens
- **Backgrounds**: Soft paper ivory (`#F8FAFC`), crisp card surfaces (`#FFFFFF`), muted elevation wells (`#F1F4F8`).
- **Typography**: Inter & Geist typographic hierarchy with tabular numerals for countdowns and timer dial readings.
- **Color Accents**:
  - Primary Academic Accent: `#6477D9`
  - Success / Completion: `#16A37B`
  - Warning / Looming Deadline: `#E6A23C`
  - Critical / Overdue: `#E06464`
  - Subtle Dividing Borders: `#E8ECF2`
- **Component Geometry**: 10–14px rounded corners (`rounded-xl`), hairline 1px borders, subtle soft-diffused elevation shadows (`0 1px 3px rgba(0,0,0,0.04)`), and zero garish neon effects.

---

## 2. Implemented Modules (14 Complete Functional Modules)

### Module 1: Authentication & Demo Personas
- **Full Auth Flow**: Login, Registration, and Forgot Password recovery forms with password visibility toggling, input validation, and protected route guards.
- **One-Click Scholar Personas**: Instant evaluation with Julian Vance (B.Sc. CS & Math), Elena Rostova (M.Sc. AI), or Marcus Sterling (Pre-Med Biology).
- **Persistent Session State**: Synchronized with browser storage.

### Module 2: Onboarding Wizard
- **3-Step Guided Setup**: Collects degree program, university, academic year, semester, weekly study targets, daily focus minutes, and initial subject list.
- **Automatic Generation**: Immediately populates personalized dashboard and schedule upon completion.

### Module 3: Academic Command Dashboard
- **Dynamic Greeting**: Time-aware greeting ("Good morning / afternoon / evening") with live academic date.
- **Real-Time KPI Cards**: Today's study time, weekly study duration vs quota, pending deliverables, upcoming examination count, completed tasks, and streak counter.
- **Chronological Today Schedule**: Timeline of scheduled seminars, lectures, and revision blocks.
- **Quick Continue Studying**: Recently accessed subjects with one-click Pomodoro launch.
- **Upcoming Deadlines**: Assignments and exam milestones with urgency badges.
- **Weekly Analytics**: Real Recharts bar chart showing daily logged hours vs daily target.
- **Daily Target Radial Progress**: Interactive circular progress toward daily focus goal.
- **Quick Action Bar**: Add task, add subject, schedule exam, or launch focus timer.

### Module 4: Subject Curriculum Management
- **Course Catalog**: Filter by semester, search by course code (e.g. `CS 201`) or instructor name.
- **Course CRUD**: Add, edit, and delete courses with customizable hex color tokens, weekly targets, instructor details, syllabus summaries, and attendance tracking.
- **Dedicated Subject Detail Page (`/subjects/:id`)**:
  - Tabbed interface: Overview & Syllabus, Assignments, Lecture Notes, and Study Session History.
  - Direct timer invocation pre-bound to the subject.

### Module 5: Comprehensive Study Planner & Calendar
- **Triple-View Switcher**: Day View, Week Grid View, and Monthly Overview.
- **Schedule Event CRUD**: Title, subject binding, date, start/end time, location, and reminder toggle.
- **Conflict & Overlap Detection**: Algorithmic warning when events overlap in time.
- **Direct Completion Toggling**: Mark scheduled study blocks completed with instantaneous persistence.

### Module 6: Precision Study Timer (Pomodoro + Custom)
- **Zero-Drift Architecture**: Timestamp-differential engine (`startTimestamp` + `accumulatedSeconds`) that preserves exact elapsed time even across tab switching, page reloads, and route navigation.
- **Interval Modes**: 25m Focus, 5m Short Break, 15m Long Break, and Custom Duration modal.
- **Subject & Description Tagging**: Bind study sessions to specific subjects with topics covered.
- **Audio Synthesis**: Integrated Web Audio API chime sounds on timer completion (requires zero external audio assets).
- **Manual Session Logging**: Modal allowing students to log offline library or lecture study hours.

### Module 7: Deliverables & Assignments Manager
- **3 Dynamic Views**:
  - **List View**: Dense scholarly list with subject badges, priority chips, due date countdowns, and quick completion toggles.
  - **Kanban Board**: Drag-and-drop styled 3-column workflow (*To Do*, *In Progress*, *Completed*).
  - **Calendar View**: Chronological timeline of upcoming academic deadlines.
- **Multi-parameter Filtering**: Filter by Subject, Priority (High, Medium, Low), Category (Assignment, Revision, Homework, Reading, Project, Exam Prep), and Status.

### Module 8: Examination & Assessment Manager
- **Exam Countdown**: Automatic real-time day countdown calculation ("In 5 days", "Today!", "Tomorrow", "Completed").
- **Preparation Readiness**: Visual status badges (*Not Started*, *Review in Progress*, *Exam Ready*).
- **Automated Revision Scheduler**: Generates linked revision tasks into the student's task schedule prior to test dates.
- **Full CRUD**: Date, time, hall/room location, exam type (Midterm, Final, Quiz, Practical, Presentation), and target grade.

### Module 9: Academic Notes & Knowledge Repository
- **Markdown Toolbar**: Instant insertion of Bold, Italic, H1, H2, Bullet Lists, Checklists, and Code Blocks.
- **Categorization**: Subject bindings and multi-tag system (`#algorithms`, `#exam`, `#formulas`).
- **Organization Filters**: All Notes, Pinned to Top, Favourites, and Archive.
- **Full-Text Search**: Live substring search across titles, tags, and note content.

### Module 10: Academic Goals & Targets
- **Target Tracking**: Daily study, weekly study, monthly milestones, subject quotas, and assignment counts.
- **Progress Visualization**: Linear animated progress bars with percentages and remaining day countdowns.
- **Quick Log Increments**: Direct `+1` and `+5` increment buttons on goal cards.
- **Active vs Completed Tabs**: Review current targets and archived achievements.

### Module 11: Data-Driven Analytics & Algorithmic Insights
- **Interactive Recharts Visualizations**:
  - Daily Study Hours vs Target (Responsive Bar Chart).
  - Subject Proportion Distribution (Responsive Donut Chart with legends).
  - Cumulative Focus Trend (Gradient Area Chart).
- **Timeframe Selector**: Last 7 Days, Last 30 Days, Last 90 Days, and All Time.
- **Rule-Based Academic Insights**: Real analysis calculating consistency score, subject focus imbalance warnings, and study pacing advice.

### Module 12: Study Session History & Export
- **Tabular Study Log**: Date, start/finish time, subject code, session mode (Timer vs Manual), duration, and notes.
- **Search & Filters**: Search notes and filter by subject and session type.
- **CSV Data Export**: One-click download of a standard spreadsheet CSV file.
- **Session Edit & Delete**: Full control over logged study blocks.

### Module 13: Activity & Notification Center
- **Alert Feed**: Deadline warnings, exam countdown notifications, study reminders, goal milestone alerts, and study streak notices.
- **Status Filtering**: All Alerts vs Unread filter with "Mark All as Read" action.
- **Direct Navigation Links**: One-click jumps directly to relevant deliverables and exams.

### Module 14: Profile & Settings Workspace
- **Student Profile**: Editable university, degree program, academic year, semester, and study targets.
- **Appearance**: One-click Light / High-Contrast Dark theme toggle with persistent CSS class updates.
- **Timer Settings**: Focus session duration, break durations, long break intervals, and audio chimes.
- **Schedule Format**: Week start day (Monday/Sunday) and 12-hour/24-hour time formatting.
- **Data Portability**:
  - Export Complete Backup (JSON).
  - Import Backup (with JSON schema validation and error feedback).
  - Reset to Demo Data (restores default Julian Vance dataset).
  - Clear All Data (with confirmation modal).

### System Extras
- **Global Search (`Ctrl+K` / `⌘K`)**: Instant modal search across courses, tasks, notes, and exams.
- **Global Quick Add Modal**: Top navigation button to instantly create tasks, courses, notes, or exams from anywhere in the application.
- **Toast Notifications**: Non-blocking toast feedback across all create, update, and delete actions.

---

## 3. Technology Stack

- **Framework**: React 18 with TypeScript (Strict Mode enabled).
- **Build Tool**: Vite 6.
- **Routing**: React Router v7 (`BrowserRouter`, protected route guards).
- **Styling**: Tailwind CSS with custom academic tokens, custom scrollbars, and keyframe animations.
- **Icons**: Lucide React (`lucide-react`).
- **Charts**: Recharts (`ResponsiveContainer`, `BarChart`, `PieChart`, `AreaChart`).
- **Form & Validation**: React Hook Form with Zod schemas.
- **Dates**: `date-fns` for calculations, countdowns, and formatting.
- **Storage**: Modular `StorageService` abstraction targeting `localStorage` with a clean data contract designed for effortless migration to Supabase/PostgreSQL.

---

## 4. Installation & Running

```bash
# 1. Navigate to the project directory
cd Member-2/edutrack-minimal-academic

# 2. Install dependencies
npm install

# 3. Start development server
npm run dev

# 4. Build for production (TypeScript check + Vite bundle)
npm run build

# 5. Preview production build
npm run preview
```

---

## 5. Architectural Verification & Quality Assurance

- **Zero TypeScript Errors**: Passed strict compilation (`npx tsc --noEmit`).
- **Production Build Verified**: Successfully bundled with exit code 0 (`vite build`).
- **Persistence Verification**: All CRUD operations write immediately to scoped local storage keys (`edutrack_minimal_academic_v2_*`).
- **Independence Guarantee**: Zero dependencies on `Member-1`, `Member-3`, or `Member-4`.
