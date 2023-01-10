package com.zhangyue.ireader.traceMethod.transform

import com.android.build.api.transform.TransformInvocation
import com.zhangyue.ireader.traceMethod.visitor.TraceClassVisitor
import org.gradle.api.Project
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import kotlin.properties.Delegates


class TraceTransform(project: Project) : BaseTransform(project) {

    private var startTime by Delegates.notNull<Long>()

    override fun shouldHookClassInner(className: String): Boolean {
        return true
    }

    override fun transformClassInner(className: String, sourceBytes: ByteArray): ByteArray? {
        val classReader = ClassReader(sourceBytes)
        val classWriter = ClassWriter(
            classReader,
            ClassWriter.COMPUTE_MAXS //自动计算栈深和局部变量表大小
        )
        val cv = TraceClassVisitor(ASM_API, classWriter)
        classReader.accept(cv, ClassReader.EXPAND_FRAMES)
        return classWriter.toByteArray()
    }

    override fun onTransformStart(transformInvocation: TransformInvocation) {
        println("$name start--------------->")
        startTime = System.currentTimeMillis()
    }

    override fun onTransformEnd(transformInvocation: TransformInvocation) {
        println("$name end---------------> duration : ${System.currentTimeMillis() - startTime}")
    }

    companion object {
        const val ASM_API = Opcodes.ASM9
    }

}