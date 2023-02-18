package com.zhangyue.ireader.methodtrace

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.export.test.Export
import com.zhangyue.ireader.notInList.HookClassDemo
import com.zhangyue.ireader.notInList.HookMethodDemo

class MainActivity : AppCompatActivity() {

    private var cycleTime = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        methodConstError()
        methodConstWarn()
        methodConstInfo()

        Thread({
            methodCostThread()
        }, "const_thread").start()

        //多线程访问
        Thread({
            multiThreadConst()
            Thread.sleep(10)
        }, "thread_1").start()
        Thread({
            multiThreadConst()
            Thread.sleep(20)
        }, "thread_2").start()


        //递归访问
        cycleCall()

        //访问 static 方法
        MainActivity.staticFun()

        //访问注解方法
        HookClassDemo().canHook()
        HookMethodDemo().canHook()

        //访问外部方法
        Export.export()

        //访问异常方法
        try{
            throwError()
        }catch (_:java.lang.Exception){

        }
    }

    private fun throwError(){

        Thread.sleep(100)
        val i = 2 / 0
    }

    companion object {
        @JvmStatic
        fun staticFun() {
            Thread.sleep(100)
        }
    }


    private fun cycleCall() {
        Thread.sleep(10)
        cycleTime++
        if (cycleTime < 10) {
            cycleCall()
        }
    }


    //多线程访问
    private fun multiThreadConst() {
        Thread.sleep(100)
    }

    private fun methodCostThread() {
        Thread.sleep(100)
    }


    private fun methodConstInfo() {
        Thread.sleep(11)

    }

    private fun methodConstWarn() {

        Thread.sleep(31)
    }


    private fun methodConstError() {

        Thread.sleep(51)
    }

    private fun emptyFun(){

    }
}