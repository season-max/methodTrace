package com.zhangyue.ireader.traceMethod.transform

import com.android.build.api.transform.TransformInvocation
import org.gradle.api.Project
import org.objectweb.asm.Opcodes


class MethodTraceFirstTranceTransform(project: Project) : BaseTransform(project) {

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


    override fun transformClassInner(name: String, sourceBytes: ByteArray): ByteArray {
        val transforms =
            listOf(CustomHandleTransform(), ApplyConfigTransform(), MethodTraceTransform())
        return transforms.fold(sourceBytes) { a, b ->
            b.onTransform(name, a)
        }
    }

    companion object {
        const val ASM_API = Opcodes.ASM9

        const val DOT = "."

        const val SEPARATOR = "/"

        /**
         * 方法耗时处理类所在的包名，该包名下的类都不进行插桩
         */
        const val TRACE_METHOD_PROCESS_PACKAGE = "com.zhangyue.ireader.trace_1_2_3_7_process"

        /**
         * 将插件配置通过插桩应用到代码中
         */
        const val APPLY_CONFIG_CLASS_NAME =
            "$TRACE_METHOD_PROCESS_PACKAGE.MethodTraceConfigKt"
        const val APPLY_CONFIG_METHOD_NAME = "applyConfig"
        const val APPLY_CONFIG_FIELD_ONLY_CHECK_MAIN = "onlyCheckMainThread1"
        const val APPLY_CONFIG_FIELD_INFO_THRESHOLD = "infoConstThreshold1"
        const val APPLY_CONFIG_FIELD_WARN_THRESHOLD = "warnConstThreshold1"
        const val APPLY_CONFIG_FIELD_ERROR_THRESHOLD = "errorConstThreshold1"

        /**
         * 耗时处理类
         */
        const val METHOD_TRACE_CLASS_NAME = "$TRACE_METHOD_PROCESS_PACKAGE.MethodTrace"
        const val METHOD_TRACE_ENTER = "onMethodEnter"
        const val METHOD_TRACE_EXIT = "onMethodExit"

        /**
         * 类全限定名、方法名 分割符
         */
        const val METHOD_TRACE_PARTITION = "$"

        /**
         * 方法耗时处理接口
         */
        const val INTERFACE_METHOD_TRACE_HANDLE =
            "$TRACE_METHOD_PROCESS_PACKAGE.handle.IMethodTraceHandle"

        /**
         * 忽略插桩注解
         */
        const val IGNORE_ANNOTATION_NAME =
            "$TRACE_METHOD_PROCESS_PACKAGE.annotation.IgnoreMethodTrace"
    }

}