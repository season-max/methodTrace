package com.zhangyue.ireader.traceMethod.visitor

import com.zhangyue.ireader.traceMethod.GlobalConfig
import com.zhangyue.ireader.traceMethod.transform.TraceTransform.Companion.APPLY_CONFIG_METHOD_DESCRIPTOR
import com.zhangyue.ireader.traceMethod.utils.Logger
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.commons.AdviceAdapter

class TraceMethodConfigAdapter(
    className: String,
    api: Int,
    mv: MethodVisitor,
    access: Int,
    name: String,
    descriptor: String
) : AdviceAdapter(api, mv, access, name, descriptor) {
    private val className: String

    init {
        this.className = className
    }

    override fun onMethodEnter() {
        super.onMethodEnter()
        Logger.info("-----> $name enter")
    }

    override fun onMethodExit(opcode: Int) {
        super.onMethodExit(opcode)
        Logger.info("-----> $name exit")
        val onlyCheckMain: Boolean = GlobalConfig.pluginConfig.checkOnlyMainThread
        val info: Int = GlobalConfig.pluginConfig.infoThreshold
        val warn: Int = GlobalConfig.pluginConfig.warnThreshold
        val error: Int = GlobalConfig.pluginConfig.errorThreshold
        mv.visitLdcInsn(onlyCheckMain)
        mv.visitLdcInsn(info)
        mv.visitLdcInsn(warn)
        mv.visitLdcInsn(error)
        mv.visitMethodInsn(
            Opcodes.INVOKESTATIC,
            className,
            "applyConfigInner",
            APPLY_CONFIG_METHOD_DESCRIPTOR,
            false
        )
    }


}
