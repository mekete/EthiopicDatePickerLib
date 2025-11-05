package com.shalom.android.material.datepicker;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Adapter for ViewPager2 to scroll through months.
 */
public class MonthsPagerAdapter extends RecyclerView.Adapter<MonthsPagerAdapter.MonthViewHolder> {

    private final CalendarConstraints calendarConstraints;
    private final DateSelector<?> dateSelector;
    private final MonthAdapter.OnDayClickListener onDayClickListener;
    private final int monthCount;

    public MonthsPagerAdapter(
            @NonNull CalendarConstraints calendarConstraints,
            @Nullable DateSelector<?> dateSelector,
            @Nullable MonthAdapter.OnDayClickListener onDayClickListener) {
        this.calendarConstraints = calendarConstraints;
        this.dateSelector = dateSelector;
        this.onDayClickListener = onDayClickListener;
        this.monthCount = calendarConstraints.getStart().monthsUntil(calendarConstraints.getEnd()) + 1;
    }

    @NonNull
    @Override
    public MonthViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MonthView monthView = new MonthView(parent.getContext());
        monthView.setLayoutParams(new RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.MATCH_PARENT
        ));
        return new MonthViewHolder(monthView);
    }

    @Override
    public void onBindViewHolder(@NonNull MonthViewHolder holder, int position) {
        Month month = calendarConstraints.getStart().monthsLater(position);
        holder.monthView.setMonth(month, dateSelector, calendarConstraints, onDayClickListener);
    }

    @Override
    public int getItemCount() {
        return monthCount;
    }

    public int getPositionForMonth(Month month) {
        return calendarConstraints.getStart().monthsUntil(month);
    }

    public Month getMonthForPosition(int position) {
        return calendarConstraints.getStart().monthsLater(position);
    }

    public void notifyDataSetChanged(int position) {
        notifyItemChanged(position);
    }

    static class MonthViewHolder extends RecyclerView.ViewHolder {
        final MonthView monthView;

        MonthViewHolder(@NonNull MonthView monthView) {
            super(monthView);
            this.monthView = monthView;
        }
    }





}
