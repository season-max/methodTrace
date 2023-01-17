package com.zhangyue.ireader.traceMethod.transform

import com.zhangyue.ireader.traceMethod.GlobalConfig
import com.zhangyue.ireader.traceMethod.transform.MethodTraceFirstTranceTransform.Companion.ASM_API
import com.zhangyue.ireader.traceMethod.transform.MethodTraceFirstTranceTransform.Companion.DOT
import com.zhangyue.ireader.traceMethod.transform.MethodTraceFirstTranceTransform.Companion.METHOD_TRACE_CLASS_NAME
import com.zhangyue.ireader.traceMethod.transform.MethodTraceFirstTranceTransform.Companion.SEPARATOR
import com.zhangyue.ireader.traceMethod.utils.Logger
import com.zhangyue.ireader.traceMethod.visitor.CustomHandleClassVisitor
import org.objectweb.asm.*
import org.objectweb.asm.Opcodes.*


/**
 * 自定义方法耗时处理接口 transform
 * 如果插件配置了 [com.zhangyue.ireader.traceMethod.TraceConfig.customHandle] ,会执行该 transform 的逻辑
 */
class CustomHandleTransform : TransformListener {
    override fun onTransform(className: String, bytes: ByteArray): ByteArray {
        return if (className == METHOD_TRACE_CLASS_NAME) {
            val hasSetCustomHandleMethodTrace = GlobalConfig.pluginConfig.customHandle.let {
                it != null && it.isNotEmpty()
            }
            Logger.info("set custom method trance handle:$hasSetCustomHandleMethodTrace")
            if (hasSetCustomHandleMethodTrace) {
                //有两种处理方案。
                // 1 是替换接口对应的实例
                // 2 是重新生成一份 [com.zhangyue.ireader.trace_1_2_3_7_process.MethodTrace] 对应的 class 文件
                // 方案一
                if (USE_IMPL_WAY_ONE) {
                    val classReader = ClassReader(bytes)
                    val classWriter = ClassWriter(
                        classReader,
                        ClassWriter.COMPUTE_MAXS //自动计算栈深和局部变量表大小
                    )
                    val cv = CustomHandleClassVisitor(
                        ASM_API,
                        classWriter,
                        GlobalConfig.pluginConfig.customHandle!!
                    )
                    classReader.accept(cv, ClassReader.EXPAND_FRAMES)
                    classWriter.toByteArray()
                } else {
                    // 方案二
                    dump(GlobalConfig.pluginConfig.customHandle!!)
                }
            } else {
                bytes
            }
        } else {
            bytes
        }
    }

    companion object {
        const val USE_IMPL_WAY_ONE = false
        private fun dump(customImpl: String): ByteArray {
            val classWriter = ClassWriter(0)
            val fieldVisitor: FieldVisitor
            var methodVisitor: MethodVisitor
            var annotationVisitor0: AnnotationVisitor

            // visit
            classWriter.visit(
                V1_8,
                ACC_PUBLIC or ACC_SUPER,
                "com/zhangyue/ireader/trace_1_2_3_7_process/MethodTrace",
                null,
                "java/lang/Object",
                null
            )

            // visit source
            classWriter.visitSource("MethodTrace.java", null)

            // annotation
            run {
                annotationVisitor0 =
                    classWriter.visitAnnotation("Landroidx/annotation/Keep;", false)
                annotationVisitor0.visitEnd()
            }

            // field
            run {
                fieldVisitor = classWriter.visitField(
                    ACC_PRIVATE or ACC_FINAL or ACC_STATIC,
                    "METHOD_TRACE_HANDLE",
                    "Lcom/zhangyue/ireader/trace_1_2_3_7_process/handle/IMethodTraceHandle;",
                    null,
                    null
                )
                fieldVisitor.visitEnd()
            }

            // <init>
            run {
                methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null)
                methodVisitor.visitCode()
                val label0 = Label()
                methodVisitor.visitLabel(label0)
                methodVisitor.visitLineNumber(9, label0)
                methodVisitor.visitVarInsn(ALOAD, 0)
                methodVisitor.visitMethodInsn(
                    INVOKESPECIAL,
                    "java/lang/Object",
                    "<init>",
                    "()V",
                    false
                )
                methodVisitor.visitInsn(RETURN)
                val label1 = Label()
                methodVisitor.visitLabel(label1)
                methodVisitor.visitLocalVariable(
                    "this",
                    "Lcom/zhangyue/ireader/trace_1_2_3_7_process/MethodTrace;",
                    null,
                    label0,
                    label1,
                    0
                )
                methodVisitor.visitMaxs(1, 1)
                methodVisitor.visitEnd()
            }

            // onMethodEnter
            run {
                methodVisitor = classWriter.visitMethod(
                    ACC_PUBLIC or ACC_STATIC,
                    "onMethodEnter",
                    "()V",
                    null,
                    null
                )
                methodVisitor.visitCode()
                val label0 = Label()
                methodVisitor.visitLabel(label0)
                methodVisitor.visitLineNumber(18, label0)
                methodVisitor.visitFieldInsn(
                    GETSTATIC,
                    "com/zhangyue/ireader/trace_1_2_3_7_process/MethodTrace",
                    "METHOD_TRACE_HANDLE",
                    "Lcom/zhangyue/ireader/trace_1_2_3_7_process/handle/IMethodTraceHandle;"
                )
                methodVisitor.visitMethodInsn(
                    INVOKEINTERFACE,
                    "com/zhangyue/ireader/trace_1_2_3_7_process/handle/IMethodTraceHandle",
                    "onMethodEnter",
                    "()V",
                    true
                )
                val label1: Label = Label()
                methodVisitor.visitLabel(label1)
                methodVisitor.visitLineNumber(19, label1)
                methodVisitor.visitInsn(RETURN)
                methodVisitor.visitMaxs(1, 0)
                methodVisitor.visitEnd()
            }

            // onMethodExit
            run {
                methodVisitor = classWriter.visitMethod(
                    ACC_PUBLIC or ACC_STATIC,
                    "onMethodExit",
                    "(Ljava/lang/String;)V",
                    null,
                    null
                )
                methodVisitor.visitCode()
                val label0 = Label()
                methodVisitor.visitLabel(label0)
                methodVisitor.visitLineNumber(22, label0)
                methodVisitor.visitFieldInsn(
                    GETSTATIC,
                    "com/zhangyue/ireader/trace_1_2_3_7_process/MethodTrace",
                    "METHOD_TRACE_HANDLE",
                    "Lcom/zhangyue/ireader/trace_1_2_3_7_process/handle/IMethodTraceHandle;"
                )
                methodVisitor.visitVarInsn(ALOAD, 0)
                methodVisitor.visitMethodInsn(
                    INVOKEINTERFACE,
                    "com/zhangyue/ireader/trace_1_2_3_7_process/handle/IMethodTraceHandle",
                    "onMethodExit",
                    "(Ljava/lang/String;)V",
                    true
                )
                val label1 = Label()
                methodVisitor.visitLabel(label1)
                methodVisitor.visitLineNumber(23, label1)
                methodVisitor.visitInsn(RETURN)
                val label2 = Label()
                methodVisitor.visitLabel(label2)
                methodVisitor.visitLocalVariable(
                    "str",
                    "Ljava/lang/String;",
                    null,
                    label0,
                    label2,
                    0
                )
                methodVisitor.visitMaxs(2, 1)
                methodVisitor.visitEnd()
            }

            // <clinit>
            run {
                methodVisitor = classWriter.visitMethod(ACC_STATIC, "<clinit>", "()V", null, null)
                methodVisitor.visitCode()
                val label0 = Label()
                methodVisitor.visitLabel(label0)
                methodVisitor.visitLineNumber(11, label0)
                methodVisitor.visitTypeInsn(
                    NEW,
//                    "com/zhangyue/ireader/trace_1_2_3_7_process/handle/MethodTraceHandle"
                    customImpl.replace(DOT, SEPARATOR)
                )
                methodVisitor.visitInsn(DUP)
                methodVisitor.visitMethodInsn(
                    INVOKESPECIAL,
//                    "com/zhangyue/ireader/trace_1_2_3_7_process/handle/MethodTraceHandle",
                    customImpl.replace(DOT, SEPARATOR),
                    "<init>",
                    "()V",
                    false
                )
                methodVisitor.visitFieldInsn(
                    PUTSTATIC,
                    "com/zhangyue/ireader/trace_1_2_3_7_process/MethodTrace",
                    "METHOD_TRACE_HANDLE",
                    "Lcom/zhangyue/ireader/trace_1_2_3_7_process/handle/IMethodTraceHandle;"
                )
                val label1 = Label()
                methodVisitor.visitLabel(label1)
                methodVisitor.visitLineNumber(14, label1)
                methodVisitor.visitMethodInsn(
                    INVOKESTATIC,
                    "com/zhangyue/ireader/trace_1_2_3_7_process/MethodTraceConfigKt",
                    "applyConfig",
                    "()V",
                    false
                )
                val label2 = Label()
                methodVisitor.visitLabel(label2)
                methodVisitor.visitLineNumber(15, label2)
                methodVisitor.visitInsn(RETURN)
                methodVisitor.visitMaxs(2, 0)
                methodVisitor.visitEnd()
            }

            classWriter.visitEnd()

            return classWriter.toByteArray()
        }
    }
}