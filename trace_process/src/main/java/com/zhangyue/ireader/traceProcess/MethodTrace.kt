package com.zhangyue.ireader.traceProcess

import android.os.Looper
import android.util.Log

class MethodTrace {

    companion object {
        const val TAG = "methodTrace"

        private const val ERROR_CONST_THRESHOLD: Int = 50

        private const val ONLY_CHECK_MAIN_THREAD: Boolean = true

        private var startTime: Long = 0L

        private const val METHOD_TRACE_PARTITION = "$"

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
            val check = if (ONLY_CHECK_MAIN_THREAD) {
                Looper.getMainLooper() == Looper.myLooper()
            } else {
                true
            }
            if (const >= ERROR_CONST_THRESHOLD && check) {
                saveSlowMethod(name, const)
            }
        }

        private fun saveSlowMethod(name: String, const: Long) {
            val fullClassName = name.split(METHOD_TRACE_PARTITION).firstOrNull()
            val methodName = name.split(METHOD_TRACE_PARTITION).lastOrNull()
            val className = fullClassName?.substringAfterLast(".", "") ?: "null"
            val pkgName = fullClassName?.substringBeforeLast(".", "") ?: "null"
            val info = SlowMethodInfo().apply {
                this.pkgName = pkgName
                this.className = className
                this.methodName = methodName
                this.costTimeMs = const
                this.time = System.currentTimeMillis()
                this.callStack = traceToString(Throwable().stackTrace)
            }
            Log.i(TAG, "info---> ${info.printlnLog()}")
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