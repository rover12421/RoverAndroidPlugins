@file:Suppress("UnstableApiUsage")

pluginManagement {
    repositories {
        mavenLocal()
        maven("https://mirrors.huaweicloud.com/repository/maven/")
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenLocal()
        maven("https://mirrors.huaweicloud.com/repository/maven/")
        google()
        mavenCentral()
    }
}

rootProject.name = "RoverAndroidPlugins"
include(
    ":app",
    ":removeAnnotation:plugin",
    ":nameHash:core",
    ":nameHash:plugin",
    ":dependencyToMavenLocal:plugin",
)
