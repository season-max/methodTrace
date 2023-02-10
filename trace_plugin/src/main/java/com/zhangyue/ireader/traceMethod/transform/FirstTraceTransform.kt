package com.zhangyue.ireader.traceMethod.transform

import com.android.build.api.transform.TransformInvocation
import com.zhangyue.ireader.traceMethod.GlobalConfig
import com.zhangyue.ireader.traceMethod.TraceConfig
import com.zhangyue.ireader.traceMethod.TraceConfig.Companion.SCOPE_JAR
import com.zhangyue.ireader.traceMethod.TraceConfig.Companion.SCOPE_PROJECT
import com.zhangyue.ireader.traceMethod.printer.TraceMethodManager
import com.zhangyue.ireader.traceMethod.utils.Logger
import com.zhangyue.ireader.traceMethod.utils.itemMatchAction
import org.gradle.api.Project
import org.objectweb.asm.Opcodes
import java.io.File


class FirstTraceTransform(project: Project, config: TraceConfig) : BaseTransform(project) {

    private val config: TraceConfig

    init {
        this.config = config
    }

    private var startTime: Long = 0

    override fun needTransformClass(className: String) =
        true

    override fun needTransformJar(jarFile: File): Boolean {
        return (config.injectScope and SCOPE_JAR == SCOPE_JAR || isPluginJar(jarFile)).also {
            if (it) {
                Logger.info("transformJar ${jarFile.absolutePath}")
            }
        }
    }

    override fun needTransformDirectory(file: File): Boolean {
        return (config.injectScope and SCOPE_PROJECT == SCOPE_PROJECT).also {
            if (it) {
                Logger.info("transformDirectory ${file.absolutePath}")
            }
        }
    }

    /**
     * 处理插件内部 jar 包，即使外部设置不插桩 jar 包也需要处理
     */
    private fun isPluginJar(jarFile: File): Boolean {
        return jarFile.absolutePath.contains(PROCESS_MODULE_NAME)
    }

    override fun onTransformStart(transformInvocation: TransformInvocation) {
        println("$name start--------------->")
        startTime = System.currentTimeMillis()
    }

    override fun onTransformEnd(transformInvocation: TransformInvocation) {
        println("$name end---------------> duration : ${System.currentTimeMillis() - startTime}")
        TraceMethodManager.get().print(project)
    }


    override fun onTransform(className: String, bytes: ByteArray): ByteArray {
        return if (matchWhiteList(className)) {
            bytes
        } else {
            Logger.info("start transform class $className")
            listOf(
                CustomHandleTransform(),
                ApplyConfigTransform(),
                MethodTraceTransform()
            ).fold(bytes) { b, t ->
                t.onTransform(className, b)
            }
        }
    }

    /**
     * 是非匹配白名单
     * @param className class name ，aaa/bbb/ccc
     * @return true-匹配，不进行 transform
     */
    private fun matchWhiteList(className: String): Boolean {
        return GlobalConfig.pluginConfig.whiteList?.itemMatchAction(
            className.replace(
                SEPARATOR,
                DOT
            )
        ) { init, it ->
            init.startsWith(it)
        } ?: false
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
        const val APPLY_CONFIG_FIELD_PRINT_CALLSTACK = "printCallStack"

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
        const val METHOD_TRACE_ENTER_DESCRIPTOR =
            "(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V"

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

        /**
         * 处理逻辑的 module 名称
         */
        const val PROCESS_MODULE_NAME = "trace_process_1_2_3_7"
    }

}