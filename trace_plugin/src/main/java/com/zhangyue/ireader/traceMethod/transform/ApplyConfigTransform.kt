package com.zhangyue.ireader.traceMethod.transform

import com.zhangyue.ireader.traceMethod.visitor.ApplyConfigClassVisitor
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter

class ApplyConfigTransform : TransformListener {
    override fun onTransform(bytes: ByteArray): ByteArray {
        val classReader = ClassReader(bytes)
        val classWriter = ClassWriter(
            classReader,
            ClassWriter.COMPUTE_MAXS //自动计算栈深和局部变量表大小
        )
        val cv = ApplyConfigClassVisitor(TraceTransform.ASM_API, classWriter)
        classReader.accept(cv, ClassReader.EXPAND_FRAMES)
        return classWriter.toByteArray()
    }
}