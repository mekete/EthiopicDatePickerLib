package com.shalom.android.material.datepicker;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Represents a month in a calendar.
 * Simplified version based on Material Components DatePicker Month class.
 */
public class Month implements Comparable<Month>, Parcelable {

    private final int month;
    private final int year;
    private final Calendar calendar;
    private final int daysInMonth;
    private final int daysInWeek = 7;

    private Month(int year, int month) {
        this.month = month;
        this.year = year;
        this.calendar = getCanonicalCalendar();
        this.calendar.set(Calendar.YEAR, year);
        this.calendar.set(Calendar.MONTH, month);
        this.calendar.set(Calendar.DAY_OF_MONTH, 1);
        this.daysInMonth = this.calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    /**
     * Creates a Month instance for the given year and month.
     */
    public static Month create(int year, int month) {
        return new Month(year, month);
    }

    /**
     * Creates a Month instance from a timestamp in milliseconds.
     */
    public static Month create(long timeInMillis) {
        Calendar calendar = getCanonicalCalendar();
        calendar.setTimeInMillis(timeInMillis);
        return create(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH));
    }

    /**
     * Creates the current month.
     */
    public static Month current() {
        Calendar today = Calendar.getInstance();
        return create(today.get(Calendar.YEAR), today.get(Calendar.MONTH));
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public int getDaysInMonth() {
        return daysInMonth;
    }

    public int getDaysInWeek() {
        return daysInWeek;
    }

    /**
     * Returns the timestamp in milliseconds for the first day of this month.
     */
    public long getTimeInMillis() {
        return calendar.getTimeInMillis();
    }

    /**
     * Gets the day of week for a given day in this month.
     * @param day Day of month (1-based)
     * @return Day of week (Calendar.SUNDAY, Calendar.MONDAY, etc.)
     */
    public int getDayOfWeek(int day) {
        Calendar cal = getCanonicalCalendar();
        cal.setTimeInMillis(calendar.getTimeInMillis());
        cal.set(Calendar.DAY_OF_MONTH, day);
        return cal.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * Returns the Month that is {@code months} months after this Month.
     */
    public Month monthsLater(int months) {
        Calendar laterCalendar = getCanonicalCalendar();
        laterCalendar.setTimeInMillis(calendar.getTimeInMillis());
        laterCalendar.add(Calendar.MONTH, months);
        return create(laterCalendar.get(Calendar.YEAR), laterCalendar.get(Calendar.MONTH));
    }

    /**
     * Returns the number of months between this Month and {@code other}.
     */
    public int monthsUntil(Month other) {
        int yearDiff = other.year - this.year;
        int monthDiff = other.month - this.month;
        return yearDiff * 12 + monthDiff;
    }

    private static Calendar getCanonicalCalendar() {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.clear();
        return calendar;
    }

    @Override
    public int compareTo(@NonNull Month other) {
        int yearComparison = Integer.compare(this.year, other.year);
        if (yearComparison != 0) {
            return yearComparison;
        }
        return Integer.compare(this.month, other.month);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Month)) return false;
        Month that = (Month) o;
        return month == that.month && year == that.year;
    }

    @Override
    public int hashCode() {
        return 31 * year + month;
    }

    // Parcelable implementation

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(year);
        dest.writeInt(month);
    }

    public static final Creator<Month> CREATOR = new Creator<Month>() {
        @Override
        public Month createFromParcel(Parcel in) {
            int year = in.readInt();
            int month = in.readInt();
            return Month.create(year, month);
        }

        @Override
        public Month[] newArray(int size) {
            return new Month[size];
        }
    };
}
