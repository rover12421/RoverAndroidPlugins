import java.net.URI

buildscript {
    repositories {
        mavenLocal()
        maven("https://mirrors.huaweicloud.com/repository/maven/")
        google()
        gradlePluginPortal()
    }
    dependencies {
        val removeAnnotationPluginVer = project.property("RemoveAnnotationPluginVer")
        classpath("com.rover12421.android.plugins.removeAnnotation:plugin:$removeAnnotationPluginVer")
    }
}

fun Project.extVal(extName: String): String {
    return if (this.extra.has(extName)) {
        this.extra.get(extName).toString()
    } else {
        ""
    }
}

fun Project.extSet(extName: String, extValue: String) {
    this.extra.set(extName, extValue)
}

fun Project.extProp(propName: String): String {
    val propValue = this.extVal(propName)
    return if (project.hasProperty(propValue)) {
        project.property(propValue).toString()
    } else {
        ""
    }
}

object VersionInfo {
    const val agp: String = "7.3.1"
    const val kotlin: String = "1.8.10"
    const val asm = "9.5"
    const val jvm = "1.8"
}

plugins {
    id("com.android.application") version "8.1.1" apply false
    id("org.jetbrains.kotlin.android") version "1.8.10" apply false
    id("org.jetbrains.kotlin.jvm") version "1.8.10" apply false
}

fun loadProperties(file: File, project: Project) {
    if (!file.canRead()) {
        println("$file read failed")
    }
    val config = java.util.Properties()
    file.inputStream().use {
        config.load(it)
    }
    config.forEach { key, value ->
//        println("set prop [$key: $value]")
        project.extSet(key.toString(), value.toString())
    }
}

loadProperties(rootProject.file("local.properties"), rootProject)

allprojects {
}

subprojects {
    if (project.name in arrayOf("plugin", "core")) {
        apply(plugin = "kotlin")
        apply(plugin = "java-library")
    }
    afterEvaluate {
        val configFile = project.file("config.properties")
        if (configFile.exists()) {
//            println("project: ${project.name}")
//            println("load config $configFile")
            loadProperties(configFile, project)

            val ver = project.extProp("versionName")
//            println("version: $ver")

            project.extSet("version", ver)
            version = ver

            val groupId = project.extVal("groupId")
            val artifactId = project.extVal("artifactId")
            val pluginId = project.extVal("pluginId")
            val description = project.extVal("description")
//            println("groupId: $groupId")
//            println("artifactId: $artifactId")
//            println("pluginId: $pluginId")

            apply(plugin = "maven-publish")
            apply(plugin = "signing")

            if (pluginId.isNotEmpty()) {
                apply(plugin = "java-gradle-plugin")

                extensions.getByName<GradlePluginDevelopmentExtension>("gradlePlugin").apply {
                    isAutomatedPublishing = false
                    plugins {
                        create("roverAndroidPlugin") {
                            id = pluginId
                            implementationClass = "$groupId.$artifactId.PluginMain"
                            this.description = description
                        }
                    }

                    configurations.named("compileOnly") {
                        dependencies.add(project.dependencies.gradleApi())
                        dependencies.add(project.dependencies.create("com.android.tools.build:gradle:${VersionInfo.agp}"))
                    }

                    configurations.named("implementation") {
                        dependencies.add(project.dependencies.create("org.ow2.asm:asm:${VersionInfo.asm}"))
                        dependencies.add(project.dependencies.create("org.ow2.asm:asm-util:${VersionInfo.asm}"))
                    }
                }
            }

            tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
                kotlinOptions {
                    jvmTarget = VersionInfo.jvm
                }
            }

            tasks.withType<JavaCompile>().configureEach {
                sourceCompatibility = VersionInfo.jvm
                targetCompatibility = VersionInfo.jvm
            }

            tasks.withType<Jar>().configureEach {
                exclude("**/*.kotlin_module")
            }

            val sourceSets = project.extensions.findByName("sourceSets") as SourceSetContainer
//            println("sourceSets cls: ${sourceSets::class.java}")

            val sourcesJar by tasks.registering(Jar::class) {
                group = "publishing"
                archiveClassifier.set("sources")
                from(sourceSets["main"].allSource)
            }

            tasks.register("generateJavadoc", Javadoc::class) {
                source = sourceSets.getByName("main").allJava
                setDestinationDir(file("$buildDir/docs/javadoc"))
            }

            val javadocJar = tasks.register("javadocJar", Jar::class) {
                group = "publishing"
                archiveClassifier.set("javadoc")
                from(tasks.named("generateJavadoc"))
            }

            val sonatypeUsername = project.property("sonatypeUsername").toString()
            val sonatypePassword = project.property("sonatypePassword").toString()
//            println("sonatypeUsername: $sonatypeUsername")
//            println("sonatypePassword: $sonatypePassword")

            val publicationName = "sonatype"
            extensions.getByName<PublishingExtension>("publishing").apply {
                publications {
                    create(publicationName, MavenPublication::class) {
                        from(components["java"])
                        this.groupId = groupId
                        this.artifactId = artifactId
                        this.version = version

                        artifact(sourcesJar)
                        artifact(javadocJar)

                        pom {
                            name.set(project.name)
                            if (description.isNotBlank()) {
                                this.description.set(description)
                            } else {
                                this.description.set("Rover12421's Android Plugins / $groupId:$artifactId")
                            }
                            url.set("https://github.com/rover12421/")
                            packaging = "jar"
                            licenses {
                                license {
                                    name.set("MIT License")
                                    url.set("https://opensource.org/licenses/mit-license.php")
                                    distribution.set("repo")
                                }
                            }
                            scm {
                                url.set("https://github.com/rover12421/")
                                connection.set("scm:git:https://github.com/rover12421/RoverAndroidPlugins")
                                developerConnection.set("scm:git:git@github.com:rover12421/RoverAndroidPlugins.git")
                            }
                            developers {
                                developer {
                                    id.set("rover12421")
                                    name.set("rover12421")
                                    email.set("rover12421@163.com")
                                }
                            }
                        }
                    }

                    extensions.getByName<SigningExtension>("signing")
                        .sign(findByName(publicationName))
                }

                repositories {
                    mavenLocal()
                    maven {
                        url = URI("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
                        credentials {
                            username = sonatypeUsername
                            password = sonatypePassword
                        }
                    }
                }
            }

        }
    }

}