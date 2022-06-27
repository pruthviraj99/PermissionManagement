plugins {
    id("com.android.application") version ProjectBuildGradleDependency.applicationVersion apply false
    id("com.android.library") version ProjectBuildGradleDependency.androidLibraryVersion apply false
    id("org.jetbrains.kotlin.android") version "1.6.21" apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}