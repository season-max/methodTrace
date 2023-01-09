package com.zhangyue.ireader.hook_plugin.transform

import com.android.build.api.transform.TransformInvocation
import org.gradle.api.Project

class TraceTransform(project: Project) : BaseTransform(project) {
    override fun shouldHookClassInner(className: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun transformClassInner(className: String, sourceBytes: ByteArray?): ByteArray? {
        TODO("Not yet implemented")
    }

    override fun onTransformStart(transformInvocation: TransformInvocation) {
        TODO("Not yet implemented")
    }

    override fun onTransformEnd(transformInvocation: TransformInvocation) {
        TODO("Not yet implemented")
    }

}