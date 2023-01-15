package com.zhangyue.ireader.traceMethod.transform

import com.zhangyue.ireader.traceMethod.GlobalConfig
import com.zhangyue.ireader.traceMethod.visitor.TraceClassVisitor
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter

class MethodTraceTransform : TransformListener {
    override fun onTransform(bytes: ByteArray): ByteArray {
        if (!GlobalConfig.enableMethodTrace) {
            return bytes
        }
        val classReader = ClassReader(bytes)
        val classWriter = ClassWriter(
            classReader,
            ClassWriter.COMPUTE_MAXS //自动计算栈深和局部变量表大小
        )
        val cv = TraceClassVisitor(TraceTransform.ASM_API, classWriter)
        classReader.accept(cv, ClassReader.EXPAND_FRAMES)
        return classWriter.toByteArray()
    }
}