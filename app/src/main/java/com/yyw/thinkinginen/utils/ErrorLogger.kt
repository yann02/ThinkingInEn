package com.yyw.thinkinginen.utils

import android.os.Environment
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.*

class ErrorLogger {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    fun logException(throwable: Throwable) {
        val time = dateFormat.format(Date())
        val stackTrace = StringWriter()
        throwable.printStackTrace(PrintWriter(stackTrace))
        val fileName = "error_log_$time.txt"
        val fileContents = "Time: $time\n${stackTrace}"
        val root = Environment.getExternalStorageDirectory()
        val directory = File(root, "BizErrors")
        if (!directory.exists()) {
            directory.mkdirs()
        }
        val file = File(directory, fileName)
        FileWriter(file).use {
            it.write(fileContents)
        }
    }
}