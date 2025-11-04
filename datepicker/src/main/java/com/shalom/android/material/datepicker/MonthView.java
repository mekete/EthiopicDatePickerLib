package com.shalom.android.material.datepicker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Displays a single month's calendar grid.
 */
public class MonthView extends FrameLayout {

    private final RecyclerView recyclerView;
    private MonthAdapter adapter;

    public MonthView(@NonNull Context context) {
        super(context);

        recyclerView = new RecyclerView(context);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 7));
        recyclerView.setHasFixedSize(true);

        addView(recyclerView, new LayoutParams(
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
        recyclerView.setAdapter(adapter);
    }

    public void notifyDataSetChanged() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
}
