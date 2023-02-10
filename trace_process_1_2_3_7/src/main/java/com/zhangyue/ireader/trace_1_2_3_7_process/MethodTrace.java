package com.zhangyue.ireader.trace_1_2_3_7_process;

import androidx.annotation.Keep;

import com.zhangyue.ireader.trace_1_2_3_7_process.handle.IMethodTraceHandle;
import com.zhangyue.ireader.trace_1_2_3_7_process.handle.SampleMethodTraceHandle;

@Keep
public class MethodTrace {

    private static final IMethodTraceHandle METHOD_TRACE_HANDLE = new SampleMethodTraceHandle();

    static {
        //应用插件配置
        MethodTraceConfigKt.applyConfig();
    }

    public static void onMethodEnter(Object object, String className, String methodName, String args, String returnType) {
        METHOD_TRACE_HANDLE.onMethodEnter(object, className, methodName, args, returnType);
    }

    public static void onMethodExit(Object object, String className, String methodName, String args, String returnType) {
        METHOD_TRACE_HANDLE.onMethodExit(object, className, methodName, args, returnType);
    }

}
