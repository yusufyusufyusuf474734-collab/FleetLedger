plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ksp) apply false
    // Firebase için Google Services plugin (aktifleştirmek için yorumu kaldır)
    // id("com.google.gms.google-services") version "4.4.0" apply false
}
