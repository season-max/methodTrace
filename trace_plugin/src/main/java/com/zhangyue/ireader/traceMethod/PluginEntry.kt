package com.zhangyue.ireader.traceMethod

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.zhangyue.ireader.traceMethod.GlobalConfig.checkPluginSet
import com.zhangyue.ireader.traceMethod.transform.MethodTraceFirstTranceTransform
import com.zhangyue.ireader.traceMethod.utils.Logger
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * 插件入口
 */
class PluginEntry : Plugin<Project> {

    override fun apply(project: Project) {
        println("apply project ${project.name}")
        val isAndroid: Boolean = project.plugins.hasPlugin(AppPlugin::class.java)
        val config = project.extensions.create("trace_config", TraceConfig::class.java)
        if (isAndroid) {
            project.extensions.getByType(AppExtension::class.java).apply {
                registerTransform(MethodTraceFirstTranceTransform(project))
            }
            project.afterEvaluate {
                applyProjectConfig(project, config)
            }
        } else {
            println("project ${project.name} is not a android project~")
        }
    }

    private fun applyProjectConfig(project: Project, config: TraceConfig) {
        GlobalConfig.pluginConfig = config
        //检查插件中参数的设置
        checkPluginSet(project)
        println("plugin config -> $config")
        Logger.make(project, config)
    }
}