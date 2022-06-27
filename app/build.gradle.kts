plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    compileSdk = AppBuildGradleDependency.compileSdk

    defaultConfig {
        applicationId = "com.permissionmanagement"
        minSdk = AppBuildGradleDependency.minSdk
        targetSdk = AppBuildGradleDependency.targetSdk
        versionCode = AppBuildGradleDependency.versionCode
        versionName = AppBuildGradleDependency.versionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation("androidx.core:core-ktx:${Dependencies.coreKtxVersion}")
    implementation("androidx.appcompat:appcompat:${Dependencies.appcompatVersion}")
    implementation("com.google.android.material:material:${Dependencies.materialVersion}")
    implementation("androidx.constraintlayout:constraintlayout:${Dependencies.constraintLayoutVersion}")

    testImplementation("junit:junit:${Dependencies.junitVersion}")
    androidTestImplementation("androidx.test.ext:junit:${Dependencies.extJunitVersion}")
    androidTestImplementation("androidx.test.espresso:espresso-core:${Dependencies.espressoVersion}")

    implementation(project(mapOf("path" to ":permission-management")))

    //Reactive
    implementation("io.reactivex.rxjava2:rxandroid:${Dependencies.rxAndroidVersion}")
    implementation("io.reactivex.rxjava2:rxkotlin:${Dependencies.rxkotlinVersion}")
}