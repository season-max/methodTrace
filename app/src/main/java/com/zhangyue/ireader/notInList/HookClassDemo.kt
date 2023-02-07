package com.zhangyue.ireader.notInList

import com.zhangyue.ireader.trace_1_2_3_7_process.annotation.HookMethodTrace

@HookMethodTrace
class HookClassDemo {

    private fun canHook() {
        Thread.sleep(60)
    }

}