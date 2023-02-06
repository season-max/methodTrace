package com.zhangyue.ireader.traceMethod.visitor

import com.zhangyue.ireader.traceMethod.transform.FirstTranceTransform.Companion.DOT
import com.zhangyue.ireader.traceMethod.transform.FirstTranceTransform.Companion.METHOD_TRACE_CLASS_NAME
import com.zhangyue.ireader.traceMethod.transform.FirstTranceTransform.Companion.METHOD_TRACE_ENTER_NAME
import com.zhangyue.ireader.traceMethod.transform.FirstTranceTransform.Companion.METHOD_TRACE_EXIT_NAME
import com.zhangyue.ireader.traceMethod.transform.FirstTranceTransform.Companion.SEPARATOR
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.commons.AdviceAdapter

/**
 * 用来访问 [com.zhangyue.ireader.trace_1_2_3_7_process.MethodTrace] onMethodEnter 和 onMethodExit 方法
 * @author yaoxinxin
 */
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
    private var init: Boolean = false
    private var clinit: Boolean = false
    private var static: Boolean = false

    init {
        this.className = className
        this.methodName = name
        init = name == "<init>"
        clinit = name == "<clinit>"
        static = access and Opcodes.ACC_STATIC == Opcodes.ACC_STATIC
    }

    override fun onMethodEnter() {
        super.onMethodEnter()
        if (static) {
            mv.visitInsn(ACONST_NULL)
        } else {
            mv.visitVarInsn(ALOAD, 0)
        }
        mv.visitLdcInsn(className)
        mv.visitLdcInsn(methodName)
        mv.visitLdcInsn(args())
        mv.visitLdcInsn(returns())
        mv.visitMethodInsn(
            Opcodes.INVOKESTATIC,
            METHOD_TRACE_CLASS_NAME.replace(DOT, SEPARATOR),
            METHOD_TRACE_ENTER_NAME,
            "()V",
            false
        )
    }

    override fun onMethodExit(opcode: Int) {
        super.onMethodExit(opcode)
        mv.visitMethodInsn(
            Opcodes.INVOKESTATIC,
            METHOD_TRACE_CLASS_NAME.replace(DOT, SEPARATOR),
            METHOD_TRACE_EXIT_NAME,
            "(Ljava/lang/String;)V",
            false
        )
    }

    private fun args(): String {
        val arg = argumentTypes

        return ""
    }

    private fun returns(): String {
        return ""
    }


}