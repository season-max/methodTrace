package com.zhangyue.ireader.traceMethod

object GlobalConfig {
    var pluginConfig: TraceConfig = TraceConfig()

    val enableMethodTrace = pluginConfig.pkgList.isNotEmpty()
}