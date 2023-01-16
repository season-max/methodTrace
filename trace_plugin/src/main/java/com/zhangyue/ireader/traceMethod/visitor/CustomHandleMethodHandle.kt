package com.zhangyue.ireader.traceMethod.visitor

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
        val internalName = customHandle.replace(".", "/")
        mv.visitTypeInsn(Opcodes.NEW, internalName)
        mv.visitInsn(Opcodes.DUP)
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, internalName, "<init>", "()V", false)
        mv.visitTypeInsn(
            Opcodes.CHECKCAST,
            "com/zhangyue/ireader/traceProcess/handle/IMethodTraceHandle"
        )
        mv.visitFieldInsn(
            Opcodes.PUTSTATIC,
            "com/zhangyue/ireader/traceProcess/MethodTrace",
            "METHOD_TRACE_HANDLE",
            "Lcom/zhangyue/ireader/traceProcess/handle/IMethodTraceHandle;"
        )
    }

}