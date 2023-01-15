package com.zhangyue.ireader.traceMethod.transform

interface TransformListener {
    fun onTransform(bytes: ByteArray):ByteArray
}