package com.zhangyue.ireader.traceMethod.visitor

import com.zhangyue.ireader.traceMethod.GlobalConfig
import com.zhangyue.ireader.traceMethod.printer.TraceMethodBean
import com.zhangyue.ireader.traceMethod.printer.TraceMethodManager
import com.zhangyue.ireader.traceMethod.transform.FirstTraceTransform.Companion.DOT
import com.zhangyue.ireader.traceMethod.transform.FirstTraceTransform.Companion.EXECUTOR_ANNOTATION_DESCRIPTOR
import com.zhangyue.ireader.traceMethod.transform.FirstTraceTransform.Companion.IGNORE_ANNOTATION_DESCRIPTOR
import com.zhangyue.ireader.traceMethod.transform.FirstTraceTransform.Companion.TRACE_METHOD_PROCESS_PACKAGE
import com.zhangyue.ireader.traceMethod.transform.FirstTraceTransform.Companion.SEPARATOR
import com.zhangyue.ireader.traceMethod.utils.Logger
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class TraceClassVisitor(api: Int, cv: ClassVisitor) : ClassVisitor(api, cv) {

    lateinit var className: String

    /**
     * 是否匹配设置的包名路径
     */
    private var isInSetupPathList = false

    /**
     * 是否是插件内部的 package
     * 如果是，不做插桩处理
     */
    private var isInPluginPkg = false

    /**
     * 是否包含忽略插桩注解
     */
    private var hasIgnoreAnnotation = false

    /**
     * 是否包含执行插桩注解
     */
    private var hasExecutorAnnotation = false


    private val bean: TraceMethodBean = TraceMethodBean(className.replace(SEPARATOR, DOT))

    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        super.visit(version, access, name, signature, superName, interfaces)
        className = name ?: "UNKNOWN"
        isInSetupPathList = inSetUpPathList(className)
        isInPluginPkg = inPluginPkg(className)
    }

    override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor {
        when (descriptor) {
            IGNORE_ANNOTATION_DESCRIPTOR -> hasIgnoreAnnotation = true
            EXECUTOR_ANNOTATION_DESCRIPTOR -> hasExecutorAnnotation = true
        }
        return super.visitAnnotation(descriptor, visible)
    }

    private fun inPluginPkg(className: String): Boolean {
        return className.replace(SEPARATOR, DOT).startsWith(
            TRACE_METHOD_PROCESS_PACKAGE
        )
    }

    private fun inSetUpPathList(className: String): Boolean {
        val tempName = className.replace(SEPARATOR, DOT)
        val isInPkgList = GlobalConfig.pluginConfig.pkgList.startWith(tempName) { init, it ->
            init.startsWith(it)
        }
        return isInPkgList.also {
            if (it) {
                Logger.info("transform class $className")
            }
        }
    }


    private inline fun <T : CharSequence> Iterable<T>.startWith(
        init: T,
        action: (init: T, T) -> Boolean
    ): Boolean {
        var contains = false
        for (e in this) {
            if (action(init, e)) {
                contains = true
                break
            }
        }
        return contains
    }


    override fun visitMethod(
        access: Int,
        name: String,
        descriptor: String,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        val abstract = access and Opcodes.ACC_ABSTRACT == Opcodes.ACC_ABSTRACT
        val mv = super.visitMethod(access, name, descriptor, signature, exceptions)
        // 抽象方法不处理
        if (abstract) {
            return mv
        }
        // 有忽略插桩的注解
        if (hasIgnoreAnnotation) {
            return mv
        }
        // 在插件内部的包内部不处理
        if (isInPluginPkg) {
            return mv
        }
        return if (isInSetupPathList || hasExecutorAnnotation) {
            Logger.info("trace method $className --> $name")
            TraceMethodAdapter(
                className, api, mv, access, name, descriptor, TraceMethodBean(
                    className.replace(
                        SEPARATOR, DOT
                    )
                )
            )
        } else {
            mv
        }
    }

    override fun visitEnd() {
        super.visitEnd()
        TraceMethodManager.get().addItem(bean)
    }
}