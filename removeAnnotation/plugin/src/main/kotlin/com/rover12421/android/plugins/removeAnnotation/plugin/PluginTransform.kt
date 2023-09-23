package com.rover12421.android.plugins.removeAnnotation.plugin

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
        return PluginClassVisitor(nextClassVisitor, PluginMain.prop)
    }

    override fun isInstrumentable(classData: ClassData): Boolean {
        return PluginMain.isMatchClass(classData.className)
    }
}