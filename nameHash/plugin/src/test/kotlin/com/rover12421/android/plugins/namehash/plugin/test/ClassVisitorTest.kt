package com.rover12421.android.plugins.namehash.plugin.test

import com.rover12421.android.plugins.namehash.plugin.PluginClassVisitor
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import kotlin.io.path.absolute
import kotlin.io.path.deleteIfExists
import kotlin.io.path.readBytes
import kotlin.io.path.writeBytes

object ClassVisitorTest {
    @JvmStatic
    fun main(args: Array<String>) {
        val workPath = Paths.get("").absolute()
        val srcClassPath = "app/build/tmp/kotlin-classes/release/com/rover12421/android/plugins/app/MainActivity.class"
        val fromClsPath = workPath.resolve(srcClassPath)
        val classBytes: ByteArray = fromClsPath.readBytes()
        val classReader = ClassReader(classBytes)
        val classWriter = ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
        val classVisitor = PluginClassVisitor(classWriter)
        classReader.accept(classVisitor, 0)
        val transformedClassBytes = classWriter.toByteArray()
        val toClsPath = workPath.resolve("nameHash/plugin/build/tmp/MainActivity.class")
        toClsPath.deleteIfExists()
        toClsPath.writeBytes(transformedClassBytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
    }
}