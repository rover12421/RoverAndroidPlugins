@file:Suppress("UnstableApiUsage")

plugins {
    alias(libs.plugins.agp.app)
    alias(libs.plugins.kotlin.android)

    id("com.rover12421.android.plugins.namehash")
    id("com.rover12421.android.plugins.removeAnnotation")
//    id("com.rover12421.gradle.plugins.dependencyToMavenLocal")
}

val jvmVer = JavaVersion.VERSION_17

android {
    namespace = "com.rover12421.android.plugins.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.rover12421.android.plugins.app"
        minSdk = 21
        //noinspection OldTargetApi
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        create("sign") {
            storeFile = rootProject.file("keyStore/RoverAndroidPlugins.jks")
            storePassword = "RoverAndroidPlugins"
            keyAlias = "RoverAndroidPlugins"
            keyPassword = "RoverAndroidPlugins"
        }
    }

    buildTypes {
        val sign = signingConfigs.getByName("sign")
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = sign
        }
        debug {
            signingConfig = sign
        }
    }
    compileOptions {
        sourceCompatibility = jvmVer
        targetCompatibility = jvmVer
    }
    kotlinOptions {
        jvmTarget = jvmVer.toString()
    }
    buildFeatures {
        compose = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    lint {
        checkReleaseBuilds = false
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose.ui)
    implementation(libs.androidx.material3)
    androidTestImplementation(libs.bundles.androidx.test)
    androidTestImplementation(platform(libs.androidx.compose.bom))

    implementation(libs.kotlin.stdlib)
}

removeAnnotation {
    debug = true
    allProject = true
    annotations.add("kotlin.Metadata")
    filter.add("com.rover12421")
}

nameHash {
    debug = true
    allProject = true
    filter.add("com.rover12421")
    hash.algorithm = object: com.rover12421.android.plugins.namehash.plugin.HashAlgorithm<String> {
        override fun algorithmName(): String {
            return "Reversed"
        }

        override fun hash(data: String, param: Map<String, Any>): String {
            return data.reversed()
        }
    }

//    hash.algorithm = com.rover12421.android.plugins.namehash.plugin.HashAlgorithm.MurmurHash32
}

//dependencyToMavenLocal {
//    debug = true
//}
