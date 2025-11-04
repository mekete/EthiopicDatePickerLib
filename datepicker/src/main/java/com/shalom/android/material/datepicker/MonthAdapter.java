package com.shalom.android.material.datepicker;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.threeten.extra.chrono.EthiopicDate;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.Calendar;
import java.util.Collection;

/**
 * Adapter for displaying days in a month grid using GridView.
 */
public class MonthAdapter extends BaseAdapter {

    private static final String TAG = "MonthAdapter";
    private static final int MAXIMUM_GRID_CELLS = 42; // 6 rows * 7 days

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

    public Month getMonth() {
        return month;
    }

    public DateSelector<?> getDateSelector() {
        return dateSelector;
    }

    @Override
    public int getCount() {
        return MAXIMUM_GRID_CELLS;
    }

    @Override
    public Object getItem(int position) {
        if (withinMonth(position)) {
            return positionToDay(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Context context = parent.getContext();
        TextView dayView = (TextView) convertView;

        if (dayView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            dayView = (TextView) inflater.inflate(R.layout.mtrl_calendar_day, parent, false);
        }

        int firstDayOffset = getFirstDayOffset();

        if (position < firstDayOffset || position >= firstDayOffset + month.getDaysInMonth()) {
            // Empty cell - outside the month
            dayView.setText("");
            dayView.setEnabled(false);
            dayView.setVisibility(View.INVISIBLE);
            dayView.setOnClickListener(null);
        } else {
            dayView.setVisibility(View.VISIBLE);
            int day = position - firstDayOffset + 1;
            dayView.setText(String.valueOf(day));

            // Convert Ethiopic date to Gregorian timestamp
            EthiopicDate ethiopicDate = EthiopicDate.of(month.getYear(), month.getMonth(), day);
            LocalDate gregorianDate = LocalDate.from(ethiopicDate);
            long timeInMillis = gregorianDate.atStartOfDay(Month.TIME_ZONE)
                    .toInstant().toEpochMilli();

            boolean isValid = calendarConstraints.isWithinBounds(timeInMillis);
            dayView.setEnabled(isValid);

            // Check if this day is selected
            boolean isSelected = false;
            if (dateSelector != null) {
                Collection<Long> selectedDays = dateSelector.getSelectedDays();
                for (Long selectedDay : selectedDays) {
                    LocalDate selectedGregorian = Instant.ofEpochMilli(selectedDay)
                            .atZone(Month.TIME_ZONE)
                            .toLocalDate();
                    EthiopicDate selectedEthiopic = EthiopicDate.from(selectedGregorian);

                    if (selectedEthiopic.get(ChronoField.YEAR) == month.getYear() &&
                        selectedEthiopic.get(ChronoField.MONTH_OF_YEAR) == month.getMonth() &&
                        selectedEthiopic.get(ChronoField.DAY_OF_MONTH) == day) {
                        isSelected = true;
                        break;
                    }
                }
            }

            // Style the day view
            styleDayView(dayView, isSelected, isValid);

            if (isValid) {
                final long clickedDay = timeInMillis;
                dayView.setOnClickListener(v -> {
                    if (onDayClickListener != null) {
                        onDayClickListener.onDayClick(clickedDay);
                    }
                });
            } else {
                dayView.setOnClickListener(null);
            }
        }

        return dayView;
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
            // Restore the original ripple background from the theme
            android.util.TypedValue outValue = new android.util.TypedValue();
            context.getTheme().resolveAttribute(
                    android.R.attr.selectableItemBackgroundBorderless,
                    outValue,
                    true
            );
            if (outValue.resourceId != 0) {
                dayView.setBackgroundResource(outValue.resourceId);
            } else {
                dayView.setBackground(null);
            }

            if (isValid) {
                dayView.setTextColor(Color.BLACK);
            } else {
                dayView.setTextColor(Color.LTGRAY);
            }
        }
    }

    private int getPrimaryColor(Context context) {
        android.util.TypedValue typedValue = new android.util.TypedValue();
        boolean resolved = context.getTheme().resolveAttribute(
                android.R.attr.colorPrimary,
                typedValue,
                true
        );
        if (resolved) {
            return typedValue.data;
        }
        return 0xFF6200EE; // Default Material purple
    }

    private int getFirstDayOffset() {
        int dayOfWeek = month.getDayOfWeek(1);
        int offset = dayOfWeek - firstDayOfWeek;
        if (offset < 0) {
            offset += 7;
        }
        return offset;
    }

    /**
     * Returns the position in the grid where the first day of the month appears.
     */
    public int firstPositionInMonth() {
        return getFirstDayOffset();
    }

    /**
     * Returns the position in the grid where the last day of the month appears.
     */
    public int lastPositionInMonth() {
        return getFirstDayOffset() + month.getDaysInMonth() - 1;
    }

    /**
     * Returns true if the position is within the bounds of the month.
     */
    public boolean withinMonth(int position) {
        return position >= firstPositionInMonth() && position <= lastPositionInMonth();
    }

    /**
     * Converts a grid position to a day number (1-based).
     */
    public int positionToDay(int position) {
        return position - firstPositionInMonth() + 1;
    }

    /**
     * Converts a day number (1-based) to a grid position.
     */
    public int dayToPosition(int day) {
        return firstPositionInMonth() + day - 1;
    }
}
