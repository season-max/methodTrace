package com.zhangyue.ireader.traceProcess

import android.os.Looper
import android.util.Log

class MethodTrace {

    companion object {
        const val TAG = "methodTrace"

        private var startTime: Long = 0L

        private const val METHOD_TRACE_PARTITION = "$"

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
         * 方法入口
         */
        @JvmStatic
        fun onMethodEnter() {
            startTime = System.currentTimeMillis()
        }


        /**
         * 方法出口
         */
        @JvmStatic
        fun onMethodExit(name: String) {
            methodConst(name, (System.currentTimeMillis() - startTime))
        }

        private fun methodConst(name: String, const: Long) {
            val check = if (onlyCheckMainThread1) {
                Looper.getMainLooper() == Looper.myLooper()
            } else {
                true
            }
            if (check) {
                val info = "info ---> ${saveSlowMethod(name, const)}"
                when {
                    const >= errorConstThreshold1 -> {
                        Log.e(TAG, info)
                    }
                    const >= warnConstThreshold1 -> {
                        Log.w(TAG, info)
                    }
                    const >= infoConstThreshold1 -> {
                        Log.i(TAG, info)
                    }
                }
            }
        }

        private fun saveSlowMethod(name: String, const: Long): String {
            val fullClassName = name.split(METHOD_TRACE_PARTITION).firstOrNull()
            val methodName = name.split(METHOD_TRACE_PARTITION).lastOrNull()
            val className = fullClassName?.substringAfterLast(".", "") ?: "null"
            val pkgName = fullClassName?.substringBeforeLast(".", "") ?: "null"
            return SlowMethodInfo().apply {
                this.pkgName = pkgName
                this.className = className
                this.methodName = methodName
                this.costTimeMs = const
                this.time = System.currentTimeMillis()
                this.threadName = Thread.currentThread().name
                this.callStack = traceToString(Throwable().stackTrace)
            }.printlnLog()
        }

        private fun traceToString(
            stackArray: Array<StackTraceElement>
        ): String {
            if (stackArray.isEmpty()) {
                return "[]"
            }
            //跳过插件代码的堆栈
            val skipFrameCount = 4
            //最多记录的堆栈
            val maxLineNumber = 15
            val stringBuilder = StringBuilder()
            for (i in 0 until stackArray.size - skipFrameCount) {
                if (i < skipFrameCount) {
                    continue
                }
                stringBuilder.append(stackArray[i])
                stringBuilder.append("\r\n")
                if (i > maxLineNumber) {
                    break
                }
            }
            return stringBuilder.toString()
        }
    }
}