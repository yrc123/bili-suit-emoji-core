package com.yrc.service

import java.io.InputStream
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import org.apache.commons.io.IOUtils

class BiliSuitService {
    //套装详情页api
    fun getSuitDetailByItemId(itemId:Int):String{
        val uri = URI.create("https://api.bilibili.com/x/garb/mall/item/suit/v2?item_id=$itemId")
        val response = sendGet(uri)
        return IOUtils.toString(response.body(), Charsets.UTF_8)

    }
    //获取套装列表api
    fun getSuitList():String{
        val uri = URI.create("https://www.bilibili.com/h5/mall/home")
        val response = sendGet(uri)
        return IOUtils.toString(response.body(), Charsets.UTF_8)
    }
}
fun sendGet(uri:URI):HttpResponse<InputStream>{
    val request = HttpRequest
        .newBuilder()
        .GET()
        .uri(uri)
        .build()
    return HttpClient
        .newHttpClient()
        .send(request,
            HttpResponse.BodyHandlers.ofInputStream())
}
