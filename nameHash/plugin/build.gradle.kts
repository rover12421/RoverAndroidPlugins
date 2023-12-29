dependencies {
    val nameHashPluginVer = project.property("nameHashPluginVer")
    implementation("com.rover12421.android.plugins.namehash:core:${nameHashPluginVer}")
    implementation(libs.commons.codec)
}