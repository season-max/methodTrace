package com.zhangyue.ireader.traceMethod.visitor

import com.zhangyue.ireader.traceMethod.GlobalConfig
import com.zhangyue.ireader.traceMethod.transform.MethodTraceFirstTranceTransform.Companion.DOT
import com.zhangyue.ireader.traceMethod.transform.MethodTraceFirstTranceTransform.Companion.IGNORE_ANNOTATION_NAME
import com.zhangyue.ireader.traceMethod.transform.MethodTraceFirstTranceTransform.Companion.TRACE_METHOD_PROCESS_PACKAGE
import com.zhangyue.ireader.traceMethod.transform.MethodTraceFirstTranceTransform.Companion.SEPARATOR
import com.zhangyue.ireader.traceMethod.utils.Logger
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class TraceClassVisitor(api: Int, cv: ClassVisitor) : ClassVisitor(api, cv) {

    lateinit var className: String

    private var isInPkgList = false

    /**
     * 是否是耗时函数处理类
     * 如果是，不做插桩处理
     */
    private var isInTraceProcessPkg = false

    private var needIgnore = false

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
        isInPkgList = inPkgList(className)
        isInTraceProcessPkg = traceProcess(className)
    }

    override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor {
        if (descriptor == "L${IGNORE_ANNOTATION_NAME.replace(DOT, SEPARATOR)};") {
            needIgnore = true
        }
        return super.visitAnnotation(descriptor, visible)
    }

    private fun traceProcess(className: String): Boolean {
        return className.replace(SEPARATOR, DOT).startsWith(
            TRACE_METHOD_PROCESS_PACKAGE
        )
    }

    private fun inPkgList(className: String): Boolean {
        val tempName = className.replace(SEPARATOR, DOT)
        val isInPkgList = GlobalConfig.pluginConfig.pkgList.contains(tempName) { init, it ->
            init.startsWith(it)
        }
        return isInPkgList.also {
            if (it) {
                Logger.info("transform class $className")
            }
        }
    }


    private inline fun <T : CharSequence> Iterable<T>.contains(
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
        val init = name == "<init>"
        val cinit = name == "<clinit>"
        val mv = super.visitMethod(access, name, descriptor, signature, exceptions)
        return if (
            !abstract
            && !init
            && !cinit
            && !isInTraceProcessPkg
            && isInPkgList
            && !needIgnore
        ) {
            Logger.info("trace method $className --> $name")
            TraceMethodAdapter(className, api, mv, access, name, descriptor)
        } else {
            mv
        }
    }
}