package com.zhangyue.ireader.notInList

import androidx.annotation.Keep
import com.zhangyue.ireader.trace_1_2_3_7_process.annotation.HookMethodTrace

@Keep
@HookMethodTrace
class HookClassDemo {

    fun canHook() {
        Thread.sleep(60)
    }

}