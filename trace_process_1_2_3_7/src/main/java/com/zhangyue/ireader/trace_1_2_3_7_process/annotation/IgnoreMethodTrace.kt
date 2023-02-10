package com.zhangyue.ireader.trace_1_2_3_7_process.annotation

/**
 * 忽略插桩的注解
 * 大于 [HookMethodTrace] 的优先级
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class IgnoreMethodTrace
