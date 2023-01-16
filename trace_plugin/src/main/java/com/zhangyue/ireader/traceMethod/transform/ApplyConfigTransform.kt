package com.zhangyue.ireader.traceMethod.transform

import com.zhangyue.ireader.traceMethod.transform.TraceTransform.Companion.APPLY_CONFIG_CLASS_NAME
import com.zhangyue.ireader.traceMethod.visitor.ApplyConfigClassVisitor
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter

class ApplyConfigTransform : TransformListener {
    override fun onTransform(className: String, bytes: ByteArray): ByteArray {
        return if (className == APPLY_CONFIG_CLASS_NAME) {
            val classReader = ClassReader(bytes)
            val classWriter = ClassWriter(
                classReader,
                ClassWriter.COMPUTE_MAXS //自动计算栈深和局部变量表大小
            )
            val cv = ApplyConfigClassVisitor(TraceTransform.ASM_API, classWriter)
            classReader.accept(cv, ClassReader.EXPAND_FRAMES)
            classWriter.toByteArray()
        } else {
            bytes
        }
    }
}