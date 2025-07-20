import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    //noinspection GradleDependency
    compileSdk = 36

    defaultConfig {
        applicationId = "com.tlsu.opluscamerapro"
        minSdk = 34
        targetSdk = 36
        versionCode = 2210
        versionName = "Lavender 2.2.10"
    }

    val properties = Properties()
    runCatching { properties.load(project.rootProject.file("local.properties").inputStream()) }
    val keystorePath = properties.getProperty("KEYSTORE_PATH") ?: System.getenv("KEYSTORE_PATH")
    val keystorePwd = properties.getProperty("KEYSTORE_PASS") ?: System.getenv("KEYSTORE_PASS")
    val alias = properties.getProperty("KEY_ALIAS") ?: System.getenv("KEY_ALIAS")
    val pwd = properties.getProperty("KEY_PASSWORD") ?: System.getenv("KEY_PASSWORD")
    if (keystorePath != null) {
        signingConfigs {
            create("release") {
                storeFile = file(keystorePath)
                storePassword = keystorePwd
                keyAlias = alias
                keyPassword = pwd
                enableV3Signing = true
            }
        }
    }

    buildTypes {
        release {
            isShrinkResources = true
            isMinifyEnabled = true
            proguardFiles("proguard-rules.pro")
            if (keystorePath != null) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
        debug {
            if (keystorePath != null) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }

    androidResources {
        additionalParameters("--allow-reserved-package-id", "--package-id", "0x45")
    }

    packagingOptions {
        resources {
            excludes += "/META-INF/**"
            excludes += "/kotlin/**"
            excludes += "/*.txt"
            excludes += "/*.bin"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
    }
    namespace = "com.tlsu.opluscamerapro"
    
    // 启用Jetpack Compose
    buildFeatures {
        compose = true
    }
    
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"

    }
    bundle {
        language {
            enableSplit = true
        }
        density {
            enableSplit = true
        }
        abi {
            enableSplit = true
        }
    }
    
    // 移除未使用的资源
    buildFeatures {
        buildConfig = false  // 如果不使用 BuildConfig
        resValues = false    // 如果不使用 resValue
    }
}

dependencies {
    implementation("com.github.kyuubiran:EzXHelper:2.2.0")
    compileOnly("de.robv.android.xposed:api:82")
    
    // Jetpack Compose相关依赖
    implementation(platform("androidx.compose:compose-bom:2024.02.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    
    // Compose Material You (MD3)
    implementation("androidx.compose.material3:material3:1.2.0")
    implementation("androidx.compose.material3:material3-window-size-class:1.2.0")
    implementation("androidx.compose.material:material-icons-extended")
    
    // Compose 导航
    implementation("androidx.navigation:navigation-compose:2.7.6")
    
    // Android Lifecycle 相关
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")

    val libsuVersion = "6.0.0"

    // The core module that provides APIs to a shell
    implementation("com.github.topjohnwu.libsu:core:${libsuVersion}")

    // Optional: APIs for creating root services. Depends on ":core"
    implementation("com.github.topjohnwu.libsu:service:${libsuVersion}")

    // Optional: Provides remote file system support
    implementation("com.github.topjohnwu.libsu:nio:${libsuVersion}")
    
    // JSON处理
    implementation("org.json:json:20240303")
    
    // 调试工具
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    implementation("org.luckypray:dexkit:2.0.6")
}
