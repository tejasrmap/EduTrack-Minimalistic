import React from 'react';
import { LucideIcon } from 'lucide-react';

interface EmptyStateProps {
  icon: LucideIcon;
  title: string;
  description: string;
  actionLabel?: string;
  onAction?: () => void;
}

export const EmptyState: React.FC<EmptyStateProps> = ({
  icon: Icon,
  title,
  description,
  actionLabel,
  onAction,
}) => {
  return (
    <div className="card-academic p-8 sm:p-12 text-center flex flex-col items-center justify-center max-w-lg mx-auto my-6 border-dashed border-2">
      <div className="w-12 h-12 rounded-full bg-slate-muted dark:bg-slate-800 flex items-center justify-center text-slate-secondary mb-3.5">
        <Icon className="w-6 h-6 text-primary" />
      </div>
      <h3 className="font-bold text-slate-primary dark:text-white text-base sm:text-lg mb-1">
        {title}
      </h3>
      <p className="text-xs sm:text-sm text-slate-secondary max-w-sm mb-5 leading-relaxed">
        {description}
      </p>
      {actionLabel && onAction && (
        <button
          onClick={onAction}
          className="px-4 py-2 rounded-academic bg-primary text-white text-xs sm:text-sm font-semibold hover:bg-primary-hover shadow-sm transition-colors"
        >
          {actionLabel}
        </button>
      )}
    </div>
  );
};

