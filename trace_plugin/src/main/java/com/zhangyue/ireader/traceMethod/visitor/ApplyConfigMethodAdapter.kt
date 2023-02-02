package com.zhangyue.ireader.traceMethod.visitor

import com.zhangyue.ireader.traceMethod.GlobalConfig
import com.zhangyue.ireader.traceMethod.transform.MethodTraceFirstTranceTransform.Companion.APPLY_CONFIG_CLASS_NAME
import com.zhangyue.ireader.traceMethod.transform.MethodTraceFirstTranceTransform.Companion.APPLY_CONFIG_FIELD_ERROR_THRESHOLD
import com.zhangyue.ireader.traceMethod.transform.MethodTraceFirstTranceTransform.Companion.APPLY_CONFIG_FIELD_INFO_THRESHOLD
import com.zhangyue.ireader.traceMethod.transform.MethodTraceFirstTranceTransform.Companion.APPLY_CONFIG_FIELD_ONLY_CHECK_MAIN
import com.zhangyue.ireader.traceMethod.transform.MethodTraceFirstTranceTransform.Companion.APPLY_CONFIG_FIELD_WARN_THRESHOLD
import com.zhangyue.ireader.traceMethod.transform.MethodTraceFirstTranceTransform.Companion.DOT
import com.zhangyue.ireader.traceMethod.transform.MethodTraceFirstTranceTransform.Companion.SEPARATOR
import com.zhangyue.ireader.traceMethod.utils.Logger
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.commons.AdviceAdapter

class ApplyConfigMethodAdapter(
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

    private fun Int?.ifNull(): Int {
        return this ?: Int.MAX_VALUE
    }


    override fun onMethodExit(opcode: Int) {
        super.onMethodExit(opcode)
        Logger.info("-----> $name exit")
        val onlyCheckMain: Boolean = GlobalConfig.pluginConfig.checkOnlyMainThread
        val info: Int = GlobalConfig.pluginConfig.infoThreshold.ifNull()
        val warn: Int = GlobalConfig.pluginConfig.warnThreshold.ifNull()
        val error: Int = GlobalConfig.pluginConfig.errorThreshold.ifNull()
        val owner = APPLY_CONFIG_CLASS_NAME.replace(DOT, SEPARATOR)
        // load onlyCheckMain
        mv.visitLdcInsn(onlyCheckMain)
        mv.visitVarInsn(Opcodes.ISTORE, 0)
        // load info
        mv.visitLdcInsn(info)
        mv.visitVarInsn(Opcodes.ISTORE, 1)
        // load warn
        mv.visitLdcInsn(warn)
        mv.visitVarInsn(Opcodes.ISTORE, 2)
        // load error
        mv.visitLdcInsn(error)
        mv.visitVarInsn(Opcodes.ISTORE, 3)
        // put field
        mv.visitVarInsn(Opcodes.ILOAD, 0)
        mv.visitFieldInsn(Opcodes.PUTSTATIC, owner, APPLY_CONFIG_FIELD_ONLY_CHECK_MAIN, "Z")
        // put field
        mv.visitVarInsn(Opcodes.ILOAD, 1)
        mv.visitFieldInsn(Opcodes.PUTSTATIC, owner, APPLY_CONFIG_FIELD_INFO_THRESHOLD, "I")
        // put field
        mv.visitVarInsn(Opcodes.ILOAD, 2)
        mv.visitFieldInsn(Opcodes.PUTSTATIC, owner, APPLY_CONFIG_FIELD_WARN_THRESHOLD, "I")
        // put field
        mv.visitVarInsn(Opcodes.ILOAD, 3)
        mv.visitFieldInsn(Opcodes.PUTSTATIC, owner, APPLY_CONFIG_FIELD_ERROR_THRESHOLD, "I")
    }


}
