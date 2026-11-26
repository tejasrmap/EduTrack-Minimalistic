import React, { useState } from 'react';
import { 
  Moon, 
  Sun, 
  Download, 
  Upload, 
  RotateCcw, 
  Trash2, 
  Save, 
  FileSpreadsheet, 
  FileCode, 
  ShieldAlert 
} from 'lucide-react';
import { useData } from '../../contexts/DataContext';
import { useToast } from '../../contexts/ToastContext';

export const SettingsPage: React.FC = () => {
  const { settings, updateSettings, toggleTheme, exportJSON, exportCSV, importBackup, resetDemoData, clearAllData } = useData();
  const { toast } = useToast();

  const [focusMins, setFocusMins] = useState(settings.pomodoroFocusMinutes);
  const [shortBreakMins, setShortBreakMins] = useState(settings.pomodoroShortBreakMinutes);
  const [longBreakMins, setLongBreakMins] = useState(settings.pomodoroLongBreakMinutes);
  const [longInterval, setLongInterval] = useState(settings.longBreakInterval);
  const [weekStart, setWeekStart] = useState(settings.weekStartDay);
  const [timeFmt, setTimeFmt] = useState(settings.timeFormat);
  const [sound, setSound] = useState(settings.soundEnabled);
  const [dailyTarget, setDailyTarget] = useState(settings.dailyStudyTargetMinutes);

  // Import State
  const [importJsonText, setImportJsonText] = useState('');
  const [showImportModal, setShowImportModal] = useState(false);
  const [showClearConfirm, setShowClearConfirm] = useState(false);

  const handleSaveTimerSettings = (e: React.FormEvent) => {
    e.preventDefault();
    updateSettings({
      pomodoroFocusMinutes: Number(focusMins),
      pomodoroShortBreakMinutes: Number(shortBreakMins),
      pomodoroLongBreakMinutes: Number(longBreakMins),
      longBreakInterval: Number(longInterval),
      weekStartDay: weekStart,
      timeFormat: timeFmt,
      soundEnabled: sound,
      dailyStudyTargetMinutes: Number(dailyTarget)
    });
    toast.success('Settings updated');
  };

  const handleDownloadJSON = () => {
    const jsonStr = exportJSON();
    const blob = new Blob([jsonStr], { type: 'application/json' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', `edutrack_backup_${new Date().toISOString().split('T')[0]}.json`);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    toast.success('JSON backup downloaded');
  };

  const handleDownloadCSV = () => {
    const csvStr = exportCSV();
    const blob = new Blob([csvStr], { type: 'text/csv;charset=utf-8;' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', `edutrack_history_${new Date().toISOString().split('T')[0]}.csv`);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    toast.success('CSV log exported');
  };

  const handleImportSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!importJsonText.trim()) {
      toast.error('Please paste valid JSON backup data');
      return;
    }

    const res = importBackup(importJsonText.trim());
    if (res.success) {
      toast.success(res.message);
      setShowImportModal(false);
      setImportJsonText('');
    } else {
      toast.error(res.message);
    }
  };

  const handleFileUpload = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;

    const reader = new FileReader();
    reader.onload = (event) => {
      const text = event.target?.result as string;
      if (text) {
        setImportJsonText(text);
      }
    };
    reader.readAsText(file);
  };

  const handleResetDemo = () => {
    if (window.confirm('Reset all academic data to the default Julian Vance demo dataset? Any custom additions will be replaced.')) {
      resetDemoData();
      toast.success('Demo data restored successfully');
    }
  };

  const handleExecuteClear = () => {
    clearAllData();
    setShowClearConfirm(false);
    toast.info('All local application data cleared');
  };

  return (
    <div className="space-y-6 max-w-4xl mx-auto">
      {/* Header */}
      <div>
        <h1 className="text-2xl font-bold text-academic-text tracking-tight">System & Workspace Settings</h1>
        <p className="text-sm text-academic-muted mt-1">
          Customize productivity timers, visual appearance, data backups and academic preferences.
        </p>
      </div>

      {/* Appearance Section */}
      <div className="bg-academic-card p-6 rounded-xl border border-academic-border shadow-sm">
        <h3 className="font-semibold text-sm text-academic-text mb-4 pb-2 border-b border-academic-border">
          Interface Appearance
        </h3>

        <div className="flex items-center justify-between">
          <div>
            <span className="font-semibold text-sm text-academic-text block">Application Theme</span>
            <span className="text-xs text-academic-muted">
              Choose between Minimal Academic Light mode or High-contrast Dark mode
            </span>
          </div>

          <button
            onClick={toggleTheme}
            className="flex items-center gap-2 px-4 py-2 rounded-lg border border-academic-border bg-academic-muted-bg hover:bg-academic-card transition-colors text-xs font-semibold text-academic-text shadow-sm"
          >
            {settings.theme === 'dark' ? (
              <>
                <Moon className="w-4 h-4 text-academic-primary" />
                <span>Dark Theme</span>
              </>
            ) : (
              <>
                <Sun className="w-4 h-4 text-amber-500" />
                <span>Light Theme</span>
              </>
            )}
          </button>
        </div>
      </div>

      {/* Timer & Routine Preferences */}
      <form onSubmit={handleSaveTimerSettings} className="bg-academic-card p-6 rounded-xl border border-academic-border shadow-sm space-y-5">
        <h3 className="font-semibold text-sm text-academic-text pb-2 border-b border-academic-border flex items-center justify-between">
          <span>Timer & Study Routine Configurations</span>
          <span className="text-xs font-normal text-academic-muted">Saved to persistent storage</span>
        </h3>

        <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
          <div>
            <label className="block text-xs font-medium text-academic-text mb-1">
              Focus Duration (Minutes)
            </label>
            <input
              type="number"
              min="5"
              max="120"
              value={focusMins}
              onChange={e => setFocusMins(Number(e.target.value))}
              className="w-full px-3 py-2 text-xs bg-academic-muted-bg border border-academic-border rounded-lg text-academic-text focus:outline-none focus:ring-1 focus:ring-academic-primary font-medium"
            />
          </div>

          <div>
            <label className="block text-xs font-medium text-academic-text mb-1">
              Short Break (Minutes)
            </label>
            <input
              type="number"
              min="1"
              max="30"
              value={shortBreakMins}
              onChange={e => setShortBreakMins(Number(e.target.value))}
              className="w-full px-3 py-2 text-xs bg-academic-muted-bg border border-academic-border rounded-lg text-academic-text focus:outline-none focus:ring-1 focus:ring-academic-primary font-medium"
            />
          </div>

          <div>
            <label className="block text-xs font-medium text-academic-text mb-1">
              Long Break (Minutes)
            </label>
            <input
              type="number"
              min="5"
              max="60"
              value={longBreakMins}
              onChange={e => setLongBreakMins(Number(e.target.value))}
              className="w-full px-3 py-2 text-xs bg-academic-muted-bg border border-academic-border rounded-lg text-academic-text focus:outline-none focus:ring-1 focus:ring-academic-primary font-medium"
            />
          </div>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
          <div>
            <label className="block text-xs font-medium text-academic-text mb-1">
              Long Break Interval
            </label>
            <select
              value={longInterval}
              onChange={e => setLongInterval(Number(e.target.value))}
              className="w-full px-3 py-2 text-xs bg-academic-muted-bg border border-academic-border rounded-lg text-academic-text focus:outline-none focus:ring-1 focus:ring-academic-primary"
            >
              <option value={2}>Every 2 sessions</option>
              <option value={3}>Every 3 sessions</option>
              <option value={4}>Every 4 sessions (Default)</option>
              <option value={5}>Every 5 sessions</option>
            </select>
          </div>

          <div>
            <label className="block text-xs font-medium text-academic-text mb-1">
              Week Starts On
            </label>
            <select
              value={weekStart}
              onChange={e => setWeekStart(e.target.value as any)}
              className="w-full px-3 py-2 text-xs bg-academic-muted-bg border border-academic-border rounded-lg text-academic-text focus:outline-none focus:ring-1 focus:ring-academic-primary"
            >
              <option value="monday">Monday (Academic standard)</option>
              <option value="sunday">Sunday</option>
            </select>
          </div>

          <div>
            <label className="block text-xs font-medium text-academic-text mb-1">
              Time Format
            </label>
            <select
              value={timeFmt}
              onChange={e => setTimeFmt(e.target.value as any)}
              className="w-full px-3 py-2 text-xs bg-academic-muted-bg border border-academic-border rounded-lg text-academic-text focus:outline-none focus:ring-1 focus:ring-academic-primary"
            >
              <option value="12h">12-Hour (e.g. 02:30 PM)</option>
              <option value="24h">24-Hour (e.g. 14:30)</option>
            </select>
          </div>

          <div>
            <label className="block text-xs font-medium text-academic-text mb-1">
              Daily Target (Minutes)
            </label>
            <input
              type="number"
              min="30"
              max="720"
              step="15"
              value={dailyTarget}
              onChange={e => setDailyTarget(Number(e.target.value))}
              className="w-full px-3 py-2 text-xs bg-academic-muted-bg border border-academic-border rounded-lg text-academic-text focus:outline-none focus:ring-1 focus:ring-academic-primary font-medium"
            />
          </div>
        </div>

        <div className="flex items-center justify-between pt-2">
          <label className="flex items-center gap-2 cursor-pointer text-xs font-medium text-academic-text">
            <input
              type="checkbox"
              checked={sound}
              onChange={e => setSound(e.target.checked)}
              className="rounded border-academic-border text-academic-primary focus:ring-academic-primary"
            />
            <span>Enable Synthesized Web Audio Chimes on session completion</span>
          </label>

          <button
            type="submit"
            className="flex items-center gap-2 px-4 py-2 bg-academic-primary text-white rounded-lg text-xs font-semibold hover:bg-academic-primary/90 transition-colors shadow-sm"
          >
            <Save className="w-3.5 h-3.5" />
            <span>Save Preferences</span>
          </button>
        </div>
      </form>

      {/* Data Management, Import/Export & Reset */}
      <div className="bg-academic-card p-6 rounded-xl border border-academic-border shadow-sm space-y-4">
        <h3 className="font-semibold text-sm text-academic-text pb-2 border-b border-academic-border">
          Data Portability & Database Maintenance
        </h3>

        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div className="p-4 rounded-xl border border-academic-border bg-academic-muted-bg/40 space-y-2">
            <div className="flex items-center gap-2">
              <FileCode className="w-4 h-4 text-academic-primary" />
              <h4 className="font-semibold text-xs text-academic-text">Export Complete JSON Backup</h4>
            </div>
            <p className="text-[11px] text-academic-muted">
              Download your entire database (subjects, tasks, notes, goals, study sessions) as a portable JSON file.
            </p>
            <button
              onClick={handleDownloadJSON}
              className="flex items-center gap-1.5 px-3 py-1.5 rounded-lg border border-academic-border bg-academic-card hover:bg-academic-muted-bg text-xs font-semibold text-academic-text transition-colors mt-2"
            >
              <Download className="w-3.5 h-3.5" />
              <span>Download Backup (.json)</span>
            </button>
          </div>

          <div className="p-4 rounded-xl border border-academic-border bg-academic-muted-bg/40 space-y-2">
            <div className="flex items-center gap-2">
              <FileSpreadsheet className="w-4 h-4 text-emerald-600" />
              <h4 className="font-semibold text-xs text-academic-text">Export Study Logs (CSV)</h4>
            </div>
            <p className="text-[11px] text-academic-muted">
              Export study duration history and notes into a tabular CSV spreadsheet compatible with Excel or Google Sheets.
            </p>
            <button
              onClick={handleDownloadCSV}
              className="flex items-center gap-1.5 px-3 py-1.5 rounded-lg border border-academic-border bg-academic-card hover:bg-academic-muted-bg text-xs font-semibold text-academic-text transition-colors mt-2"
            >
              <Download className="w-3.5 h-3.5" />
              <span>Export CSV</span>
            </button>
          </div>
        </div>

        <div className="flex flex-wrap items-center justify-between gap-3 pt-4 border-t border-academic-border">
          <div className="flex items-center gap-2">
            <button
              onClick={() => setShowImportModal(true)}
              className="flex items-center gap-1.5 px-3 py-2 rounded-lg border border-academic-border bg-academic-card hover:bg-academic-muted-bg text-xs font-semibold text-academic-text transition-colors shadow-sm"
            >
              <Upload className="w-4 h-4 text-academic-primary" />
              <span>Import Backup JSON</span>
            </button>

            <button
              onClick={handleResetDemo}
              className="flex items-center gap-1.5 px-3 py-2 rounded-lg border border-academic-border bg-academic-card hover:bg-academic-muted-bg text-xs font-semibold text-academic-muted hover:text-academic-text transition-colors shadow-sm"
            >
              <RotateCcw className="w-4 h-4 text-amber-500" />
              <span>Reset Demo Data</span>
            </button>
          </div>

          <button
            onClick={() => setShowClearConfirm(true)}
            className="flex items-center gap-1.5 px-3 py-2 rounded-lg border border-red-200 dark:border-red-900/40 text-xs font-semibold text-red-600 dark:text-red-400 hover:bg-red-50 dark:hover:bg-red-950/30 transition-colors"
          >
            <Trash2 className="w-4 h-4" />
            <span>Clear All Data</span>
          </button>
        </div>
      </div>

      {/* Import Modal */}
      {showImportModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/40 backdrop-blur-sm animate-fade-in">
          <div className="bg-academic-card w-full max-w-lg rounded-xl border border-academic-border shadow-xl p-5 space-y-4 animate-slide-up">
            <h3 className="font-semibold text-academic-text flex items-center gap-2">
              <Upload className="w-4 h-4 text-academic-primary" />
              Restore Database from JSON Backup
            </h3>

            <p className="text-xs text-academic-muted">
              Upload a previously exported `.json` file or paste the JSON text directly. This will replace your current workspace records.
            </p>

            <div>
              <input
                type="file"
                accept=".json,application/json"
                onChange={handleFileUpload}
                className="text-xs text-academic-muted file:mr-3 file:py-1.5 file:px-3 file:rounded-lg file:border-0 file:text-xs file:font-semibold file:bg-academic-primary file:text-white hover:file:bg-academic-primary/90 cursor-pointer"
              />
            </div>

            <textarea
              rows={6}
              placeholder="Or paste backup JSON payload here..."
              value={importJsonText}
              onChange={e => setImportJsonText(e.target.value)}
              className="w-full p-2.5 text-xs font-mono bg-academic-muted-bg border border-academic-border rounded-lg text-academic-text focus:outline-none focus:ring-1 focus:ring-academic-primary"
            />

            <div className="flex items-center justify-end gap-2 pt-2 border-t border-academic-border">
              <button
                type="button"
                onClick={() => setShowImportModal(false)}
                className="px-3 py-1.5 rounded-lg border border-academic-border text-xs font-medium text-academic-muted hover:text-academic-text"
              >
                Cancel
              </button>
              <button
                type="button"
                onClick={handleImportSubmit}
                className="px-4 py-1.5 rounded-lg bg-academic-primary text-white text-xs font-semibold hover:bg-academic-primary/90 shadow-sm"
              >
                Validate & Import
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Clear All Confirmation Modal */}
      {showClearConfirm && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/40 backdrop-blur-sm animate-fade-in">
          <div className="bg-academic-card w-full max-w-md rounded-xl border border-red-200 dark:border-red-900/60 shadow-xl p-5 space-y-4 animate-slide-up">
            <div className="flex items-center gap-3 text-red-600">
              <ShieldAlert className="w-6 h-6" />
              <h3 className="font-bold text-base">Clear Entire Database?</h3>
            </div>

            <p className="text-xs text-academic-muted leading-relaxed">
              This will irreversibly delete all subjects, assignments, notes, goals, examination dates and study logs stored in your browser's local storage.
            </p>

            <div className="flex items-center justify-end gap-2 pt-2 border-t border-academic-border">
              <button
                onClick={() => setShowClearConfirm(false)}
                className="px-3 py-1.5 rounded-lg border border-academic-border text-xs font-medium text-academic-muted hover:text-academic-text"
              >
                Cancel
              </button>
              <button
                onClick={handleExecuteClear}
                className="px-4 py-1.5 rounded-lg bg-red-600 text-white text-xs font-semibold hover:bg-red-700 shadow-sm"
              >
                Permanently Delete All Data
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
