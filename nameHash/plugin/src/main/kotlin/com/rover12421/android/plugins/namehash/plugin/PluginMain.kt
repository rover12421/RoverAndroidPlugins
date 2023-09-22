package com.rover12421.android.plugins.namehash.plugin

import com.android.build.api.instrumentation.FramesComputationMode
import com.android.build.api.instrumentation.InstrumentationScope
import com.android.build.api.variant.AndroidComponentsExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class PluginMain : Plugin<Project> {
    companion object {
        const val pluginName = "nameHash"
        var prop: PluginProp = PluginProp()
        var first = true
    }

    override fun apply(project: Project) {
        val version = javaClass.`package`.implementationVersion
        println("plugin - $pluginName($version)")

        val appExtension = project.extensions.getByType(
            AndroidComponentsExtension::class.java
        )

        project.extensions.create(pluginName, PluginProp::class.java)

        project.dependencies.add("implementation", "com.rover12421.android.plugins.namehash:core:$version")

        appExtension.onVariants { variant ->
            if (first) {
                prop = project.extensions.findByType(PluginProp::class.java)!!
                if (prop.debug) {
                    println("Using hash algorithm: ${prop.hash.algorithm}")
                }
                first = false
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
