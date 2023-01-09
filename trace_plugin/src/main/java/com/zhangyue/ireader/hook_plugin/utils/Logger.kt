package com.zhangyue.ireader.hook_plugin.utils

import org.gradle.api.Project
import org.gradle.api.logging.Logger

/**
 * 日志输出工具类
 */
class Logger {
    companion object {
        private var logger: Logger? = null

        private var debug: Boolean = false

        fun make(project: Project) {
            logger = project.logger
            debug = project.trace_config.debug
        }

        fun debug(msg: String?) {
            if (debug) {
                logger?.debug(msg)
            }
        }

        fun info(msg: String?) {
            if (debug) {
                logger?.info(msg)
            }
        }

        fun warn(msg: String?) {
            if (debug) {
                logger?.warn(msg)
            }
        }

        fun error(msg: String?) {
            if (debug) {
                logger?.error(msg)
            }
        }
    }
}