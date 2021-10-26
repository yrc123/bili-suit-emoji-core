package com.yrc.service

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query

interface BiliSuitService {
    //套装详情页api
    @GET("https://api.bilibili.com/x/garb/mall/item/suit/v2")
    suspend fun getSuitDetailByItemId(@Query("item_id") itemId:Int):ResponseBody
    //获取套装列表api
    @GET("https://www.bilibili.com/h5/mall/home")
    suspend fun getSuitList():ResponseBody
}