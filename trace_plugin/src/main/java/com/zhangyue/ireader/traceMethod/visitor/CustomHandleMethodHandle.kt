package com.zhangyue.ireader.traceMethod.visitor

import com.zhangyue.ireader.traceMethod.transform.FirstTraceTransform.Companion.DOT
import com.zhangyue.ireader.traceMethod.transform.FirstTraceTransform.Companion.FILED_NAME
import com.zhangyue.ireader.traceMethod.transform.FirstTraceTransform.Companion.INTERFACE_METHOD_TRACE_HANDLE
import com.zhangyue.ireader.traceMethod.transform.FirstTraceTransform.Companion.METHOD_TRACE_CLASS_NAME
import com.zhangyue.ireader.traceMethod.transform.FirstTraceTransform.Companion.SEPARATOR
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.commons.AdviceAdapter

class CustomHandleMethodHandle(
    className: String,
    api: Int,
    mv: MethodVisitor,
    access: Int,
    name: String,
    descriptor: String,
    customHandle: String
) : AdviceAdapter(api, mv, access, name, descriptor) {

    private val customHandle: String

    init {
        this.customHandle = customHandle
    }

    /**
     * 方法出口之前，重新设置接口的值
     */
    override fun onMethodExit(opcode: Int) {
        super.onMethodExit(opcode)
        val internalName = customHandle.replace(DOT, SEPARATOR)
        mv.visitTypeInsn(Opcodes.NEW, internalName)
        mv.visitInsn(Opcodes.DUP)
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, internalName, "<init>", "()V", false)
        mv.visitTypeInsn(
            Opcodes.CHECKCAST,
            INTERFACE_METHOD_TRACE_HANDLE.replace(DOT, SEPARATOR)
        )
        mv.visitFieldInsn(
            Opcodes.PUTSTATIC,
            METHOD_TRACE_CLASS_NAME.replace(DOT, SEPARATOR),
            FILED_NAME,
            "L${INTERFACE_METHOD_TRACE_HANDLE.replace(DOT, SEPARATOR)};"
        )
    }

}