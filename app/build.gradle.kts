@file:Suppress("UnstableApiUsage")

import com.rover12421.android.plugins.namehash.plugin.HashAlgorithm

plugins {
    alias(libs.plugins.agp.app)
    alias(libs.plugins.kotlin.android)

    id("rover.android.namehash")
    id("rover.android.removeAnnotation")
//    id("rover.gradle.dependencyToMavenLocal")
}

val jvmVer = JavaVersion.VERSION_11

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
    composeOptions {
        // https://developer.android.com/jetpack/androidx/releases/compose-kotlin
        kotlinCompilerExtensionVersion = "1.5.7"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    lint {
        checkReleaseBuilds = false
    }

    tasks.register("publishToLocal") {
        group = "test"

        rootProject.allprojects.forEach { pj ->
            val publishTask = pj.tasks.findByPath("publishToMavenLocal")
            if (publishTask != null) {
                dependsOn(publishTask)
                mustRunAfter(publishTask)
            }
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    implementation(libs.bundles.kotlin.stdlib)

//    constraints {
//        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.9.21") {
//            because("kotlin-stdlib-jdk7 is now a part of kotlin-stdlib")
//        }
//        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.21") {
//            because("kotlin-stdlib-jdk8 is now a part of kotlin-stdlib")
//        }
//    }
}

removeAnnotation {
    debug = true
    allProject = true
    annotations.add("kotlin.Metadata")
    filter.add("com.rover12421")
}

nameHash {
    debug = true
//    hash.algorithm = object: HashAlgorithm {
//        override fun algorithmName(): String {
//            return "Reversed"
//        }
//
//        override fun hash(data: String, param: Map<String, Any>): String {
//            return data.reversed()
//        }
//    }

    hash.algorithm = HashAlgorithm.MurmurHash32
}

//dependencyToMavenLocal {
//    debug = true
//}
