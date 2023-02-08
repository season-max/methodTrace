package com.zhangyue.ireader.traceMethod.printer

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.serializer.SerializerFeature
import com.zhangyue.ireader.traceMethod.utils.Logger
import org.gradle.api.Project
import java.io.File
import java.nio.charset.Charset

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

    @Synchronized
    fun addItem(item: TraceMethodBean) {
        items.add(item)
    }


    private fun toJsonBytes(): ByteArray {
        if (items.isEmpty()) {
            return "{}".toByteArray()
        }
        val map = mutableMapOf<String, List<String>>()
        for (item in items) {
            val list = ArrayList<String>()
            for (info in item.methodList) {
                val name = info.methodName
                val args = info.args
                val returns = info.returns
                list.add(assembleMethod(name, args, returns))
            }
            map[item.className] = list
        }
        return try {
            JSON.toJSONString(
                map,
                //输出值为 null 的字段
                SerializerFeature.WriteMapNullValue,
                //格式化
                SerializerFeature.PrettyFormat
            ).toByteArray(Charset.defaultCharset())
        } catch (e: Exception) {
            Logger.error(e.message)
            "{}".toByteArray()
        }
    }

    /**
     * 拼接成
     * name[args] --> returns
     */
    private fun assembleMethod(name: String, args: String, returns: String): String {
        val builder = StringBuilder()
        builder.append(name)
        builder.append(args)
        builder.append(" ---> ")
        builder.append(returns)
        return builder.toString()
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