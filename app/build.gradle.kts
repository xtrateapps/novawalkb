plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
}
android {
    namespace = "com.novaservices.netwalk"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.novaservices.netwalk"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
    sourceSets.getByName("main") {
        jniLibs.srcDir("libs")
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation ("com.squareup.retrofit2:retrofit:2.8.0")
    // Retrofit Converter GSON
    implementation ("com.squareup.retrofit2:converter-gson:2.8.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.6")
    implementation("androidx.annotation:annotation:1.6.0")
    implementation("com.airbnb.android:lottie:3.4.0")
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation("com.airbnb.android:lottie:3.4.0")

    implementation("org.apache.poi:poi:5.2.5")  // Para POI
    implementation("org.apache.poi:poi-ooxml:5.2.5")  // Para manejo de archivos OOXML (xlsx)
    implementation("org.apache.commons:commons-collections4:4.4")  // Dependencia opcional recomendada

    implementation(files("libs/meSdk-3.7.114-RELEASE.jar"))
}