package com.zhangyue.ireader.traceMethod.utils

import com.zhangyue.ireader.traceMethod.TraceConfig
import org.gradle.api.Project
import org.gradle.api.logging.Logger

/**
 * 日志输出工具类
 */
class Logger {
    companion object {
        private var logger: Logger? = null

        private var debug: Boolean = false

        fun make(project: Project, config: TraceConfig) {
            logger = project.logger
            debug = config.printlnLog
        }


        fun info(msg: String?) {
            if (debug) {
                println(msg)
            }
        }

        fun error(msg: String?) {
            if (debug) {
                logger?.error(msg)
            }
        }
    }
}