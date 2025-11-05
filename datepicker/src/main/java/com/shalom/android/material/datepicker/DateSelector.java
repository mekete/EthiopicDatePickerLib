package com.shalom.android.material.datepicker;

import android.os.Parcelable;

import androidx.annotation.Nullable;

import java.util.Collection;

/**
 * Interface for date selection behavior in the date picker.
 * Based on Material Components DateSelector interface.
 *
 * @param <S> The type of selection (e.g., Long for single date, Pair<Long, Long> for date range)
 */
public interface DateSelector<S> extends Parcelable {

    /**
     * Returns the current selection.
     */
    @Nullable
    S getSelection();

    /**
     * Sets the current selection.
     */
    void setSelection(@Nullable S selection);

    /**
     * Returns all selected days as timestamps.
     */
    Collection<Long> getSelectedDays();

    /**
     * Returns the text to display as the selected value.
     */
    String getSelectionDisplayString();

    /**
     * Returns true if the current selection is valid.
     */
    boolean isSelectionComplete();

    /**
     * Handles selection of a date.
     *
     * @param selection timestamp in milliseconds
     */
    void select(long selection);
}
