package com.shalom.android.material.datepicker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.shalom.android.material.datepicker.databinding.FragmentFirstBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonFirst.setOnClickListener(v ->
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment)
        );

        binding.buttonShowDatepicker.setOnClickListener(v -> showDatePicker());
    }

    private void showDatePicker() {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder(new SingleDateSelector())
                .setTitleText("Select a date")
                .setSelection(System.currentTimeMillis())
                .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            if (selection != null) {
                // Format Gregorian date
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
                String gregorianDate = dateFormat.format(new Date(selection));
                binding.textviewSelectedDate.setText("Gregorian: " + gregorianDate);

                // Convert to Ethiopic date
                EthiopicDateConverter.EthiopicDate ethiopicDate =
                        EthiopicDateConverter.gregorianToEthiopic(selection);
                binding.textviewEthiopicDate.setText("Ethiopic: " + ethiopicDate.toString());
            }
        });

        datePicker.show(getParentFragmentManager(), "date_picker");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}