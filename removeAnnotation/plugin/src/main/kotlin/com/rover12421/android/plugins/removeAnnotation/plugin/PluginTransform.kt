package com.rover12421.android.plugins.removeAnnotation.plugin

import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.ClassContext
import com.android.build.api.instrumentation.ClassData
import org.objectweb.asm.ClassVisitor

abstract class PluginTransform : AsmClassVisitorFactory<PluginParam> {

    override fun createClassVisitor(
        classContext: ClassContext,
        nextClassVisitor: ClassVisitor
    ): ClassVisitor {
        return PluginClassVisitor(nextClassVisitor, parameters.get())
    }

    override fun isInstrumentable(classData: ClassData): Boolean {
        val className = classData.className
//        println("isInstrumentable $className")
        val filter = parameters.get().filters.get()
        if (filter.isEmpty()) {
            return true
        }
        val match =  filter.firstOrNull {
            it.toRegex().matches(className)
        } != null
        if (match && parameters.get().debug.get() == true) {
            println("[match] $className")
        }
        return match
    }
}