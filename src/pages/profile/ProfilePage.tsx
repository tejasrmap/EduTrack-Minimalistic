import React, { useState } from 'react';
import { 
  Mail, 
  GraduationCap, 
  BookOpen, 
  CheckCircle2, 
  Award, 
  Save, 
  Sparkles
} from 'lucide-react';
import { useAuth } from '../../contexts/AuthContext';
import { useData } from '../../contexts/DataContext';
import { useToast } from '../../contexts/ToastContext';

export const ProfilePage: React.FC = () => {
  const { user, updateProfile } = useAuth();
  const { subjects, tasks, examinations, studySessions, goals } = useData();
  const { toast } = useToast();

  const [name, setName] = useState(user?.name || '');
  const [email, setEmail] = useState(user?.email || '');
  const [university, setUniversity] = useState(user?.university || '');
  const [program, setProgram] = useState(user?.program || '');
  const [academicYear, setAcademicYear] = useState(user?.academicYear || 'Year 2');
  const [semester, setSemester] = useState(user?.semester || 'Semester 1');
  const [preferredHours, setPreferredHours] = useState(user?.preferredStudyHours || 25);
  const [dailyTarget, setDailyTarget] = useState(user?.dailyTargetMinutes || 180);

  const handleSave = (e: React.FormEvent) => {
    e.preventDefault();
    if (!name.trim()) {
      toast.error('Name cannot be empty');
      return;
    }

    updateProfile({
      name: name.trim(),
      email: email.trim(),
      university: university.trim(),
      program: program.trim(),
      academicYear,
      semester,
      preferredStudyHours: Number(preferredHours),
      dailyTargetMinutes: Number(dailyTarget)
    });

    toast.success('Profile updated successfully');
  };

  // Cumulative Academic Stats
  const totalStudyMinutes = studySessions.reduce((acc, s) => acc + s.durationMinutes, 0);
  const totalStudyHours = (totalStudyMinutes / 60).toFixed(1);
  const completedTasks = tasks.filter(t => t.status === 'completed').length;
  const completedGoals = goals.filter(g => g.isCompleted).length;

  return (
    <div className="space-y-6 max-w-4xl mx-auto">
      {/* Header */}
      <div>
        <h1 className="text-2xl font-bold text-academic-text tracking-tight">Student Academic Profile</h1>
        <p className="text-sm text-academic-muted mt-1">
          Personalise your academic identity, degree program details and weekly study targets.
        </p>
      </div>

      {/* Profile Overview Card */}
      <div className="bg-academic-card p-6 rounded-xl border border-academic-border shadow-sm flex flex-col sm:flex-row items-center gap-6">
        <div className="w-20 h-20 rounded-full bg-academic-primary/10 text-academic-primary font-bold text-2xl flex items-center justify-center border-2 border-academic-primary/30 flex-shrink-0">
          {name.split(' ').map((n: string) => n[0]).join('').substring(0, 2).toUpperCase() || 'ST'}
        </div>

        <div className="flex-1 text-center sm:text-left min-w-0">
          <div className="flex flex-col sm:flex-row sm:items-center gap-2">
            <h2 className="text-xl font-bold text-academic-text">{name}</h2>
            <span className="px-2.5 py-0.5 rounded-full text-xs font-semibold bg-academic-primary/10 text-academic-primary border border-academic-primary/20 self-center sm:self-auto">
              {academicYear} • {semester}
            </span>
          </div>

          <p className="text-xs text-academic-muted mt-1 flex items-center justify-center sm:justify-start gap-1.5">
            <GraduationCap className="w-4 h-4 text-academic-primary" />
            {program || 'Degree Not Specified'} — {university || 'University'}
          </p>

          <p className="text-xs text-academic-muted mt-0.5 flex items-center justify-center sm:justify-start gap-1.5">
            <Mail className="w-3.5 h-3.5" />
            {email}
          </p>
        </div>

        <div className="flex sm:flex-col items-center gap-2 border-t sm:border-t-0 sm:border-l border-academic-border pt-4 sm:pt-0 sm:pl-6 text-center">
          <div>
            <span className="block text-2xl font-bold text-academic-text">{totalStudyHours}h</span>
            <span className="text-[10px] text-academic-muted uppercase font-semibold">Total Focused</span>
          </div>
        </div>
      </div>

      {/* Academic Milestones Grid */}
      <div className="grid grid-cols-2 sm:grid-cols-4 gap-4">
        <div className="bg-academic-card p-4 rounded-xl border border-academic-border shadow-sm text-center">
          <BookOpen className="w-5 h-5 text-academic-primary mx-auto mb-1" />
          <div className="text-xl font-bold text-academic-text">{subjects.length}</div>
          <span className="text-xs text-academic-muted">Enrolled Subjects</span>
        </div>

        <div className="bg-academic-card p-4 rounded-xl border border-academic-border shadow-sm text-center">
          <CheckCircle2 className="w-5 h-5 text-emerald-500 mx-auto mb-1" />
          <div className="text-xl font-bold text-academic-text">{completedTasks}</div>
          <span className="text-xs text-academic-muted">Completed Tasks</span>
        </div>

        <div className="bg-academic-card p-4 rounded-xl border border-academic-border shadow-sm text-center">
          <Award className="w-5 h-5 text-amber-500 mx-auto mb-1" />
          <div className="text-xl font-bold text-academic-text">{examinations.length}</div>
          <span className="text-xs text-academic-muted">Examinations</span>
        </div>

        <div className="bg-academic-card p-4 rounded-xl border border-academic-border shadow-sm text-center">
          <Sparkles className="w-5 h-5 text-indigo-500 mx-auto mb-1" />
          <div className="text-xl font-bold text-academic-text">{completedGoals}</div>
          <span className="text-xs text-academic-muted">Goals Achieved</span>
        </div>
      </div>

      {/* Edit Profile Form */}
      <div className="bg-academic-card p-6 rounded-xl border border-academic-border shadow-sm">
        <h3 className="font-semibold text-sm text-academic-text mb-4 pb-2 border-b border-academic-border">
          Academic Details & Study Targets
        </h3>

        <form onSubmit={handleSave} className="space-y-4">
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label className="block text-xs font-medium text-academic-text mb-1">
                Full Name
              </label>
              <input
                type="text"
                required
                value={name}
                onChange={e => setName(e.target.value)}
                className="w-full px-3 py-2 text-xs bg-academic-muted-bg border border-academic-border rounded-lg text-academic-text focus:outline-none focus:ring-1 focus:ring-academic-primary"
              />
            </div>

            <div>
              <label className="block text-xs font-medium text-academic-text mb-1">
                Student Email
              </label>
              <input
                type="email"
                required
                value={email}
                onChange={e => setEmail(e.target.value)}
                className="w-full px-3 py-2 text-xs bg-academic-muted-bg border border-academic-border rounded-lg text-academic-text focus:outline-none focus:ring-1 focus:ring-academic-primary"
              />
            </div>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label className="block text-xs font-medium text-academic-text mb-1">
                University / Institution
              </label>
              <input
                type="text"
                value={university}
                onChange={e => setUniversity(e.target.value)}
                className="w-full px-3 py-2 text-xs bg-academic-muted-bg border border-academic-border rounded-lg text-academic-text focus:outline-none focus:ring-1 focus:ring-academic-primary"
              />
            </div>

            <div>
              <label className="block text-xs font-medium text-academic-text mb-1">
                Degree / Program
              </label>
              <input
                type="text"
                value={program}
                onChange={e => setProgram(e.target.value)}
                className="w-full px-3 py-2 text-xs bg-academic-muted-bg border border-academic-border rounded-lg text-academic-text focus:outline-none focus:ring-1 focus:ring-academic-primary"
              />
            </div>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-xs font-medium text-academic-text mb-1">
                Academic Year
              </label>
              <select
                value={academicYear}
                onChange={e => setAcademicYear(e.target.value)}
                className="w-full px-3 py-2 text-xs bg-academic-muted-bg border border-academic-border rounded-lg text-academic-text focus:outline-none focus:ring-1 focus:ring-academic-primary"
              >
                <option value="Year 1">Year 1 (Freshman)</option>
                <option value="Year 2">Year 2 (Sophomore)</option>
                <option value="Year 3">Year 3 (Junior)</option>
                <option value="Year 4">Year 4 (Senior)</option>
                <option value="Postgraduate">Postgraduate / Masters</option>
                <option value="Doctoral">Doctoral / PhD</option>
              </select>
            </div>

            <div>
              <label className="block text-xs font-medium text-academic-text mb-1">
                Current Semester
              </label>
              <input
                type="text"
                value={semester}
                onChange={e => setSemester(e.target.value)}
                placeholder="Semester 1, Fall 2026"
                className="w-full px-3 py-2 text-xs bg-academic-muted-bg border border-academic-border rounded-lg text-academic-text focus:outline-none focus:ring-1 focus:ring-academic-primary"
              />
            </div>
          </div>

          <div className="grid grid-cols-2 gap-4 pt-2">
            <div>
              <label className="block text-xs font-medium text-academic-text mb-1">
                Preferred Study Target (Hours / Week)
              </label>
              <input
                type="number"
                min="5"
                max="80"
                value={preferredHours}
                onChange={e => setPreferredHours(Number(e.target.value))}
                className="w-full px-3 py-2 text-xs bg-academic-muted-bg border border-academic-border rounded-lg text-academic-text focus:outline-none focus:ring-1 focus:ring-academic-primary"
              />
            </div>

            <div>
              <label className="block text-xs font-medium text-academic-text mb-1">
                Daily Target (Minutes / Day)
              </label>
              <input
                type="number"
                min="30"
                max="720"
                step="15"
                value={dailyTarget}
                onChange={e => setDailyTarget(Number(e.target.value))}
                className="w-full px-3 py-2 text-xs bg-academic-muted-bg border border-academic-border rounded-lg text-academic-text focus:outline-none focus:ring-1 focus:ring-academic-primary"
              />
            </div>
          </div>

          <div className="flex justify-end pt-4 border-t border-academic-border">
            <button
              type="submit"
              className="flex items-center gap-2 px-5 py-2 bg-academic-primary text-white rounded-lg text-xs font-semibold hover:bg-academic-primary/90 transition-colors shadow-sm"
            >
              <Save className="w-4 h-4" />
              <span>Save Academic Profile</span>
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};
