package com.zhangyue.ireader.trace_1_2_3_7_process.handle

import android.os.Looper
import android.os.SystemClock
import android.util.Log
import com.zhangyue.ireader.trace_1_2_3_7_process.*
import java.util.concurrent.atomic.AtomicInteger

class SampleMethodTraceHandle : IMethodTraceHandle {

    companion object {

        private const val TAG = "methodTrace"

        // 用来解决多线程同时访问同一个方法
        @JvmStatic
        val threadLocal: ThreadLocal<Map<String, Addition>> = ThreadLocal()
    }

    /**
     * @param enterTime 方法入口时间戳
     * @param recursion 用来处理递归调用
     */
    class Addition(val enterTime: Long, val recursion: AtomicInteger = AtomicInteger(0))

    override fun onMethodEnter(
        any: Any,
        className: String,
        methodName: String,
        args: String,
        returnType: String
    ) {
        val enterTime = SystemClock.elapsedRealtime()
        val key = className + methodName + args + returnType
        val map = threadLocal.get()?.toMutableMap() ?: HashMap()
        if (map[key] == null) {
            map[key] = Addition(enterTime)
        }
        map[key]!!.recursion.incrementAndGet()
    }

    override fun onMethodExit(
        any: Any,
        className: String,
        methodName: String,
        args: String,
        returnType: String
    ) {
        val exitTime = SystemClock.elapsedRealtime()
        val key = className + methodName + args + returnType
        val map = threadLocal.get() ?: return
        val ato = map[key] ?: return
        //递归调用，处理最外层调用
        if (ato.recursion.decrementAndGet() > 0) {
            return
        }
        val enterTime = ato.enterTime
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