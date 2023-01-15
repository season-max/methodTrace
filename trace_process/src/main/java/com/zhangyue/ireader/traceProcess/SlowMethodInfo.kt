package com.zhangyue.ireader.traceProcess

class SlowMethodInfo {
    var pkgName: String? = null

    var className: String? = null

    var methodName: String? = null

    var costTimeMs: Long = 0

    var time: Long = 0

    var callStack: String? = null

    var threadName: String? = null

    /**
     * 格式化输出
     */
    fun printlnLog(): String {
        return StringBuilder().apply {
            append("pkgName : $pkgName")
                .append("\r\n")
                .append("className : $className")
                .append("\r\n")
                .append("methodName : $methodName")
                .append("\r\n")
                .append("costTimeMs : $costTimeMs")
                .append("\r\n")
                .append("threadName : $threadName")
                .append("\r\n")
                .append("time : $time")
                .append("\r\n")
                .append("callStack : $callStack")
        }.let {
            it.toString()
        }
    }


}