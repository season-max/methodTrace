package com.zhangyue.ireader.traceMethod

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.zhangyue.ireader.traceMethod.transform.TraceTransform
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
                registerTransform(TraceTransform(project))
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
        GlobalConfig.enableMethodTrace = config.pkgList.isNotEmpty()
        println("plugin config:$config,enableMethodTrace:${GlobalConfig.enableMethodTrace}")
        Logger.make(project, config)
    }
}