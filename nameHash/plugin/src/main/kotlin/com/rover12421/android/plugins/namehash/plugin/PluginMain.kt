package com.rover12421.android.plugins.namehash.plugin

import com.android.build.api.instrumentation.FramesComputationMode
import com.android.build.api.instrumentation.InstrumentationScope
import com.android.build.api.variant.AndroidComponentsExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin

class PluginMain : Plugin<Project> {
    companion object {
        const val pluginName = "nameHash"
        var prop: PluginProp = PluginProp()
        val filterRegex: MutableList<Regex> = mutableListOf()

        fun isMatchClass(className: String): Boolean {
            val filter = prop.filter
            if (filter.isEmpty()) {
                return true
            }
            val match = if (prop.filterRegex) {
                filterRegex.firstOrNull {
                    it.matches(className)
                }
            } else {
                filter.firstOrNull {
                    className.startsWith(it)
                }
            } != null

            if (match && prop.debug) {
                println("[$pluginName] match $className")
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

        val appExtension = project.extensions.getByType(
            AndroidComponentsExtension::class.java
        )

        project.extensions.create(pluginName, PluginProp::class.java)

        project.dependencies.add(JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME, "com.rover12421.android.plugins.namehash:core:$version")

        appExtension.onVariants { variant ->
            initProp(project)
            if (prop.debug) {
                println("Using hash algorithm: ${prop.hash.algorithm.algorithmName()}")
            }

            val scope = if (prop.allProject) {
                InstrumentationScope.ALL
            } else {
                InstrumentationScope.PROJECT
            }

            variant.instrumentation.apply {
                transformClassesWith(
                    PluginTransform::class.java, scope
                ) {}

                setAsmFramesComputationMode(FramesComputationMode.COPY_FRAMES)
            }
        }
    }
}
