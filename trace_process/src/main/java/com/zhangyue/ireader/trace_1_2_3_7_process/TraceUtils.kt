package com.zhangyue.ireader.trace_1_2_3_7_process

import java.text.SimpleDateFormat
import java.util.*

/**
 * 工具类
 * @author yaoxinxin
 */
object TraceUtils {

    /**
     * 日期转换
     */
    fun getDateUntilMills(time: Long): String {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.CHINA)
        return format.format(time)
    }

    /**
     * 获取调用堆栈
     */
    fun getThreadStackTrace(thread: Thread): String {
        val stackArray = thread.stackTrace
        if (stackArray.isEmpty()) {
            return "[]"
        }
        //跳过插件代码的堆栈
        val skipFrameCount = 7
        //最多记录的堆栈
        val maxLineNumber = 15
        val stringBuilder = StringBuilder()
        for (i in stackArray.indices) {
            if (i < skipFrameCount) {
                continue
            }
            stringBuilder.append(stackArray[i])
            stringBuilder.append("\r\n")
            if (i > maxLineNumber) {
                break
            }
        }
        return stringBuilder.toString()
    }


}