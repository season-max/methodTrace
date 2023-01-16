package com.zhangyue.ireader.traceMethod.visitor

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor

class CustomHandleClassVisitor(
    api: Int,
    cv: ClassVisitor,
    customHandle: String
) : ClassVisitor(api, cv) {

    private var className: String? = null
    private val customHandle: String

    init {
        this.customHandle = customHandle
    }

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
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        val mv = super.visitMethod(access, name, descriptor, signature, exceptions)
        return if (name == "<clinit>" && descriptor == "()V") {//类初始化
            CustomHandleMethodHandle(className!!, api, mv, access, name, descriptor,customHandle)
        } else {
            mv
        }
    }
}