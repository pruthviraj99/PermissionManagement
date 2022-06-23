plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("maven-publish")
}

android {
    compileSdk = AppBuildGradleDependency.compileSdk

    defaultConfig {
        minSdk = AppBuildGradleDependency.minSdk
        targetSdk = AppBuildGradleDependency.targetSdk
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

    testImplementation("junit:junit:${Dependencies.junitVersion}")
    androidTestImplementation("androidx.test.ext:junit:${Dependencies.extJunitVersion}")
    androidTestImplementation("androidx.test.espresso:espresso-core:${Dependencies.espressoVersion}")
}

afterEvaluate {
    publishing {
        publications {
            register<MavenPublication>("release") {
                from(components["release"])

                groupId = "com.github.pruthviraj99"
                artifactId = "permission_management"
                version = "1.0.0"
            }
        }
    }
}