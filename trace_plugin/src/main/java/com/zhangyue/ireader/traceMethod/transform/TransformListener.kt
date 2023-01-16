package com.zhangyue.ireader.traceMethod.transform

interface TransformListener {
    fun onTransform(className: String, bytes: ByteArray): ByteArray
}