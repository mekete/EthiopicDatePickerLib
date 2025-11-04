package com.shalom.android.material.datepicker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.Calendar;

/**
 * Adapter for displaying days of the week in the calendar header.
 */
public class DaysOfWeekAdapter extends BaseAdapter {

    private static final int DAYS_IN_WEEK = 7;
    private final int firstDayOfWeek;
    private final String[] dayNames;

    public DaysOfWeekAdapter() {
        this(Calendar.SUNDAY);
    }

    public DaysOfWeekAdapter(int firstDayOfWeek) {
        this.firstDayOfWeek = firstDayOfWeek;
        // We'll populate day names when we have context in getView
        this.dayNames = new String[DAYS_IN_WEEK];
    }

    @Override
    public int getCount() {
        return DAYS_IN_WEEK;
    }

    @Override
    public Object getItem(int position) {
        return positionToDayOfWeek(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        TextView dayView = (TextView) convertView;

        if (dayView == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            dayView = (TextView) inflater.inflate(
                    R.layout.mtrl_calendar_day_of_week,
                    parent,
                    false
            );
        }

        // Get day name from context resources
        Context context = parent.getContext();
        String[] weekdayNames = context.getResources().getStringArray(R.array.weekday_names_short);

        // Map position to weekday name
        // Our weekday_names_short array starts with Monday, so we need to map correctly
        int dayOfWeek = positionToDayOfWeek(position);
        String dayName = getDayName(weekdayNames, dayOfWeek);

        dayView.setText(dayName);

        // Set content description for accessibility
        dayView.setContentDescription(dayName);

        return dayView;
    }

    private int positionToDayOfWeek(int position) {
        int dayOfWeek = position + firstDayOfWeek;
        if (dayOfWeek > Calendar.SATURDAY) {
            dayOfWeek -= DAYS_IN_WEEK;
        }
        return dayOfWeek;
    }

    private String getDayName(String[] weekdayNames, int dayOfWeek) {
        // weekdayNames array: [Mon, Tue, Wed, Thu, Fri, Sat, Sun]
        // Calendar constants: SUNDAY=1, MONDAY=2, ..., SATURDAY=7
        // Map to array index: Mon=0, Tue=1, ..., Sun=6

        int index;
        switch (dayOfWeek) {
            case Calendar.MONDAY:
                index = 0;
                break;
            case Calendar.TUESDAY:
                index = 1;
                break;
            case Calendar.WEDNESDAY:
                index = 2;
                break;
            case Calendar.THURSDAY:
                index = 3;
                break;
            case Calendar.FRIDAY:
                index = 4;
                break;
            case Calendar.SATURDAY:
                index = 5;
                break;
            case Calendar.SUNDAY:
                index = 6;
                break;
            default:
                index = 0;
        }

        return weekdayNames[index];
    }
}
