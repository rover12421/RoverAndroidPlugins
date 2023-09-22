package com.rover12421.android.plugins.namehash.plugin

import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.ClassContext
import com.android.build.api.instrumentation.ClassData
import com.android.build.api.instrumentation.InstrumentationParameters
import org.objectweb.asm.ClassVisitor

abstract class PluginTransform : AsmClassVisitorFactory<InstrumentationParameters.None> {

    override fun createClassVisitor(
        classContext: ClassContext,
        nextClassVisitor: ClassVisitor
    ): ClassVisitor {
        return PluginClassVisitor(nextClassVisitor)
    }

    override fun isInstrumentable(classData: ClassData): Boolean {
        val className = classData.className
//        println("isInstrumentable $className")
        val filter = PluginMain.prop.filter
        if (filter.isEmpty()) {
            return true
        }
        val match =  filter.firstOrNull {
            it.toRegex().matches(className)
        } != null
        if (match && PluginMain.prop.debug) {
            println("[match] $className")
        }
        return match
    }
}