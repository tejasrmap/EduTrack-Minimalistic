import React, { useState } from 'react';
import { 
  RotateCcw, 
  LogOut, 
  Check, 
  Volume2, 
  VolumeX, 
  AlertCircle 
} from 'lucide-react';
import { useAcademic } from '../context/AppContext';

export const ProfileSettingsView: React.FC = () => {
  const { user, updateUser, settings, updateSettings, resetDemoData, logout } = useAcademic();

  const [name, setName] = useState(user.name);
  const [email, setEmail] = useState(user.email);
  const [degree, setDegree] = useState(user.degree);
  const [institution, setInstitution] = useState(user.institution);
  const [targetGpa, setTargetGpa] = useState(user.targetGpa);
  const [weeklyHourGoal, setWeeklyHourGoal] = useState(user.weeklyHourGoal);

  const [focusMins, setFocusMins] = useState(settings.focusSessionMinutes);
  const [breakMins, setBreakMins] = useState(settings.breakMinutes);

  const [isResetOpen, setIsResetOpen] = useState(false);
  const [notice, setNotice] = useState('');

  const handleProfileSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    updateUser({
      name,
      email,
      degree,
      institution,
      targetGpa: Number(targetGpa),
      weeklyHourGoal: Number(weeklyHourGoal),
    });
    setNotice('Profile credentials updated.');
    setTimeout(() => setNotice(''), 3000);
  };

  const handleSettingsSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    updateSettings({
      focusSessionMinutes: Number(focusMins),
      breakMinutes: Number(breakMins),
    });
    setNotice('Focus room intervals updated.');
    setTimeout(() => setNotice(''), 3000);
  };

  const handleConfirmReset = () => {
    resetDemoData();
    setIsResetOpen(false);
    setNotice('Demo dataset restored to initial state.');
    setTimeout(() => setNotice(''), 3000);
  };

  return (
    <div className="max-w-4xl mx-auto space-y-6">
      {/* Header */}
      <div className="paper-card rounded-2xl p-5 flex items-center justify-between">
        <div>
          <h2 className="font-serif text-xl font-bold text-stone-900">
            Scholar Credentials & Workspace Preferences
          </h2>
          <p className="text-xs text-stone-500">
            Configure personal profile, focus intervals and reset cached demo data
          </p>
        </div>

        {notice && (
          <span className="text-xs font-mono text-[#2A5C43] bg-[#EBF3ED] px-3 py-1 rounded-md flex items-center gap-1.5">
            <Check className="w-3.5 h-3.5" />
            {notice}
          </span>
        )}
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        {/* Profile */}
        <div className="paper-card rounded-2xl p-6 space-y-4">
          <div className="pb-3 border-b border-stone-200">
            <h3 className="font-serif text-base font-bold text-stone-900">
              Scholar Information
            </h3>
            <p className="text-xs text-stone-500">
              University matriculation and degree details
            </p>
          </div>

          <form onSubmit={handleProfileSubmit} className="space-y-3">
            <div>
              <label className="block text-xs font-medium text-stone-700 mb-1">
                Full Name
              </label>
              <input
                type="text"
                value={name}
                onChange={(e) => setName(e.target.value)}
                className="w-full px-3 py-2 rounded-xl bg-stone-50 border border-stone-200 text-xs text-stone-800 focus:outline-none focus:border-[#2A5C43]"
              />
            </div>

            <div>
              <label className="block text-xs font-medium text-stone-700 mb-1">
                Email Address
              </label>
              <input
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                className="w-full px-3 py-2 rounded-xl bg-stone-50 border border-stone-200 text-xs text-stone-800 focus:outline-none focus:border-[#2A5C43]"
              />
            </div>

            <div>
              <label className="block text-xs font-medium text-stone-700 mb-1">
                Degree Programme
              </label>
              <input
                type="text"
                value={degree}
                onChange={(e) => setDegree(e.target.value)}
                className="w-full px-3 py-2 rounded-xl bg-stone-50 border border-stone-200 text-xs text-stone-800 focus:outline-none focus:border-[#2A5C43]"
              />
            </div>

            <div>
              <label className="block text-xs font-medium text-stone-700 mb-1">
                Faculty / University
              </label>
              <input
                type="text"
                value={institution}
                onChange={(e) => setInstitution(e.target.value)}
                className="w-full px-3 py-2 rounded-xl bg-stone-50 border border-stone-200 text-xs text-stone-800 focus:outline-none focus:border-[#2A5C43]"
              />
            </div>

            <div className="grid grid-cols-2 gap-3">
              <div>
                <label className="block text-xs font-medium text-stone-700 mb-1">
                  Target GPA
                </label>
                <input
                  type="number"
                  step="0.01"
                  min="2.0"
                  max="4.0"
                  value={targetGpa}
                  onChange={(e) => setTargetGpa(Number(e.target.value))}
                  className="w-full px-3 py-2 rounded-xl bg-stone-50 border border-stone-200 text-xs text-stone-800 focus:outline-none focus:border-[#2A5C43] font-mono"
                />
              </div>

              <div>
                <label className="block text-xs font-medium text-stone-700 mb-1">
                  Weekly Goal (hrs)
                </label>
                <input
                  type="number"
                  min="10"
                  max="70"
                  value={weeklyHourGoal}
                  onChange={(e) => setWeeklyHourGoal(Number(e.target.value))}
                  className="w-full px-3 py-2 rounded-xl bg-stone-50 border border-stone-200 text-xs text-stone-800 focus:outline-none focus:border-[#2A5C43] font-mono"
                />
              </div>
            </div>

            <button
              type="submit"
              className="w-full mt-2 py-2 rounded-xl bg-[#2A5C43] hover:bg-[#234e38] text-white text-xs font-medium transition-colors"
            >
              Update Credentials
            </button>
          </form>
        </div>

        {/* Preferences */}
        <div className="paper-card rounded-2xl p-6 space-y-4">
          <div className="pb-3 border-b border-stone-200">
            <h3 className="font-serif text-base font-bold text-stone-900">
              Study Room Intervals
            </h3>
            <p className="text-xs text-stone-500">
              Reading session lengths and ambient chime options
            </p>
          </div>

          <form onSubmit={handleSettingsSubmit} className="space-y-4">
            <div className="p-3 rounded-xl bg-stone-50 border border-stone-200 flex items-center justify-between">
              <div>
                <p className="text-xs font-medium text-stone-800">
                  Quiet Audio Chimes
                </p>
                <p className="text-[11px] text-stone-500">
                  Subtle chime tone upon session conclusion
                </p>
              </div>
              <button
                type="button"
                onClick={() => updateSettings({ soundAlerts: !settings.soundAlerts })}
                className="p-1.5 rounded-lg bg-stone-200 text-stone-700"
              >
                {settings.soundAlerts ? <Volume2 className="w-4 h-4 text-[#2A5C43]" /> : <VolumeX className="w-4 h-4" />}
              </button>
            </div>

            <div className="grid grid-cols-2 gap-3">
              <div>
                <label className="block text-xs font-medium text-stone-700 mb-1">
                  Reading Block (Mins)
                </label>
                <input
                  type="number"
                  min="15"
                  max="180"
                  value={focusMins}
                  onChange={(e) => setFocusMins(Number(e.target.value))}
                  className="w-full px-3 py-2 rounded-xl bg-stone-50 border border-stone-200 text-xs text-stone-800 focus:outline-none focus:border-[#2A5C43] font-mono"
                />
              </div>

              <div>
                <label className="block text-xs font-medium text-stone-700 mb-1">
                  Intermission (Mins)
                </label>
                <input
                  type="number"
                  min="5"
                  max="45"
                  value={breakMins}
                  onChange={(e) => setBreakMins(Number(e.target.value))}
                  className="w-full px-3 py-2 rounded-xl bg-stone-50 border border-stone-200 text-xs text-stone-800 focus:outline-none focus:border-[#2A5C43] font-mono"
                />
              </div>
            </div>

            <button
              type="submit"
              className="w-full py-2 rounded-xl bg-stone-800 hover:bg-stone-900 text-white text-xs font-medium transition-colors"
            >
              Save Intervals
            </button>
          </form>

          {/* Reset Demo State & Sign Out */}
          <div className="pt-4 border-t border-stone-200 space-y-2">
            <button
              type="button"
              onClick={() => setIsResetOpen(true)}
              className="w-full py-2 rounded-xl bg-stone-100 hover:bg-stone-200 text-stone-700 text-xs font-medium flex items-center justify-center gap-2 transition-colors"
            >
              <RotateCcw className="w-3.5 h-3.5" />
              <span>Reset Demo Academic Data</span>
            </button>

            <button
              type="button"
              onClick={logout}
              className="w-full py-2 rounded-xl bg-rose-50 hover:bg-rose-100 text-rose-700 text-xs font-medium flex items-center justify-center gap-2 transition-colors"
            >
              <LogOut className="w-3.5 h-3.5" />
              <span>Sign Out</span>
            </button>
          </div>
        </div>
      </div>

      {/* Reset Confirmation Modal */}
      {isResetOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-stone-900/40 backdrop-blur-xs animate-fadeIn">
          <div className="bg-white w-full max-w-md rounded-2xl p-6 shadow-xl border border-stone-200 animate-modalIn">
            <div className="flex items-center gap-3 text-amber-700 mb-3">
              <AlertCircle className="w-5 h-5" />
              <h3 className="font-serif text-lg font-bold">
                Reset Demo Data?
              </h3>
            </div>
            <p className="text-xs text-stone-600 mb-4 leading-relaxed">
              This will re-initialize the Cambridge architecture subjects, lent term assignments, and sample reading logs for Clara Beauchamp.
            </p>
            <div className="flex items-center justify-end gap-3">
              <button
                onClick={() => setIsResetOpen(false)}
                className="px-4 py-2 text-xs text-stone-500 hover:text-stone-800"
              >
                Cancel
              </button>
              <button
                onClick={handleConfirmReset}
                className="px-4 py-2 rounded-xl bg-stone-900 hover:bg-stone-800 text-white text-xs font-medium"
              >
                Confirm Reset
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
