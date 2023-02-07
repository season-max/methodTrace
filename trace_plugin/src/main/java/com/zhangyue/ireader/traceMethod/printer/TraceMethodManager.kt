package com.zhangyue.ireader.traceMethod.printer

import org.gradle.api.Project
import java.io.File

class TraceMethodManager private constructor() {

    private val items: MutableList<TraceMethodBean> = mutableListOf()

    companion object {
        @JvmStatic
        fun get(): TraceMethodManager {
            return Holder.inner
        }
    }

    private object Holder {
        val inner = TraceMethodManager()
    }

    fun addItem(item: TraceMethodBean) {
        items.add(item)
    }

    /**
     * @param project 构建的项目
     */
    fun print(project: Project) {
        val dir = project.buildDir
        if (!dir.exists()) {
            dir.mkdirs()
        }
        val file = File(project.buildDir.absolutePath, "methodTrace.json")



    }


}