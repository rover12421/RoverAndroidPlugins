package com.rover12421.gradle.plugins.dependencyToMavenLocal.plugin

import org.gradle.BuildListener
import org.gradle.BuildResult
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.initialization.Settings
import org.gradle.api.invocation.Gradle

class PluginMain : Plugin<Project> {
    companion object {
        const val pluginName = "dependencyToMavenLocal"
        var prop: PluginProp = PluginProp()
        val filterRegex: MutableList<Regex> = mutableListOf()

        fun isFilterDep(dep: String): Boolean {
            val filter = prop.filter
            if (filter.isEmpty()) {
                return false // 黑名单模式
            }
            val match = if (prop.filterRegex) {
                filterRegex.firstOrNull {
                    it.matches(dep)
                }
            } else {
                filter.firstOrNull {
                    dep.startsWith(it)
                }
            } != null

            if (match && prop.debug) {
                println("[$pluginName] match $dep")
            }
            return match
        }
    }

    private fun initProp(project: Project) {
        prop = project.extensions.findByType(PluginProp::class.java)!!
        if (prop.filterRegex) {
            prop.filter.mapTo(filterRegex) {
                it.toRegex()
            }
        }
    }

    override fun apply(project: Project) {
        val version = javaClass.`package`.implementationVersion
        println("plugin - $pluginName($version)")
        project.extensions.create(pluginName, PluginProp::class.java)

        project.gradle.addBuildListener(object: BuildListener {
            override fun settingsEvaluated(settings: Settings) {
            }

            override fun projectsLoaded(gradle: Gradle) {
            }

            override fun projectsEvaluated(gradle: Gradle) {
                initProp(project)

                val mavenLoader = MavenLoader()
                prop.repo.forEach { (name, url) ->
                    if (prop.debug) {
                        println("add Repo [$name] $url")
                    }
                    mavenLoader.addRepo(name, url)
                }

                gradle.allprojects { pj ->
//                    if (prop.debug) {
//                        println("[$pluginName] check project : ${pj.name}")
//                    }

                    try {
                        pj.repositories.apply {
                            forEach { repo ->
                                println("[${pj.name}]repo - ${repo.name} : $repo")
                            }
                            if (size== 0 || get(0).name != "MavenLocal") {
                                // 在最前面添加 mavenLocal
                                add(0, mavenLocal())
                            }
                        }

                    } catch (_: Throwable){}

                    try {
                        pj.configurations.named("compileClasspath").get().apply {
                            allDependencies.forEach { dependency ->
                                val group = dependency.group
                                val name = dependency.name
                                val ver = dependency.version
                                val dep = "$group:$name:$ver"
//                                if (prop.debug) {
//                                    println("[$pluginName] check dep : $dep")
//                                }
                                if (!group.isNullOrBlank() && !name.isNullOrBlank() && !ver.isNullOrBlank()) {
                                    if (!isFilterDep(dep)) {
                                        if (prop.debug) {
                                            println("[$pluginName] add dep : $dep")
                                        }
                                        mavenLoader.addDependencies(dep)
                                    }
                                }
                            }
                        }
                    } catch (_: Throwable){}
                }

                try {
                    val resolveDependency = mavenLoader.resolveDependency()
                    if (prop.debug) {
                        resolveDependency.forEach {
                            println("${it.coordinate.toCanonicalForm()} > ${it.asFile()}")
                        }
                    }
                } catch (_: Throwable){}
            }

            @Deprecated("Deprecated in Java")
            override fun buildFinished(result: BuildResult) {
            }

        })

    }
}
