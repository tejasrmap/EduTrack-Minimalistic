import React from 'react';
import {
  AreaChart,
  Area,
  XAxis,
  YAxis,
  Tooltip,
  ResponsiveContainer,
  CartesianGrid
} from 'recharts';

interface MonthlyTrendChartProps {
  data: { date: string; hours: number }[];
}

export const MonthlyTrendChart: React.FC<MonthlyTrendChartProps> = ({ data }) => {
  return (
    <div className="w-full h-56 select-none">
      <ResponsiveContainer width="100%" height="100%">
        <AreaChart data={data} margin={{ top: 10, right: 10, left: -20, bottom: 0 }}>
          <defs>
            <linearGradient id="colorHours" x1="0" y1="0" x2="0" y2="1">
              <stop offset="5%" stopColor="#6477D9" stopOpacity={0.25} />
              <stop offset="95%" stopColor="#6477D9" stopOpacity={0.0} />
            </linearGradient>
          </defs>
          <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#E8ECF2" opacity={0.7} />
          <XAxis
            dataKey="date"
            axisLine={false}
            tickLine={false}
            tick={{ fill: '#64748B', fontSize: 11 }}
          />
          <YAxis
            axisLine={false}
            tickLine={false}
            tick={{ fill: '#64748B', fontSize: 11 }}
            unit="h"
          />
          <Tooltip
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
          <Area
            type="monotone"
            dataKey="hours"
            stroke="#6477D9"
            strokeWidth={2}
            fillOpacity={1}
            fill="url(#colorHours)"
          />
        </AreaChart>
      </ResponsiveContainer>
    </div>
  );
};

