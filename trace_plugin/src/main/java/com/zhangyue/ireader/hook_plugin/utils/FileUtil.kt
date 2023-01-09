package com.zhangyue.ireader.hook_plugin.utils

import org.apache.commons.codec.digest.DigestUtils
import java.io.File

class FileUtil {
    companion object {
        fun path2ClassName(path: String): String {
            return path.replace(File.separator, ".").replace(".class", "")
        }

        fun generateJarFileName(file: File): String {
            return getMd5ByFilePath(file) + "_" + file.name
        }

        private fun getMd5ByFilePath(file: File): String {
            return DigestUtils.md5Hex(file.absolutePath).substring(0, 8)
        }
    }
}