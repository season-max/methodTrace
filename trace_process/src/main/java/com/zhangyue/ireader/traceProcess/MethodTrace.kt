package com.zhangyue.ireader.traceProcess

import android.util.Log
import com.zhangyue.ireader.traceProcess.handle.IMethodTraceHandle
import com.zhangyue.ireader.traceProcess.handle.MethodTraceHandle
import com.zhangyue.ireader.traceProcess.handle.MethodTraceHandle.Companion.TAG

class MethodTrace {

    companion object {
        init {
            applyConfig()
            Log.i(
                TAG,
                "plugin config --> [onlyCheckMainThread1:$onlyCheckMainThread1 ," +
                        "infoConstThreshold1:$infoConstThreshold1," +
                        "warnConstThreshold1:$warnConstThreshold1," +
                        "errorConstThreshold1:$errorConstThreshold1]"
            )
        }

        /**
         * 当插件设置自定义处理类时，进行替换
         */
        @JvmStatic
        private val METHOD_TRACE_HANDLE: IMethodTraceHandle = MethodTraceHandle()

        /**
         * 方法入口
         */
        @JvmStatic
        fun onMethodEnter() {
            METHOD_TRACE_HANDLE.onMethodEnter()
        }


        /**
         * 方法出口
         */
        @JvmStatic
        fun onMethodExit(str: String) {
            METHOD_TRACE_HANDLE.onMethodExit(str)
        }
    }
}