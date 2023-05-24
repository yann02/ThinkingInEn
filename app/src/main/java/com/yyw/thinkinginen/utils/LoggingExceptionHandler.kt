package com.yyw.thinkinginen.utils

import com.yyw.thinkinginen.BuildConfig
import kotlin.system.exitProcess

class LoggingExceptionHandler:Thread.UncaughtExceptionHandler {
    fun startCatch() {
        if (BuildConfig.BUILD_TYPE == "release") {
            Thread.setDefaultUncaughtExceptionHandler(this)
        }
    }

    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        throwable.printStackTrace()
        ErrorLogger().logException(throwable)
        exitProcess(1)
    }
}