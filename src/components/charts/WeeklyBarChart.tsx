import React from 'react';
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  Tooltip,
  ResponsiveContainer,
  CartesianGrid
} from 'recharts';

interface WeeklyBarChartProps {
  data: { day: string; hours: number; target: number }[];
}

export const WeeklyBarChart: React.FC<WeeklyBarChartProps> = ({ data }) => {
  return (
    <div className="w-full h-56 select-none">
      <ResponsiveContainer width="100%" height="100%">
        <BarChart data={data} margin={{ top: 10, right: 10, left: -20, bottom: 0 }}>
          <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#E8ECF2" opacity={0.7} />
          <XAxis
            dataKey="day"
            axisLine={false}
            tickLine={false}
            tick={{ fill: '#64748B', fontSize: 11, fontWeight: 500 }}
          />
          <YAxis
            axisLine={false}
            tickLine={false}
            tick={{ fill: '#64748B', fontSize: 11 }}
            unit="h"
          />
          <Tooltip
            cursor={{ fill: 'rgba(100, 119, 217, 0.06)' }}
            contentStyle={{
              backgroundColor: '#FFFFFF',
              borderRadius: '8px',
              border: '1px solid #E8ECF2',
              boxShadow: '0 4px 12px -2px rgba(23, 32, 51, 0.08)',
              fontSize: '12px',
              padding: '8px 12px'
            }}
            formatter={(value: unknown) => {
              const val = typeof value === 'number' ? value : 0;
              return [`${val.toFixed(1)} hours`, 'Study Duration'];
            }}
          />
          <Bar
            dataKey="hours"
            fill="#6477D9"
            radius={[4, 4, 0, 0]}
            maxBarSize={32}
          />
        </BarChart>
      </ResponsiveContainer>
    </div>
  );
};

