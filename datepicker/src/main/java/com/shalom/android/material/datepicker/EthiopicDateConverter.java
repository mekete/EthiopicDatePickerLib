package com.shalom.android.material.datepicker;

import androidx.annotation.NonNull;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Converts between Gregorian and Ethiopic calendar dates.
 * Based on the Ethiopic calendar algorithm.
 *
 * The Ethiopic calendar has 13 months:
 * - 12 months of 30 days each
 * - 1 month (Pagume) of 5 or 6 days depending on leap year
 *
 * Month names:
 * 1. Meskerem, 2. Tikimt, 3. Hidar, 4. Tahsas, 5. Tir, 6. Yekatit,
 * 7. Megabit, 8. Miazia, 9. Ginbot, 10. Sene, 11. Hamle, 12. Nehase, 13. Pagume
 */
public class EthiopicDateConverter {

    private static final String[] ETHIOPIC_MONTH_NAMES = {
            "Meskerem", "Tikimt", "Hidar", "Tahsas", "Tir", "Yekatit",
            "Megabit", "Miazia", "Ginbot", "Sene", "Hamle", "Nehase", "Pagume"
    };

    private static final int ETHIOPIC_EPOCH_OFFSET_DAYS = 2796; // Days between Gregorian and Ethiopic epoch

    /**
     * Represents an Ethiopic date.
     */
    public static class EthiopicDate {
        public final int year;
        public final int month; // 1-13
        public final int day;   // 1-30 (or 1-5/6 for Pagume)

        public EthiopicDate(int year, int month, int day) {
            this.year = year;
            this.month = month;
            this.day = day;
        }

        public String getMonthName() {
            if (month >= 1 && month <= 13) {
                return ETHIOPIC_MONTH_NAMES[month - 1];
            }
            return "";
        }

        @NonNull
        @Override
        public String toString() {
            return getMonthName() + " " + day + ", " + year;
        }

        /**
         * Format as short string (e.g., "Meskerem 1")
         */
        public String toShortString() {
            return getMonthName() + " " + day;
        }
    }

    /**
     * Converts a Gregorian date (as timestamp) to Ethiopic date.
     */
    public static EthiopicDate gregorianToEthiopic(long gregorianTimeMillis) {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTimeInMillis(gregorianTimeMillis);

        int gregYear = calendar.get(Calendar.YEAR);
        int gregMonth = calendar.get(Calendar.MONTH) + 1; // Calendar.MONTH is 0-based
        int gregDay = calendar.get(Calendar.DAY_OF_MONTH);

        return gregorianToEthiopic(gregYear, gregMonth, gregDay);
    }

    /**
     * Converts a Gregorian date to Ethiopic date.
     */
    public static EthiopicDate gregorianToEthiopic(int gregYear, int gregMonth, int gregDay) {
        // Calculate Julian Day Number for Gregorian date
        int jdn = gregorianToJDN(gregYear, gregMonth, gregDay);

        // Convert JDN to Ethiopic date
        return jdnToEthiopic(jdn);
    }

    /**
     * Converts an Ethiopic date to Gregorian timestamp.
     */
    public static long ethiopicToGregorian(int ethYear, int ethMonth, int ethDay) {
        // Calculate Julian Day Number for Ethiopic date
        int jdn = ethiopicToJDN(ethYear, ethMonth, ethDay);

        // Convert JDN to Gregorian date
        int[] gregorian = jdnToGregorian(jdn);

        Calendar calendar = GregorianCalendar.getInstance();
        calendar.clear();
        calendar.set(gregorian[0], gregorian[1] - 1, gregorian[2]); // Month is 0-based in Calendar
        return calendar.getTimeInMillis();
    }

    /**
     * Calculate Julian Day Number from Gregorian date.
     */
    private static int gregorianToJDN(int year, int month, int day) {
        int a = (14 - month) / 12;
        int y = year + 4800 - a;
        int m = month + 12 * a - 3;

        return day + (153 * m + 2) / 5 + 365 * y + y / 4 - y / 100 + y / 400 - 32045;
    }

    /**
     * Convert Julian Day Number to Ethiopic date.
     */
    private static EthiopicDate jdnToEthiopic(int jdn) {
        int r = (jdn - 1723856) % 1461;
        int n = (r % 365) + 365 * (r / 1460);

        int year = 4 * ((jdn - 1723856) / 1461) + r / 365 - r / 1460;
        int month = n / 30 + 1;
        int day = n % 30 + 1;

        return new EthiopicDate(year, month, day);
    }

    /**
     * Calculate Julian Day Number from Ethiopic date.
     */
    private static int ethiopicToJDN(int year, int month, int day) {
        return (1723856 + 365) + 365 * (year - 1) + year / 4 + 30 * month + day - 31;
    }

    /**
     * Convert Julian Day Number to Gregorian date.
     */
    private static int[] jdnToGregorian(int jdn) {
        int a = jdn + 32044;
        int b = (4 * a + 3) / 146097;
        int c = a - (146097 * b) / 4;

        int d = (4 * c + 3) / 1461;
        int e = c - (1461 * d) / 4;
        int m = (5 * e + 2) / 153;

        int day = e - (153 * m + 2) / 5 + 1;
        int month = m + 3 - 12 * (m / 10);
        int year = 100 * b + d - 4800 + m / 10;

        return new int[]{year, month, day};
    }

    /**
     * Checks if an Ethiopic year is a leap year.
     */
    public static boolean isEthiopicLeapYear(int year) {
        return (year % 4) == 3;
    }

    /**
     * Gets the number of days in an Ethiopic month.
     */
    public static int getEthiopicMonthDays(int year, int month) {
        if (month >= 1 && month <= 12) {
            return 30;
        } else if (month == 13) {
            return isEthiopicLeapYear(year) ? 6 : 5;
        }
        return 0;
    }

    /**
     * Gets all Ethiopic month names.
     */
    public static String[] getMonthNames() {
        return ETHIOPIC_MONTH_NAMES.clone();
    }
}
