package com.zhangyue.ireader.traceMethod.visitor

import com.zhangyue.ireader.traceMethod.GlobalConfig
import com.zhangyue.ireader.traceMethod.transform.TraceTransform
import com.zhangyue.ireader.traceMethod.transform.TraceTransform.Companion.APPLY_CONFIG_METHOD_NAME
import com.zhangyue.ireader.traceMethod.transform.TraceTransform.Companion.HANDLE_METHOD_CONST_PACKAGE
import com.zhangyue.ireader.traceMethod.utils.Logger
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class TraceClassVisitor(api: Int, cv: ClassVisitor) : ClassVisitor(api, cv) {

    lateinit var className: String

    private var isInPkgList = false

    private var isHandleClass = false

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
        isHandleClass = handleClass(className)
    }

    private fun handleClass(className: String): Boolean {
        return className.replace("/", ".").startsWith(
            HANDLE_METHOD_CONST_PACKAGE
        )
    }

    private fun inPkgList(className: String): Boolean {
        val tempName = className.replace("/", ".")
        val isInPkgList = GlobalConfig.pluginConfig.pkgList.contains(tempName) { init, it ->
            init.startsWith(it)
        }
        return isInPkgList.also {
            if (it) {
                Logger.info("contains $className")
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
        val isApplyConfigMethod =
            className == TraceTransform.APPLY_CONFIG_CLASS_NAME
                    && name == APPLY_CONFIG_METHOD_NAME
        return if (isApplyConfigMethod) {
            TraceMethodConfigAdapter(className, api, mv, access, name, descriptor)
        } else if (!abstract && !init && !cinit && !isHandleClass && isInPkgList) {
            Logger.info("trace method $className --> $name")
            TraceMethodAdapter(className, api, mv, access, name, descriptor)
        } else {
            mv
        }
    }
}