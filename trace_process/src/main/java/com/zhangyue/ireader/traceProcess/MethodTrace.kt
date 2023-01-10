package com.zhangyue.ireader.traceProcess

import android.util.Log

class MethodTrace {

    companion object {

        private val traceList:MutableList<MethodRecord> = mutableListOf()

        const val TAG = "methodTrace"

        var startTime: Long = 0L

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
            val enterTime = startTime
            val exitTime = System.currentTimeMillis()
            val const = exitTime - enterTime
            Log.d(TAG, "$name const $const")
            val record = MethodRecord(name,enterTime,exitTime,const)
            traceList.add(record)
        }
    }
}