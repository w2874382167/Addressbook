plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.jiong.addressbook"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.jiong.addressbook"
        minSdk = 28
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(files("libs\\hanlp-portable-1.8.4.jar"))
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)


    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    // 网络请求依赖
    implementation("net.sf.json-lib:json-lib:2.4:jdk15")

    // json-lib 依赖的其他库
    implementation("commons-beanutils:commons-beanutils:1.9.4")
    implementation("commons-collections:commons-collections:3.2.2")
    implementation("commons-lang:commons-lang:2.6")
    implementation("commons-logging:commons-logging:1.2")
    implementation("net.sf.ezmorph:ezmorph:1.0.6")

    // 加载gif
    implementation("com.github.bumptech.glide:glide:4.13.2")
    annotationProcessor("com.github.bumptech.glide:compiler:4.13.2")
}