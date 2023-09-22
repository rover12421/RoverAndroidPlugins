dependencies {
    val nameHashPluginVer = project.property("NameHashPluginVer")
    implementation("com.rover12421.android.plugins.namehash:core:${nameHashPluginVer}")
    implementation("commons-codec:commons-codec:1.16.0")
}