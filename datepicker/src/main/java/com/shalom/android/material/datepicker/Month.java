package com.shalom.android.material.datepicker;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import org.threeten.extra.chrono.EthiopicDate;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;

/**
 * Represents a month in the Ethiopic calendar.
 * Uses threeten-extra library for Ethiopic date support.
 */
public class Month implements Comparable<Month>, Parcelable {

//    static final ZoneId TIME_ZONE = ZoneId.of("Africa/Nairobi");
    static final ZoneId TIME_ZONE = ZoneId.systemDefault();
//    public static final ZoneId TIME_ZONE = ZoneId.of("UTC");//ZoneId.systemDefault();//

    private final EthiopicDate ethiopicMonthStart;

    private Month(int year, int month) {
        this.ethiopicMonthStart = EthiopicDate.of(year, month, 1);
    }

    private Month(EthiopicDate monthStartDate) {
        this.ethiopicMonthStart = monthStartDate.with(ChronoField.DAY_OF_MONTH, 1);
    }

    public static Month create(int year, int month) {
        return new Month(year, month);
    }

    public static Month create(long timeInMillis) {
        EthiopicDate ethiopicDate = EthiopicDate.from(
                Instant.ofEpochMilli(timeInMillis).atZone(TIME_ZONE)
        );
        return new Month(ethiopicDate);
    }

    /** Creates the current month in Ethiopic calendar. */
    public static Month current() {
        EthiopicDate ethiopicDate = EthiopicDate.now(TIME_ZONE);
        return new Month(ethiopicDate);
    }

    public long getTimeInMillis() {
        LocalDate gregorianDate = LocalDate.from(ethiopicMonthStart);
        return gregorianDate.atStartOfDay(TIME_ZONE).toInstant().toEpochMilli();
    }

    public int getMonth() {
        return ethiopicMonthStart.get(ChronoField.MONTH_OF_YEAR);
    }

    public int getYear() {
        return ethiopicMonthStart.get(ChronoField.YEAR);
    }

    public int getDaysInMonth() {
        return ethiopicMonthStart.lengthOfMonth();
    }

    public int getDayOfWeek(int day) {
        EthiopicDate updatedDate = ethiopicMonthStart.with(ChronoField.DAY_OF_MONTH, day);
        return updatedDate.get(ChronoField.DAY_OF_WEEK);
    }

    public Month monthsLater(int months) {
        EthiopicDate nextMonth = ethiopicMonthStart.plus(months, ChronoUnit.MONTHS)
                .with(ChronoField.DAY_OF_MONTH, 1);
        return new Month(nextMonth);
    }

    public int monthsUntil(Month end) {
        return (int) ethiopicMonthStart.until(end.ethiopicMonthStart, ChronoUnit.MONTHS);
    }

    // ===================== Comparable =====================
    @Override
    public int compareTo(@NonNull Month other) {
        int yearComparison = Integer.compare(this.getYear(), other.getYear());
        if (yearComparison != 0) return yearComparison;
        return Integer.compare(this.getMonth(), other.getMonth());
    }

    // ===================== Equals & HashCode =====================
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Month)) return false;
        Month other = (Month) o;
        return this.getYear() == other.getYear() && this.getMonth() == other.getMonth();
    }

    @Override
    public int hashCode() {
        return 31 * getYear() + getMonth();
    }

    // ===================== Parcelable =====================
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(getYear());
        dest.writeInt(getMonth());
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
