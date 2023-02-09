package com.zhangyue.ireader.traceMethod.utils

inline fun <T : CharSequence> Iterable<T>.itemMatchAction(
    init: T,
    action: (init: T, T) -> Boolean
): Boolean {
    var contains = false
    for (e in this) {
        if (action(init, e)) {
            contains = true
            break
        }
    }
    return contains
}