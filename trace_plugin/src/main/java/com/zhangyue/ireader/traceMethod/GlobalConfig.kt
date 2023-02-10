package com.zhangyue.ireader.traceMethod

import com.zhangyue.ireader.traceMethod.TraceConfig.Companion.SCOPE_ALL
import com.zhangyue.ireader.traceMethod.TraceConfig.Companion.SCOPE_JAR
import com.zhangyue.ireader.traceMethod.TraceConfig.Companion.SCOPE_PROJECT
import com.zhangyue.ireader.traceMethod.utils.Logger
import org.gradle.api.Project

object GlobalConfig {
    @JvmField
    var pluginConfig: TraceConfig = TraceConfig()

    /**
     * 是否在指定的范围执行全插桩
     */
    var injectAllInScope = false

    /**
     * 设置上限 50s
     */
    private const val THRESHOLD_UPPER_LIMIT = Int.MAX_VALUE

    fun setPluginConfig(config: TraceConfig) {
        pluginConfig = config
        injectAllInScope = config.pkgList?.isEmpty() ?: true
        if (injectAllInScope) {
            Logger.info(
                "injectAll in ${
                    when (config.injectScope) {
                        SCOPE_PROJECT -> "SCOPE_PROJECT"
                        SCOPE_JAR -> "SCOPE_JAR"
                        SCOPE_ALL -> "SCOPE_ALL"
                        else -> {
                            ""
                        }
                    }
                }"
            )
        }
    }

    /**
     * return if arg <= 0
     */
    private fun lessThanZero(vararg args: Int?): Boolean {
        for (i in args) {
            if (i != null) {
                if (i <= 0) {
                    return true
                }
            }
        }
        return false
    }

    /**
     * 设置的阈值是否大于 [THRESHOLD_UPPER_LIMIT]
     */
    private fun moreThanUpperLimit(limit: Int, vararg args: Int?): Boolean {
        for (i in args) {
            if (i != null) {
                if (i > limit) {
                    return true
                }
            }
        }
        return false
    }

    /**
     * 判断是否都为 null
     */
    private fun isAllNull(vararg args: Int?): Boolean {
        var allNull = true
        for (arg in args) {
            if (arg != null) {
                allNull = false
                break
            }
        }
        return allNull
    }

    /**
     * 比较两个 Int?
     * 如果 this >= that，return true
     */
    private fun Int?.jge(that: Int?): Boolean {
        return if (this == null || that == null) {
            false
        } else {
            this >= that
        }
    }

    /**
     * 检查设定的阈值是否正确
     */
    fun checkPluginSet(project: Project) {
        val i = pluginConfig.infoThreshold
        val w = pluginConfig.warnThreshold
        val e = pluginConfig.errorThreshold
        val s = pluginConfig.injectScope
        if (isAllNull(i, w, e)) {
            throw RuntimeException("项目 ${project.name} 中需要设置任意一个耗时阈值")
        }
        if (lessThanZero(i, w, e)) {
            throw RuntimeException("项目 ${project.name} 中方法耗时监测插件的设置阈值需要大于 0")
        }
        if (moreThanUpperLimit(THRESHOLD_UPPER_LIMIT, i, w, e)) {
            throw RuntimeException("项目 ${project.name} 中方法耗时监测插件的设置阈值不能大于 $THRESHOLD_UPPER_LIMIT")
        }
        if (i.jge(w) || i.jge(e) || w.jge(e)) {
            throw RuntimeException("项目 ${project.name} 中方法耗时监测插件的设置阈值请保证 info < warn < error")
        }
        if (s < SCOPE_PROJECT || s > SCOPE_ALL) {
            throw RuntimeException("项目 ${project.name} 中设置的插桩范围为 $SCOPE_PROJECT-$SCOPE_ALL")
        }
    }
}