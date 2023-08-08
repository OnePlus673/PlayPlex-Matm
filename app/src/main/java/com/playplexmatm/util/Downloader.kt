package com.playplexmatm.util

import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

object Downloader {
    fun DownloadFile(fileURL: String?, directory: File?) {
        try {
            val f = FileOutputStream(directory)
            val u = URL(fileURL)
            val c: HttpURLConnection = u.openConnection() as HttpURLConnection
            c.setRequestMethod("GET")
            c.setDoOutput(true)
            c.connect()
            val `in`: InputStream = c.getInputStream()
            val buffer = ByteArray(1024)
            var len1 = 0
            while (`in`.read(buffer).also { len1 = it } > 0) {
                f.write(buffer, 0, len1)
            }
            f.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}