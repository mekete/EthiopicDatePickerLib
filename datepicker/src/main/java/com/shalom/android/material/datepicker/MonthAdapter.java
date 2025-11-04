package com.shalom.android.material.datepicker;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;
import java.util.Collection;

/**
 * Adapter for displaying days in a month grid.
 */
public class MonthAdapter extends RecyclerView.Adapter<MonthAdapter.DayViewHolder> {

    private final Month month;
    private final DateSelector<?> dateSelector;
    private final CalendarConstraints calendarConstraints;
    private final OnDayClickListener onDayClickListener;
    private final int firstDayOfWeek;

    public interface OnDayClickListener {
        void onDayClick(long day);
    }


    public MonthAdapter(
            @NonNull Month month,
            @Nullable DateSelector<?> dateSelector,
            @NonNull CalendarConstraints calendarConstraints,
            @Nullable OnDayClickListener onDayClickListener) {
        this.month = month;
        this.dateSelector = dateSelector;
        this.calendarConstraints = calendarConstraints;
        this.onDayClickListener = onDayClickListener;
        this.firstDayOfWeek = Calendar.SUNDAY;
    }

    @NonNull
    @Override
    public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TextView dayView = (TextView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mtrl_calendar_day, parent, false);
        return new DayViewHolder(dayView);
    }

    @Override
    public void onBindViewHolder(@NonNull DayViewHolder holder, int position) {
        int firstDayOffset = getFirstDayOffset();

        if (position < firstDayOffset || position >= firstDayOffset + month.getDaysInMonth()) {
            // Empty cell
            holder.dayView.setText("");
            holder.dayView.setEnabled(false);
            holder.dayView.setOnClickListener(null);
        } else {
            int day = position - firstDayOffset + 1;
            holder.dayView.setText(String.valueOf(day));

            Calendar calendar = Calendar.getInstance();
            calendar.set(month.getYear(), month.getMonth(), day);
            long timeInMillis = calendar.getTimeInMillis();

            boolean isValid = calendarConstraints.isWithinBounds(timeInMillis);
            holder.dayView.setEnabled(isValid);

            // Check if this day is selected
            boolean isSelected = false;
            if (dateSelector != null) {
                Collection<Long> selectedDays = dateSelector.getSelectedDays();
                for (Long selectedDay : selectedDays) {
                    Calendar selectedCal = Calendar.getInstance();
                    selectedCal.setTimeInMillis(selectedDay);
                    if (selectedCal.get(Calendar.YEAR) == month.getYear() &&
                        selectedCal.get(Calendar.MONTH) == month.getMonth() &&
                        selectedCal.get(Calendar.DAY_OF_MONTH) == day) {
                        isSelected = true;
                        break;
                    }
                }
            }

            // Style the day view
            styleDayView(holder.dayView, isSelected, isValid);

            if (isValid) {
                final long clickedDay = timeInMillis;
                holder.dayView.setOnClickListener(v -> {
                    if (onDayClickListener != null) {
                        onDayClickListener.onDayClick(clickedDay);
                    }
                });
            } else {
                holder.dayView.setOnClickListener(null);
            }
        }
    }

    private void styleDayView(TextView dayView, boolean isSelected, boolean isValid) {
        Context context = dayView.getContext();

        if (isSelected) {
            // Selected day - circle background with primary color
            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.OVAL);
            drawable.setColor(getPrimaryColor(context));
            dayView.setBackground(drawable);
            dayView.setTextColor(Color.WHITE);
        } else {
            dayView.setBackground(null);
            if (isValid) {
                dayView.setTextColor(Color.BLACK);
            } else {
                dayView.setTextColor(Color.LTGRAY);
            }
        }
    }

    private int getPrimaryColor(Context context) {
        // Get primary color from theme
        android.util.TypedValue typedValue = new android.util.TypedValue();
        boolean resolved = context.getTheme().resolveAttribute(
                android.R.attr.colorPrimary,
                typedValue,
                true
        );
        if (resolved) {
            return typedValue.data;
        }
        // Fallback to a default color if not found
        return 0xFF6200EE; // Default Material purple
    }

    @Override
    public int getItemCount() {
        // 6 rows * 7 days = 42 cells
        return 42;
    }

    private int getFirstDayOffset() {
        int dayOfWeek = month.getDayOfWeek(1);
        int offset = dayOfWeek - firstDayOfWeek;
        if (offset < 0) {
            offset += 7;
        }
        return offset;
    }

    static class DayViewHolder extends RecyclerView.ViewHolder {
        final TextView dayView;

        DayViewHolder(@NonNull TextView dayView) {
            super(dayView);
            this.dayView = dayView;
        }
    }
}
