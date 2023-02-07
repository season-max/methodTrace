package com.zhangyue.ireader.traceMethod.transform

import com.android.build.api.transform.TransformInvocation
import com.zhangyue.ireader.traceMethod.printer.TraceMethodManager
import org.gradle.api.Project
import org.objectweb.asm.Opcodes


class FirstTraceTransform(project: Project) : BaseTransform(project) {

    private var startTime: Long = 0

    override fun needTransform() =
        true

    override fun onTransformStart(transformInvocation: TransformInvocation) {
        println("$name start--------------->")
        startTime = System.currentTimeMillis()
    }

    override fun onTransformEnd(transformInvocation: TransformInvocation) {
        println("$name end---------------> duration : ${System.currentTimeMillis() - startTime}")
        TraceMethodManager.get().print(project)
    }


    override fun onTransform(className: String, bytes: ByteArray): ByteArray {
        val transforms =
            listOf(
                CustomHandleTransform(),
                ApplyConfigTransform(),
                MethodTraceTransform()
            )
        return transforms.fold(bytes) { b, t ->
            t.onTransform(className, b)
        }
    }

    companion object {
        const val ASM_API = Opcodes.ASM7

        const val DOT = "."

        const val SEPARATOR = "/"

        const val COMMA = ","

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
        val METHOD_TRACE_CLASS_DESCRIPTOR = "L$TRACE_METHOD_PROCESS_PACKAGE.MethodTrace;".replace(
            DOT, SEPARATOR
        )
        //字段名称
        const val FILED_NAME = "METHOD_TRACE_HANDLE"
        //方法入口
        const val METHOD_TRACE_ENTER_NAME = "onMethodEnter"
        const val METHOD_TRACE_ENTER_DESCRIPTOR = "(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V"
        //方法出口
        const val METHOD_TRACE_EXIT_NAME = "onMethodExit"
        const val METHOD_TRACE_EXIT_DESCRIPTOR = METHOD_TRACE_ENTER_DESCRIPTOR

        /**
         * 方法耗时处理接口
         */
        const val INTERFACE_METHOD_TRACE_HANDLE =
            "$TRACE_METHOD_PROCESS_PACKAGE.handle.IMethodTraceHandle"
        val INTERFACE_METHOD_TRACE_HANDLE_DESCRIPTOR = "L$INTERFACE_METHOD_TRACE_HANDLE;".replace(
            DOT, SEPARATOR
        )

        /**
         * 忽略插桩注解
         */
        val IGNORE_ANNOTATION_DESCRIPTOR =
            "L$TRACE_METHOD_PROCESS_PACKAGE.annotation.IgnoreMethodTrace;".replace(DOT, SEPARATOR)

        val EXECUTOR_ANNOTATION_DESCRIPTOR =
            "L$TRACE_METHOD_PROCESS_PACKAGE.annotation.HookMethodTrace;".replace(DOT, SEPARATOR)
    }

}