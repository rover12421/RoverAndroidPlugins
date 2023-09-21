package com.rover12421.android.plugins.removeAnnotation.plugin

import com.android.build.api.instrumentation.FramesComputationMode
import com.android.build.api.instrumentation.InstrumentationScope
import com.android.build.api.variant.AndroidComponentsExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class PluginMain : Plugin<Project> {
    override fun apply(project: Project) {
        println("plugin - removeAnnotation")

        val appExtension = project.extensions.getByType(
            AndroidComponentsExtension::class.java
        )

        project.extensions.create("removeAnnotation", PluginProp::class.java)

        appExtension.onVariants { variant ->
            // 在外部获取为空,需要在这里获取才有数据
            val prop: PluginProp = project.extensions.findByType(PluginProp::class.java)!!
            println("removeAnnotation in ${variant.name}. Debug: ${prop.debug}")

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
