package com.zhangyue.ireader.traceMethod.printer

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.serializer.SerializerFeature
import com.zhangyue.ireader.traceMethod.utils.Logger
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


    private fun toJsonBytes(): ByteArray {
        if (items.isEmpty()) {
            return "[]".toByteArray()
        }
        val map = mutableMapOf<String, List<MethodInfo>>()
        for (item in items) {
            map[item.className] = item.methodList
        }
        return JSON.toJSONBytes(
            map,
            //输出值为 null 的字段
            SerializerFeature.WriteMapNullValue,
            //格式化
            SerializerFeature.PrettyFormat
        )
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
        file.createNewFile()
        file.writeBytes(toJsonBytes())
        Logger.info("flush to ${file.absoluteFile} finish~")
    }
}