package com.shalom.android.material.datepicker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Fragment that displays a calendar for date selection.
 */
public class MaterialCalendar<S> extends Fragment {

    private static final String CALENDAR_CONSTRAINTS_KEY = "CALENDAR_CONSTRAINTS_KEY";
    private static final String DATE_SELECTOR_KEY = "DATE_SELECTOR_KEY";
    private static final String CURRENT_MONTH_KEY = "CURRENT_MONTH_KEY";

    private CalendarConstraints calendarConstraints;
    private DateSelector<S> dateSelector;
    private Month currentMonth;

    private ViewPager2 monthsPager;
    private MonthsPagerAdapter pagerAdapter;
    private Button monthYearButton;
    private ImageButton previousButton;
    private ImageButton nextButton;

    private OnSelectionChangedListener<S> selectionChangedListener;

    public interface OnSelectionChangedListener<S> {
        void onSelectionChanged(S selection);
    }

    public static <S> MaterialCalendar<S> newInstance(
            @NonNull DateSelector<S> dateSelector,
            @NonNull CalendarConstraints calendarConstraints) {
        MaterialCalendar<S> calendar = new MaterialCalendar<>();
        Bundle args = new Bundle();
        args.putParcelable(DATE_SELECTOR_KEY, dateSelector);
        args.putParcelable(CALENDAR_CONSTRAINTS_KEY, calendarConstraints);
        calendar.setArguments(args);
        return calendar;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = savedInstanceState != null ? savedInstanceState : getArguments();
        if (args != null) {
            dateSelector = args.getParcelable(DATE_SELECTOR_KEY);
            calendarConstraints = args.getParcelable(CALENDAR_CONSTRAINTS_KEY);
            currentMonth = args.getParcelable(CURRENT_MONTH_KEY);
        }

        if (currentMonth == null) {
            currentMonth = calendarConstraints.getOpenAt();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.mtrl_calendar, container, false);

        monthYearButton = root.findViewById(R.id.mtrl_picker_header_toggle);
        previousButton = root.findViewById(R.id.month_navigation_previous);
        nextButton = root.findViewById(R.id.month_navigation_next);
        monthsPager = root.findViewById(R.id.mtrl_calendar_months);

        // Setup days of week header
        setupDaysOfWeekHeader(root);

        // Setup ViewPager
        pagerAdapter = new MonthsPagerAdapter(
                calendarConstraints,
                dateSelector,
                this::onDayClick
        );
        monthsPager.setAdapter(pagerAdapter);

        // Set current month
        int currentPosition = pagerAdapter.getPositionForMonth(currentMonth);
        monthsPager.setCurrentItem(currentPosition, false);

        // Update month/year display
        updateMonthYearDisplay();

        // Setup listeners
        monthsPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                currentMonth = pagerAdapter.getMonthForPosition(position);
                updateMonthYearDisplay();
            }
        });

        previousButton.setOnClickListener(v -> {
            int position = monthsPager.getCurrentItem();
            if (position > 0) {
                monthsPager.setCurrentItem(position - 1);
            }
        });

        nextButton.setOnClickListener(v -> {
            int position = monthsPager.getCurrentItem();
            if (position < pagerAdapter.getItemCount() - 1) {
                monthsPager.setCurrentItem(position + 1);
            }
        });

        return root;
    }

    private void setupDaysOfWeekHeader(View root) {
        GridLayout daysOfWeek = root.findViewById(R.id.mtrl_calendar_days_of_week);

        String[] dayNames = {"S", "M", "T", "W", "T", "F", "S"};

        for (String dayName : dayNames) {
            TextView dayView = new TextView(requireContext());
            dayView.setText(dayName);
            dayView.setGravity(android.view.Gravity.CENTER);
            dayView.setTextSize(12);
            dayView.setLayoutParams(new GridLayout.LayoutParams(
                    GridLayout.spec(GridLayout.UNDEFINED, 1f),
                    GridLayout.spec(GridLayout.UNDEFINED, 1f)
            ));
            daysOfWeek.addView(dayView);
        }
    }

    private void updateMonthYearDisplay() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(currentMonth.getYear(), currentMonth.getMonth(), 1);

        SimpleDateFormat format = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        monthYearButton.setText(format.format(calendar.getTime()));

        // Update button states
        int currentPosition = monthsPager.getCurrentItem();
        previousButton.setEnabled(currentPosition > 0);
        nextButton.setEnabled(currentPosition < pagerAdapter.getItemCount() - 1);
    }

    private void onDayClick(long day) {
        dateSelector.select(day);

        // Refresh the current month view to show selection
        int currentPosition = monthsPager.getCurrentItem();
        pagerAdapter.notifyDataSetChanged(currentPosition);

        // Notify listener
        if (selectionChangedListener != null) {
            selectionChangedListener.onSelectionChanged(dateSelector.getSelection());
        }
    }

    public void setOnSelectionChangedListener(OnSelectionChangedListener<S> listener) {
        this.selectionChangedListener = listener;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(CALENDAR_CONSTRAINTS_KEY, calendarConstraints);
        outState.putParcelable(DATE_SELECTOR_KEY, dateSelector);
        outState.putParcelable(CURRENT_MONTH_KEY, currentMonth);
    }

    public DateSelector<S> getDateSelector() {
        return dateSelector;
    }
}
