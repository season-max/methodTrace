package com.zhangyue.ireader.traceMethod

/**
 * 插件配置项
 * 不能是 final 类，所以添加 open
 */
open class TraceConfig {

    /**
     * 是否打印日志
     */
    @JvmField
    var printlnLog: Boolean = false

    /**
     * 插桩范围
     */
    @JvmField
    var pkgList: List<String> = ArrayList()

    /**
     * 是否只检测主线程
     */
    @JvmField
    var checkOnlyMainThread = true

    /**
     * log error 阈值
     */
    @JvmField
    var errorThreshold: Int = 0

    /**
     * log warn 阈值
     */
    @JvmField
    var warnThreshold: Int = 0

    /**
     * log info 阈值
     */
    @JvmField
    var infoThreshold: Int = 0
    override fun toString(): String {
        return "TraceConfig(printlnLog=$printlnLog, pkgList=$pkgList, checkOnlyMainThread=$checkOnlyMainThread, errorThreshold=$errorThreshold, warnThreshold=$warnThreshold, infoThreshold=$infoThreshold)"
    }


}