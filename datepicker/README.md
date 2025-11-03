# Ethiopic DatePicker Library

A minimal Android date picker library based on Material Components DatePicker design, with the package name `com.shalom.android.material.datepicker`.

## Overview

This library provides a date picker component that will eventually support Ethiopic calendar dates. It is structured similarly to Google's Material Components DatePicker but uses a custom package name for future customization.

## Package Structure

```
com.shalom.android.material.datepicker/
├── MaterialDatePicker.java           - Main entry point and DialogFragment
├── DateSelector.java                 - Interface for date selection behavior
├── SingleDateSelector.java           - Single date selection implementation
├── Month.java                        - Represents a calendar month
├── CalendarConstraints.java          - Date range validation and constraints
└── DateValidator.java                - Interface for date validation
```

## Key Features (Current)

- **MaterialDatePicker**: Main dialog component with Builder pattern API
- **Month**: Parcelable month representation with date calculations
- **CalendarConstraints**: Define valid date ranges and selection constraints
- **DateSelector**: Pluggable selection behavior (currently supports single date)
- **DateValidator**: Interface for custom date validation

## Dependencies

The library includes the following dependencies from Maven:

- AndroidX Core KTX (1.13.1)
- AndroidX AppCompat (1.7.0)
- Material Design Components (1.13.0)
- ConstraintLayout (2.1.4)
- RecyclerView (1.3.2) - for calendar grid
- Fragment KTX (1.8.5) - for dialog support
- Annotation (1.9.1)
- ThreeTen-Extra (1.8.0) - for future EthiopicDate support

## Usage

### Basic Single Date Picker

```java
MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder(new SingleDateSelector())
    .setTitleText("Select a date")
    .setSelection(System.currentTimeMillis())
    .build();

datePicker.show(getSupportFragmentManager(), "date_picker");

datePicker.addOnPositiveButtonClickListener(selection -> {
    // Handle selected date (timestamp in milliseconds)
    long selectedDate = selection;
});
```

### With Calendar Constraints

```java
CalendarConstraints constraints = new CalendarConstraints.Builder()
    .setStart(Month.create(2020, 0).getTimeInMillis())
    .setEnd(Month.create(2025, 11).getTimeInMillis())
    .setOpenAt(Month.current().getTimeInMillis())
    .build();

MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder(new SingleDateSelector())
    .setTitleText("Select a date")
    .setCalendarConstraints(constraints)
    .build();
```

## Future Enhancements

### Phase 1: Java Time Migration
- Replace Calendar with Java Time API (java.time.*)
- Use LocalDate, ZonedDateTime for date operations
- Improve date calculation performance

### Phase 2: Ethiopic Calendar Support
- Integrate ThreeTen-Extra's EthiopicDate
- Convert between Gregorian and Ethiopic calendars
- Display Ethiopic month names (Meskerem, Tikimt, etc.)
- Support 13-month calendar structure

### Phase 3: UI Components
- MaterialCalendarView - calendar grid display
- MonthView - individual month rendering
- MonthViewAdapter - RecyclerView adapter for scrolling
- PickerHeaderView - selected date display
- Custom styling and theming

### Phase 4: Additional Features
- Range date selection
- Multi-date selection
- Custom date validators
- Localization support (Amharic)
- Accessibility improvements

## Architecture

This library follows the Material Components DatePicker architecture:

1. **Dialog Layer**: MaterialDatePicker (DialogFragment)
2. **Selection Layer**: DateSelector implementations
3. **Model Layer**: Month, CalendarConstraints
4. **Validation Layer**: DateValidator interface

## Integration

Add the library to your app's `build.gradle.kts`:

```kotlin
dependencies {
    implementation(project(":datepicker"))
}
```

## Build Requirements

- Min SDK: 26 (Android 8.0)
- Target SDK: 36
- Compile SDK: 36
- Java Version: 11

## License

[Add your license information here]

## References

- [Material Components DatePicker](https://github.com/material-components/material-components-android/tree/master/lib/java/com/google/android/material/datepicker)
- [ThreeTen-Extra EthiopicDate](https://github.com/ThreeTen/threeten-extra/blob/main/src/main/java/org/threeten/extra/chrono/EthiopicDate.java)
