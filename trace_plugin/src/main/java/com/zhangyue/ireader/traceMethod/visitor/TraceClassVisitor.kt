package com.zhangyue.ireader.traceMethod.visitor

import com.zhangyue.ireader.traceMethod.utils.Logger
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class TraceClassVisitor(api: Int, cv: ClassVisitor) : ClassVisitor(api, cv) {

    lateinit var className: String

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
        val mv = super.visitMethod(access, name, descriptor, signature, exceptions)
        return if (abstract || init) {
            Logger.info("$className --> $name is abstract or init method")
            mv
        } else {
            TraceMethodAdapter(className, api, mv, access, name, descriptor)
        }
    }
}