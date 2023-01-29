package com.zhangyue.ireader.methodtrace

import android.util.Log
import androidx.annotation.Keep
import com.zhangyue.ireader.trace_1_2_3_7_process.annotation.IgnoreMethodTrace
import com.zhangyue.ireader.trace_1_2_3_7_process.handle.IMethodTraceHandle

/**
 * 自定义日志输出
 * 需要添加 [IgnoreMethodTrace] 注解
 */
@Keep
@IgnoreMethodTrace
class MyMethodTraceHandle : IMethodTraceHandle {
    override fun onMethodEnter() {
        Log.e("method_trace_handle", "onMethodEnter")
    }

    override fun onMethodExit(str: String) {
        Log.e("method_trace_handle", "onMethodExit --> $str")
    }
}