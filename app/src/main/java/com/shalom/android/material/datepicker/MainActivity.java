package com.shalom.android.material.datepicker;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView selectedDateTextView;
    private SimpleDateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize date format
        dateFormat = new SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault());

        // Find views
        Button pickDateButton = findViewById(R.id.pick_date_button);
        selectedDateTextView = findViewById(R.id.selected_date_text);

        // Set up date picker button
        pickDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });
    }

    private void showDatePicker() {
        // Create a single date selector
        SingleDateSelector dateSelector = new SingleDateSelector();

        // Build the date picker
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder(dateSelector)
                .setTitleText("Select a date")
                .setSelection(System.currentTimeMillis())
                .build();

        // Add listener for positive button click
        datePicker.addOnPositiveButtonClickListener(selection -> {
            if (selection != null) {
                String formattedDate = dateFormat.format(new Date(selection));
                selectedDateTextView.setText("Selected: " + formattedDate);
            }
        });

        // Add cancel listener
        datePicker.addOnCancelListener(dialog -> {
            selectedDateTextView.setText("Date selection cancelled");
        });

        // Show the date picker
        datePicker.show(getSupportFragmentManager(), "date_picker");
    }
}
