package com.shalom.android.material.datepicker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
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
    private RecyclerView yearPicker;
    private YearGridAdapter yearAdapter;
    private GridView daysOfWeekHeader;
    private Button monthYearButton;
    private ImageButton previousButton;
    private ImageButton nextButton;

    private boolean isYearPickerVisible = false;
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
        yearPicker = root.findViewById(R.id.mtrl_calendar_year_picker);
        daysOfWeekHeader = root.findViewById(R.id.mtrl_calendar_days_of_week);

        // Setup days of week header
        setupDaysOfWeekHeader(root);

        // Setup year picker
        setupYearPicker();

        // Setup ViewPager
        pagerAdapter = new MonthsPagerAdapter(
                calendarConstraints,
                dateSelector,
                this::onDayClick
        );
        monthsPager.setAdapter(pagerAdapter);

        // Configure ViewPager2 to prevent adjacent pages from being visible
        // Ensure pages are clipped properly to prevent page overlap on some devices
        View recyclerView = monthsPager.getChildAt(0);
        if (recyclerView != null) {
            recyclerView.setClipToOutline(true);
            if (recyclerView instanceof RecyclerView) {
                ((RecyclerView) recyclerView).setClipChildren(true);
            }
        }

        // Add page transformer to ensure pages are properly separated
        monthsPager.setPageTransformer((page, position) -> {
            // Only show the current page, hide adjacent pages
            if (Math.abs(position) >= 1.0f) {
                page.setAlpha(0f);
            } else {
                page.setAlpha(1f);
            }
        });

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

        monthYearButton.setOnClickListener(v -> toggleYearPicker());

        // Fix alignment issue: Force both GridViews to remeasure after initial layout
        // This ensures they calculate column widths with the parent's final width
        root.post(() -> {
            daysOfWeekHeader.requestLayout();
            monthsPager.requestLayout();
        });

        return root;
    }

    private void setupDaysOfWeekHeader(View root) {
        GridView daysOfWeek = root.findViewById(R.id.mtrl_calendar_days_of_week);

        // Set minimum width to match the date grid: 7 columns × 36dp
        float density = requireContext().getResources().getDisplayMetrics().density;
        int minWidth = (int) (7 * 36 * density);  // 252dp minimum
        daysOfWeek.setMinimumWidth(minWidth);

        // Set up the adapter for days of week
        DaysOfWeekAdapter adapter = new DaysOfWeekAdapter(Calendar.SUNDAY);
        daysOfWeek.setAdapter(adapter);
        daysOfWeek.setEnabled(false); // Make header non-interactive
    }

    private void updateMonthYearDisplay() {
        // Display Ethiopic month name and year
        String[] monthNames = requireContext().getResources().getStringArray(R.array.ethiopian_months);
        String monthName = (currentMonth.getMonth() >= 1 && currentMonth.getMonth() <= 13)
                ? monthNames[currentMonth.getMonth() - 1]
                : "";

        String displayText = monthName + " " + currentMonth.getYear();
        monthYearButton.setText(displayText);

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

    private void setupYearPicker() {
        // Setup GridLayoutManager with 3 columns
        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), 3);
        yearPicker.setLayoutManager(layoutManager);

        // Setup adapter with current year
        int currentYear = currentMonth.getYear();
        yearAdapter = new YearGridAdapter(currentYear, this::onYearSelected);
        yearPicker.setAdapter(yearAdapter);
    }

    private void toggleYearPicker() {
        isYearPickerVisible = !isYearPickerVisible;

        if (isYearPickerVisible) {
            // Show year picker, hide calendar
            monthsPager.setVisibility(View.GONE);
            daysOfWeekHeader.setVisibility(View.GONE);
            previousButton.setVisibility(View.GONE);
            nextButton.setVisibility(View.GONE);
            yearPicker.setVisibility(View.VISIBLE);

            // Scroll to current year
            int currentYear = currentMonth.getYear();
            int position = yearAdapter.getPositionForYear(currentYear);
            yearPicker.scrollToPosition(position);
        } else {
            // Show calendar, hide year picker
            monthsPager.setVisibility(View.VISIBLE);
            daysOfWeekHeader.setVisibility(View.VISIBLE);
            previousButton.setVisibility(View.VISIBLE);
            nextButton.setVisibility(View.VISIBLE);
            yearPicker.setVisibility(View.GONE);
        }
    }

    private void onYearSelected(int year) {
        // Calculate the selected month in the new year
        Month newMonth = Month.create(year, currentMonth.getMonth());

        // Check if the new month is within constraints
        if (newMonth.compareTo(calendarConstraints.getStart()) >= 0 &&
                newMonth.compareTo(calendarConstraints.getEnd()) <= 0) {
            currentMonth = newMonth;

            // Update the ViewPager to show the new month
            int newPosition = pagerAdapter.getPositionForMonth(currentMonth);
            monthsPager.setCurrentItem(newPosition, false);

            // Update display
            updateMonthYearDisplay();
        }

        // Close year picker
        toggleYearPicker();
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
