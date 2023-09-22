package com.rover12421.android.plugins.namehash.plugin

import com.rover12421.android.plugins.namehash.core.Hash
import com.rover12421.android.plugins.namehash.core.HashName
import com.rover12421.android.plugins.namehash.core.HashValue
import org.objectweb.asm.Type
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.FieldVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

interface VisitSuperInfo {
    fun visitSuperAnnotation(descriptor: String, visible: Boolean): AnnotationVisitor?
}

class PluginClassVisitor(nextClassVisitor: ClassVisitor)
    : ClassVisitor(Opcodes.ASM9, nextClassVisitor), VisitSuperInfo {

    companion object {
        val HashNameAnnotationDesc = Type.getDescriptor(HashName::class.java)!!
        val HashAnnotationDesc = Type.getDescriptor(Hash::class.java)!!
        val HashValueAnnotationDesc = Type.getDescriptor(HashValue::class.java)!!
        val SimpleNameRegex = "(.+/)?(.+)".toRegex()
    }

//    private val debug = param?.debug.get()

    class HashNameAnnotationVisitor(private val visitSuperInfo: VisitSuperInfo, private val name: String): AnnotationVisitor(Opcodes.ASM9) {
        private val namesArrayVisitor = NamesArrayVisitor()
        override fun visitArray(name: String?): AnnotationVisitor {
            if (name == "names") {
                return namesArrayVisitor
            }
            return super.visitArray(name)
        }

        override fun visitEnd() {
            val debug = PluginMain.prop.debug
            super.visitEnd()
            val names = namesArrayVisitor.names
            if (names.isEmpty()) {
                if (debug) {
                    println("HashName names is empty. Add default name: $name")
                }
                names.add(name)
            }

            visitSuperInfo.visitSuperAnnotation(HashAnnotationDesc, true)?.apply {
                visitArray("values").apply {
//                    println("names: $names")
                    names.forEach { name ->
//                        println("visitAnnotation $name")
                        visitAnnotation(null, HashValueAnnotationDesc).apply {
                            val hash = PluginMain.prop.hash.hash(name)
                            val len = name.length
                            val hashCode = name.hashCode()
                            if (debug) {
                                println("Inject HashValue(len=$len, hashCode=$hashCode, hash=$hash)")
                            }
                            visit("hash", hash)
                            visit("hashcode", hashCode)
                            visit("len", len)
                            visitEnd()
                        }
                    }
                    visitEnd()
                }
                visitEnd()
            }
        }
    }

    class NamesArrayVisitor: AnnotationVisitor(Opcodes.ASM9) {
        val names: MutableList<String> = mutableListOf()
        override fun visit(name: String?, value: Any?) {
            super.visit(name, value)
//            println("[NamesArrayVisitor] visit: $name - $value")
            if (value is String) {
                names.add(value)
            }
        }
    }

    private fun passAnnotation(descriptor: String,
                               visitSuperInfo: VisitSuperInfo,
                               name: String
    ): AnnotationVisitor? {
        if (HashNameAnnotationDesc == descriptor) {
            return HashNameAnnotationVisitor(visitSuperInfo, name)
        }

        return null
    }

    private var clsSimpleName: String = ""

    private fun toClsSimpleName(clsName: String): String {
        var simple = clsName
        SimpleNameRegex.matchEntire(clsName)?.apply {
            groups[2]?.apply {
                simple = value
            }
        }
        return simple
    }

    override fun visit(
        version: Int,
        access: Int,
        name: String,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        this.clsSimpleName = toClsSimpleName(name)
//        println("clsSimpleName: $clsSimpleName")
        super.visit(version, access, name, signature, superName, interfaces)
    }

    override fun visitAnnotation(descriptor: String, visible: Boolean): AnnotationVisitor? {
        return passAnnotation(descriptor, this, clsSimpleName) ?: super.visitAnnotation(descriptor, visible)
    }

    override fun visitMethod(
        access: Int,
        name: String,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        val methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions)
        return object: MethodVisitor(Opcodes.ASM9, methodVisitor), VisitSuperInfo {
            override fun visitAnnotation(descriptor: String, visible: Boolean): AnnotationVisitor? {
                return passAnnotation(descriptor, this, name) ?: super.visitAnnotation(descriptor, visible)
            }

            override fun visitSuperAnnotation(
                descriptor: String,
                visible: Boolean
            ): AnnotationVisitor? {
                return super.visitAnnotation(descriptor, visible)
            }
        }
    }

    override fun visitField(
        access: Int,
        name: String,
        descriptor: String?,
        signature: String?,
        value: Any?
    ): FieldVisitor {
        val visitField = super.visitField(access, name, descriptor, signature, value)
        return object: FieldVisitor(Opcodes.ASM9, visitField), VisitSuperInfo {
            override fun visitAnnotation(descriptor: String, visible: Boolean): AnnotationVisitor? {
                return passAnnotation(descriptor, this, name) ?: super.visitAnnotation(descriptor, visible)
            }

            override fun visitSuperAnnotation(
                descriptor: String,
                visible: Boolean
            ): AnnotationVisitor? {
                return super.visitAnnotation(descriptor, visible)
            }
        }
    }

    override fun visitSuperAnnotation(descriptor: String, visible: Boolean): AnnotationVisitor? {
        return super.visitAnnotation(descriptor, visible)
    }
}