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

        // Calculate minimum width: 7 columns × 36dp/column + padding
        float density = context.getResources().getDisplayMetrics().density;
        int minWidth = (int) (7 * 36 * density);  // 252dp minimum
        gridView.setMinimumWidth(minWidth);

        LayoutParams params = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT
        );
        addView(gridView, params);
    }

    public void setMonth(
            @NonNull Month month,
            @Nullable DateSelector<?> dateSelector,
            @NonNull CalendarConstraints calendarConstraints,
            @Nullable MonthAdapter.OnDayClickListener onDayClickListener) {

        // Check if we can reuse the existing adapter (same month)
        if (adapter != null &&
            adapter.getMonth().equals(month) &&
            adapter.getDateSelector() == dateSelector) {
            // Same month, just refresh the data (e.g., selection changed)
            adapter.notifyDataSetChanged();
            // Force GridView to maintain its width during refresh
            gridView.post(() -> gridView.requestLayout());
        } else {
            // Different month or first time, create new adapter
            adapter = new MonthAdapter(month, dateSelector, calendarConstraints, onDayClickListener);
            gridView.setAdapter(adapter);
        }
    }

    public void notifyDataSetChanged() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
            // Force GridView to remeasure and maintain width
            gridView.post(() -> gridView.requestLayout());
        }
    }
}
