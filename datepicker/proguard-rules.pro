# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Keep Parcelable implementations
-keepclassmembers class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator CREATOR;
}

# Keep DateSelector implementations
-keep class * implements com.shalom.android.material.datepicker.DateSelector {
    public static final android.os.Parcelable$Creator CREATOR;
}

# Keep DateValidator implementations
-keep class * implements com.shalom.android.material.datepicker.DateValidator {
    public static final android.os.Parcelable$Creator CREATOR;
}
