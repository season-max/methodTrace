package com.zhangyue.ireader.traceMethod.printer

data class TraceMethodBean(
    val className: String,
    var methodList: MutableList<MethodInfo> = ArrayList()
)

data class MethodInfo(val methodName: String, val args: String, val returns: String)