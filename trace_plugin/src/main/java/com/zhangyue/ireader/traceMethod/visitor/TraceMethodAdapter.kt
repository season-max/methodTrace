package com.zhangyue.ireader.traceMethod.visitor

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.commons.AdviceAdapter

class TraceMethodAdapter(
    className: String,
    api: Int,
    mv: MethodVisitor,
    access: Int,
    name: String,
    descriptor: String
) : AdviceAdapter(api, mv, access, name, descriptor) {

    private var className: String
    private var methodName: String

    init {
        this.className = className
        this.methodName = name
    }

    companion object {
        /**
         * 方法处理类
         */
        const val METHOD_TRACE_CLASS = "com/zhangyue/ireader/traceProcess/MethodTrace"
        const val METHOD_TRACE_ENTER = "onMethodEnter"
        const val METHOD_TRACE_EXIT = "onMethodExit"

        /**
         * 类全限定名.方法名 分割符
         */
        const val METHOD_TRACE_PARTITION = "$"
    }

    override fun onMethodEnter() {
        super.onMethodEnter()
        mv.visitMethodInsn(
            Opcodes.INVOKESTATIC,
            METHOD_TRACE_CLASS,
            METHOD_TRACE_ENTER,
            "()V",
            false
        )
    }

    override fun onMethodExit(opcode: Int) {
        super.onMethodExit(opcode)
        mv.visitLdcInsn(generateMethodName())
        mv.visitMethodInsn(
            Opcodes.INVOKESTATIC,
            METHOD_TRACE_CLASS,
            METHOD_TRACE_EXIT,
            "(Ljava/lang/String;)V",
            false
        )
    }

    private fun generateMethodName(): String {
        return className.replace("/", ".") + METHOD_TRACE_PARTITION + methodName
    }


}