package com.zhangyue.ireader.traceMethod

import org.gradle.api.Project
import java.lang.RuntimeException

object GlobalConfig {
    var pluginConfig: TraceConfig = TraceConfig()

    var enableMethodTrace = false


    private fun lessThanZero(vararg args: Int): Boolean {
        for (i in args) {
            if (i < 0) {
                return true
            }
        }
        return false
    }

    private fun moreThanIntMax(vararg args: Int): Boolean {
        for (i in args) {
            if (i > Int.MAX_VALUE) {
                return true
            }
        }
        return false
    }

    /**
     * 检查设定的阈值是否正确
     */
    fun checkPluginSet(project: Project) {
        val i = pluginConfig.infoThreshold
        val w = pluginConfig.warnThreshold
        val e = pluginConfig.errorThreshold
        if (lessThanZero(i, w, e)) {
            throw RuntimeException("项目 ${project.name} 中方法耗时监测插件的设置阈值不能小于 0")
        }
        if (moreThanIntMax(i, w, e)) {
            throw RuntimeException("项目 ${project.name} 中方法耗时监测插件的设置阈值不能大于 Int.MAX_VALUE")
        }
        if (i > w || i > e || w > e) {
            throw RuntimeException("项目 ${project.name} 中方法耗时监测插件的设置阈值请保证 info < warn < error")
        }
    }
}