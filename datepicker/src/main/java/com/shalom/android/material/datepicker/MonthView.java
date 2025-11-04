package com.shalom.android.material.datepicker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Displays a single month's calendar grid using MaterialCalendarGridView.
 */
public class MonthView extends FrameLayout {

    private final MaterialCalendarGridView gridView;
    private MonthAdapter adapter;

    public MonthView(@NonNull Context context) {
        super(context);

        // Inflate the MaterialCalendarGridView layout
        LayoutInflater inflater = LayoutInflater.from(context);
        gridView = (MaterialCalendarGridView) inflater.inflate(
                R.layout.mtrl_calendar_month,
                this,
                false
        );

        addView(gridView, new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT
        ));
    }

    public void setMonth(
            @NonNull Month month,
            @Nullable DateSelector<?> dateSelector,
            @NonNull CalendarConstraints calendarConstraints,
            @Nullable MonthAdapter.OnDayClickListener onDayClickListener) {
        adapter = new MonthAdapter(month, dateSelector, calendarConstraints, onDayClickListener);
        gridView.setAdapter(adapter);
    }

    public void notifyDataSetChanged() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
}
