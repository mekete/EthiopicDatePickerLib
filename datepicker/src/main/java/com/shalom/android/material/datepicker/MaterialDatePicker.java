package com.shalom.android.material.datepicker;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.annotation.StringRes;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link Dialog} with a header, {@link MaterialCalendar}, and set of actions.
 * Simplified version based on Material Components MaterialDatePicker.
 *
 * @param <S> The type of selection (e.g., Long for single date)
 */
public final class MaterialDatePicker<S> extends DialogFragment {

    private static final String TITLE_TEXT_KEY = "TITLE_TEXT_KEY";
    private static final String TITLE_TEXT_RES_ID_KEY = "TITLE_TEXT_RES_ID_KEY";
    private static final String DATE_SELECTOR_KEY = "DATE_SELECTOR_KEY";
    private static final String CALENDAR_CONSTRAINTS_KEY = "CALENDAR_CONSTRAINTS_KEY";

    @Nullable
    private DateSelector<S> dateSelector;

    @Nullable
    private CalendarConstraints calendarConstraints;

    @Nullable
    private CharSequence titleText;

    @StringRes
    private int titleTextResId = 0;

    private final List<MaterialPickerOnPositiveButtonClickListener<? super S>>
            onPositiveButtonClickListeners = new ArrayList<>();

    private final List<View.OnClickListener> onNegativeButtonClickListeners = new ArrayList<>();

    private final List<DialogInterface.OnCancelListener> onCancelListeners = new ArrayList<>();

    private final List<DialogInterface.OnDismissListener> onDismissListeners = new ArrayList<>();

    @NonNull
    public static <S> Builder<S> Builder(@NonNull DateSelector<S> dateSelector) {
        return new Builder<>(dateSelector);
    }

    /**
     * Used to create MaterialDatePicker instances with default and overridden settings.
     *
     * @param <S> The type of selection (e.g., Long for single date)
     */
    public static final class Builder<S> {

        private final DateSelector<S> dateSelector;
        private CalendarConstraints calendarConstraints;
        private CharSequence titleText;
        @StringRes private int titleTextResId = 0;
        private S selection;

        private Builder(@NonNull DateSelector<S> dateSelector) {
            this.dateSelector = dateSelector;
        }

        /**
         * Sets the title text.
         */
        @NonNull
        public Builder<S> setTitleText(@Nullable CharSequence titleText) {
            this.titleText = titleText;
            this.titleTextResId = 0;
            return this;
        }

        /**
         * Sets the title text from a resource id.
         */
        @NonNull
        public Builder<S> setTitleText(@StringRes int titleTextResId) {
            this.titleTextResId = titleTextResId;
            this.titleText = null;
            return this;
        }

        /**
         * Sets the selection.
         */
        @NonNull
        public Builder<S> setSelection(@Nullable S selection) {
            this.selection = selection;
            return this;
        }

        /**
         * Sets the calendar constraints.
         */
        @NonNull
        public Builder<S> setCalendarConstraints(@Nullable CalendarConstraints calendarConstraints) {
            this.calendarConstraints = calendarConstraints;
            return this;
        }

        /**
         * Creates a {@link MaterialDatePicker} with the provided options.
         */
        @NonNull
        public MaterialDatePicker<S> build() {
            if (calendarConstraints == null) {
                calendarConstraints = new CalendarConstraints.Builder().build();
            }
            if (titleText == null && titleTextResId == 0) {
                titleText = "Select Date";
            }
            if (selection != null) {
                dateSelector.setSelection(selection);
            }

            MaterialDatePicker<S> materialDatePicker = new MaterialDatePicker<>();
            materialDatePicker.dateSelector = dateSelector;
            materialDatePicker.calendarConstraints = calendarConstraints;
            materialDatePicker.titleText = titleText;
            materialDatePicker.titleTextResId = titleTextResId;

            return materialDatePicker;
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle bundle) {
        Context context = requireContext();

        if (bundle != null) {
            restoreState(bundle);
        }

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);

        // Create a simple view for now
        View contentView = createContentView(context);

        builder.setView(contentView);
        builder.setPositiveButton("OK", (dialog, which) -> {
            for (MaterialPickerOnPositiveButtonClickListener<? super S> listener :
                    onPositiveButtonClickListeners) {
                listener.onPositiveButtonClick(getSelection());
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            for (View.OnClickListener listener : onNegativeButtonClickListeners) {
                listener.onClick(null);
            }
        });

        return builder.create();
    }

    private View createContentView(Context context) {
        TextView textView = new TextView(context);
        textView.setPadding(50, 50, 50, 50);

        String title = titleText != null ? titleText.toString() :
                       (titleTextResId != 0 ? context.getString(titleTextResId) : "Select Date");

        String selectionText = dateSelector != null ? dateSelector.getSelectionDisplayString() : "No date selected";

        textView.setText(title + "\n\n" + selectionText + "\n\n(Calendar UI coming soon)");
        textView.setTextSize(16);

        return textView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putParcelable(DATE_SELECTOR_KEY, dateSelector);
        bundle.putParcelable(CALENDAR_CONSTRAINTS_KEY, calendarConstraints);
        bundle.putCharSequence(TITLE_TEXT_KEY, titleText);
        bundle.putInt(TITLE_TEXT_RES_ID_KEY, titleTextResId);
    }

    private void restoreState(Bundle bundle) {
        dateSelector = bundle.getParcelable(DATE_SELECTOR_KEY);
        calendarConstraints = bundle.getParcelable(CALENDAR_CONSTRAINTS_KEY);
        titleText = bundle.getCharSequence(TITLE_TEXT_KEY);
        titleTextResId = bundle.getInt(TITLE_TEXT_RES_ID_KEY);
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialogInterface) {
        for (DialogInterface.OnCancelListener listener : onCancelListeners) {
            listener.onCancel(dialogInterface);
        }
        super.onCancel(dialogInterface);
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialogInterface) {
        for (DialogInterface.OnDismissListener listener : onDismissListeners) {
            listener.onDismiss(dialogInterface);
        }
        super.onDismiss(dialogInterface);
    }

    /**
     * Returns the selection or null if there is no selection.
     */
    @Nullable
    public S getSelection() {
        return dateSelector.getSelection();
    }

    /**
     * Adds a listener for positive button clicks.
     */
    public boolean addOnPositiveButtonClickListener(
            @NonNull MaterialPickerOnPositiveButtonClickListener<? super S> onPositiveButtonClickListener) {
        return onPositiveButtonClickListeners.add(onPositiveButtonClickListener);
    }

    /**
     * Removes a listener for positive button clicks.
     */
    public boolean removeOnPositiveButtonClickListener(
            @NonNull MaterialPickerOnPositiveButtonClickListener<? super S> onPositiveButtonClickListener) {
        return onPositiveButtonClickListeners.remove(onPositiveButtonClickListener);
    }

    /**
     * Adds a listener for negative button clicks.
     */
    public boolean addOnNegativeButtonClickListener(@NonNull View.OnClickListener onNegativeButtonClickListener) {
        return onNegativeButtonClickListeners.add(onNegativeButtonClickListener);
    }

    /**
     * Removes a listener for negative button clicks.
     */
    public boolean removeOnNegativeButtonClickListener(@NonNull View.OnClickListener onNegativeButtonClickListener) {
        return onNegativeButtonClickListeners.remove(onNegativeButtonClickListener);
    }

    /**
     * Adds a listener for cancel events.
     */
    public boolean addOnCancelListener(@NonNull DialogInterface.OnCancelListener onCancelListener) {
        return onCancelListeners.add(onCancelListener);
    }

    /**
     * Removes a listener for cancel events.
     */
    public boolean removeOnCancelListener(@NonNull DialogInterface.OnCancelListener onCancelListener) {
        return onCancelListeners.remove(onCancelListener);
    }

    /**
     * Adds a listener for dismiss events.
     */
    public boolean addOnDismissListener(@NonNull DialogInterface.OnDismissListener onDismissListener) {
        return onDismissListeners.add(onDismissListener);
    }

    /**
     * Removes a listener for dismiss events.
     */
    public boolean removeOnDismissListener(@NonNull DialogInterface.OnDismissListener onDismissListener) {
        return onDismissListeners.remove(onDismissListener);
    }

    /**
     * Listener for positive button click events.
     *
     * @param <S> type of the selection
     */
    public interface MaterialPickerOnPositiveButtonClickListener<S> {
        /**
         * Called when the positive button is clicked.
         *
         * @param selection the current selection
         */
        void onPositiveButtonClick(@Nullable S selection);
    }
}
