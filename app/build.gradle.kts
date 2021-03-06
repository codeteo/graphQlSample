plugins {
    id("com.android.application")
    id("kotlin-android")
    id("androidx.navigation.safeargs.kotlin")

    // Apollo
    id("com.apollographql.apollo").version("2.2.0")
}

android {
    compileSdkVersion(29)
    buildToolsVersion("29.0.3")

    defaultConfig {
        applicationId = "com.example.rocketreserver"
        minSdkVersion(23)
        targetSdkVersion(29)
        versionCode = 1
        versionName = "1.0"
    }
    
    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    apollo {
        generateKotlinModels.set(true)
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("androidx.appcompat:appcompat:1.1.0")
    implementation("androidx.core:core-ktx:1.3.0")
    implementation("androidx.constraintlayout:constraintlayout:1.1.3")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.2.0")
    implementation("androidx.recyclerview:recyclerview:1.1.0")
    implementation("io.coil-kt:coil:0.11.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.3.0-rc01")
    implementation("androidx.navigation:navigation-ui-ktx:2.3.0-rc01")
    implementation("androidx.paging:paging-runtime-ktx:2.1.2")
    implementation("com.google.android.material:material:1.1.0")
    implementation("androidx.security:security-crypto:1.0.0-rc02")

    testImplementation("junit:junit:4.13")
    androidTestImplementation("androidx.test.ext:junit:1.1.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.2.0")

    // Apollo
    implementation("com.apollographql.apollo:apollo-runtime:2.2.0")
    implementation("com.apollographql.apollo:apollo-rx2-support:2.2.0")

    // RxJava
    implementation("io.reactivex.rxjava2:rxjava:2.2.19")
    implementation("com.squareup.retrofit2:adapter-rxjava2:2.3.0")
    implementation("io.reactivex.rxjava2:rxandroid:2.1.1")

    // Retrofit logging
    implementation("com.squareup.okhttp3:logging-interceptor:4.7.2")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
}
