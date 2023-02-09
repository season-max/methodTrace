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
    var pkgList: List<String>? = ArrayList()

    /**
     * 是否只检测主线程
     */
    @JvmField
    var checkOnlyMainThread = true

    /**
     * log error 阈值
     */
    @JvmField
    var errorThreshold: Int? = null

    /**
     * log warn 阈值
     */
    @JvmField
    var warnThreshold: Int? = null

    /**
     * log info 阈值
     */
    @JvmField
    var infoThreshold: Int? = null

    /**
     * 自定义耗时处理类名称
     */
    @JvmField
    var customHandle: String? = null

    /**
     * 是否打印调用堆栈
     * 打印堆栈会导致日志过长，可以选择不打印
     *
     */
    @JvmField
    var printCallStack: Boolean = false

    /**
     * 白名单，匹配白名单的类不执行插桩
     */
    @JvmField
    var whiteList: List<String>? = ArrayList()
    override fun toString(): String {
        return "TraceConfig(printlnLog=$printlnLog, pkgList=$pkgList, checkOnlyMainThread=$checkOnlyMainThread, errorThreshold=$errorThreshold, warnThreshold=$warnThreshold, infoThreshold=$infoThreshold, customHandle=$customHandle, printCallStack=$printCallStack,whiteList=$whiteList)"
    }


}