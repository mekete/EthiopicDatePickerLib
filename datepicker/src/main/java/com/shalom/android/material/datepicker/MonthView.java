package com.shalom.android.material.datepicker;

import android.content.Context;
import android.view.Gravity;
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

        // Directly instantiate MaterialCalendarGridView to avoid ClassCastException
        // from layout inflation conflicts with Material Components library
        gridView = new MaterialCalendarGridView(context);
        gridView.setId(R.id.month_grid);
        gridView.setNumColumns(7);
        gridView.setGravity(Gravity.CENTER);
        gridView.setHorizontalSpacing(0);
        gridView.setVerticalSpacing(0);
        gridView.setStretchMode(MaterialCalendarGridView.STRETCH_COLUMN_WIDTH);
        gridView.setSelector(android.R.color.transparent);

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
