package com.shalom.android.material.datepicker;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Custom GridView for displaying a month's calendar with proper alignment and nested scroll support.
 */
public class MaterialCalendarGridView extends GridView {

    private final boolean nestedScrollable;

    public MaterialCalendarGridView(@NonNull Context context) {
        this(context, null);
    }

    public MaterialCalendarGridView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaterialCalendarGridView(
            @NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // Enable nested scrolling for proper behavior inside ScrollView/ViewPager
        this.nestedScrollable = true;

        // Disable focus to prevent keyboard navigation issues
        if (isInEditMode()) {
            return;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (nestedScrollable) {
            // When nested in a scrollable container (like ViewPager2),
            // measure the full height to show all rows without scrolling
            int expandSpec = MeasureSpec.makeMeasureSpec(
                    MeasureSpec.getSize(heightMeasureSpec),
                    MeasureSpec.AT_MOST
            );
            super.onMeasure(widthMeasureSpec, expandSpec);

            // Force width to match parent to prevent shrinking
            // This ensures the grid maintains full width even after data changes
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = getMeasuredHeight();
            setMeasuredDimension(width, height);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, @Nullable android.graphics.Rect previouslyFocusedRect) {
        if (gainFocus) {
            // When gaining focus, ensure we focus on a valid day within the month
            if (getAdapter() instanceof MonthAdapter) {
                MonthAdapter adapter = (MonthAdapter) getAdapter();
                int position = adapter.firstPositionInMonth();
                setSelection(position);
            }
        }
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (super.onKeyDown(keyCode, event)) {
            // Ensure selection stays within the current month's bounds
            int currentPosition = getSelectedItemPosition();
            if (getAdapter() instanceof MonthAdapter) {
                MonthAdapter adapter = (MonthAdapter) getAdapter();
                if (!adapter.withinMonth(currentPosition)) {
                    // Move selection to nearest valid position
                    int firstPos = adapter.firstPositionInMonth();
                    int lastPos = adapter.lastPositionInMonth();
                    if (currentPosition < firstPos) {
                        setSelection(firstPos);
                    } else if (currentPosition > lastPos) {
                        setSelection(lastPos);
                    }
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public void setSelection(int position) {
        // Constrain selection to valid month positions
        if (getAdapter() instanceof MonthAdapter) {
            MonthAdapter adapter = (MonthAdapter) getAdapter();
            if (!adapter.withinMonth(position)) {
                return; // Don't select positions outside the month
            }
        }
        super.setSelection(position);
    }
}
