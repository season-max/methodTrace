package com.zhangyue.ireader.traceMethod.visitor

import com.zhangyue.ireader.traceMethod.GlobalConfig
import com.zhangyue.ireader.traceMethod.utils.Logger
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class TraceClassVisitor(api: Int, cv: ClassVisitor) : ClassVisitor(api, cv) {

    lateinit var className: String

    private var isInPkgList = false

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
        inPkgList(className)
    }

    private fun inPkgList(className: String) {
        val tempName = className.replace("/", ".")
        isInPkgList = GlobalConfig.pluginConfig.pkgList.contains(tempName) { init, it ->
            init.startsWith(it)
        }
        if (isInPkgList) {
            Logger.info("contains $className")
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
        return if (abstract || init || cinit || !isInPkgList) {
            mv
        } else {
            Logger.info("trace method $className --> $name")
            TraceMethodAdapter(className, api, mv, access, name, descriptor)
        }
    }
}