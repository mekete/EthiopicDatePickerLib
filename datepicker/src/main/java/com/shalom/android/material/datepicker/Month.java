package com.shalom.android.material.datepicker;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import org.threeten.extra.chrono.EthiopicChronology;
import org.threeten.extra.chrono.EthiopicDate;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Represents a month in the Ethiopic calendar.
 * Uses threeten-extra library for Ethiopic date support.
 */
public class Month implements Comparable<Month>, Parcelable {

    private final int month; // 1-13 for Ethiopic calendar
    private final int year;  // Ethiopic year
    private final int daysInMonth;
    private final int daysInWeek = 7;

    private Month(int year, int month) {
        this.month = month;
        this.year = year;
        // Calculate days in this Ethiopic month
        // Months 1-12 have 30 days, month 13 (Pagume) has 5 or 6 days
        if (month >= 1 && month <= 12) {
            this.daysInMonth = 30;
        } else if (month == 13) {
            // Ethiopic leap year: year % 4 == 3
            this.daysInMonth = ((year % 4) == 3) ? 6 : 5;
        } else {
            this.daysInMonth = 30; // Default
        }
    }

    /**
     * Creates a Month instance for the given Ethiopic year and month.
     * @param year Ethiopic year
     * @param month Ethiopic month (1-13)
     */
    public static Month create(int year, int month) {
        return new Month(year, month);
    }

    /**
     * Creates a Month instance from a Gregorian timestamp in milliseconds.
     * Converts Gregorian to Ethiopic calendar.
     */
    public static Month create(long timeInMillis) {
        // Convert timestamp to LocalDate
        LocalDate gregorianDate = LocalDate.ofInstant(
                java.time.Instant.ofEpochMilli(timeInMillis),
                ZoneId.systemDefault()
        );

        // Convert to Ethiopic date
        EthiopicDate ethiopicDate = EthiopicDate.from(gregorianDate);

        return create(ethiopicDate.get(ChronoField.YEAR),
                     ethiopicDate.get(ChronoField.MONTH_OF_YEAR));
    }

    /**
     * Creates the current month in Ethiopic calendar.
     */
    public static Month current() {
        EthiopicDate today = EthiopicDate.now();
        return create(today.get(ChronoField.YEAR),
                     today.get(ChronoField.MONTH_OF_YEAR));
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
     * Returns the Gregorian timestamp in milliseconds for the first day of this Ethiopic month.
     */
    public long getTimeInMillis() {
        EthiopicDate ethiopicDate = EthiopicDate.of(year, month, 1);
        LocalDate gregorianDate = LocalDate.from(ethiopicDate);
        return gregorianDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * Gets the day of week for a given day in this Ethiopic month.
     * @param day Day of month (1-based)
     * @return Day of week (Calendar.SUNDAY, Calendar.MONDAY, etc.)
     */
    public int getDayOfWeek(int day) {
        EthiopicDate ethiopicDate = EthiopicDate.of(year, month, day);
        LocalDate gregorianDate = LocalDate.from(ethiopicDate);

        // Convert Java 8 DayOfWeek to Calendar constant
        // Java 8: MONDAY=1, SUNDAY=7
        // Calendar: SUNDAY=1, MONDAY=2
        int javaDayOfWeek = gregorianDate.getDayOfWeek().getValue();
        return (javaDayOfWeek % 7) + 1; // Convert to Calendar constants
    }

    /**
     * Returns the Month that is {@code months} months after this Ethiopic Month.
     */
    public Month monthsLater(int months) {
        // Ethiopic calendar has 13 months per year
        int totalMonths = (year * 13 + month) + months;
        int newYear = (totalMonths - 1) / 13;
        int newMonth = ((totalMonths - 1) % 13) + 1;
        return create(newYear, newMonth);
    }

    /**
     * Returns the number of months between this Month and {@code other}.
     * Calculated based on Ethiopic calendar (13 months per year).
     */
    public int monthsUntil(Month other) {
        int yearDiff = other.year - this.year;
        int monthDiff = other.month - this.month;
        return yearDiff * 13 + monthDiff;
    }

    /**
     * Gets the month name for this Ethiopic month.
     */
    public String getMonthName() {
        String[] monthNames = {
            "Meskerem", "Tikimt", "Hidar", "Tahsas", "Tir", "Yekatit",
            "Megabit", "Miazia", "Ginbot", "Sene", "Hamle", "Nehase", "Pagume"
        };
        if (month >= 1 && month <= 13) {
            return monthNames[month - 1];
        }
        return "";
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
