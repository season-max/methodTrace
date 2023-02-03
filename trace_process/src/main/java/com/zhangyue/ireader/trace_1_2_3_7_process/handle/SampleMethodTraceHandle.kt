package com.zhangyue.ireader.trace_1_2_3_7_process.handle

import android.os.Looper
import android.os.SystemClock
import android.util.Log
import com.zhangyue.ireader.trace_1_2_3_7_process.*

class SampleMethodTraceHandle : IMethodTraceHandle {

    companion object {

        private const val TAG = "methodTrace"

        // 用来解决多线程同时访问同一个方法
        @JvmStatic
        val threadLocal: ThreadLocal<Map<String, Addition>> = ThreadLocal()
    }

    /**
     *
     */
    class Addition(val enterTime: Long) {

    }

    /**
     *
     */
    // TODO: 如何解决迭代问题
    override fun onMethodEnter(any: Any, className: String, methodName: String, args: String) {
        val enterTime = SystemClock.elapsedRealtime()
        val key = className + methodName + args
        val map = threadLocal.get()?.toMutableMap() ?: HashMap()
        map[key] = Addition(enterTime)
    }

    override fun onMethodExit(any: Any, className: String, methodName: String, args: String) {
        val exitTime = SystemClock.elapsedRealtime()
        val key = className + methodName + args
        val map = threadLocal.get() ?: return
        val enterTime = map[key]?.enterTime ?: return
        if (enterTime <= 0) {
            return
        }
        val const = exitTime - enterTime
        val check = if (onlyCheckMainThread1) {
            Looper.getMainLooper() == Looper.myLooper()
        } else {
            true
        }
        if (check) {
            val info = saveSlowMethod(any, className, methodName, const)
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

    private fun saveSlowMethod(
        any: Any,
        fullClassName: String?,
        methodName: String?,
        const: Long
    ): String {
        val className = fullClassName?.substringAfterLast(".", "") ?: "null"
        val pkgName = fullClassName?.substringBeforeLast(".", "") ?: "null"
        return StringBuilder().apply {
            append("\r\n")
                .append("[this] : $any")
                .append("\r\n")
                .append("[pkgName] : $pkgName")
                .append("\r\n")
                .append("[className] : $className")
                .append("\r\n")
                .append("[methodName] : $methodName")
                .append("\r\n")
                .append("[costTime] : $const ms")
                .append("\r\n")
                .append("[threadName] : ${Thread.currentThread().name}")
                .append("\r\n")
                .append("[callStack] : ")
                .append("\r\n")
                .append(TraceUtils.getThreadStackTrace(Thread.currentThread()))
        }.toString()
    }
}