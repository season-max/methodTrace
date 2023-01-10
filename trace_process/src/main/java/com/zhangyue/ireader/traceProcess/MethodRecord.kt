package com.zhangyue.ireader.traceProcess

class MethodRecord(
    val name: String,
    val enterTime: Long,
    val exitTime: Long,
    val constTime: Long
) {


    override fun toString(): String {
        return "MethodRecord(name='$name', enterTime=$enterTime, exitTime=$exitTime, constTime=$constTime)"
    }
}