package com.zhangyue.ireader.hook_plugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.zhangyue.ireader.hook_plugin.transform.TraceTransform
import com.zhangyue.ireader.hook_plugin.utils.Logger
import org.gradle.api.Plugin
import org.gradle.api.Project


class PluginEntry : Plugin<Project> {

    override fun apply(project: Project) {
        println("apply project ${project.name}")
        val isApp: Boolean = project.plugins.hasPlugin(AppPlugin::class.java)
        if (isApp) {
            project.extensions.create("trace_config", TraceConfig::class.java)
            val appExtension = project.extensions.getByType(AppExtension::class.java)
            appExtension.registerTransform(TraceTransform(project))
            project.afterEvaluate {
                applyProjectConfig(project)
            }
        } else {
            println("project ${project.name} is not a android project~")
        }
    }

    fun applyProjectConfig(project: Project){
        Logger.make(project)
    }
}