import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import {
  BookOpen,
  Plus,
  Search,
  Edit3,
  Trash2,
  ArrowRight,
  Clock,
  CheckSquare,
  User,
  X
} from 'lucide-react';
import { useData } from '../../contexts/DataContext';
import { useToast } from '../../contexts/ToastContext';
import { Subject } from '../../types';
import { EmptyState } from '../../components/common/EmptyState';

export const SubjectsPage: React.FC = () => {
  const { subjects, addSubject, updateSubject, deleteSubject, tasks, studySessions } = useData();
  const { toast } = useToast();

  const [searchQuery, setSearchQuery] = useState('');
  const [semesterFilter, setSemesterFilter] = useState('all');
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingSubject, setEditingSubject] = useState<Subject | null>(null);

  // Form states
  const [name, setName] = useState('');
  const [code, setCode] = useState('');
  const [color, setColor] = useState('#6477D9');
  const [description, setDescription] = useState('');
  const [semester, setSemester] = useState('Fall 2026');
  const [instructor, setInstructor] = useState('');
  const [weeklyTargetHours, setWeeklyTargetHours] = useState(6);
  const [attendanceRate, setAttendanceRate] = useState(95);

  const semesters = Array.from(new Set(subjects.map(s => s.semester)));

  const handleOpenAdd = () => {
    setEditingSubject(null);
    setName('');
    setCode('');
    setColor('#6477D9');
    setDescription('');
    setSemester('Fall 2026');
    setInstructor('');
    setWeeklyTargetHours(6);
    setAttendanceRate(95);
    setIsModalOpen(true);
  };

  const handleOpenEdit = (s: Subject, e: React.MouseEvent) => {
    e.stopPropagation();
    e.preventDefault();
    setEditingSubject(s);
    setName(s.name);
    setCode(s.code);
    setColor(s.color);
    setDescription(s.description);
    setSemester(s.semester);
    setInstructor(s.instructor);
    setWeeklyTargetHours(s.weeklyTargetHours);
    setAttendanceRate(s.attendanceRate);
    setIsModalOpen(true);
  };

  const handleDelete = (id: string, e: React.MouseEvent) => {
    e.stopPropagation();
    e.preventDefault();
    if (confirm('Are you sure you want to remove this academic subject?')) {
      deleteSubject(id);
      toast.success('Subject removed');
    }
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!name.trim() || !code.trim()) return;

    if (editingSubject) {
      updateSubject(editingSubject.id, {
        name: name.trim(),
        code: code.trim(),
        color,
        description: description.trim(),
        semester,
        instructor: instructor.trim() || 'Staff',
        weeklyTargetHours: Number(weeklyTargetHours),
        attendanceRate: Number(attendanceRate),
      });
      toast.success('Subject updated');
    } else {
      addSubject({
        name: name.trim(),
        code: code.trim(),
        color,
        description: description.trim(),
        semester,
        instructor: instructor.trim() || 'Staff',
        weeklyTargetHours: Number(weeklyTargetHours),
        attendanceRate: Number(attendanceRate),
      });
      toast.success('Subject registered');
    }

    setIsModalOpen(false);
  };

  const filtered = subjects.filter(s => {
    const matchesSearch = s.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
      s.code.toLowerCase().includes(searchQuery.toLowerCase()) ||
      s.instructor.toLowerCase().includes(searchQuery.toLowerCase());
    const matchesSemester = semesterFilter === 'all' || s.semester === semesterFilter;
    return matchesSearch && matchesSemester;
  });

  return (
    <div className="space-y-6 animate-fadeIn">
      {/* Title & Actions Bar */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-2 border-b border-slate-border dark:border-slate-800">
        <div>
          <h1 className="text-2xl sm:text-3xl font-bold text-slate-primary dark:text-white tracking-tight">
            Academic Subjects
          </h1>
          <p className="text-xs sm:text-sm text-slate-secondary mt-0.5">
            Manage your courses, instructors, study hours, and academic targets
          </p>
        </div>

        <button
          onClick={handleOpenAdd}
          className="flex items-center gap-1.5 px-4 py-2 rounded-academic bg-primary text-white text-xs sm:text-sm font-semibold hover:bg-primary-hover shadow-sm transition-colors self-start sm:self-auto"
        >
          <Plus className="w-4 h-4" />
          <span>Add Subject</span>
        </button>
      </div>

      {/* Filter & Search Toolbar */}
      <div className="flex flex-col sm:flex-row items-center justify-between gap-3 bg-white dark:bg-slate-800 p-3 rounded-academic border border-slate-border dark:border-slate-700">
        <div className="relative w-full sm:w-72">
          <Search className="w-4 h-4 text-slate-secondary absolute left-3 top-2.5" />
          <input
            type="text"
            value={searchQuery}
            onChange={e => setSearchQuery(e.target.value)}
            placeholder="Filter by course title or code..."
            className="w-full pl-9 pr-3 py-1.5 text-xs rounded-academic border border-slate-border dark:border-slate-700 bg-slate-muted/50 dark:bg-slate-900 text-slate-primary dark:text-white focus:outline-none focus:border-primary"
          />
        </div>

        <div className="flex items-center gap-2 w-full sm:w-auto">
          <span className="text-xs text-slate-secondary whitespace-nowrap">Semester:</span>
          <select
            value={semesterFilter}
            onChange={e => setSemesterFilter(e.target.value)}
            className="px-2.5 py-1.5 text-xs rounded-academic border border-slate-border dark:border-slate-700 bg-white dark:bg-slate-900 text-slate-primary dark:text-white focus:outline-none focus:border-primary"
          >
            <option value="all">All Semesters</option>
            {semesters.map(sem => (
              <option key={sem} value={sem}>{sem}</option>
            ))}
          </select>
        </div>
      </div>

      {/* Subject Grid */}
      {filtered.length === 0 ? (
        <EmptyState
          icon={BookOpen}
          title="No Academic Subjects Found"
          description={searchQuery ? "No subjects match your current search criteria." : "Start by registering your enrolled semester courses."}
          actionLabel="Add Your First Subject"
          onAction={handleOpenAdd}
        />
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
          {filtered.map(sub => {
            const relatedTasks = tasks.filter(t => t.subjectId === sub.id);
            const relatedSessions = studySessions.filter(s => s.subjectId === sub.id);
            const totalHours = (sub.totalStudyMinutes / 60).toFixed(1);
            const weeklyHours = (sub.currentWeeklyMinutes / 60).toFixed(1);

            return (
              <Link
                key={sub.id}
                to={`/subjects/${sub.id}`}
                className="card-academic-interactive p-5 bg-white dark:bg-slate-800 border-slate-border dark:border-slate-700 flex flex-col justify-between group"
              >
                <div>
                  {/* Top Bar with color badge & actions */}
                  <div className="flex items-center justify-between mb-2">
                    <span
                      className="px-2 py-0.5 rounded text-[11px] font-mono font-bold"
                      style={{ backgroundColor: `${sub.color}15`, color: sub.color }}
                    >
                      {sub.code}
                    </span>
                    <div className="flex items-center gap-1 opacity-80 group-hover:opacity-100 transition-opacity">
                      <button
                        onClick={e => handleOpenEdit(sub, e)}
                        className="p-1 rounded text-slate-secondary hover:text-slate-primary hover:bg-slate-muted dark:hover:bg-slate-700"
                        title="Edit Subject"
                      >
                        <Edit3 className="w-3.5 h-3.5" />
                      </button>
                      <button
                        onClick={e => handleDelete(sub.id, e)}
                        className="p-1 rounded text-slate-secondary hover:text-danger hover:bg-danger/10"
                        title="Delete Subject"
                      >
                        <Trash2 className="w-3.5 h-3.5" />
                      </button>
                    </div>
                  </div>

                  {/* Subject Name & Description */}
                  <h3 className="font-bold text-base text-slate-primary dark:text-white group-hover:text-primary transition-colors line-clamp-1 mb-1">
                    {sub.name}
                  </h3>
                  <p className="text-xs text-slate-secondary line-clamp-2 mb-4 leading-relaxed">
                    {sub.description || 'No course syllabus description provided.'}
                  </p>

                  {/* Instructor & Semester metadata */}
                  <div className="space-y-1.5 text-xs text-slate-secondary border-t border-slate-border/60 dark:border-slate-700/60 pt-3">
                    <div className="flex items-center gap-2">
                      <User className="w-3.5 h-3.5" />
                      <span className="truncate">{sub.instructor}</span>
                    </div>
                    <div className="flex items-center justify-between text-[11px]">
                      <span>{sub.semester}</span>
                      <span className="text-success font-medium">Attendance: {sub.attendanceRate}%</span>
                    </div>
                  </div>
                </div>

                {/* Progress Indicators & Hours */}
                <div className="mt-5 pt-3 border-t border-slate-border dark:border-slate-700">
                  <div className="flex items-center justify-between text-xs mb-1">
                    <span className="text-slate-secondary flex items-center gap-1">
                      <Clock className="w-3.5 h-3.5" /> Weekly Progress
                    </span>
                    <span className="font-mono font-bold text-slate-primary dark:text-white">
                      {weeklyHours}h / {sub.weeklyTargetHours}h
                    </span>
                  </div>

                  <div className="w-full bg-slate-muted dark:bg-slate-700 h-1.5 rounded-full overflow-hidden mb-3">
                    <div
                      className="h-full rounded-full transition-all duration-500"
                      style={{
                        width: `${Math.min(100, (sub.currentWeeklyMinutes / (sub.weeklyTargetHours * 60)) * 100)}%`,
                        backgroundColor: sub.color
                      }}
                    />
                  </div>

                  <div className="flex items-center justify-between text-xs pt-1">
                    <span className="text-slate-secondary text-[11px]">
                      {relatedTasks.length} tasks · {relatedSessions.length} sessions
                    </span>
                    <span className="text-primary font-semibold text-xs flex items-center gap-1 group-hover:translate-x-1 transition-transform">
                      <span>Course Details</span>
                      <ArrowRight className="w-3.5 h-3.5" />
                    </span>
                  </div>
                </div>
              </Link>
            );
          })}
        </div>
      )}

      {/* Add / Edit Modal */}
      {isModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/40 backdrop-blur-xs animate-fadeIn">
          <div className="bg-white dark:bg-slate-900 w-full max-w-lg rounded-academic-lg border border-slate-border dark:border-slate-800 shadow-modal p-6 animate-modalIn">
            <div className="flex items-center justify-between pb-4 border-b border-slate-border dark:border-slate-800">
              <h3 className="font-bold text-slate-primary dark:text-white text-base">
                {editingSubject ? 'Edit Academic Subject' : 'Add New Subject'}
              </h3>
              <button
                onClick={() => setIsModalOpen(false)}
                className="p-1 rounded-lg text-slate-secondary hover:text-slate-primary"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleSubmit} className="mt-4 space-y-4">
              <div>
                <label className="block text-xs font-semibold text-slate-primary dark:text-white mb-1">
                  Subject Title *
                </label>
                <input
                  type="text"
                  required
                  placeholder="e.g. Algorithms & Data Structures"
                  value={name}
                  onChange={e => setName(e.target.value)}
                  className="w-full px-3 py-2 text-xs sm:text-sm rounded-academic border border-slate-border dark:border-slate-700 bg-white dark:bg-slate-800 text-slate-primary dark:text-white focus:outline-none focus:border-primary"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-semibold text-slate-primary dark:text-white mb-1">
                    Course Code *
                  </label>
                  <input
                    type="text"
                    required
                    placeholder="e.g. CS 201"
                    value={code}
                    onChange={e => setCode(e.target.value)}
                    className="w-full px-3 py-2 text-xs sm:text-sm rounded-academic border border-slate-border dark:border-slate-700 bg-white dark:bg-slate-800 text-slate-primary dark:text-white focus:outline-none focus:border-primary"
                  />
                </div>
                <div>
                  <label className="block text-xs font-semibold text-slate-primary dark:text-white mb-1">
                    Semester
                  </label>
                  <input
                    type="text"
                    value={semester}
                    onChange={e => setSemester(e.target.value)}
                    className="w-full px-3 py-2 text-xs sm:text-sm rounded-academic border border-slate-border dark:border-slate-700 bg-white dark:bg-slate-800 text-slate-primary dark:text-white focus:outline-none focus:border-primary"
                  />
                </div>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-semibold text-slate-primary dark:text-white mb-1">
                    Instructor
                  </label>
                  <input
                    type="text"
                    placeholder="Prof. Alistair Sterling"
                    value={instructor}
                    onChange={e => setInstructor(e.target.value)}
                    className="w-full px-3 py-2 text-xs sm:text-sm rounded-academic border border-slate-border dark:border-slate-700 bg-white dark:bg-slate-800 text-slate-primary dark:text-white focus:outline-none focus:border-primary"
                  />
                </div>
                <div>
                  <label className="block text-xs font-semibold text-slate-primary dark:text-white mb-1">
                    Weekly Study Target (Hours)
                  </label>
                  <input
                    type="number"
                    min="1"
                    max="40"
                    value={weeklyTargetHours}
                    onChange={e => setWeeklyTargetHours(Number(e.target.value))}
                    className="w-full px-3 py-2 text-xs sm:text-sm rounded-academic border border-slate-border dark:border-slate-700 bg-white dark:bg-slate-800 text-slate-primary dark:text-white focus:outline-none focus:border-primary"
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-primary dark:text-white mb-1">
                  Color Accent
                </label>
                <div className="flex items-center gap-3">
                  {['#6477D9', '#16A37B', '#E6A23C', '#8B5CF6', '#EC4899', '#3B82F6', '#10B981', '#F59E0B'].map(c => (
                    <button
                      type="button"
                      key={c}
                      onClick={() => setColor(c)}
                      className={`w-6 h-6 rounded-full border-2 transition-transform ${color === c ? 'scale-125 border-slate-primary dark:border-white' : 'border-transparent'
                        }`}
                      style={{ backgroundColor: c }}
                    />
                  ))}
                </div>
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-primary dark:text-white mb-1">
                  Description / Syllabus Topics
                </label>
                <textarea
                  rows={3}
                  placeholder="Outline key topics, prerequisites, or exam details..."
                  value={description}
                  onChange={e => setDescription(e.target.value)}
                  className="w-full px-3 py-2 text-xs sm:text-sm rounded-academic border border-slate-border dark:border-slate-700 bg-white dark:bg-slate-800 text-slate-primary dark:text-white focus:outline-none focus:border-primary resize-none"
                />
              </div>

              <div className="flex justify-end gap-2 pt-3 border-t border-slate-border dark:border-slate-800">
                <button
                  type="button"
                  onClick={() => setIsModalOpen(false)}
                  className="px-4 py-2 text-xs font-semibold text-slate-secondary hover:text-slate-primary"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-5 py-2 text-xs font-semibold rounded-academic bg-primary text-white hover:bg-primary-hover shadow-sm"
                >
                  {editingSubject ? 'Save Changes' : 'Create Subject'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

