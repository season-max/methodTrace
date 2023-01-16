package com.zhangyue.ireader.traceProcess.handle

interface IMethodTraceHandle {

    fun onMethodEnter()

    fun onMethodExit(str:String)

}