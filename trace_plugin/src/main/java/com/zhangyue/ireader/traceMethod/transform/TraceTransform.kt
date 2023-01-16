package com.zhangyue.ireader.traceMethod.transform

import com.android.build.api.transform.TransformInvocation
import com.zhangyue.ireader.traceMethod.GlobalConfig
import com.zhangyue.ireader.traceMethod.visitor.TraceClassVisitor
import org.gradle.api.Project
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes


class TraceTransform(project: Project) : BaseTransform(project) {

    private var startTime: Long = 0

    override fun needTransform() =
        true

    override fun onTransformStart(transformInvocation: TransformInvocation) {
        println("$name start--------------->")
        startTime = System.currentTimeMillis()
    }

    override fun onTransformEnd(transformInvocation: TransformInvocation) {
        println("$name end---------------> duration : ${System.currentTimeMillis() - startTime}")
    }


    override fun transformClassInner(name: String, sourceBytes: ByteArray): ByteArray? {
        val transforms =
            listOf(ApplyConfigTransform(), MethodTraceTransform(), CustomHandleTransform())
        return transforms.fold(sourceBytes) { a, b ->
            b.onTransform(name, a)
        }
    }

    companion object {
        const val ASM_API = Opcodes.ASM9

        /**
         * 耗时测试工具类包名
         */
        const val HANDLE_METHOD_CONST_PACKAGE = "com.zhangyue.ireader.traceProcess"

        /**
         * 将插件配置通过插桩应用到代码中
         */
        const val APPLY_CONFIG_CLASS_NAME = "com/zhangyue/ireader/traceProcess/MethodTraceConfigKt"
        const val APPLY_CONFIG_METHOD_NAME = "applyConfig"
        const val APPLY_CONFIG_FIELD_ONLY_CHECK_MAIN = "onlyCheckMainThread1"
        const val APPLY_CONFIG_FIELD_INFO_THRESHOLD = "infoConstThreshold1"
        const val APPLY_CONFIG_FIELD_WARN_THRESHOLD = "warnConstThreshold1"
        const val APPLY_CONFIG_FIELD_ERROR_THRESHOLD = "errorConstThreshold1"

        const val METHOD_TRACE_CLASS_NAME = "com.zhangyue.ireader.traceProcess.MethodTrace"
    }

}