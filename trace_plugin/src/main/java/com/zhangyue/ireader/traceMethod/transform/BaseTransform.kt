package com.zhangyue.ireader.traceMethod.transform

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.zhangyue.ireader.traceMethod.utils.FileUtil
import com.zhangyue.ireader.traceMethod.utils.Logger
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.gradle.api.Project
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.lang.IllegalArgumentException
import java.util.concurrent.AbstractExecutorService
import java.util.concurrent.Callable
import java.util.concurrent.ForkJoinPool
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream

abstract class BaseTransform(val project: Project) : Transform() {

    private val service: AbstractExecutorService = ForkJoinPool.commonPool()

    private val taskList: MutableList<Callable<Unit>> = ArrayList()

    override fun getName(): String =
        javaClass.simpleName

    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> =
        TransformManager.CONTENT_CLASS

    override fun getScopes(): MutableSet<in QualifiedContent.Scope> =
        TransformManager.SCOPE_FULL_PROJECT

    override fun isIncremental(): Boolean = true

    override fun transform(transformInvocation: TransformInvocation) {
        onTransformStart(transformInvocation)
        val inputs = transformInvocation.inputs
        val outputProvider = transformInvocation.outputProvider
        val context = transformInvocation.context
        val incremental = transformInvocation.isIncremental
        if (outputProvider == null) {
            throw IllegalArgumentException("outputProvider is null!")
        }
        Logger.info(if (incremental) "增量编译" else "全量编译")
        //非增量模式，删除所有内容
        if (!incremental) {
            outputProvider.deleteAll()
        }
        inputs.forEach {
            it.jarInputs.forEach { jarInput ->
                submitTask {
                    forEachJar(jarInput, outputProvider, context, incremental)
                }
            }

            it.directoryInputs.forEach { directoryInput ->
                submitTask {
                    forEachDir(directoryInput, outputProvider, context, incremental)
                }
            }
        }
        val futures = service.invokeAll(taskList)
        futures.forEach {
            it.get()
        }
        onTransformEnd(transformInvocation)
    }

    private fun forEachDir(
        directoryInput: DirectoryInput,
        outputProvider: TransformOutputProvider,
        context: Context,
        incremental: Boolean
    ) {
        val inputDir = directoryInput.file
        val dest = outputProvider.getContentLocation(
            directoryInput.name,
            directoryInput.contentTypes,
            directoryInput.scopes,
            Format.DIRECTORY
        )
        val srcDirPath = inputDir.absolutePath
        val destDirPath = dest.absolutePath
        FileUtils.forceMkdir(dest)
        //增量编译
        if (incremental) {
            directoryInput.changedFiles.forEach {
                val file = it.key
                when (it.value) {
                    Status.NOTCHANGED -> {
                    }
                    Status.REMOVED -> {
                        file.delete()
                    }
                    Status.ADDED,
                    Status.CHANGED -> {
                        transformDirFile(file, srcDirPath, destDirPath)
                    }
                    else -> {
                        Logger.error("${file.absolutePath} status is ${it.value} !!")
                    }
                }
            }
        } else {
            //全量编译
            val fileTree = inputDir.walk()
            fileTree.filter {
                it.isFile
            }.forEach {
                transformDirFile(it, srcDirPath, destDirPath)
            }
        }


    }

    private fun transformDirFile(
        file: File,
        srcDirPath: String,
        destDirPath: String,
    ) {
        val destFilePath = file.absolutePath.replace(srcDirPath, destDirPath)
        val destFile = File(destFilePath)
        if (destFile.exists()) {
            destFile.delete()
        }
        val className =
            FileUtil.path2ClassName(file.absolutePath.replace(srcDirPath + File.separator, ""))
        val sourceBytes = IOUtils.toByteArray(FileInputStream(file))
        val modifyBytes = when (file.name.substringAfterLast(".", "")) {
            "class" -> {
                if (needTransform()) {
                    transformClass(className, sourceBytes)
                } else {
                    sourceBytes
                }
            }
            else -> {
                sourceBytes
            }
        }
        FileUtils.writeByteArrayToFile(destFile, modifyBytes)
    }

    abstract fun needTransform(): Boolean

    private fun transformClass(className: String, sourceBytes: ByteArray): ByteArray? {
        var bytes: ByteArray?
        try {
            bytes = transformClassInner(sourceBytes)
        } catch (e: Throwable) {
            bytes = sourceBytes
            Logger.error("throw exception when modify class $className}")
        }
        return bytes
    }

    abstract fun transformClassInner(sourceBytes: ByteArray): ByteArray?

    private fun forEachJar(
        jarInput: JarInput,
        outputProvider: TransformOutputProvider,
        context: Context,
        incremental: Boolean
    ) {
        val destFile = outputProvider.getContentLocation(
            //防止出现同名的 jar 文件
            FileUtil.generateJarFileName(jarInput.file),
            jarInput.contentTypes,
            jarInput.scopes,
            Format.JAR
        )
        val isLegalJar = isLegalJar(jarInput.file)
        if (incremental) {
            when (jarInput.status) {
                Status.NOTCHANGED -> {
                }
                Status.REMOVED -> {
                    jarInput.file.delete()
                }
                Status.CHANGED,
                Status.ADDED -> {
                    if (isLegalJar) {
                        transformJar(jarInput.file, destFile)
                    } else {
                        FileUtils.copyFile(jarInput.file, destFile)
                    }
                }
                else -> {
                    Logger.error("${jarInput.name} status is ${jarInput.status}")
                }
            }
        } else {
            if (destFile.exists()) {
                destFile.delete()
            }
            if (isLegalJar) {
                transformJar(jarInput.file, destFile)
            } else {
                FileUtils.copyFile(jarInput.file, destFile)
            }
        }
    }

    private fun transformJar(jarFile: File, destFile: File) {
        val jarOutputStream = JarOutputStream(FileOutputStream(destFile))
        val inputJarFile = JarFile(jarFile, false)
        try {
            val entries = inputJarFile.entries()
            while (entries.hasMoreElements()) {
                val jarEntry = entries.nextElement()
                val entryName = jarEntry.name
                val inputStream = inputJarFile.getInputStream(jarEntry)
                try {
                    val sourceBytes = IOUtils.toByteArray(inputStream)
                    if (!jarEntry.isDirectory) {
                        val modifyBytes = when (entryName.substringAfterLast('.', "")) {
                            "class" -> {
                                if (needTransform()) {
                                    transformClass(entryName, sourceBytes)
                                } else {
                                    sourceBytes
                                }
                            }
                            else -> {
                                sourceBytes
                            }
                        }
                        jarOutputStream.putNextEntry(JarEntry(entryName))
                        jarOutputStream.write(modifyBytes!!)
                        jarOutputStream.closeEntry()
                    }
                } finally {
                    IOUtils.closeQuietly(inputStream)
                }
            }
        } finally {
            jarOutputStream.flush()
            IOUtils.closeQuietly(jarOutputStream)
            IOUtils.closeQuietly(inputJarFile)
        }
    }

    private fun submitTask(runnable: Runnable) {
        taskList.add(Callable<Unit> { runnable.run() })
    }

    abstract fun onTransformStart(transformInvocation: TransformInvocation)

    abstract fun onTransformEnd(transformInvocation: TransformInvocation)

    private fun isLegalJar(file: File): Boolean {
        return file.isFile
                && file.name != "R.jar"
                && file.length() > 0L
                && file.name.endsWith(".jar")
    }
}