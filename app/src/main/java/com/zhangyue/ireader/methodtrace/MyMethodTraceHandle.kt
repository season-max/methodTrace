package com.zhangyue.ireader.methodtrace

import android.util.Log
import androidx.annotation.Keep
import com.zhangyue.ireader.trace_1_2_3_7_process.annotation.IgnoreMethodTrace
import com.zhangyue.ireader.trace_1_2_3_7_process.handle.IMethodTraceHandle

/**
 * @author yaoxinixn
 * 对方法入口、出口的逻辑自定义处理
 *
 * 需要添加 [IgnoreMethodTrace] 注解
 */
@Keep
@IgnoreMethodTrace
class MyMethodTraceHandle : IMethodTraceHandle {
    override fun onMethodEnter(
        any: Any,
        className: String,
        methodName: String,
        args: String,
        returnType: String
    ) {
        Log.e("method_trace_handle", "-------> onMethodEnter")
    }

    override fun onMethodExit(
        any: Any,
        className: String,
        methodName: String,
        args: String,
        returnType: String
    ) {
        Log.e("method_trace_handle", "-------> onMethodExit")
    }
}