package com.zhangyue.ireader.traceProcess

var onlyCheckMainThread1: Boolean = true

var errorConstThreshold1: Int = Integer.MAX_VALUE

var warnConstThreshold1 = Integer.MAX_VALUE

var infoConstThreshold1 = Integer.MAX_VALUE

/**
 * 插件中通过 ASM 调用，应用插件的配置
 */
fun applyConfig() {

}

/**
 * 插件中通过 ASM 调用，应用插件的配置
 */
fun applyConfigInner(
    onlyCheckMainThread: Boolean,
    infoConstThreshold: Int,
    warnConstThreshold: Int,
    errorConstThreshold: Int,
) {
    onlyCheckMainThread1 = onlyCheckMainThread
    infoConstThreshold1 = infoConstThreshold
    warnConstThreshold1 = warnConstThreshold
    errorConstThreshold1 = errorConstThreshold
}