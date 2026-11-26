import { 
  UserProfile, 
  Subject, 
  Task, 
  Examination, 
  Note, 
  Goal, 
  StudySession, 
  ScheduleEvent, 
  NotificationItem, 
  UserSettings 
} from '../../types';

export const initialUserProfile: UserProfile = {
  id: 'user-default-1',
  name: 'Julian Vance',
  email: 'julian.vance@university.edu',
  university: 'Oxford University / Institute of Technology',
  program: 'B.Sc. Computer Science & Mathematics',
  academicYear: 'Year 2 (Sophomore)',
  semester: 'Fall Semester 2026',
  preferredStudyHours: 25,
  dailyTargetMinutes: 180, // 3 hours daily
  avatar: 'JV',
  isOnboarded: true,
  createdAt: '2026-09-01T08:00:00Z',
};

export const initialSubjects: Subject[] = [
  {
    id: 'subj-1',
    name: 'Algorithms & Data Structures',
    code: 'CS 201',
    color: '#6477D9', // primary academic indigo
    description: 'Graph algorithms, dynamic programming, asymptotic analysis, and advanced tree data structures.',
    semester: 'Fall 2026',
    instructor: 'Prof. Alistair Sterling',
    weeklyTargetHours: 7,
    currentWeeklyMinutes: 340,
    totalStudyMinutes: 2160,
    completedAssignmentsCount: 4,
    attendanceRate: 96,
    createdAt: '2026-09-01T08:00:00Z',
  },
  {
    id: 'subj-2',
    name: 'Linear Algebra & Differential Calculus',
    code: 'MATH 210',
    color: '#16A37B', // academic sage
    description: 'Vector spaces, eigenvalues, matrix decompositions, and ordinary differential equations.',
    semester: 'Fall 2026',
    instructor: 'Dr. Evelyn Montgomery',
    weeklyTargetHours: 6,
    currentWeeklyMinutes: 280,
    totalStudyMinutes: 1820,
    completedAssignmentsCount: 5,
    attendanceRate: 92,
    createdAt: '2026-09-01T08:00:00Z',
  },
  {
    id: 'subj-3',
    name: 'Computer Systems & Architecture',
    code: 'CS 240',
    color: '#E6A23C', // academic amber
    description: 'Assembly language, memory hierarchies, pipelining, and processor micro-architectures.',
    semester: 'Fall 2026',
    instructor: 'Prof. Marcus Chen',
    weeklyTargetHours: 5,
    currentWeeklyMinutes: 210,
    totalStudyMinutes: 1440,
    completedAssignmentsCount: 3,
    attendanceRate: 88,
    createdAt: '2026-09-01T08:00:00Z',
  },
  {
    id: 'subj-4',
    name: 'Database Management Systems',
    code: 'CS 320',
    color: '#8B5CF6', // violet
    description: 'Relational algebra, SQL query optimization, ACID transactions, and index structures.',
    semester: 'Fall 2026',
    instructor: 'Dr. Sarah Jenkins',
    weeklyTargetHours: 5,
    currentWeeklyMinutes: 190,
    totalStudyMinutes: 1280,
    completedAssignmentsCount: 3,
    attendanceRate: 94,
    createdAt: '2026-09-01T08:00:00Z',
  },
  {
    id: 'subj-5',
    name: 'Technical Writing & Academic Ethics',
    code: 'ENG 190',
    color: '#EC4899', // rose
    description: 'Scientific paper authorship, peer review ethics, and research literature synthesis.',
    semester: 'Fall 2026',
    instructor: 'Dr. Clara Beauchamp',
    weeklyTargetHours: 3,
    currentWeeklyMinutes: 120,
    totalStudyMinutes: 820,
    completedAssignmentsCount: 2,
    attendanceRate: 100,
    createdAt: '2026-09-01T08:00:00Z',
  },
];

export const initialTasks: Task[] = [
  {
    id: 'task-1',
    title: 'Implement Dijkstra & Bellman-Ford Shortest Paths Benchmark',
    description: 'Code adjacency-list graph representations and benchmark performance on random 10k-node graphs.',
    subjectId: 'subj-1',
    category: 'assignment',
    priority: 'high',
    status: 'in_progress',
    dueDate: '2026-11-25',
    dueTime: '23:59',
    estimatedMinutes: 240,
    createdAt: '2026-11-18T10:00:00Z',
  },
  {
    id: 'task-2',
    title: 'Complete Problem Set 7: Singular Value Decomposition (SVD)',
    description: 'Problems 1 through 6 on principal components, low-rank matrix approximation, and spectral radius.',
    subjectId: 'subj-2',
    category: 'homework',
    priority: 'high',
    status: 'not_started',
    dueDate: '2026-11-24',
    dueTime: '17:00',
    estimatedMinutes: 180,
    createdAt: '2026-11-19T14:30:00Z',
  },
  {
    id: 'task-3',
    title: 'Design 4-Way Set-Associative Cache Controller Simulation',
    description: 'Build Verilog or C++ cache simulator with LRU replacement policy and write-back buffer.',
    subjectId: 'subj-3',
    category: 'project',
    priority: 'medium',
    status: 'in_progress',
    dueDate: '2026-11-28',
    dueTime: '23:59',
    estimatedMinutes: 300,
    createdAt: '2026-11-15T09:00:00Z',
  },
  {
    id: 'task-4',
    title: 'Draft B+ Tree Node Splitting & Merging Unit Tests',
    description: 'Write comprehensive test suite for leaf and interior node rebalancing in SQLite clone project.',
    subjectId: 'subj-4',
    category: 'assignment',
    priority: 'medium',
    status: 'completed',
    dueDate: '2026-11-20',
    dueTime: '18:00',
    estimatedMinutes: 150,
    completedAt: '2026-11-20T16:45:00Z',
    createdAt: '2026-11-14T11:00:00Z',
  },
  {
    id: 'task-5',
    title: 'Peer Review: Ethical Implications of Autonomous Medical Diagnostics',
    description: 'Write 800-word critique analyzing algorithmic accountability and patient consent precedents.',
    subjectId: 'subj-5',
    category: 'reading',
    priority: 'low',
    status: 'completed',
    dueDate: '2026-11-19',
    dueTime: '12:00',
    estimatedMinutes: 90,
    completedAt: '2026-11-18T20:10:00Z',
    createdAt: '2026-11-12T15:00:00Z',
  },
  {
    id: 'task-6',
    title: 'Review Chapter 5: Pipelining Hazards & Forwarding Units',
    description: 'Study data hazards, branch prediction stalls, and out-of-order execution concepts for midterm.',
    subjectId: 'subj-3',
    category: 'revision',
    priority: 'high',
    status: 'not_started',
    dueDate: '2026-11-26',
    dueTime: '20:00',
    examId: 'exam-2',
    estimatedMinutes: 120,
    createdAt: '2026-11-20T08:00:00Z',
  },
  {
    id: 'task-7',
    title: 'Prepare Midterm Flashcards: Graph Algorithms & Red-Black Trees',
    description: 'Generate active recall deck covering rotation invariants, Dijkstra edge-relaxation proofs, and amortized bounds.',
    subjectId: 'subj-1',
    category: 'exam_prep',
    priority: 'high',
    status: 'in_progress',
    dueDate: '2026-11-29',
    dueTime: '21:00',
    examId: 'exam-1',
    estimatedMinutes: 180,
    createdAt: '2026-11-20T10:00:00Z',
  },
];

export const initialExaminations: Examination[] = [
  {
    id: 'exam-1',
    title: 'Algorithms & Complexity Midterm Examination',
    subjectId: 'subj-1',
    examDate: '2026-12-02',
    examTime: '09:30',
    location: 'Turing Hall, Room 301',
    examType: 'midterm',
    preparationStatus: 'in_progress',
    targetGrade: 'A (94%)',
    notes: 'Covers Lectures 1–14: Graph algorithms, Greedy proofs, Dynamic programming, Master Theorem.',
    linkedTaskIds: ['task-7'],
    createdAt: '2026-10-15T09:00:00Z',
  },
  {
    id: 'exam-2',
    title: 'Computer Systems Architecture Midterm',
    subjectId: 'subj-3',
    examDate: '2026-12-05',
    examTime: '14:00',
    location: 'Lovelace Auditorium, East Wing',
    examType: 'midterm',
    preparationStatus: 'not_prepared',
    targetGrade: 'A- (90%)',
    notes: 'Closed book. Formulas provided for cache hit rates and Amdahl’s law calculations.',
    linkedTaskIds: ['task-6'],
    createdAt: '2026-10-15T09:00:00Z',
  },
  {
    id: 'exam-3',
    title: 'Linear Algebra & ODE Final Examination',
    subjectId: 'subj-2',
    examDate: '2026-12-16',
    examTime: '10:00',
    location: 'Newton Mathematical Institute, Room 104',
    examType: 'final',
    preparationStatus: 'in_progress',
    targetGrade: 'A+ (98%)',
    notes: 'Comprehensive 3-hour examination covering orthogonal projections, SVD, Jordan canonical form, and systems of linear ODEs.',
    linkedTaskIds: [],
    createdAt: '2026-10-15T09:00:00Z',
  },
];

export const initialNotes: Note[] = [
  {
    id: 'note-1',
    title: 'Graph Traversal & Dijkstra Proof Invariants',
    content: `# Dijkstra's Shortest Path Algorithm

## Invariant
At each step, when a vertex $u$ is extracted from the priority queue $Q$, the distance label $d[u]$ is guaranteed to be optimal ($d[u] = \\delta(s, u)$) assuming non-negative edge weights.

### Key Observations:
- **Time Complexity:** $O((V + E) \\log V)$ using a binary min-heap.
- **Fibonacci Heap:** Reduces amortized decrease-key to $O(1)$, yielding $O(E + V \\log V)$.
- **Negative Weights:** Fails on negative edge cycles. Must use Bellman-Ford or Johnson's algorithm instead.

### Review Checklist:
- [x] Prove triangle inequality property
- [x] Verify priority queue update edge cases
- [ ] Implement Fibonacci heap alternative for sparse graphs`,
    subjectId: 'subj-1',
    tags: ['Algorithms', 'Graphs', 'Proofs', 'Midterm'],
    isPinned: true,
    isFavourite: true,
    isArchived: false,
    createdAt: '2026-11-16T14:00:00Z',
    updatedAt: '2026-11-20T17:30:00Z',
  },
  {
    id: 'note-2',
    title: 'Singular Value Decomposition (SVD) Summary & Geometry',
    content: `# Singular Value Decomposition (SVD)

Every real matrix $A \\in \\mathbb{R}^{m \\times n}$ can be factored as:
$$A = U \\Sigma V^T$$

Where:
1. $U \\in \\mathbb{R}^{m \\times m}$ is orthogonal (left singular vectors).
2. $\\Sigma \\in \\mathbb{R}^{m \\times n}$ contains non-negative singular values on its diagonal in descending order.
3. $V \\in \\mathbb{R}^{n \\times n}$ is orthogonal (right singular vectors).

### Geometric Meaning
- $V^T$ represents a rotation/reflection in the domain $\\mathbb{R}^n$.
- $\\Sigma$ represents axis-aligned scaling along coordinate axes.
- $U$ represents a rotation/reflection into the codomain $\\mathbb{R}^m$.

### Applications:
- Low-rank matrix approximations (Eckart-Young Theorem)
- Principal Component Analysis (PCA)
- Pseudo-inverse computation: $A^+ = V \\Sigma^+ U^T$`,
    subjectId: 'subj-2',
    tags: ['Math', 'LinearAlgebra', 'Geometry', 'SVD'],
    isPinned: true,
    isFavourite: true,
    isArchived: false,
    createdAt: '2026-11-17T11:20:00Z',
    updatedAt: '2026-11-19T09:15:00Z',
  },
  {
    id: 'note-3',
    title: 'Memory Hierarchy & Cache Mapping Strategies',
    content: `# Cache Architecture & Replacement Policies

## Mapping Strategies
1. **Direct-Mapped:** Block mapped to exactly one line ($index = block \\pmod{num\\_lines}$). High collision misses.
2. **Fully Associative:** Block can go to any line. Zero conflict misses, but requires expensive parallel comparator hardware.
3. **Set-Associative ($N$-way):** Block maps to a specific set ($set = block \\pmod{num\\_sets}$), containing $N$ cache lines. Good compromise.

## 3C Miss Model
- **Compulsory:** First reference to a memory block.
- **Capacity:** Cache is too small to contain all working set blocks.
- **Conflict:** Multiple active memory blocks map to the same set.`,
    subjectId: 'subj-3',
    tags: ['Architecture', 'Hardware', 'Cache'],
    isPinned: false,
    isFavourite: false,
    isArchived: false,
    createdAt: '2026-11-10T16:00:00Z',
    updatedAt: '2026-11-14T18:40:00Z',
  },
];

export const initialGoals: Goal[] = [
  {
    id: 'goal-1',
    title: 'Achieve 25 Hours of Focused Study This Week',
    type: 'weekly_study',
    targetValue: 25,
    currentValue: 19.5,
    unit: 'hours',
    targetDate: '2026-11-28',
    isCompleted: false,
    createdAt: '2026-11-15T08:00:00Z',
  },
  {
    id: 'goal-2',
    title: 'Complete 15 Core Problem Sets & Programming Assignments',
    type: 'assignment_completion',
    targetValue: 15,
    currentValue: 12,
    unit: 'assignments',
    targetDate: '2026-12-05',
    isCompleted: false,
    createdAt: '2026-11-01T08:00:00Z',
  },
  {
    id: 'goal-3',
    title: 'Dedicate 10 Hours to Algorithms Midterm Preparation',
    type: 'exam_prep',
    subjectId: 'subj-1',
    targetValue: 10,
    currentValue: 7.5,
    unit: 'hours',
    targetDate: '2026-12-01',
    isCompleted: false,
    createdAt: '2026-11-16T09:00:00Z',
  },
  {
    id: 'goal-4',
    title: 'Maintain a 14-Day Consecutive Daily Study Streak',
    type: 'daily_study',
    targetValue: 14,
    currentValue: 14,
    unit: 'days',
    targetDate: '2026-11-22',
    isCompleted: true,
    completedAt: '2026-11-22T19:00:00Z',
    createdAt: '2026-11-08T08:00:00Z',
  },
];

// Past 14 days of realistic study sessions for authentic analytics
export const initialStudySessions: StudySession[] = [
  {
    id: 'sess-1',
    subjectId: 'subj-1',
    durationMinutes: 90,
    sessionType: 'deep_work',
    notes: 'Proved correctness of Dijkstra relaxation loop invariants and coded test graph.',
    isManual: false,
    startTime: '2026-11-21T10:00:00Z',
    endTime: '2026-11-21T11:30:00Z',
    createdAt: '2026-11-21T11:30:00Z',
  },
  {
    id: 'sess-2',
    subjectId: 'subj-2',
    durationMinutes: 75,
    sessionType: 'pomodoro',
    notes: 'Calculated 3x3 SVD decompositions and plotted singular vectors in NumPy.',
    isManual: false,
    startTime: '2026-11-21T14:00:00Z',
    endTime: '2026-11-21T15:15:00Z',
    createdAt: '2026-11-21T15:15:00Z',
  },
  {
    id: 'sess-3',
    subjectId: 'subj-3',
    durationMinutes: 60,
    sessionType: 'revision',
    notes: 'Reviewed memory hierarchy formulas and cache miss taxonomy.',
    isManual: false,
    startTime: '2026-11-20T09:30:00Z',
    endTime: '2026-11-20T10:30:00Z',
    createdAt: '2026-11-20T10:30:00Z',
  },
  {
    id: 'sess-4',
    subjectId: 'subj-4',
    durationMinutes: 80,
    sessionType: 'deep_work',
    notes: 'Drafted B+ Tree leaf insertion and split logic unit tests.',
    isManual: false,
    startTime: '2026-11-20T15:00:00Z',
    endTime: '2026-11-20T16:20:00Z',
    createdAt: '2026-11-20T16:20:00Z',
  },
  {
    id: 'sess-5',
    subjectId: 'subj-1',
    durationMinutes: 100,
    sessionType: 'deep_work',
    notes: 'Graph cycle detection and topological sort implementation in C++.',
    isManual: false,
    startTime: '2026-11-19T10:00:00Z',
    endTime: '2026-11-19T11:40:00Z',
    createdAt: '2026-11-19T11:40:00Z',
  },
  {
    id: 'sess-6',
    subjectId: 'subj-2',
    durationMinutes: 90,
    sessionType: 'pomodoro',
    notes: 'Second-order linear differential equations with constant coefficients.',
    isManual: false,
    startTime: '2026-11-18T13:30:00Z',
    endTime: '2026-11-18T15:00:00Z',
    createdAt: '2026-11-18T15:00:00Z',
  },
  {
    id: 'sess-7',
    subjectId: 'subj-5',
    durationMinutes: 60,
    sessionType: 'reading',
    notes: 'Read biomedical AI literature regarding ethical guidelines in diagnostics.',
    isManual: true,
    startTime: '2026-11-18T16:00:00Z',
    endTime: '2026-11-18T17:00:00Z',
    createdAt: '2026-11-18T17:00:00Z',
  },
  {
    id: 'sess-8',
    subjectId: 'subj-3',
    durationMinutes: 70,
    sessionType: 'assignment',
    notes: 'Simulated direct-mapped cache miss ratios with varying block sizes.',
    isManual: false,
    startTime: '2026-11-17T11:00:00Z',
    endTime: '2026-11-17T12:10:00Z',
    createdAt: '2026-11-17T12:10:00Z',
  },
  {
    id: 'sess-9',
    subjectId: 'subj-1',
    durationMinutes: 90,
    sessionType: 'pomodoro',
    notes: 'Binary search tree amortized analysis and 2-3 tree rotations.',
    isManual: false,
    startTime: '2026-11-16T14:00:00Z',
    endTime: '2026-11-16T15:30:00Z',
    createdAt: '2026-11-16T15:30:00Z',
  },
  {
    id: 'sess-10',
    subjectId: 'subj-4',
    durationMinutes: 50,
    sessionType: 'revision',
    notes: 'SQL query plan inspection using EXPLAIN ANALYZE.',
    isManual: true,
    startTime: '2026-11-15T16:30:00Z',
    endTime: '2026-11-15T17:20:00Z',
    createdAt: '2026-11-15T17:20:00Z',
  },
];

export const initialScheduleEvents: ScheduleEvent[] = [
  {
    id: 'sched-1',
    title: 'Algorithms Lecture & Problem Session',
    subjectId: 'subj-1',
    date: '2026-11-23',
    startTime: '09:00',
    endTime: '10:30',
    description: 'Lecture 16: Minimum Spanning Trees (Prim vs Kruskal).',
    reminder: true,
    isCompleted: false,
    createdAt: '2026-11-15T08:00:00Z',
  },
  {
    id: 'sched-2',
    title: 'Linear Algebra Recitation & Tutorial',
    subjectId: 'subj-2',
    date: '2026-11-23',
    startTime: '11:00',
    endTime: '12:30',
    description: 'Review eigenvalues and Jordan blocks with TA.',
    reminder: true,
    isCompleted: false,
    createdAt: '2026-11-15T08:00:00Z',
  },
  {
    id: 'sched-3',
    title: 'Computer Architecture Laboratory',
    subjectId: 'subj-3',
    date: '2026-11-24',
    startTime: '14:00',
    endTime: '16:00',
    description: 'Hardware lab: logic analyzer measurements on FPGA cache board.',
    reminder: true,
    isCompleted: false,
    createdAt: '2026-11-15T08:00:00Z',
  },
  {
    id: 'sched-4',
    title: 'Database Query Optimization Workshop',
    subjectId: 'subj-4',
    date: '2026-11-25',
    startTime: '10:00',
    endTime: '12:00',
    description: 'Hands-on query tuning on 500,000-row benchmark dataset.',
    reminder: true,
    isCompleted: false,
    createdAt: '2026-11-15T08:00:00Z',
  },
  {
    id: 'sched-5',
    title: 'Deep Focus: Graph Algorithm Coding Block',
    subjectId: 'subj-1',
    date: '2026-11-25',
    startTime: '14:30',
    endTime: '17:00',
    description: 'Complete Dijkstra benchmark assignment submission.',
    reminder: true,
    isCompleted: false,
    createdAt: '2026-11-15T08:00:00Z',
  },
];

export const initialNotifications: NotificationItem[] = [
  {
    id: 'notif-1',
    title: 'Upcoming Deadline in 3 Days',
    message: 'Implement Dijkstra & Bellman-Ford Shortest Paths Benchmark is due on Nov 25 at 23:59.',
    type: 'deadline',
    read: false,
    timestamp: '2026-11-22T08:00:00Z',
    linkTo: '/assignments',
  },
  {
    id: 'notif-2',
    title: 'Examination Approaching',
    message: 'Algorithms & Complexity Midterm is scheduled in 10 days (Dec 02 at 09:30).',
    type: 'exam',
    read: false,
    timestamp: '2026-11-21T18:00:00Z',
    linkTo: '/examinations',
  },
  {
    id: 'notif-3',
    title: '14-Day Study Streak Unlocked! 🎉',
    message: 'Incredible consistency! You have logged focused academic study every day for the past 14 days.',
    type: 'streak',
    read: true,
    timestamp: '2026-11-21T21:00:00Z',
    linkTo: '/analytics',
  },
  {
    id: 'notif-4',
    title: 'Goal Target Completed',
    message: 'You concluded the "14-Day Consecutive Daily Study Streak" goal milestone.',
    type: 'goal',
    read: true,
    timestamp: '2026-11-20T19:00:00Z',
    linkTo: '/goals',
  },
];

export const initialUserSettings: UserSettings = {
  theme: 'light',
  pomodoroFocusMinutes: 25,
  pomodoroShortBreakMinutes: 5,
  pomodoroLongBreakMinutes: 15,
  longBreakInterval: 4,
  weekStartDay: 'monday',
  timeFormat: '12h',
  soundEnabled: true,
  browserNotifications: false,
  dailyStudyTargetMinutes: 180,
};
