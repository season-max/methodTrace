package com.zhangyue.ireader.traceMethod.transform

import com.zhangyue.ireader.traceMethod.GlobalConfig
import com.zhangyue.ireader.traceMethod.transform.TraceTransform.Companion.METHOD_TRACE_CLASS_NAME
import com.zhangyue.ireader.traceMethod.utils.Logger
import com.zhangyue.ireader.traceMethod.visitor.CustomHandleClassVisitor
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter

class CustomHandleTransform : TransformListener {
    override fun onTransform(className: String, bytes: ByteArray): ByteArray {
        println("className --> $className")
        return if (className == METHOD_TRACE_CLASS_NAME) {
            val hasSetCustomHandleMethodTrace = GlobalConfig.pluginConfig.customHandle.let {
                it != null && it.isNotEmpty()
            }
            Logger.info("hasSetCustomHandleMethodTrace:$hasSetCustomHandleMethodTrace")
            if (hasSetCustomHandleMethodTrace) {
                Logger.info("transform custom handle method class:$className")
                val classReader = ClassReader(bytes)
                val classWriter = ClassWriter(
                    classReader,
                    ClassWriter.COMPUTE_MAXS //自动计算栈深和局部变量表大小
                )
                val cv = CustomHandleClassVisitor(
                    TraceTransform.ASM_API,
                    classWriter,
                    GlobalConfig.pluginConfig.customHandle!!
                )
                classReader.accept(cv, ClassReader.EXPAND_FRAMES)
                classWriter.toByteArray()
            } else {
                bytes
            }
        } else {
            bytes
        }

    }
}