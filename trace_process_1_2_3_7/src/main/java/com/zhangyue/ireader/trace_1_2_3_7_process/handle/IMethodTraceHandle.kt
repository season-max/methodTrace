package com.zhangyue.ireader.trace_1_2_3_7_process.handle

/**
 * 方法处理接口
 * 需要注意的是，对多线程进行处理
 * 例如，对于方法 A()，线程 thread_1 和线程 thread_2
 * thread_1 先进入 A，thread_2 后进入 A，执行 A 的逻辑之后，可能是 thread_2 先达到方法出口，
 * 导致计算的时间差是 (thread_2 的出口时间 - thread_1 的入口时间)，从而导致计算错误
 * @author yaoxinxin
 */
interface IMethodTraceHandle {

    /**
     * 方法入口
     * className + methodName + args + returnType 作为一个方法的 key
     *
     */
    fun onMethodEnter(
        any: Any,
        classNameFullName: String,
        methodName: String,
        args: String,
        returnType: String
    )

    /**
     * 方法出口
     * className + methodName + args + returnType 作为一个方法的 key
     */
    fun onMethodExit(
        any: Any,
        classNameFullName: String,
        methodName: String,
        args: String,
        returnType: String
    )

    /**
     * 入口校验
     */
    fun checkMathStart(): Boolean


    /**
     * 出口校验
     */
    fun checkMatchExit(): Boolean

}