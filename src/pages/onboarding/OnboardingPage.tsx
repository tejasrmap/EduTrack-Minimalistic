import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { 
  GraduationCap, 
  BookOpen, 
  Clock, 
  Check, 
  ArrowRight, 
  ArrowLeft, 
  Sparkles 
} from 'lucide-react';
import { useAuth } from '../../contexts/AuthContext';
import { useData } from '../../contexts/DataContext';
import { useToast } from '../../contexts/ToastContext';

export const OnboardingPage: React.FC = () => {
  const { user, completeOnboarding } = useAuth();
  const { addSubject, updateSettings } = useData();
  const { toast } = useToast();
  const navigate = useNavigate();

  const [step, setStep] = useState(1);

  // Step 1: Profile & University
  const [name, setName] = useState(user.name || '');
  const [university, setUniversity] = useState(user.university || 'Oxford Institute of Technology');
  const [program, setProgram] = useState(user.program || 'Computer Science & Mathematics');
  const [academicYear, setAcademicYear] = useState(user.academicYear || 'Year 2 (Sophomore)');
  const [semester, setSemester] = useState(user.semester || 'Fall Semester 2026');

  // Step 2: Targets
  const [preferredHours, setPreferredHours] = useState(user.preferredStudyHours || 25);
  const [dailyTargetMinutes, setDailyTargetMinutes] = useState(user.dailyTargetMinutes || 180);
  const [pomodoroLength, setPomodoroLength] = useState(25);

  // Step 3: Initial subjects
  const [initialSubjectList, setInitialSubjectList] = useState([
    { name: 'Algorithms & Data Structures', code: 'CS 201', color: '#6477D9' },
    { name: 'Linear Algebra & Calculus', code: 'MATH 210', color: '#16A37B' },
    { name: 'Computer Systems Architecture', code: 'CS 240', color: '#E6A23C' },
  ]);
  const [newSubjName, setNewSubjName] = useState('');
  const [newSubjCode, setNewSubjCode] = useState('');

  const handleAddCustomSubject = (e: React.FormEvent) => {
    e.preventDefault();
    if (!newSubjName.trim() || !newSubjCode.trim()) return;
    setInitialSubjectList(prev => [
      ...prev,
      { name: newSubjName.trim(), code: newSubjCode.trim(), color: '#8B5CF6' }
    ]);
    setNewSubjName('');
    setNewSubjCode('');
  };

  const handleFinishOnboarding = () => {
    // 1. Complete user profile onboarding
    completeOnboarding({
      name,
      university,
      program,
      academicYear,
      semester,
      preferredStudyHours: preferredHours,
      dailyTargetMinutes,
      isOnboarded: true,
    });

    // 2. Configure user settings
    updateSettings({
      dailyStudyTargetMinutes,
      pomodoroFocusMinutes: pomodoroLength,
    });

    // 3. Ensure subjects exist if new
    initialSubjectList.forEach(s => {
      addSubject({
        name: s.name,
        code: s.code,
        color: s.color,
        description: `Enrolled course for ${semester}`,
        semester,
        instructor: 'Faculty Staff',
        weeklyTargetHours: Math.round(preferredHours / initialSubjectList.length) || 5,
        attendanceRate: 100,
      });
    });

    toast.success('Onboarding complete! Your personalized academic dashboard is ready.');
    navigate('/dashboard');
  };

  return (
    <div className="min-h-screen bg-background dark:bg-slate-900 flex flex-col justify-center items-center px-4 py-12">
      <div className="w-full max-w-2xl">
        {/* Progress header */}
        <div className="text-center mb-8">
          <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-primary/10 text-primary text-xs font-semibold mb-2">
            <Sparkles className="w-3.5 h-3.5" />
            <span>Academic Setup · Step {step} of 3</span>
          </div>
          <h1 className="text-2xl sm:text-3xl font-bold text-slate-primary dark:text-white tracking-tight">
            Personalize Your Study Workspace
          </h1>
          <p className="text-xs sm:text-sm text-slate-secondary mt-1">
            EduTrack adapts to your university schedule and study commitments
          </p>

          {/* Stepper Dots */}
          <div className="flex items-center justify-center gap-2 mt-5">
            {[1, 2, 3].map(i => (
              <div
                key={i}
                className={`h-1.5 rounded-full transition-all duration-300 ${
                  step === i ? 'w-8 bg-primary' : step > i ? 'w-5 bg-success' : 'w-4 bg-slate-border dark:bg-slate-700'
                }`}
              />
            ))}
          </div>
        </div>

        {/* Form Card */}
        <div className="card-academic p-6 sm:p-8 bg-white dark:bg-slate-800 border-slate-border dark:border-slate-700 shadow-modal">
          {step === 1 && (
            <div className="space-y-4 animate-fadeIn">
              <div className="border-b border-slate-border dark:border-slate-700 pb-3 mb-4">
                <h2 className="font-bold text-slate-primary dark:text-white text-base sm:text-lg flex items-center gap-2">
                  <GraduationCap className="w-5 h-5 text-primary" />
                  <span>Academic Identification & Program</span>
                </h2>
                <p className="text-xs text-slate-secondary">Your student identity and university affiliation</p>
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-primary dark:text-white mb-1.5">
                  Your Full Name
                </label>
                <input
                  type="text"
                  required
                  value={name}
                  onChange={e => setName(e.target.value)}
                  className="w-full px-3 py-2 text-xs sm:text-sm rounded-academic border border-slate-border dark:border-slate-700 bg-white dark:bg-slate-900 text-slate-primary dark:text-white focus:outline-none focus:border-primary"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-primary dark:text-white mb-1.5">
                  University / College Institution
                </label>
                <input
                  type="text"
                  required
                  value={university}
                  onChange={e => setUniversity(e.target.value)}
                  className="w-full px-3 py-2 text-xs sm:text-sm rounded-academic border border-slate-border dark:border-slate-700 bg-white dark:bg-slate-900 text-slate-primary dark:text-white focus:outline-none focus:border-primary"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-primary dark:text-white mb-1.5">
                  Degree / Program of Study
                </label>
                <input
                  type="text"
                  required
                  value={program}
                  onChange={e => setProgram(e.target.value)}
                  className="w-full px-3 py-2 text-xs sm:text-sm rounded-academic border border-slate-border dark:border-slate-700 bg-white dark:bg-slate-900 text-slate-primary dark:text-white focus:outline-none focus:border-primary"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-semibold text-slate-primary dark:text-white mb-1.5">
                    Academic Year
                  </label>
                  <select
                    value={academicYear}
                    onChange={e => setAcademicYear(e.target.value)}
                    className="w-full px-3 py-2 text-xs sm:text-sm rounded-academic border border-slate-border dark:border-slate-700 bg-white dark:bg-slate-900 text-slate-primary dark:text-white focus:outline-none focus:border-primary"
                  >
                    <option value="Year 1 (Freshman)">Year 1 (Freshman)</option>
                    <option value="Year 2 (Sophomore)">Year 2 (Sophomore)</option>
                    <option value="Year 3 (Junior)">Year 3 (Junior)</option>
                    <option value="Year 4 (Senior)">Year 4 (Senior)</option>
                    <option value="Postgraduate / Masters">Postgraduate / Masters</option>
                  </select>
                </div>
                <div>
                  <label className="block text-xs font-semibold text-slate-primary dark:text-white mb-1.5">
                    Current Semester
                  </label>
                  <input
                    type="text"
                    value={semester}
                    onChange={e => setSemester(e.target.value)}
                    className="w-full px-3 py-2 text-xs sm:text-sm rounded-academic border border-slate-border dark:border-slate-700 bg-white dark:bg-slate-900 text-slate-primary dark:text-white focus:outline-none focus:border-primary"
                  />
                </div>
              </div>

              <div className="flex justify-end pt-4">
                <button
                  type="button"
                  onClick={() => setStep(2)}
                  className="px-5 py-2.5 rounded-academic bg-primary text-white font-semibold text-xs sm:text-sm hover:bg-primary-hover shadow-sm transition-colors flex items-center gap-2"
                >
                  <span>Continue to Study Targets</span>
                  <ArrowRight className="w-4 h-4" />
                </button>
              </div>
            </div>
          )}

          {step === 2 && (
            <div className="space-y-5 animate-fadeIn">
              <div className="border-b border-slate-border dark:border-slate-700 pb-3 mb-4">
                <h2 className="font-bold text-slate-primary dark:text-white text-base sm:text-lg flex items-center gap-2">
                  <Clock className="w-5 h-5 text-primary" />
                  <span>Target Hours & Pomodoro Preferences</span>
                </h2>
                <p className="text-xs text-slate-secondary">Calibrate your daily and weekly study pacing</p>
              </div>

              <div>
                <div className="flex items-center justify-between mb-2">
                  <label className="text-xs font-semibold text-slate-primary dark:text-white">
                    Weekly Study Target
                  </label>
                  <span className="font-mono text-sm font-bold text-primary">{preferredHours} Hours / Week</span>
                </div>
                <input
                  type="range"
                  min="5"
                  max="60"
                  step="1"
                  value={preferredHours}
                  onChange={e => setPreferredHours(Number(e.target.value))}
                  className="w-full accent-primary cursor-pointer"
                />
                <div className="flex justify-between text-[10px] text-slate-secondary mt-1">
                  <span>5h (Light)</span>
                  <span>25h (Recommended)</span>
                  <span>60h (Intensive)</span>
                </div>
              </div>

              <div>
                <div className="flex items-center justify-between mb-2">
                  <label className="text-xs font-semibold text-slate-primary dark:text-white">
                    Daily Study Target
                  </label>
                  <span className="font-mono text-sm font-bold text-success">
                    {Math.floor(dailyTargetMinutes / 60)}h {dailyTargetMinutes % 60}m Daily
                  </span>
                </div>
                <input
                  type="range"
                  min="30"
                  max="480"
                  step="15"
                  value={dailyTargetMinutes}
                  onChange={e => setDailyTargetMinutes(Number(e.target.value))}
                  className="w-full accent-success cursor-pointer"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-primary dark:text-white mb-2">
                  Preferred Pomodoro Focus Block
                </label>
                <div className="grid grid-cols-3 gap-2">
                  {[25, 45, 60].map(mins => (
                    <button
                      key={mins}
                      type="button"
                      onClick={() => setPomodoroLength(mins)}
                      className={`p-3 rounded-academic border text-center text-xs font-semibold transition-colors ${
                        pomodoroLength === mins
                          ? 'border-primary bg-primary/10 text-primary font-bold'
                          : 'border-slate-border dark:border-slate-700 text-slate-secondary hover:text-slate-primary'
                      }`}
                    >
                      {mins} Minutes
                    </button>
                  ))}
                </div>
              </div>

              <div className="flex justify-between pt-4">
                <button
                  type="button"
                  onClick={() => setStep(1)}
                  className="px-4 py-2.5 rounded-academic border border-slate-border dark:border-slate-700 text-slate-secondary hover:text-slate-primary font-semibold text-xs sm:text-sm flex items-center gap-1.5"
                >
                  <ArrowLeft className="w-4 h-4" />
                  <span>Back</span>
                </button>
                <button
                  type="button"
                  onClick={() => setStep(3)}
                  className="px-5 py-2.5 rounded-academic bg-primary text-white font-semibold text-xs sm:text-sm hover:bg-primary-hover shadow-sm transition-colors flex items-center gap-2"
                >
                  <span>Continue to Subjects</span>
                  <ArrowRight className="w-4 h-4" />
                </button>
              </div>
            </div>
          )}

          {step === 3 && (
            <div className="space-y-5 animate-fadeIn">
              <div className="border-b border-slate-border dark:border-slate-700 pb-3 mb-4">
                <h2 className="font-bold text-slate-primary dark:text-white text-base sm:text-lg flex items-center gap-2">
                  <BookOpen className="w-5 h-5 text-primary" />
                  <span>Enrolled Subjects & Courses</span>
                </h2>
                <p className="text-xs text-slate-secondary">Select or add the academic subjects you are currently reading</p>
              </div>

              {/* Current subject list */}
              <div className="space-y-2">
                {initialSubjectList.map((s, idx) => (
                  <div
                    key={idx}
                    className="flex items-center justify-between p-3 rounded-academic border border-slate-border dark:border-slate-700 bg-slate-muted/40 dark:bg-slate-900"
                  >
                    <div className="flex items-center gap-3">
                      <span className="w-3 h-3 rounded-full flex-shrink-0" style={{ backgroundColor: s.color }} />
                      <div>
                        <div className="text-xs sm:text-sm font-semibold text-slate-primary dark:text-white">{s.name}</div>
                        <div className="text-[10px] font-mono text-slate-secondary">{s.code}</div>
                      </div>
                    </div>
                    <Check className="w-4 h-4 text-success" />
                  </div>
                ))}
              </div>

              {/* Add extra subject */}
              <form onSubmit={handleAddCustomSubject} className="flex gap-2 pt-2">
                <input
                  type="text"
                  placeholder="Subject name (e.g. Bioethics)"
                  value={newSubjName}
                  onChange={e => setNewSubjName(e.target.value)}
                  className="flex-1 px-3 py-2 text-xs rounded-academic border border-slate-border dark:border-slate-700 bg-white dark:bg-slate-900 text-slate-primary dark:text-white focus:outline-none focus:border-primary"
                />
                <input
                  type="text"
                  placeholder="Code (e.g. BIO 101)"
                  value={newSubjCode}
                  onChange={e => setNewSubjCode(e.target.value)}
                  className="w-28 px-3 py-2 text-xs rounded-academic border border-slate-border dark:border-slate-700 bg-white dark:bg-slate-900 text-slate-primary dark:text-white focus:outline-none focus:border-primary"
                />
                <button
                  type="submit"
                  className="px-3 py-2 rounded-academic bg-slate-muted dark:bg-slate-800 text-slate-primary dark:text-white text-xs font-semibold hover:bg-slate-200"
                >
                  + Add
                </button>
              </form>

              <div className="flex justify-between pt-4 border-t border-slate-border dark:border-slate-700">
                <button
                  type="button"
                  onClick={() => setStep(2)}
                  className="px-4 py-2.5 rounded-academic border border-slate-border dark:border-slate-700 text-slate-secondary hover:text-slate-primary font-semibold text-xs sm:text-sm flex items-center gap-1.5"
                >
                  <ArrowLeft className="w-4 h-4" />
                  <span>Back</span>
                </button>
                <button
                  type="button"
                  onClick={handleFinishOnboarding}
                  className="px-6 py-2.5 rounded-academic bg-primary text-white font-semibold text-xs sm:text-sm hover:bg-primary-hover shadow-sm transition-colors flex items-center gap-2"
                >
                  <Check className="w-4 h-4" />
                  <span>Launch Workspace</span>
                </button>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

