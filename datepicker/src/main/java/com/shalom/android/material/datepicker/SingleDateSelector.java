package com.shalom.android.material.datepicker;

import android.os.Parcel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.threeten.extra.chrono.EthiopicDate;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;

/**
 * A {@link DateSelector} that uses a {@link Long} for its selection state.
 * Based on Material Components SingleDateSelector.
 */
public class SingleDateSelector implements DateSelector<Long> {

    @Nullable
    private Long selectedItem;

    public SingleDateSelector() {
    }

    @Override
    @Nullable
    public Long getSelection() {
        return selectedItem;
    }

    @Override
    public void setSelection(@Nullable Long selection) {
        this.selectedItem = selection;
    }

    @Override
    public Collection<Long> getSelectedDays() {
        ArrayList<Long> selections = new ArrayList<>();
        if (selectedItem != null) {
            selections.add(selectedItem);
        }
        return selections;
    }

    @Override
    public String getSelectionDisplayString() {
        if (selectedItem == null) {
            return "";
        }

        // Convert timestamp to Ethiopic date
        LocalDate gregorianDate = LocalDate.ofInstant(
                java.time.Instant.ofEpochMilli(selectedItem),
                ZoneId.systemDefault()
        );
        EthiopicDate ethiopicDate = EthiopicDate.from(gregorianDate);

        // Format as "Meskerem 5, 2017" (Ethiopic format)
        String[] monthNames = {
            "Meskerem", "Tikimt", "Hidar", "Tahsas", "Tir", "Yekatit",
            "Megabit", "Miazia", "Ginbot", "Sene", "Hamle", "Nehase", "Pagume"
        };

        String monthName = monthNames[ethiopicDate.getMonthValue() - 1];
        return monthName + " " + ethiopicDate.getDayOfMonth() + ", " + ethiopicDate.getProlepticYear();
    }

    @Override
    public boolean isSelectionComplete() {
        return selectedItem != null;
    }

    @Override
    public void select(long selection) {
        this.selectedItem = selection;
    }

    // Parcelable implementation

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeValue(selectedItem);
    }

    public static final Creator<SingleDateSelector> CREATOR = new Creator<SingleDateSelector>() {
        @NonNull
        @Override
        public SingleDateSelector createFromParcel(@NonNull Parcel source) {
            SingleDateSelector singleDateSelector = new SingleDateSelector();
            singleDateSelector.selectedItem = (Long) source.readValue(Long.class.getClassLoader());
            return singleDateSelector;
        }

        @NonNull
        @Override
        public SingleDateSelector[] newArray(int size) {
            return new SingleDateSelector[size];
        }
    };
}
