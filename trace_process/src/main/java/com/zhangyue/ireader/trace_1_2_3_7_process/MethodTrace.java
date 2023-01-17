package com.zhangyue.ireader.trace_1_2_3_7_process;

import androidx.annotation.Keep;

import com.zhangyue.ireader.trace_1_2_3_7_process.handle.IMethodTraceHandle;
import com.zhangyue.ireader.trace_1_2_3_7_process.handle.MethodTraceHandle;

@Keep
public class MethodTrace {

    private static final IMethodTraceHandle METHOD_TRACE_HANDLE = new MethodTraceHandle();

    static {
        MethodTraceConfigKt.applyConfig();
    }

    public static void onMethodEnter() {
        METHOD_TRACE_HANDLE.onMethodEnter();
    }

    public static void onMethodExit(String str) {
        METHOD_TRACE_HANDLE.onMethodExit(str);
    }

}
