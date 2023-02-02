package com.zhangyue.ireader.traceMethod.transform

import com.zhangyue.ireader.traceMethod.visitor.TraceClassVisitor
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter

/**
 * 为匹配的方法入口和出口织入代码
 */
class MethodTraceTransform : TransformListener {
    override fun onTransform(className: String, bytes: ByteArray): ByteArray {
        val classReader = ClassReader(bytes)
        val classWriter = ClassWriter(
            classReader,
            ClassWriter.COMPUTE_MAXS //自动计算栈深和局部变量表大小
        )
        val cv = TraceClassVisitor(MethodTraceFirstTranceTransform.ASM_API, classWriter)
        classReader.accept(cv, ClassReader.EXPAND_FRAMES)
        return classWriter.toByteArray()
    }
}