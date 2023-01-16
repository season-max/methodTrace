package com.zhangyue.ireader.methodtrace

import android.util.Log
import androidx.annotation.Keep
import com.zhangyue.ireader.traceProcess.handle.IMethodTraceHandle

/**
 * 自定义日志输出
 */
@Keep
class MyMethodTraceHandle : IMethodTraceHandle {
    override fun onMethodEnter() {
        Log.e("method_trace_handle", "onMethodEnter")
    }

    override fun onMethodExit(str: String) {
        Log.e("method_trace_handle", "onMethodExit --> $str")
    }
}