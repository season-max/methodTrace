package com.zhangyue.ireader.traceMethod.visitor

import com.zhangyue.ireader.traceMethod.printer.MethodInfo
import com.zhangyue.ireader.traceMethod.printer.TraceMethodBean
import com.zhangyue.ireader.traceMethod.transform.FirstTraceTransform.Companion.COMMA
import com.zhangyue.ireader.traceMethod.transform.FirstTraceTransform.Companion.DOT
import com.zhangyue.ireader.traceMethod.transform.FirstTraceTransform.Companion.METHOD_TRACE_CLASS_NAME
import com.zhangyue.ireader.traceMethod.transform.FirstTraceTransform.Companion.METHOD_TRACE_ENTER_DESCRIPTOR
import com.zhangyue.ireader.traceMethod.transform.FirstTraceTransform.Companion.METHOD_TRACE_ENTER_NAME
import com.zhangyue.ireader.traceMethod.transform.FirstTraceTransform.Companion.METHOD_TRACE_EXIT_DESCRIPTOR
import com.zhangyue.ireader.traceMethod.transform.FirstTraceTransform.Companion.METHOD_TRACE_EXIT_NAME
import com.zhangyue.ireader.traceMethod.transform.FirstTraceTransform.Companion.SEPARATOR
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
    descriptor: String,
    traceMethodBean: TraceMethodBean
) : AdviceAdapter(api, mv, access, name, descriptor) {

    private var className: String
    private var methodName: String
    private var init: Boolean = false
    private var clinit: Boolean = false
    private var static: Boolean = false
    private val bean: TraceMethodBean = traceMethodBean

    init {
        this.className = className.replace(SEPARATOR, DOT)
        this.methodName = name
        init = name == "<init>"
        clinit = name == "<clinit>"
        static = access and Opcodes.ACC_STATIC == Opcodes.ACC_STATIC
    }

    override fun onMethodEnter() {
        super.onMethodEnter()
        if (static) {
//            mv.visitInsn(ACONST_NULL)
            push("null")
        } else {
            mv.visitVarInsn(ALOAD, 0)
        }
        push(className)
        push(methodName)
        push(args())
        push(returns())
        mv.visitMethodInsn(
            Opcodes.INVOKESTATIC,
            METHOD_TRACE_CLASS_NAME.replace(DOT, SEPARATOR),
            METHOD_TRACE_ENTER_NAME,
            METHOD_TRACE_ENTER_DESCRIPTOR,
            false
        )
    }

    override fun onMethodExit(opcode: Int) {
        super.onMethodExit(opcode)
        if (static) {
//            mv.visitInsn(ACONST_NULL)
            push("null")
        } else {
            mv.visitIntInsn(ALOAD, 0)
        }
        push(className)
        push(methodName)
        push(args())
        push(returns())
        mv.visitMethodInsn(
            Opcodes.INVOKESTATIC,
            METHOD_TRACE_CLASS_NAME.replace(DOT, SEPARATOR),
            METHOD_TRACE_EXIT_NAME,
            METHOD_TRACE_EXIT_DESCRIPTOR,
            false
        )
    }

    private fun args(): String {
        val arg = argumentTypes ?: return "[]"
        val builder = StringBuilder()
        builder.append("[")
        for ((i, value) in arg.withIndex()) {
            builder.append(value)
            if (i != arg.size - 1) {
                builder.append(COMMA)
            }
        }
        builder.append("]")
        return builder.toString()
    }

    private fun returns(): String {
        val `return` = returnType ?: return "[]"
        return "[${`return`.className}]"
    }

    override fun visitEnd() {
        super.visitEnd()
        bean.methodList.add(MethodInfo(methodName, args(), returns()))
    }

}