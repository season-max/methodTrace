package com.zhangyue.ireader.trace_1_2_3_7_process.handle

import android.os.Looper
import android.os.SystemClock
import android.util.Log
import com.zhangyue.ireader.trace_1_2_3_7_process.*
import java.util.concurrent.atomic.AtomicInteger

class SampleMethodTraceHandle : IMethodTraceHandle {

    companion object {

        private const val TAG = "methodTrace"

        private const val LINE =
            "==================================================================================================="

        // 用来解决多线程同时访问同一个方法
        @JvmStatic
        val threadLocal: ThreadLocal<HashMap<String, Addition>> = ThreadLocal()
    }

    /**
     * @param enterTime 方法入口时间戳
     * @param recursion 用来处理递归调用
     */
    class Addition(val enterTime: Long, val recursion: AtomicInteger = AtomicInteger(0)) {
        override fun toString(): String {
            return "Addition(enterTime=$enterTime, recursion=$recursion)"
        }
    }

    override fun onMethodEnter(
        any: Any,
        classNameFullName: String,
        methodName: String,
        args: String,
        returnType: String
    ) {
        val enterTime = nowTime()
        val key = classNameFullName + methodName + args + returnType
        val map = threadLocal.get() ?: HashMap<String, Addition>().also {
            threadLocal.set(it)
        }
        if (map[key] == null) {
            map[key] = Addition(enterTime)
        }
        map[key]!!.recursion.incrementAndGet()
    }

    private fun nowTime(): Long {
        return SystemClock.elapsedRealtime()
    }

    override fun onMethodExit(
        any: Any,
        classNameFullName: String,
        methodName: String,
        args: String,
        returnType: String
    ) {
        val exitTime = nowTime()
        val key = classNameFullName + methodName + args + returnType
        val map = threadLocal.get() ?: return
        val addition = map[key] ?: return
        //递归调用，只处理最外层调用
        if (addition.recursion.decrementAndGet() > 0) {
            return
        }
        //remove 移除 key
        map.remove(key)
        val enterTime = addition.enterTime
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
            when {
                const >= errorConstThreshold1 -> {
                    Log.e(TAG, printLog(any, classNameFullName, methodName, const))
                }
                const >= warnConstThreshold1 -> {
                    Log.w(TAG, printLog(any, classNameFullName, methodName, const))
                }
                const >= infoConstThreshold1 -> {
                    Log.i(TAG, printLog(any, classNameFullName, methodName, const))
                }
            }
        }
    }

    private fun printLog(
        any: Any,
        fullClassName: String?,
        methodName: String?,
        const: Long
    ): String {
        val className = fullClassName?.substringAfterLast(".", "") ?: "null"
        val pkgName = fullClassName?.substringBeforeLast(".", "") ?: "null"
        return StringBuilder().apply {
            append(LINE)
                .append("\r\n")
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
                .append(LINE)
        }.toString()
    }
}