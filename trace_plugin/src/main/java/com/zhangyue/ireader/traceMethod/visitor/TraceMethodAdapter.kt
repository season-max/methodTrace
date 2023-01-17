package com.zhangyue.ireader.traceMethod.visitor

import com.zhangyue.ireader.traceMethod.transform.MethodTraceFirstTranceTransform.Companion.DOT
import com.zhangyue.ireader.traceMethod.transform.MethodTraceFirstTranceTransform.Companion.METHOD_TRACE_CLASS_NAME
import com.zhangyue.ireader.traceMethod.transform.MethodTraceFirstTranceTransform.Companion.METHOD_TRACE_ENTER
import com.zhangyue.ireader.traceMethod.transform.MethodTraceFirstTranceTransform.Companion.METHOD_TRACE_EXIT
import com.zhangyue.ireader.traceMethod.transform.MethodTraceFirstTranceTransform.Companion.METHOD_TRACE_PARTITION
import com.zhangyue.ireader.traceMethod.transform.MethodTraceFirstTranceTransform.Companion.SEPARATOR
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

    override fun onMethodEnter() {
        super.onMethodEnter()
        mv.visitMethodInsn(
            Opcodes.INVOKESTATIC,
            METHOD_TRACE_CLASS_NAME.replace(DOT, SEPARATOR),
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
            METHOD_TRACE_CLASS_NAME.replace(DOT, SEPARATOR),
            METHOD_TRACE_EXIT,
            "(Ljava/lang/String;)V",
            false
        )
    }

    private fun generateMethodName(): String {
        return className.replace(SEPARATOR, DOT) + METHOD_TRACE_PARTITION + methodName
    }


}