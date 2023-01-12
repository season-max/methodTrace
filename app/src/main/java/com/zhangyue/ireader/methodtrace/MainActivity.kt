package com.zhangyue.ireader.methodtrace

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        methodConstTest()
    }



    private fun methodConstTest(){

        Thread.sleep(55)
    }
}