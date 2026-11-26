import React, { useState } from 'react';
import { X, CheckSquare, BookOpen, FileText, GraduationCap, Calendar } from 'lucide-react';
import { useData } from '../../contexts/DataContext';
import { useToast } from '../../contexts/ToastContext';
import { TaskCategory, TaskPriority } from '../../types';

interface QuickAddModalProps {
  isOpen: boolean;
  onClose: () => void;
}

type TabType = 'task' | 'subject' | 'note' | 'exam' | 'schedule';

export const QuickAddModal: React.FC<QuickAddModalProps> = ({ isOpen, onClose }) => {
  const { subjects, addTask, addSubject, addNote, addExamination, addScheduleEvent } = useData();
  const { toast } = useToast();
  const [activeTab, setActiveTab] = useState<TabType>('task');

  // Task form state
  const [taskTitle, setTaskTitle] = useState('');
  const [taskSubjectId, setTaskSubjectId] = useState(subjects[0]?.id || '');
  const [taskCategory, setTaskCategory] = useState<TaskCategory>('assignment');
  const [taskPriority, setTaskPriority] = useState<TaskPriority>('medium');
  const [taskDueDate, setTaskDueDate] = useState(() => new Date(Date.now() + 86400000 * 3).toISOString().split('T')[0]);

  // Subject form state
  const [subjectName, setSubjectName] = useState('');
  const [subjectCode, setSubjectCode] = useState('');
  const [subjectColor, setSubjectColor] = useState('#6477D9');
  const [subjectInstructor, setSubjectInstructor] = useState('');
  const [subjectTargetHours, setSubjectTargetHours] = useState(5);

  // Note form state
  const [noteTitle, setNoteTitle] = useState('');
  const [noteContent, setNoteContent] = useState('');
  const [noteSubjectId, setNoteSubjectId] = useState(subjects[0]?.id || '');

  // Exam form state
  const [examTitle, setExamTitle] = useState('');
  const [examSubjectId, setExamSubjectId] = useState(subjects[0]?.id || '');
  const [examDate, setExamDate] = useState(() => new Date(Date.now() + 86400000 * 14).toISOString().split('T')[0]);
  const [examTime, setExamTime] = useState('10:00');
  const [examLocation, setExamLocation] = useState('');

  if (!isOpen) return null;

  const handleCreateTask = (e: React.FormEvent) => {
    e.preventDefault();
    if (!taskTitle.trim()) return;
    addTask({
      title: taskTitle.trim(),
      description: '',
      subjectId: taskSubjectId || (subjects[0]?.id || ''),
      category: taskCategory,
      priority: taskPriority,
      status: 'not_started',
      dueDate: taskDueDate,
    });
    toast.success('Task created successfully');
    onClose();
  };

  const handleCreateSubject = (e: React.FormEvent) => {
    e.preventDefault();
    if (!subjectName.trim() || !subjectCode.trim()) return;
    addSubject({
      name: subjectName.trim(),
      code: subjectCode.trim(),
      color: subjectColor,
      description: '',
      semester: 'Current Semester',
      instructor: subjectInstructor.trim() || 'Staff',
      weeklyTargetHours: Number(subjectTargetHours) || 5,
      attendanceRate: 100,
    });
    toast.success(`Subject ${subjectCode} added!`);
    onClose();
  };

  const handleCreateNote = (e: React.FormEvent) => {
    e.preventDefault();
    if (!noteTitle.trim()) return;
    addNote({
      title: noteTitle.trim(),
      content: noteContent.trim(),
      subjectId: noteSubjectId || undefined,
      tags: ['QuickNote'],
      isPinned: false,
      isFavourite: false,
      isArchived: false,
    });
    toast.success('Note recorded successfully');
    onClose();
  };

  const handleCreateExam = (e: React.FormEvent) => {
    e.preventDefault();
    if (!examTitle.trim()) return;
    addExamination({
      title: examTitle.trim(),
      subjectId: examSubjectId || (subjects[0]?.id || ''),
      examDate,
      examTime,
      location: examLocation.trim() || 'TBD',
      examType: 'midterm',
      preparationStatus: 'not_prepared',
    });
    toast.success('Examination scheduled');
    onClose();
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/40 backdrop-blur-xs animate-fadeIn">
      <div className="bg-white dark:bg-slate-900 w-full max-w-lg rounded-academic-lg border border-slate-border dark:border-slate-800 shadow-modal p-6 animate-modalIn">
        {/* Header */}
        <div className="flex items-center justify-between pb-4 border-b border-slate-border dark:border-slate-800">
          <div>
            <h3 className="font-bold text-slate-primary dark:text-white text-base">Quick Add Entry</h3>
            <p className="text-xs text-slate-secondary">Instantly create academic records</p>
          </div>
          <button
            onClick={onClose}
            className="p-1 rounded-lg text-slate-secondary hover:text-slate-primary"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Tab Selectors */}
        <div className="flex border-b border-slate-border dark:border-slate-800 mt-3">
          {[
            { id: 'task', label: 'Task', icon: CheckSquare },
            { id: 'subject', label: 'Subject', icon: BookOpen },
            { id: 'note', label: 'Note', icon: FileText },
            { id: 'exam', label: 'Exam', icon: GraduationCap },
          ].map(tab => {
            const Icon = tab.icon;
            const isSel = activeTab === tab.id;
            return (
              <button
                key={tab.id}
                onClick={() => setActiveTab(tab.id as TabType)}
                className={`flex-1 py-2.5 flex items-center justify-center gap-1.5 text-xs font-semibold border-b-2 transition-colors ${isSel
                  ? 'border-primary text-primary'
                  : 'border-transparent text-slate-secondary hover:text-slate-primary dark:hover:text-white'
                  }`}
              >
                <Icon className="w-3.5 h-3.5" />
                <span>{tab.label}</span>
              </button>
            );
          })}
        </div>

        {/* Form Body */}
        <div className="mt-4">
          {activeTab === 'task' && (
            <form onSubmit={handleCreateTask} className="space-y-4">
              <div>
                <label className="block text-xs font-semibold text-slate-primary dark:text-white mb-1">
                  Task Title *
                </label>
                <input
                  type="text"
                  required
                  placeholder="e.g. Complete Problem Set 4"
                  value={taskTitle}
                  onChange={e => setTaskTitle(e.target.value)}
                  className="w-full px-3 py-2 text-xs sm:text-sm rounded-academic border border-slate-border dark:border-slate-700 bg-white dark:bg-slate-800 text-slate-primary dark:text-white focus:outline-none focus:border-primary"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-semibold text-slate-primary dark:text-white mb-1">
                    Subject
                  </label>
                  <select
                    value={taskSubjectId}
                    onChange={e => setTaskSubjectId(e.target.value)}
                    className="w-full px-3 py-2 text-xs sm:text-sm rounded-academic border border-slate-border dark:border-slate-700 bg-white dark:bg-slate-800 text-slate-primary dark:text-white focus:outline-none focus:border-primary"
                  >
                    {subjects.map(s => (
                      <option key={s.id} value={s.id}>{s.name} ({s.code})</option>
                    ))}
                  </select>
                </div>
                <div>
                  <label className="block text-xs font-semibold text-slate-primary dark:text-white mb-1">
                    Category
                  </label>
                  <select
                    value={taskCategory}
                    onChange={e => setTaskCategory(e.target.value as TaskCategory)}
                    className="w-full px-3 py-2 text-xs sm:text-sm rounded-academic border border-slate-border dark:border-slate-700 bg-white dark:bg-slate-800 text-slate-primary dark:text-white focus:outline-none focus:border-primary"
                  >
                    <option value="assignment">Assignment</option>
                    <option value="homework">Homework</option>
                    <option value="revision">Revision</option>
                    <option value="project">Project</option>
                    <option value="reading">Reading</option>
                    <option value="exam_prep">Exam Prep</option>
                  </select>
                </div>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-semibold text-slate-primary dark:text-white mb-1">
                    Priority
                  </label>
                  <select
                    value={taskPriority}
                    onChange={e => setTaskPriority(e.target.value as TaskPriority)}
                    className="w-full px-3 py-2 text-xs sm:text-sm rounded-academic border border-slate-border dark:border-slate-700 bg-white dark:bg-slate-800 text-slate-primary dark:text-white focus:outline-none focus:border-primary"
                  >
                    <option value="low">Low Priority</option>
                    <option value="medium">Medium Priority</option>
                    <option value="high">High Priority</option>
                  </select>
                </div>
                <div>
                  <label className="block text-xs font-semibold text-slate-primary dark:text-white mb-1">
                    Due Date
                  </label>
                  <input
                    type="date"
                    required
                    value={taskDueDate}
                    onChange={e => setTaskDueDate(e.target.value)}
                    className="w-full px-3 py-2 text-xs sm:text-sm rounded-academic border border-slate-border dark:border-slate-700 bg-white dark:bg-slate-800 text-slate-primary dark:text-white focus:outline-none focus:border-primary"
                  />
                </div>
              </div>

              <div className="flex justify-end gap-2 pt-2">
                <button
                  type="button"
                  onClick={onClose}
                  className="px-4 py-2 text-xs font-semibold text-slate-secondary hover:text-slate-primary"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 text-xs font-semibold rounded-academic bg-primary text-white hover:bg-primary-hover shadow-sm"
                >
                  Save Task
                </button>
              </div>
            </form>
          )}

          {activeTab === 'subject' && (
            <form onSubmit={handleCreateSubject} className="space-y-4">
              <div>
                <label className="block text-xs font-semibold text-slate-primary dark:text-white mb-1">
                  Subject Name *
                </label>
                <input
                  type="text"
                  required
                  placeholder="e.g. Distributed Operating Systems"
                  value={subjectName}
                  onChange={e => setSubjectName(e.target.value)}
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
                    placeholder="e.g. CS 340"
                    value={subjectCode}
                    onChange={e => setSubjectCode(e.target.value)}
                    className="w-full px-3 py-2 text-xs sm:text-sm rounded-academic border border-slate-border dark:border-slate-700 bg-white dark:bg-slate-800 text-slate-primary dark:text-white focus:outline-none focus:border-primary"
                  />
                </div>
                <div>
                  <label className="block text-xs font-semibold text-slate-primary dark:text-white mb-1">
                    Color Accent
                  </label>
                  <div className="flex items-center gap-2 mt-1">
                    {['#6477D9', '#16A37B', '#E6A23C', '#8B5CF6', '#EC4899', '#3B82F6'].map(c => (
                      <button
                        type="button"
                        key={c}
                        onClick={() => setSubjectColor(c)}
                        className={`w-6 h-6 rounded-full border-2 transition-transform ${subjectColor === c ? 'scale-110 border-slate-primary dark:border-white' : 'border-transparent'
                          }`}
                        style={{ backgroundColor: c }}
                      />
                    ))}
                  </div>
                </div>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-semibold text-slate-primary dark:text-white mb-1">
                    Instructor
                  </label>
                  <input
                    type="text"
                    placeholder="e.g. Prof. Adams"
                    value={subjectInstructor}
                    onChange={e => setSubjectInstructor(e.target.value)}
                    className="w-full px-3 py-2 text-xs sm:text-sm rounded-academic border border-slate-border dark:border-slate-700 bg-white dark:bg-slate-800 text-slate-primary dark:text-white focus:outline-none focus:border-primary"
                  />
                </div>
                <div>
                  <label className="block text-xs font-semibold text-slate-primary dark:text-white mb-1">
                    Weekly Study Target (Hrs)
                  </label>
                  <input
                    type="number"
                    min="1"
                    max="40"
                    value={subjectTargetHours}
                    onChange={e => setSubjectTargetHours(Number(e.target.value))}
                    className="w-full px-3 py-2 text-xs sm:text-sm rounded-academic border border-slate-border dark:border-slate-700 bg-white dark:bg-slate-800 text-slate-primary dark:text-white focus:outline-none focus:border-primary"
                  />
                </div>
              </div>

              <div className="flex justify-end gap-2 pt-2">
                <button
                  type="button"
                  onClick={onClose}
                  className="px-4 py-2 text-xs font-semibold text-slate-secondary hover:text-slate-primary"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 text-xs font-semibold rounded-academic bg-primary text-white hover:bg-primary-hover shadow-sm"
                >
                  Create Subject
                </button>
              </div>
            </form>
          )}

          {activeTab === 'note' && (
            <form onSubmit={handleCreateNote} className="space-y-4">
              <div>
                <label className="block text-xs font-semibold text-slate-primary dark:text-white mb-1">
                  Note Title *
                </label>
                <input
                  type="text"
                  required
                  placeholder="e.g. Lecture 12 Invariants"
                  value={noteTitle}
                  onChange={e => setNoteTitle(e.target.value)}
                  className="w-full px-3 py-2 text-xs sm:text-sm rounded-academic border border-slate-border dark:border-slate-700 bg-white dark:bg-slate-800 text-slate-primary dark:text-white focus:outline-none focus:border-primary"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-primary dark:text-white mb-1">
                  Subject
                </label>
                <select
                  value={noteSubjectId}
                  onChange={e => setNoteSubjectId(e.target.value)}
                  className="w-full px-3 py-2 text-xs sm:text-sm rounded-academic border border-slate-border dark:border-slate-700 bg-white dark:bg-slate-800 text-slate-primary dark:text-white focus:outline-none focus:border-primary"
                >
                  <option value="">General Knowledge / No Subject</option>
                  {subjects.map(s => (
                    <option key={s.id} value={s.id}>{s.name} ({s.code})</option>
                  ))}
                </select>
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-primary dark:text-white mb-1">
                  Note Content
                </label>
                <textarea
                  rows={4}
                  placeholder="Write quick academic observations or takeaways..."
                  value={noteContent}
                  onChange={e => setNoteContent(e.target.value)}
                  className="w-full px-3 py-2 text-xs sm:text-sm rounded-academic border border-slate-border dark:border-slate-700 bg-white dark:bg-slate-800 text-slate-primary dark:text-white focus:outline-none focus:border-primary resize-none"
                />
              </div>

              <div className="flex justify-end gap-2 pt-2">
                <button
                  type="button"
                  onClick={onClose}
                  className="px-4 py-2 text-xs font-semibold text-slate-secondary hover:text-slate-primary"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 text-xs font-semibold rounded-academic bg-primary text-white hover:bg-primary-hover shadow-sm"
                >
                  Save Note
                </button>
              </div>
            </form>
          )}

          {activeTab === 'exam' && (
            <form onSubmit={handleCreateExam} className="space-y-4">
              <div>
                <label className="block text-xs font-semibold text-slate-primary dark:text-white mb-1">
                  Exam Title *
                </label>
                <input
                  type="text"
                  required
                  placeholder="e.g. Midterm 1: Graph Theory"
                  value={examTitle}
                  onChange={e => setExamTitle(e.target.value)}
                  className="w-full px-3 py-2 text-xs sm:text-sm rounded-academic border border-slate-border dark:border-slate-700 bg-white dark:bg-slate-800 text-slate-primary dark:text-white focus:outline-none focus:border-primary"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-semibold text-slate-primary dark:text-white mb-1">
                    Subject
                  </label>
                  <select
                    value={examSubjectId}
                    onChange={e => setExamSubjectId(e.target.value)}
                    className="w-full px-3 py-2 text-xs sm:text-sm rounded-academic border border-slate-border dark:border-slate-700 bg-white dark:bg-slate-800 text-slate-primary dark:text-white focus:outline-none focus:border-primary"
                  >
                    {subjects.map(s => (
                      <option key={s.id} value={s.id}>{s.name} ({s.code})</option>
                    ))}
                  </select>
                </div>
                <div>
                  <label className="block text-xs font-semibold text-slate-primary dark:text-white mb-1">
                    Exam Date
                  </label>
                  <input
                    type="date"
                    required
                    value={examDate}
                    onChange={e => setExamDate(e.target.value)}
                    className="w-full px-3 py-2 text-xs sm:text-sm rounded-academic border border-slate-border dark:border-slate-700 bg-white dark:bg-slate-800 text-slate-primary dark:text-white focus:outline-none focus:border-primary"
                  />
                </div>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-semibold text-slate-primary dark:text-white mb-1">
                    Time
                  </label>
                  <input
                    type="time"
                    value={examTime}
                    onChange={e => setExamTime(e.target.value)}
                    className="w-full px-3 py-2 text-xs sm:text-sm rounded-academic border border-slate-border dark:border-slate-700 bg-white dark:bg-slate-800 text-slate-primary dark:text-white focus:outline-none focus:border-primary"
                  />
                </div>
                <div>
                  <label className="block text-xs font-semibold text-slate-primary dark:text-white mb-1">
                    Hall / Location
                  </label>
                  <input
                    type="text"
                    placeholder="e.g. Science Block 102"
                    value={examLocation}
                    onChange={e => setExamLocation(e.target.value)}
                    className="w-full px-3 py-2 text-xs sm:text-sm rounded-academic border border-slate-border dark:border-slate-700 bg-white dark:bg-slate-800 text-slate-primary dark:text-white focus:outline-none focus:border-primary"
                  />
                </div>
              </div>

              <div className="flex justify-end gap-2 pt-2">
                <button
                  type="button"
                  onClick={onClose}
                  className="px-4 py-2 text-xs font-semibold text-slate-secondary hover:text-slate-primary"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 text-xs font-semibold rounded-academic bg-primary text-white hover:bg-primary-hover shadow-sm"
                >
                  Schedule Exam
                </button>
              </div>
            </form>
          )}
        </div>
      </div>
    </div>
  );
};

