plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}
android {
    namespace = "com.ledger.reminders"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.ledger.reminders"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }
}
