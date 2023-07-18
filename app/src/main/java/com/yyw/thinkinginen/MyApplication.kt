package com.yyw.thinkinginen

import android.app.Application
import com.yyw.thinkinginen.utils.LoggingExceptionHandler
import com.yyw.thinkinginen.workers.Sync
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application(){
    override fun onCreate() {
        super.onCreate()
        LoggingExceptionHandler().startCatch()  //当编译类型为release时，全局异常捕获，日志保存到sdcard
        Sync.initialize(context = this)
    }
}