package com.zhangyue.ireader.traceMethod

object GlobalConfig {
    var pluginConfig: TraceConfig = TraceConfig()

    var enableMethodTrace = false

    /**
     *  过滤白名单
     */
    val filterList = listOf(
        "kotlin."
    )
}