package com.zhangyue.ireader.methodtrace

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        methodConstError()
        methodConstWarn()
        methodConstInfo()

        Thread({
            methodCostThread()
        }, "const_thread").start()
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