package com.shalom.android.material.datepicker;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import org.threeten.extra.chrono.EthiopicDate;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalField;
import java.util.Calendar;
import java.util.Collection;

/**
 * Adapter for displaying days in a month grid.
 */
public class MonthAdapter extends RecyclerView.Adapter<MonthAdapter.DayViewHolder> {

    private static final String TAG = "MonthAdapter";
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
        this.firstDayOfWeek =   DayOfWeek.MONDAY.getValue();// Calendar.MONDAY;//calendar is wrong, we are using Time
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

        Log.e(TAG, "onBindViewHolder:aaa  firstDayOffset: " +firstDayOffset );
        Log.e(TAG, "onBindViewHolder:aaa  position: " +position);
        Log.e(TAG, "onBindViewHolder:aaa  month.getDaysInMonth(): " +month.getDaysInMonth());
        Log.e(TAG, "onBindViewHolder:aaa  month.getDaysInMonth(): " +month.getStartDayOfWeek());
        Log.e(TAG, "onBindViewHolder:aaa  month.getDaysInMonth(): " +month.getMonth());
        Log.e(TAG, "onBindViewHolder:aaa  month.getDaysInMonth(): " +month.getYear());

        if (position < firstDayOffset || position >= firstDayOffset + month.getDaysInMonth()) {
            // Empty cell
            holder.dayView.setText("");
            holder.dayView.setEnabled(false);
            holder.dayView.setOnClickListener(null);
        } else {
            int day = position - firstDayOffset + 1;
            holder.dayView.setText(String.valueOf(day));

            // Convert Ethiopic date to Gregorian timestamp using UTC to avoid timezone issues
            EthiopicDate ethiopicDate = EthiopicDate.of(month.getYear(), month.getMonth(), day);
            LocalDate gregorianDate = LocalDate.from(ethiopicDate);
            long timeInMillis = gregorianDate.atStartOfDay(Month.TIME_ZONE)
                    .toInstant().toEpochMilli();

            boolean isValid = calendarConstraints.isWithinBounds(timeInMillis);
            holder.dayView.setEnabled(isValid);

            // Check if this day is selected
            boolean isSelected = false;
            if (dateSelector != null) {
                Collection<Long> selectedDays = dateSelector.getSelectedDays();
                for (Long selectedDay : selectedDays) {
                    // Convert selected day timestamp to Ethiopic date using UTC to avoid timezone issues
                    LocalDate selectedGregorian = Instant.ofEpochMilli(selectedDay)
                            .atZone(Month.TIME_ZONE)
                            .toLocalDate();
                    EthiopicDate selectedEthiopic = EthiopicDate.from(selectedGregorian);

                    if (selectedEthiopic.get(ChronoField.YEAR) == month.getYear() &&
                        selectedEthiopic.get(ChronoField.MONTH_OF_YEAR) == month.getMonth() &&
                        selectedEthiopic.get(ChronoField.DAY_OF_MONTH)  == (day)) {
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
        TypedValue typedValue = new TypedValue();
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
        int monthFirstDay = month.getStartDayOfWeek();//
        int offset = monthFirstDay - firstDayOfWeek;
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
