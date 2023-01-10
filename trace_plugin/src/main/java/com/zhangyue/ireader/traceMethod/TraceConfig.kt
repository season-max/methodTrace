package com.zhangyue.ireader.traceMethod

/**
 * 插件配置项
 * 不能是 final 类，所以添加 open
 */
open class TraceConfig {

    @JvmField
    var debug: Boolean = false

    @JvmField
    var hookJar: Boolean = false
    override fun toString(): String {
        return "TraceConfig(debug=$debug, hookJar=$hookJar)"
    }
}