plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
    id("kotlin-kapt")
}

android {
    val packageName = "com.action.round"
    namespace = packageName

    compileSdkVersion = "android-33"
    buildToolsVersion = "33.0.0"

    defaultConfig {
        applicationId = packageName
        minSdk = 30
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
        vectorDrawables.useSupportLibrary = true

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
    }
}

dependencies {
    implementation("androidx.lifecycle:lifecycle-livedata-core-ktx:2.6.1")
    implementation("androidx.core:core-ktx:1.10.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.activity:activity-ktx:1.7.1")
    implementation("com.google.android.material:material:1.8.0")

    // Room
    implementation("androidx.room:room-runtime:2.5.1")
    kapt("androidx.room:room-compiler:2.5.1")

    // Bottom Sheet
    implementation("com.google.android.material:material:1.7.0-alpha03")

    // Tests
    testImplementation("androidx.test.ext:junit-ktx:1.1.5")
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.1.0")
    testImplementation("org.mockito:mockito-inline:5.2.0")

    // AndroidTests
    androidTestImplementation("androidx.test.ext:junit-ktx:1.1.5")
    androidTestImplementation("org.mockito.kotlin:mockito-kotlin:4.1.0")
    androidTestImplementation("org.mockito:mockito-inline:5.2.0")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test:rules:1.5.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // ActivityResultAPI
    implementation("androidx.activity:activity-ktx:1.3.0-alpha02")
    implementation("androidx.fragment:fragment-ktx:1.3.0")
}
