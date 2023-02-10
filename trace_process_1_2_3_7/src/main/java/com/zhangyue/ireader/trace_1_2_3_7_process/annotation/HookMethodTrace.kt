package com.zhangyue.ireader.trace_1_2_3_7_process.annotation

/**
 * 执行插桩的注解
 * 小于 [IgnoreMethodTrace] 的优先级
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
annotation class HookMethodTrace
