package com.yrc.main

import com.yrc.converter.BiliSuitListConverter
import com.yrc.converter.printItemList
import com.yrc.converter.printRegexItemList
import com.yrc.service.BiliSuitService
import kotlinx.coroutines.runBlocking
import retrofit2.Retrofit
import retrofit2.create

class Main {

}
fun main(args: Array<String>) {
    val service = Retrofit.Builder()
        .baseUrl("https://api.bilibili.com/")
        .build()
        .create(BiliSuitService::class.java)

    val converter = BiliSuitListConverter(service)
    converter.printItemList()
//    converter.printRegexItemList("七海")
}

