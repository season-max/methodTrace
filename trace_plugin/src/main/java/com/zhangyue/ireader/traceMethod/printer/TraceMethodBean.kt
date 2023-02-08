package com.zhangyue.ireader.traceMethod.printer

import com.alibaba.fastjson.annotation.JSONField

data class TraceMethodBean(
    val className: String,
    var methodList: MutableList<MethodInfo> = ArrayList()
)

data class MethodInfo(
    @JSONField(ordinal = 0) val methodName: String,
    @JSONField(ordinal = 1) val args: String,
    @JSONField(ordinal = 2) val returns: String
)