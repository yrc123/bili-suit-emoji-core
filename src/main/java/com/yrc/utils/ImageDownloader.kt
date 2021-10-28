package com.yrc.utils

import com.yrc.pojo.Item
import com.yrc.service.sendGet
import java.io.File
import java.io.FileOutputStream
import java.net.URI
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern
import org.apache.commons.io.IOUtils

class ImageDownloader (private val items:List<Item>, private val dirPath:String){

     val poolExecutor: ThreadPoolExecutor by lazy {
		ThreadPoolExecutor(4,4,1, TimeUnit.HOURS, LinkedBlockingQueue());
     }
    fun startDownload(){
        val dir = File(dirPath)
        if(!dir.exists()){
            dir.mkdirs()
        }
        items.forEach {
            println("开始下载: ${it.name}")
            val imageDownloaderRunnable = ImageDownloaderRunnable("${dirPath}${filterSpecialChar(it.name)}.png", it.image)
            poolExecutor.execute(imageDownloaderRunnable)
        }
        poolExecutor.shutdown()
    }
    private class ImageDownloaderRunnable(val savePath:String, val uri:String):Runnable{
        override fun run() {
            val response = sendGet(URI.create(uri))
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

//    class ImageDownloader extends Thread {
//        @Override
//        public void run() {
//
//            FileOutputStream fos = null;
//            byte[] bytes = new byte[4098];
//            try {
//                fos = new FileOutputStream(file);
//                int n = 0;
//                while ((n = bis.read(bytes)) != -1)
//                    fos.write(bytes, 0, n);
//                bis.close();
//                fos.close();
//            } catch (FileNotFoundException e) {
//                System.out.println("输出文件夹不存在或权限错误");
//                e.printStackTrace();
//            } catch (IOException e) {
//                System.out.println("无法读入图片");
//                e.printStackTrace();
//            }
//        }
//    }
//
}