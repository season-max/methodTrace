package com.zhangyue.ireader.notInList

import androidx.annotation.Keep
import com.zhangyue.ireader.trace_1_2_3_7_process.annotation.HookMethodTrace

@Keep
class HookMethodDemo {

    @HookMethodTrace
    fun canHook() {
        Thread.sleep(70)
    }


}