package com.rover12421.android.plugins.removeAnnotation.plugin

import com.android.build.api.instrumentation.FramesComputationMode
import com.android.build.api.instrumentation.InstrumentationScope
import com.android.build.api.variant.AndroidComponentsExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class PluginMain : Plugin<Project> {
    private val pluginName = "removeAnnotation"
    override fun apply(project: Project) {
        val version = javaClass.`package`.implementationVersion
        println("plugin - $pluginName($version)")

        val appExtension = project.extensions.getByType(
            AndroidComponentsExtension::class.java
        )

        project.extensions.create(pluginName, PluginProp::class.java)

        appExtension.onVariants { variant ->
            val prop: PluginProp = project.extensions.findByType(PluginProp::class.java)!!
            val scope = if (prop.allProject) {
                InstrumentationScope.ALL
            } else {
                InstrumentationScope.PROJECT
            }

            if (prop.annotations.isEmpty()) {
                if (prop.debug) {
                    println("remove annotations is empty!")
                }
                return@onVariants
            }
            variant.instrumentation.apply {
                transformClassesWith(
                    PluginTransform::class.java, scope
                ) {
                    it.filters.set(prop.filter)
                    it.annotations.set(prop.annotations)
                    it.debug.set(prop.debug)
                }

                setAsmFramesComputationMode(FramesComputationMode.COPY_FRAMES)
            }
        }
    }
}
