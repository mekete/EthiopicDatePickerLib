plugins {
    id("com.android.library")
}

android {
    namespace = "com.shalom.android.material.datepicker"
    compileSdk = 36

    defaultConfig {
        minSdk = 26
        targetSdk = 36

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // AndroidX Core
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")

    // Material Design Components
    implementation("com.google.android.material:material:1.13.0")

    // ConstraintLayout
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // RecyclerView for calendar grid
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    // ViewPager2 for month scrolling
    implementation("androidx.viewpager2:viewpager2:1.1.0")

    // Fragment for dialog support
    implementation("androidx.fragment:fragment-ktx:1.8.5")

    // Annotation support
    implementation("androidx.annotation:annotation:1.9.1")

    // ThreeTen-Extra for EthiopicDate (future use)
    implementation("org.threeten:threeten-extra:1.8.0")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}
