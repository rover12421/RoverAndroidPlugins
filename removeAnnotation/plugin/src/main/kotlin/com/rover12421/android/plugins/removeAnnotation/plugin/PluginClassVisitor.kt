package com.rover12421.android.plugins.removeAnnotation.plugin

import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class PluginClassVisitor(nextClassVisitor: ClassVisitor, val param: PluginParam)
    : ClassVisitor(Opcodes.ASM9, nextClassVisitor) {

    val debug = param.debug.get()

    fun isRemove(descriptor: String): Boolean {
//        println("visitAnnotation($debug): $descriptor")
        val desc = descriptor.removeSurrounding("L", ";").replace("/", ".")
        val find = param.annotations.get().firstOrNull {
            it == desc
        } != null

        return if (find) {
            if (debug) {
                println("remove annotation : $descriptor")
            }
            true
        } else {
            false
        }
    }

    override fun visitAnnotation(descriptor: String, visible: Boolean): AnnotationVisitor? {
        if (isRemove(descriptor)) {
            return null
        }
        return super.visitAnnotation(descriptor, visible)
    }

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
//        println("visitMethod($debug): $name $descriptor $signature")
        val methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions)
        return object: MethodVisitor(Opcodes.ASM9, methodVisitor) {
            override fun visitAnnotation(descriptor: String, visible: Boolean): AnnotationVisitor? {
                if (isRemove(descriptor)) {
                    return null
                }
                return super.visitAnnotation(descriptor, visible)
            }
        }
    }
}