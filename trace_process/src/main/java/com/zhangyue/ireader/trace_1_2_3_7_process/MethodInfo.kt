package com.zhangyue.ireader.trace_1_2_3_7_process

class MethodInfo {
    var pid: String? = null

    var pkgName: String? = null

    var className: String? = null

    var methodName: String? = null

    var costTimeMs: Long = 0

    var timestamp: String? = null

    var callStack: String? = null

    var threadName: String? = null


    private fun reset() {
        pid = null
        pkgName = null
        className = null
        methodName = null
        costTimeMs = 0
        timestamp = null
        callStack = null
        threadName = null
    }

    /**
     * 格式化输出
     */
    fun printlnLog(): String {
        return StringBuilder().apply {
            append("[pkgName] : $pkgName")
                .append("\r\n")
                .append("[className] : $className")
                .append("\r\n")
                .append("[methodName] : $methodName")
                .append("\r\n")
                .append("[costTime] : $costTimeMs ms")
                .append("\r\n")
                .append("[threadName] : $threadName")
                .append("\r\n")
                .append("[timestamp] : $timestamp")
                .append("\r\n")
                .append("[callStack] : ")
                .append("\r\n")
                .append("$callStack")
        }.toString()
    }




}