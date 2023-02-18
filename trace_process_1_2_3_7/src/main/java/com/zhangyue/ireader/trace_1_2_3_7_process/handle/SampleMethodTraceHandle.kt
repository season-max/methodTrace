package com.zhangyue.ireader.trace_1_2_3_7_process.handle

import android.os.Looper
import android.os.SystemClock
import android.util.Log
import com.zhangyue.ireader.trace_1_2_3_7_process.*
import java.util.*

class SampleMethodTraceHandle : IMethodTraceHandle {

    companion object {
        private const val LINE =
            "==================================================================================================="

        private const val TAG = "methodTrace"

        /**
         * 最大调用嵌套层级。方法调用栈深度不能超过此数值
         */
        private const val MAX_LEVEL = 40

        // 用来解决多线程同时访问同一个方法
        @JvmStatic
        val threadLocal: ThreadLocal<Deque<Addition>> = object : ThreadLocal<Deque<Addition>>() {
            override fun initialValue(): Deque<Addition> {
                return ArrayDeque()
            }
        }
    }

    /**
     * @param enterTime 方法入口时间戳
     * @param name 唯一 key
     */
    class Addition(val enterTime: Long, val name: String)

    override fun onMethodEnter(
        any: Any,
        classNameFullName: String,
        methodName: String,
        args: String,
        returnType: String
    ) {
        if (!checkMathStart()) {
            return
        }
        val deque = threadLocal.get()!!
        if (deque.size > MAX_LEVEL) {
            return
        }
        val enterTime = nowTime()
        val key = classNameFullName + methodName + args + returnType
        deque.addLast(Addition(enterTime, key))
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
        if (!checkMatchExit()) {
            return
        }
        val deque = threadLocal.get() ?: return
        val exitTime = nowTime()
        val key = classNameFullName + methodName + args + returnType
        // 从栈顶遍历，找到匹配的 key
        var addition: Addition? = null
        while (deque.size > 0) {
            addition = deque.pollLast()
            if (addition.name == key) {
                break
            }
        }
        if (addition == null) {
            return
        }
        val enterTime = addition.enterTime
        if (enterTime <= 0) {
            return
        }
        val const = exitTime - enterTime
        val level = deque.size
        when {
            const >= errorConstThreshold1 -> {
                Log.e(TAG, printLog(any, classNameFullName, methodName, const, level))
            }
            const >= warnConstThreshold1 -> {
                Log.w(TAG, printLog(any, classNameFullName, methodName, const, level))
            }
            const >= infoConstThreshold1 -> {
                Log.i(TAG, printLog(any, classNameFullName, methodName, const, level))
            }
        }
    }

    override fun checkMathStart(): Boolean {
        return if (onlyCheckMainThread1) {
            Looper.getMainLooper() == Looper.myLooper()
        } else {
            true
        }
    }

    override fun checkMatchExit(): Boolean {
        return if (onlyCheckMainThread1) {
            Looper.getMainLooper() == Looper.myLooper()
        } else {
            true
        }
    }

    private fun printLog(
        any: Any,
        fullClassName: String?,
        methodName: String?,
        const: Long,
        level: Int
    ): String {
        val threadName = Thread.currentThread().name
        return if (printCallStack) {
            String.format(
                "%s%s: %d ms",
                space(level),
                "[$threadName] $fullClassName.$methodName",
                const
            )
        } else {
            val className = fullClassName?.substringAfterLast(".", "") ?: "null"
            val pkgName = fullClassName?.substringBeforeLast(".", "") ?: "null"
            return StringBuilder().apply {
                append(LINE)
                    .append("\r\n")
                    .append("[this] : $any")
                    .append("\r\n")
                    .append("[className] :$pkgName.$className")
                    .append("\r\n")
                    .append("[methodName] : $methodName")
                    .append("\r\n")
                    .append("[costTime] : $const ms")
                    .append("\r\n")
                    .append("[pid] : ${android.os.Process.myPid()}")
                    .append("\r\n")
                    .append("[threadName] : $threadName")
                    .append("\r\n")
                    .append(LINE)
            }.toString()

        }
    }

    private fun space(level: Int): String {
        return String(CharArray(level) {
            '\t'
        })
    }
}