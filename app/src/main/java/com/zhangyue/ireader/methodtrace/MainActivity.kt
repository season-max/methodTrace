package com.zhangyue.ireader.methodtrace

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

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
        val thread_1 = Thread({
            multiThreadConst()
            Thread.sleep(10)
        }, "thread_1").start()
        val thread_2 = Thread({
            multiThreadConst()
            Thread.sleep(20)
        }, "thread_2").start()


        //递归访问
        cycleCall()
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
}