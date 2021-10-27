package com.yrc.service

import org.apache.http.client.methods.HttpGet
import org.apache.http.client.utils.URIBuilder
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.util.EntityUtils

class BiliSuitService {
    //套装详情页api
    fun getSuitDetailByItemId(itemId:Int):String{
        val client: CloseableHttpClient? = HttpClientBuilder.create().build()
        val url = URIBuilder("https://api.bilibili.com/x/garb/mall/item/suit/v2")
            .addParameter("item_id", itemId.toString())
            .build()
        val httpGet = HttpGet(url)
        val entity = client
            ?.execute(httpGet)
            ?.entity
        return EntityUtils.toString(entity,"utf8")
    }
    //获取套装列表api
    fun getSuitList():String{
        val client: CloseableHttpClient? = HttpClientBuilder.create().build()
        val url = URIBuilder("https://www.bilibili.com/h5/mall/home")
            .build()
        val httpGet = HttpGet(url)
        val entity = client
            ?.execute(httpGet)
            ?.entity
        return EntityUtils.toString(entity,"utf8")
    }
}