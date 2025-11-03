package com.shalom.android.material.datepicker;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Used to limit the display range of {@link MaterialDatePicker} and set default selection.
 * Based on Material Components CalendarConstraints class.
 */
public final class CalendarConstraints implements Parcelable {

    @NonNull private final Month start;
    @NonNull private final Month end;
    @NonNull private final Month openAt;
    @Nullable private final DateValidator validator;

    private CalendarConstraints(
            @NonNull Month start,
            @NonNull Month end,
            @NonNull Month openAt,
            @Nullable DateValidator validator) {
        this.start = start;
        this.end = end;
        this.openAt = openAt;
        this.validator = validator;
    }

    @NonNull
    public Month getStart() {
        return start;
    }

    @NonNull
    public Month getEnd() {
        return end;
    }

    @NonNull
    public Month getOpenAt() {
        return openAt;
    }

    @Nullable
    public DateValidator getDateValidator() {
        return validator;
    }

    /**
     * Checks if the given month is within the allowed range.
     */
    public boolean isWithinBounds(long date) {
        if (start.getTimeInMillis() > date) {
            return false;
        }
        if (end.getTimeInMillis() < date) {
            return false;
        }
        if (validator != null) {
            return validator.isValid(date);
        }
        return true;
    }

    // Parcelable implementation

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeParcelable(start, 0);
        dest.writeParcelable(end, 0);
        dest.writeParcelable(openAt, 0);
        dest.writeParcelable(validator, 0);
    }

    public static final Creator<CalendarConstraints> CREATOR =
            new Creator<CalendarConstraints>() {
                @NonNull
                @Override
                public CalendarConstraints createFromParcel(@NonNull Parcel source) {
                    Month start = source.readParcelable(Month.class.getClassLoader());
                    Month end = source.readParcelable(Month.class.getClassLoader());
                    Month openAt = source.readParcelable(Month.class.getClassLoader());
                    DateValidator validator = source.readParcelable(DateValidator.class.getClassLoader());
                    return new CalendarConstraints(start, end, openAt, validator);
                }

                @NonNull
                @Override
                public CalendarConstraints[] newArray(int size) {
                    return new CalendarConstraints[size];
                }
            };

    /**
     * Builder for {@link CalendarConstraints}.
     */
    public static final class Builder {

        private static final long DEFAULT_START = Month.create(1900, 0).getTimeInMillis();
        private static final long DEFAULT_END = Month.create(2100, 11).getTimeInMillis();

        private long start = DEFAULT_START;
        private long end = DEFAULT_END;
        private Long openAt;
        private DateValidator validator;

        public Builder() {}

        /**
         * Sets the earliest month.
         *
         * @param month Timestamp in milliseconds
         */
        @NonNull
        public Builder setStart(long month) {
            start = month;
            return this;
        }

        /**
         * Sets the latest month.
         *
         * @param month Timestamp in milliseconds
         */
        @NonNull
        public Builder setEnd(long month) {
            end = month;
            return this;
        }

        /**
         * Sets the month the calendar should openAt.
         *
         * @param month Timestamp in milliseconds
         */
        @NonNull
        public Builder setOpenAt(long month) {
            openAt = month;
            return this;
        }

        /**
         * Sets the validator for selectable dates.
         */
        @NonNull
        public Builder setValidator(@Nullable DateValidator validator) {
            this.validator = validator;
            return this;
        }

        /**
         * Creates a {@link CalendarConstraints} instance.
         */
        @NonNull
        public CalendarConstraints build() {
            if (openAt == null) {
                openAt = Month.current().getTimeInMillis();
            }

            Month startMonth = Month.create(start);
            Month endMonth = Month.create(end);
            Month openAtMonth = Month.create(openAt);

            if (openAtMonth.compareTo(startMonth) < 0) {
                openAtMonth = startMonth;
            }
            if (openAtMonth.compareTo(endMonth) > 0) {
                openAtMonth = endMonth;
            }

            return new CalendarConstraints(startMonth, endMonth, openAtMonth, validator);
        }
    }
}
