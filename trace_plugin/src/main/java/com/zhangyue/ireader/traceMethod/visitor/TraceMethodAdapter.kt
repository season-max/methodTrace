package com.zhangyue.ireader.traceMethod.visitor

import com.zhangyue.ireader.traceMethod.GlobalConfig
import com.zhangyue.ireader.traceMethod.printer.MethodInfo
import com.zhangyue.ireader.traceMethod.printer.TraceMethodBean
import com.zhangyue.ireader.traceMethod.transform.FirstTraceTransform.Companion.COMMA
import com.zhangyue.ireader.traceMethod.transform.FirstTraceTransform.Companion.DOT
import com.zhangyue.ireader.traceMethod.transform.FirstTraceTransform.Companion.EXECUTOR_ANNOTATION_DESCRIPTOR
import com.zhangyue.ireader.traceMethod.transform.FirstTraceTransform.Companion.METHOD_TRACE_CLASS_NAME
import com.zhangyue.ireader.traceMethod.transform.FirstTraceTransform.Companion.METHOD_TRACE_ENTER_DESCRIPTOR
import com.zhangyue.ireader.traceMethod.transform.FirstTraceTransform.Companion.METHOD_TRACE_ENTER_NAME
import com.zhangyue.ireader.traceMethod.transform.FirstTraceTransform.Companion.METHOD_TRACE_EXIT_DESCRIPTOR
import com.zhangyue.ireader.traceMethod.transform.FirstTraceTransform.Companion.METHOD_TRACE_EXIT_NAME
import com.zhangyue.ireader.traceMethod.transform.FirstTraceTransform.Companion.SEPARATOR
import com.zhangyue.ireader.traceMethod.utils.itemMatchAction
import org.objectweb.asm.AnnotationVisitor
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
    hasExecutorAnnotation: Boolean,
    traceMethodBean: TraceMethodBean
) : AdviceAdapter(api, mv, access, name, descriptor) {
    private var className: String
    private var methodName: String

    // 是否匹配设置的包名路径
    private var isInSetupPathList = false

    // 包裹方法的类有 [com.zhangyue.ireader.trace_1_2_3_7_process.annotation.HookMethodTrace] 注解
    private val hasExecutorAnnotationOnClass: Boolean

    // 方法上是否有执行插桩的注解
    private var hasAnnotationOnMethod = false

    // 是否是 static 方法
    private var static: Boolean = false

    // 是否执行注入逻辑
    private var inject: Boolean = false

    // 是否是全插桩
    private var injectAll: Boolean = false

    // 用来记录哪些类执行了插桩
    private val bean: TraceMethodBean = traceMethodBean

    init {
        this.className = className.replace(SEPARATOR, DOT)
        this.methodName = name
        this.static = access and Opcodes.ACC_STATIC == Opcodes.ACC_STATIC
        this.injectAll = GlobalConfig.injectAllInScope
        this.isInSetupPathList = inSetUpPathList(this.className)
        this.hasExecutorAnnotationOnClass = hasExecutorAnnotation
    }

    private fun inSetUpPathList(className: String): Boolean {
        return GlobalConfig.pluginConfig.pkgList?.itemMatchAction(className) { init, it ->
            init.startsWith(it)
        } ?: false
    }

    override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor {
        if (descriptor == EXECUTOR_ANNOTATION_DESCRIPTOR) {
            hasAnnotationOnMethod = true
        }
        return super.visitAnnotation(descriptor, visible)
    }

    override fun visitCode() {
        inject = hasExecutorAnnotationOnClass
                || hasAnnotationOnMethod
                || isInSetupPathList
                || injectAll
        super.visitCode()
    }


    override fun onMethodEnter() {
        if (!inject) {
            return
        }
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
        if (!inject) {
            return
        }
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
        val arg = argumentTypes ?: return "()"
        val builder = StringBuilder()
        builder.append("(")
        for ((i, value) in arg.withIndex()) {
            builder.append(value.className)
            if (i != arg.size - 1) {
                builder.append(COMMA)
            }
        }
        builder.append(")")
        return builder.toString()
    }

    private fun returns(): String {
        val `return` = returnType ?: return "[]"
        return "[${`return`.className}]"
    }

    override fun visitEnd() {
        super.visitEnd()
        if (!inject) {
            return
        }
        bean.methodList.add(MethodInfo(methodName, args(), returns()))
    }

}