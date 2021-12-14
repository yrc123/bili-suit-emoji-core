package com.yrc.utils

import com.yrc.pojo.Item
import java.io.File
import java.io.FileOutputStream
import java.net.URI
import java.nio.file.Paths
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern
import org.apache.commons.io.IOUtils

class ImageDownloader (private val items:List<Item>, private val dirPath:String, val urlUtil: UrlUtil = UrlUtil()) {

    private val poolExecutor: ThreadPoolExecutor by lazy {
        ThreadPoolExecutor(4, 4, 1, TimeUnit.HOURS, LinkedBlockingQueue());
    }

    fun startDownload() {
        val dir = File(dirPath)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        items.forEach {
            println("开始下载: ${it.name}")
            val path = Paths.get(dirPath, "${filterSpecialChar(it.name)}.png").toAbsolutePath().toString()
            val imageDownloaderRunnable =
                ImageDownloaderRunnable(path, it.image, urlUtil)
            poolExecutor.execute(imageDownloaderRunnable)
        }
        poolExecutor.shutdown()
    }

    private class ImageDownloaderRunnable(val savePath: String, val uri: String, val urlUtil: UrlUtil) : Runnable {
        override fun run() {
            val response = urlUtil.doGet(URI.create(uri))
            val byteArray = IOUtils.toByteArray(response.body())
            val file = File(savePath)
            val fos = FileOutputStream(file)
            IOUtils.write(byteArray, fos)
            response.body().close()
            fos.close()
        }
    }

    private fun filterSpecialChar(fileName: String): String {
        val pattern = Pattern.compile("[\\s\\\\/:\\*\\?\\\"<>\\|]")
        val matcher = pattern.matcher(fileName)
        return matcher.replaceAll("[char]") // 将匹配到的非法字符以空替换
    }
}
