package com.zhangyue.ireader.trace_1_2_3_7_process.handle

interface IMethodTraceHandle {

    fun onMethodEnter()

    fun onMethodExit(str:String)

}