package com.shalom.android.material.datepicker;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Adapter for displaying a grid of years for selection.
 */
public class YearGridAdapter extends RecyclerView.Adapter<YearGridAdapter.YearViewHolder> {

    private static final int MIN_YEAR = 1900;
    private static final int MAX_YEAR = 2100;

    private final List<Integer> years;
    private int selectedYear;
    private final OnYearSelectedListener listener;

    public interface OnYearSelectedListener {
        void onYearSelected(int year);
    }

    public YearGridAdapter(int currentYear, OnYearSelectedListener listener) {
        this.years = new ArrayList<>();
        for (int year = MIN_YEAR; year <= MAX_YEAR; year++) {
            years.add(year);
        }
        this.selectedYear = currentYear;
        this.listener = listener;
    }

    @NonNull
    @Override
    public YearViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mtrl_calendar_year_item, parent, false);
        return new YearViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull YearViewHolder holder, int position) {
        int year = years.get(position);
        holder.bind(year);
    }

    @Override
    public int getItemCount() {
        return years.size();
    }

    public int getPositionForYear(int year) {
        return years.indexOf(year);
    }

    class YearViewHolder extends RecyclerView.ViewHolder {
        private final TextView yearTextView;

        YearViewHolder(@NonNull View itemView) {
            super(itemView);
            yearTextView = itemView.findViewById(R.id.year_text);
        }

        void bind(int year) {
            yearTextView.setText(String.valueOf(year));

            // Highlight current year
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            boolean isCurrentYear = year == currentYear;
            boolean isSelectedYear = year == selectedYear;

            if (isSelectedYear) {
                // Selected year - use primary color
                int primaryColor = ContextCompat.getColor(itemView.getContext(),
                        android.R.color.holo_blue_light);
                yearTextView.setTextColor(primaryColor);
                yearTextView.setTypeface(null, android.graphics.Typeface.BOLD);
            } else if (isCurrentYear) {
                // Current year - use accent color
                int accentColor = ContextCompat.getColor(itemView.getContext(),
                        android.R.color.holo_blue_dark);
                yearTextView.setTextColor(accentColor);
                yearTextView.setTypeface(null, android.graphics.Typeface.NORMAL);
            } else {
                // Regular year
                int textColor = ContextCompat.getColor(itemView.getContext(),
                        android.R.color.black);
                yearTextView.setTextColor(textColor);
                yearTextView.setTypeface(null, android.graphics.Typeface.NORMAL);
            }

            // Set click listener
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onYearSelected(year);
                }
            });
        }
    }
}
