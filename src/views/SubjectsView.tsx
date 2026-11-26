import React, { useState } from 'react';
import { 
  Plus, 
  Edit2, 
  Trash2, 
  User, 
  MapPin, 
  X 
} from 'lucide-react';
import { useAcademic } from '../context/AppContext';
import { AcademicSubject } from '../types';

export const SubjectsView: React.FC = () => {
  const { subjects, addSubject, updateSubject, deleteSubject } = useAcademic();

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingSubj, setEditingSubj] = useState<AcademicSubject | null>(null);

  // Form states
  const [title, setTitle] = useState('');
  const [code, setCode] = useState('');
  const [pastelColor, setPastelColor] = useState('#4D7C0F');
  const [credits, setCredits] = useState(4);
  const [gradeTarget, setGradeTarget] = useState('First Class (A)');
  const [currentGrade, setCurrentGrade] = useState('First Class (A)');
  const [attendancePercentage, setAttendancePercentage] = useState(95);
  const [weeklyTargetHours, setWeeklyTargetHours] = useState(6);
  const [instructor, setInstructor] = useState('');
  const [location, setLocation] = useState('');
  const [syllabusOverview, setSyllabusOverview] = useState('');

  const palette = [
    { color: '#4D7C0F', name: 'Sage Green' },
    { color: '#1E3A8A', name: 'Oxford Blue' },
    { color: '#9A3412', name: 'Terracotta' },
    { color: '#0F766E', name: 'Deep Teal' },
    { color: '#6B21A8', name: 'Plum' },
    { color: '#334155', name: 'Slate' },
  ];

  const handleOpenAdd = () => {
    setEditingSubj(null);
    setTitle('');
    setCode('');
    setPastelColor('#4D7C0F');
    setCredits(4);
    setGradeTarget('First Class (A)');
    setCurrentGrade('First Class (A)');
    setAttendancePercentage(95);
    setWeeklyTargetHours(6);
    setInstructor('');
    setLocation('');
    setSyllabusOverview('');
    setIsModalOpen(true);
  };

  const handleOpenEdit = (s: AcademicSubject) => {
    setEditingSubj(s);
    setTitle(s.title);
    setCode(s.code);
    setPastelColor(s.pastelColor);
    setCredits(s.credits);
    setGradeTarget(s.gradeTarget);
    setCurrentGrade(s.currentGrade);
    setAttendancePercentage(s.attendancePercentage);
    setWeeklyTargetHours(s.weeklyTargetHours);
    setInstructor(s.instructor);
    setLocation(s.location);
    setSyllabusOverview(s.syllabusOverview || '');
    setIsModalOpen(true);
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!title.trim() || !code.trim()) return;

    if (editingSubj) {
      updateSubject(editingSubj.id, {
        title,
        code,
        pastelColor,
        credits: Number(credits),
        gradeTarget,
        currentGrade,
        attendancePercentage: Number(attendancePercentage),
        weeklyTargetHours: Number(weeklyTargetHours),
        instructor: instructor || 'Faculty Member',
        location: location || 'Scroope Terrace Room',
        syllabusOverview,
      });
    } else {
      addSubject({
        title,
        code,
        pastelColor,
        badgeBg: '#F0FDF4',
        badgeText: '#15803D',
        credits: Number(credits),
        gradeTarget,
        currentGrade,
        attendancePercentage: Number(attendancePercentage),
        weeklyTargetHours: Number(weeklyTargetHours),
        currentWeekHours: 0,
        instructor: instructor || 'Faculty Member',
        location: location || 'Scroope Terrace Room',
        syllabusOverview,
      });
    }
    setIsModalOpen(false);
  };

  const handleDelete = (id: string, name: string) => {
    if (window.confirm(`Remove subject "${name}" from your curriculum schedule?`)) {
      deleteSubject(id);
    }
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="paper-card rounded-2xl p-5 flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h2 className="font-serif text-xl font-bold text-stone-900">
            Curriculum & Registered Units
          </h2>
          <p className="text-xs text-stone-500">
            {subjects.length} active units · {subjects.reduce((a, b) => a + b.credits, 0)} total credit units
          </p>
        </div>

        <button
          onClick={handleOpenAdd}
          className="flex items-center gap-2 px-4 py-2 rounded-xl bg-[#2A5C43] hover:bg-[#234e38] text-white text-xs font-medium transition-colors shadow-xs"
        >
          <Plus className="w-4 h-4" />
          <span>Add Study Unit</span>
        </button>
      </div>

      {/* Grid of Subject Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
        {subjects.map((sub) => {
          const hoursPercent = Math.min(100, Math.round((sub.currentWeekHours / sub.weeklyTargetHours) * 100));

          return (
            <div
              key={sub.id}
              className="paper-card-interactive rounded-2xl p-5 flex flex-col justify-between group"
            >
              <div>
                <div className="flex items-start justify-between gap-2 mb-2">
                  <span 
                    className="text-xs font-mono font-semibold px-2 py-0.5 rounded-md"
                    style={{ 
                      backgroundColor: `${sub.pastelColor}15`, 
                      color: sub.pastelColor 
                    }}
                  >
                    {sub.code} · {sub.credits} Credits
                  </span>

                  <div className="flex items-center gap-1 opacity-60 group-hover:opacity-100 transition-opacity">
                    <button
                      onClick={() => handleOpenEdit(sub)}
                      className="p-1 rounded-md text-stone-400 hover:text-stone-800 hover:bg-stone-100"
                      title="Edit Subject"
                    >
                      <Edit2 className="w-3.5 h-3.5" />
                    </button>
                    <button
                      onClick={() => handleDelete(sub.id, sub.title)}
                      className="p-1 rounded-md text-stone-400 hover:text-rose-600 hover:bg-rose-50"
                      title="Remove Subject"
                    >
                      <Trash2 className="w-3.5 h-3.5" />
                    </button>
                  </div>
                </div>

                <h3 className="font-serif text-base font-bold text-stone-900 leading-snug mb-2">
                  {sub.title}
                </h3>

                <div className="space-y-1 text-xs text-stone-500 mb-4">
                  <div className="flex items-center gap-1.5">
                    <User className="w-3.5 h-3.5 text-stone-400" />
                    <span className="truncate">{sub.instructor}</span>
                  </div>
                  <div className="flex items-center gap-1.5">
                    <MapPin className="w-3.5 h-3.5 text-stone-400" />
                    <span className="truncate">{sub.location}</span>
                  </div>
                </div>

                {sub.syllabusOverview && (
                  <p className="text-xs text-stone-600 italic bg-stone-50 p-2 rounded-lg mb-4 line-clamp-2">
                    "{sub.syllabusOverview}"
                  </p>
                )}

                {/* Progress bars */}
                <div className="space-y-2.5 pt-3 border-t border-stone-200 text-xs">
                  <div>
                    <div className="flex justify-between text-stone-500 font-mono text-[11px] mb-1">
                      <span>Weekly Studio / Reading</span>
                      <span className="text-stone-800 font-medium">
                        {sub.currentWeekHours}h / {sub.weeklyTargetHours}h ({hoursPercent}%)
                      </span>
                    </div>
                    <div className="w-full bg-stone-100 h-1.5 rounded-full overflow-hidden">
                      <div 
                        className="h-full rounded-full transition-all duration-300"
                        style={{ width: `${hoursPercent}%`, backgroundColor: sub.pastelColor }}
                      />
                    </div>
                  </div>

                  <div>
                    <div className="flex justify-between text-stone-500 font-mono text-[11px] mb-1">
                      <span>Seminar Attendance</span>
                      <span className="text-stone-800 font-medium">{sub.attendancePercentage}%</span>
                    </div>
                    <div className="w-full bg-stone-100 h-1.5 rounded-full overflow-hidden">
                      <div 
                        className="bg-stone-400 h-full rounded-full"
                        style={{ width: `${sub.attendancePercentage}%` }}
                      />
                    </div>
                  </div>
                </div>
              </div>

              <div className="mt-4 pt-3 border-t border-stone-200 flex items-center justify-between text-xs">
                <span className="text-stone-500">Target: <strong className="text-stone-800">{sub.gradeTarget}</strong></span>
                <span className="font-medium text-[#2A5C43]">
                  Current: {sub.currentGrade}
                </span>
              </div>
            </div>
          );
        })}
      </div>

      {/* Modal */}
      {isModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-stone-900/40 backdrop-blur-xs animate-fadeIn">
          <div className="bg-white w-full max-w-lg rounded-2xl p-6 shadow-xl border border-stone-200 animate-modalIn">
            <div className="flex items-center justify-between pb-3 border-b border-stone-200">
              <h3 className="font-serif text-lg font-bold text-stone-900">
                {editingSubj ? 'Edit Curriculum Unit' : 'Register New Curriculum Unit'}
              </h3>
              <button 
                onClick={() => setIsModalOpen(false)}
                className="p-1 text-stone-400 hover:text-stone-700"
              >
                <X className="w-4 h-4" />
              </button>
            </div>

            <form onSubmit={handleSubmit} className="mt-4 space-y-4">
              <div className="grid grid-cols-3 gap-3">
                <div className="col-span-2">
                  <label className="block text-xs font-medium text-stone-700 mb-1">
                    Unit Title *
                  </label>
                  <input
                    type="text"
                    required
                    value={title}
                    onChange={(e) => setTitle(e.target.value)}
                    placeholder="e.g. Modernist Spatial Theory"
                    className="w-full px-3 py-2 rounded-xl bg-stone-50 border border-stone-200 text-xs text-stone-800 focus:outline-none focus:border-[#2A5C43]"
                  />
                </div>
                <div>
                  <label className="block text-xs font-medium text-stone-700 mb-1">
                    Code *
                  </label>
                  <input
                    type="text"
                    required
                    value={code}
                    onChange={(e) => setCode(e.target.value)}
                    placeholder="ARC-501"
                    className="w-full px-3 py-2 rounded-xl bg-stone-50 border border-stone-200 text-xs text-stone-800 focus:outline-none focus:border-[#2A5C43] font-mono"
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-medium text-stone-700 mb-1">
                  Pastel Color Identifier
                </label>
                <div className="flex items-center gap-3">
                  {palette.map((p) => (
                    <button
                      type="button"
                      key={p.color}
                      onClick={() => setPastelColor(p.color)}
                      className={`w-6 h-6 rounded-full transition-transform ${
                        pastelColor === p.color ? 'scale-125 ring-2 ring-stone-900 ring-offset-2' : ''
                      }`}
                      style={{ backgroundColor: p.color }}
                      title={p.name}
                    />
                  ))}
                </div>
              </div>

              <div className="grid grid-cols-3 gap-3">
                <div>
                  <label className="block text-xs font-medium text-stone-700 mb-1">
                    Credits
                  </label>
                  <input
                    type="number"
                    min={1}
                    max={12}
                    value={credits}
                    onChange={(e) => setCredits(Number(e.target.value))}
                    className="w-full px-3 py-2 rounded-xl bg-stone-50 border border-stone-200 text-xs text-stone-800 focus:outline-none focus:border-[#2A5C43] font-mono"
                  />
                </div>
                <div>
                  <label className="block text-xs font-medium text-stone-700 mb-1">
                    Weekly Goal (hrs)
                  </label>
                  <input
                    type="number"
                    min={1}
                    max={40}
                    value={weeklyTargetHours}
                    onChange={(e) => setWeeklyTargetHours(Number(e.target.value))}
                    className="w-full px-3 py-2 rounded-xl bg-stone-50 border border-stone-200 text-xs text-stone-800 focus:outline-none focus:border-[#2A5C43] font-mono"
                  />
                </div>
                <div>
                  <label className="block text-xs font-medium text-stone-700 mb-1">
                    Target Grade
                  </label>
                  <input
                    type="text"
                    value={gradeTarget}
                    onChange={(e) => setGradeTarget(e.target.value)}
                    className="w-full px-3 py-2 rounded-xl bg-stone-50 border border-stone-200 text-xs text-stone-800 focus:outline-none focus:border-[#2A5C43]"
                  />
                </div>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-medium text-stone-700 mb-1">
                    Instructor
                  </label>
                  <input
                    type="text"
                    value={instructor}
                    onChange={(e) => setInstructor(e.target.value)}
                    placeholder="Prof. Evelyn Carter"
                    className="w-full px-3 py-2 rounded-xl bg-stone-50 border border-stone-200 text-xs text-stone-800 focus:outline-none focus:border-[#2A5C43]"
                  />
                </div>
                <div>
                  <label className="block text-xs font-medium text-stone-700 mb-1">
                    Hall / Laboratory
                  </label>
                  <input
                    type="text"
                    value={location}
                    onChange={(e) => setLocation(e.target.value)}
                    placeholder="Studio Hall 4"
                    className="w-full px-3 py-2 rounded-xl bg-stone-50 border border-stone-200 text-xs text-stone-800 focus:outline-none focus:border-[#2A5C43]"
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-medium text-stone-700 mb-1">
                  Syllabus Overview / Notes
                </label>
                <textarea
                  rows={2}
                  value={syllabusOverview}
                  onChange={(e) => setSyllabusOverview(e.target.value)}
                  placeholder="Summary of core readings, primary themes or term paper scope..."
                  className="w-full px-3 py-2 rounded-xl bg-stone-50 border border-stone-200 text-xs text-stone-800 focus:outline-none focus:border-[#2A5C43]"
                />
              </div>

              <div className="flex items-center justify-end gap-3 pt-3 border-t border-stone-200">
                <button
                  type="button"
                  onClick={() => setIsModalOpen(false)}
                  className="px-4 py-2 text-xs text-stone-500 hover:text-stone-800"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 rounded-xl bg-[#2A5C43] hover:bg-[#234e38] text-white text-xs font-medium transition-colors"
                >
                  {editingSubj ? 'Save Changes' : 'Register Unit'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
