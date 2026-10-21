plugins { id("com.android.application"); id("org.jetbrains.kotlin.android") }
android { namespace = "com.fnexternal.panel"; compileSdk = 35
 defaultConfig { applicationId = "com.fnexternal.panel"; minSdk = 29; targetSdk = 35; versionCode = 1; versionName = "1.0" }
 compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }
 kotlinOptions { jvmTarget = "17" }
}
dependencies { implementation("dev.rikka.shizuku:api:13.1.5"); implementation("dev.rikka.shizuku:provider:13.1.5") }
