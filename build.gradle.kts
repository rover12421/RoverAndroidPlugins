import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.net.URI

buildscript {
    repositories {
        mavenLocal()
        maven("https://mirrors.huaweicloud.com/repository/maven/")
        google()
        gradlePluginPortal()
    }
    dependencies {
        val removeAnnotationPluginVer = project.property("removeAnnotationPluginVer")
        classpath("com.rover12421.android.plugins.removeAnnotation:com.rover12421.android.plugins.removeAnnotation.gradle.plugin:$removeAnnotationPluginVer")

        val nameHashPluginVer = project.property("nameHashPluginVer")
        classpath("com.rover12421.android.plugins.namehash:com.rover12421.android.plugins.namehash.gradle.plugin:$nameHashPluginVer")

        val dependencyToMavenLocalVer = project.property("dependencyToMavenLocalVer")
        classpath("com.rover12421.gradle.plugins.dependencyToMavenLocal:com.rover12421.gradle.plugins.dependencyToMavenLocal.gradle.plugin:$dependencyToMavenLocalVer")
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

plugins {
    alias(libs.plugins.agp.lib) apply false
    alias(libs.plugins.agp.app) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.compose.compiler) apply false
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
        apply(plugin = libs.plugins.compose.compiler.get().pluginId)
        val configFile = project.file("config.properties")
        if (configFile.exists()) {
            println("project: ${project.name}")
//            println("load config $configFile")
            loadProperties(configFile, project)

            val ver = project.extProp("versionName")
//            println("version: $ver")

            project.extSet("version", ver)
            version = ver

            var groupId = project.extVal("groupId")
            var artifactId = project.extVal("artifactId")
            val pluginId = project.extVal("pluginId")
            val description = project.extVal("description")
//            println("groupId: $groupId")
//            println("artifactId: $artifactId")
//            println("pluginId: $pluginId")

            apply(plugin = "maven-publish")
            apply(plugin = "signing")

            if (pluginId.isNotEmpty()) {
                apply(plugin = "java-gradle-plugin")
                groupId = pluginId
                artifactId = "$groupId.gradle.plugin"

                extensions.getByName<GradlePluginDevelopmentExtension>("gradlePlugin").apply {
                    isAutomatedPublishing = false
                    plugins {
                        create("roverAndroidPlugin") {
                            id = pluginId
                            implementationClass = "$groupId.plugin.PluginMain"
                            this.description = description
                        }
                    }

                    project.dependencies.add(JavaPlugin.COMPILE_ONLY_CONFIGURATION_NAME, libs.agp.gradle)
                    project.dependencies.add(JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME, libs.asm)
                }
            }

            val jvmVer = JavaVersion.VERSION_17.toString()

            tasks
                .withType<org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile>()
                .configureEach {
                    compilerOptions.jvmTarget.set(JvmTarget.fromTarget(jvmVer))
                }

            tasks.withType<JavaCompile>().configureEach {
                sourceCompatibility = jvmVer
                targetCompatibility = jvmVer
            }

            tasks.withType<Jar>().configureEach {
                exclude("**/*.kotlin_module")

                manifest {
                    attributes(
                        "Implementation-Version" to version,
                        "Version" to version,
                    )
                }
            }

            val sourceSets = project.extensions.findByName("sourceSets") as SourceSetContainer
//            println("sourceSets cls: ${sourceSets::class.java}")

            val sourcesJar by tasks.registering(Jar::class) {
                group = "publishing"
                archiveClassifier.set("sources")
                from(sourceSets["main"].allSource)
            }

            val generateJavadoc = tasks.register("generateJavadoc", Javadoc::class) {
                source = sourceSets.getByName("main").allJava
                setDestinationDir(file("${layout.buildDirectory}/docs/javadoc"))
            }

            val javadocJar = tasks.register("javadocJar", Jar::class) {
                group = "publishing"
                archiveClassifier.set("javadoc")
                from(generateJavadoc)
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
