package com.shalom.android.material.datepicker;

import android.os.Parcelable;

/**
 * Interface for validating whether a date can be selected.
 * Based on Material Components DateValidator interface.
 */
public interface DateValidator extends Parcelable {

    /**
     * Returns true if the provided {@code date} is valid.
     *
     * @param date timestamp in milliseconds
     * @return true if valid
     */
    boolean isValid(long date);
}
