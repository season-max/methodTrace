package com.zhangyue.ireader.traceMethod.visitor

import com.zhangyue.ireader.traceMethod.transform.TraceTransform.Companion.APPLY_CONFIG_CLASS_NAME
import com.zhangyue.ireader.traceMethod.transform.TraceTransform.Companion.APPLY_CONFIG_METHOD_NAME
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor

class ApplyConfigClassVisitor(
    api: Int,
    cv: ClassVisitor
) : ClassVisitor(api, cv) {

    private var className: String? = null

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
        val mv = super.visitMethod(access, name, descriptor, signature, exceptions)
        val isApplyConfigMethod =
            className == APPLY_CONFIG_CLASS_NAME
                    && name == APPLY_CONFIG_METHOD_NAME
        return if (isApplyConfigMethod) {
            ApplyConfigMethodAdapter(className!!, api, mv, access, name, descriptor)
        } else {
            mv
        }
    }


}