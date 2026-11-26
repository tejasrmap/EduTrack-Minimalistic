import React, { useState } from 'react';
import { 
  Bell, 
  CheckCheck, 
  Trash2, 
  Clock, 
  AlertCircle, 
  Calendar, 
  Award, 
  Flame, 
  ExternalLink,
  CheckCircle2
} from 'lucide-react';
import { Link } from 'react-router-dom';
import { useData } from '../../contexts/DataContext';
import { useToast } from '../../contexts/ToastContext';
import { NotificationItem } from '../../types';
import EmptyState from '../../components/common/EmptyState';
import { format, parseISO } from 'date-fns';

export const NotificationsPage: React.FC = () => {
  const { notifications, markNotificationAsRead, markAllNotificationsAsRead, deleteNotification } = useData();
  const { toast } = useToast();

  const [filter, setFilter] = useState<'all' | 'unread'>('all');

  const filteredNotifications = notifications.filter(n => {
    if (filter === 'unread') return !n.read;
    return true;
  });

  const handleMarkAllRead = () => {
    markAllNotificationsAsRead();
    toast.success('All notifications marked as read');
  };

  const handleDelete = (id: string) => {
    deleteNotification(id);
    toast.info('Notification dismissed');
  };

  const getIcon = (type: NotificationItem['type']) => {
    switch (type) {
      case 'deadline':
        return <AlertCircle className="w-4 h-4 text-red-500" />;
      case 'exam':
        return <Calendar className="w-4 h-4 text-amber-500" />;
      case 'streak':
        return <Flame className="w-4 h-4 text-orange-500" />;
      case 'goal':
        return <Award className="w-4 h-4 text-emerald-500" />;
      default:
        return <Bell className="w-4 h-4 text-academic-primary" />;
    }
  };

  const unreadCount = notifications.filter(n => !n.read).length;

  return (
    <div className="space-y-6 max-w-4xl mx-auto">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-academic-text tracking-tight">Notification Center</h1>
          <p className="text-sm text-academic-muted mt-1">
            System alerts, deadline warnings, exam milestones and study reminders.
          </p>
        </div>

        {unreadCount > 0 && (
          <button
            onClick={handleMarkAllRead}
            className="flex items-center gap-1.5 px-3 py-1.5 rounded-lg border border-academic-border bg-academic-card hover:bg-academic-muted-bg text-xs font-semibold text-academic-text transition-colors shadow-sm self-start sm:self-auto"
          >
            <CheckCheck className="w-4 h-4 text-academic-primary" />
            <span>Mark All as Read</span>
          </button>
        )}
      </div>

      {/* Tabs */}
      <div className="flex items-center justify-between border-b border-academic-border pb-3">
        <div className="flex items-center gap-2">
          <button
            onClick={() => setFilter('all')}
            className={`px-3 py-1.5 rounded-lg text-xs font-semibold transition-colors ${
              filter === 'all'
                ? 'bg-academic-primary text-white shadow-sm'
                : 'text-academic-muted hover:text-academic-text hover:bg-academic-muted-bg'
            }`}
          >
            All Alerts ({notifications.length})
          </button>
          <button
            onClick={() => setFilter('unread')}
            className={`px-3 py-1.5 rounded-lg text-xs font-semibold transition-colors ${
              filter === 'unread'
                ? 'bg-academic-primary text-white shadow-sm'
                : 'text-academic-muted hover:text-academic-text hover:bg-academic-muted-bg'
            }`}
          >
            Unread ({unreadCount})
          </button>
        </div>
      </div>

      {/* Notifications List */}
      <div className="bg-academic-card rounded-xl border border-academic-border overflow-hidden">
        {filteredNotifications.length === 0 ? (
          <EmptyState
            title="All caught up!"
            description={filter === 'unread' ? "You have no unread notifications." : "There are no notifications in your activity center."}
          />
        ) : (
          <div className="divide-y divide-academic-border">
            {filteredNotifications.map(item => (
              <div 
                key={item.id}
                className={`p-4 flex items-start justify-between gap-4 transition-colors ${
                  item.read ? 'hover:bg-academic-muted-bg/30' : 'bg-academic-primary/[0.03] hover:bg-academic-primary/[0.06]'
                }`}
              >
                <div className="flex items-start gap-3 min-w-0">
                  <div className="mt-0.5 p-2 rounded-lg bg-academic-muted-bg border border-academic-border flex-shrink-0">
                    {getIcon(item.type)}
                  </div>

                  <div className="min-w-0">
                    <div className="flex items-center gap-2">
                      <h4 className={`text-sm ${item.read ? 'font-medium text-academic-text' : 'font-semibold text-academic-text'}`}>
                        {item.title}
                      </h4>
                      {!item.read && (
                        <span className="w-2 h-2 rounded-full bg-academic-primary flex-shrink-0" />
                      )}
                    </div>

                    <p className="text-xs text-academic-muted mt-1 leading-relaxed">
                      {item.message}
                    </p>

                    <div className="flex items-center gap-3 mt-2 text-[11px] text-academic-muted">
                      <span className="flex items-center gap-1">
                        <Clock className="w-3 h-3" />
                        {format(parseISO(item.timestamp), 'MMM d, yyyy • h:mm a')}
                      </span>

                      {item.linkTo && (
                        <Link 
                          to={item.linkTo}
                          className="flex items-center gap-1 text-academic-primary hover:underline font-medium"
                        >
                          View Details
                          <ExternalLink className="w-3 h-3" />
                        </Link>
                      )}
                    </div>
                  </div>
                </div>

                <div className="flex items-center gap-1 flex-shrink-0">
                  {!item.read && (
                    <button
                      onClick={() => markNotificationAsRead(item.id)}
                      className="p-1.5 text-academic-muted hover:text-academic-primary rounded hover:bg-academic-muted-bg transition-colors"
                      title="Mark as read"
                    >
                      <CheckCircle2 className="w-4 h-4" />
                    </button>
                  )}
                  <button
                    onClick={() => handleDelete(item.id)}
                    className="p-1.5 text-academic-muted hover:text-red-600 rounded hover:bg-academic-muted-bg transition-colors"
                    title="Dismiss"
                  >
                    <Trash2 className="w-4 h-4" />
                  </button>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};
