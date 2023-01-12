package com.zhangyue.ireader.traceMethod

/**
 * 插件配置项
 * 不能是 final 类，所以添加 open
 */
open class TraceConfig {

    @JvmField
    var printlnLog: Boolean = false

    @JvmField
    var pkgList: List<String> = ArrayList()
    override fun toString(): String {
        return "TraceConfig(printlnLog=$printlnLog, pkgList=$pkgList)"
    }


}