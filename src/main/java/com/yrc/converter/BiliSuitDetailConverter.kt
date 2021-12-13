package com.yrc.converter

import com.jayway.jsonpath.Configuration
import com.jayway.jsonpath.JsonPath
import com.jayway.jsonpath.Option
import com.yrc.pojo.Item
import com.yrc.utils.EmojiDataUtil
import com.yrc.utils.ImageDownloader

class BiliSuitDetailConverter(private val util: EmojiDataUtil, private var dirPath:String?) {
    companion object{
        //获取套装名
        const val suitNameJsonPath = "$.data.item.name"
        //获取套装状态码
        const val suitStateJsonPath = "$.data.item.state"
        //获取套装表情包
        const val emojiListJsonPath = "$.data.suit_items.emoji_package[0].items"
        //表情包id
        const val itemIdJsonPath = "$.item_id"
        //表情包名
        const val nameJsonPath = "$.name"
        //表情包链接
        const val imageUrlJsonPath = "$.properties.image"
        //空间背景图
        const val spaceBgJsonPath = "$.data.suit_items.space_bg[0].properties"
        //背景图key正则
        const val spaceBgRegex = ".*portrait.*"

    }

    val String.Companion.EMPTY:String
        get() = ""

    fun downloadSuitById(suitId:Int){

        val json = util.getSuitDetailByItemId(suitId)

        //配置JsonPath，使其不会返回异常，找不到的元素返回空
        val conf = Configuration
            .builder()
            .options(Option.SUPPRESS_EXCEPTIONS)
            .options(Option.DEFAULT_PATH_LEAF_TO_NULL)
            .build()
        //解析json
        val document = JsonPath.parse(json,conf)
        val suitState:String = document.read(suitStateJsonPath)?:String.EMPTY
        val suitName:String = document.read(suitNameJsonPath)?:String.EMPTY
        if (suitState.isEmpty()) {
            println("无对应套装：id=$suitId")
            return
        } else if (suitState == "1") {
            println("套装失效：id=$suitId name=$suitName")
        } else {
            println("发现套装：id=$suitId name=$suitName")
        }
        //获取表情包
        val emojiRawList:List<JsonNode> = document.read(emojiListJsonPath)?: listOf()
        val emojiItemList = emojiRawList.map {
            val root = JsonPath.parse(it,conf)
            val itemId: String = root.read<Int?>(itemIdJsonPath)?.toString() ?: String.EMPTY
            val name: String = root.read(nameJsonPath) ?: String.EMPTY
            val image: String = root.read(imageUrlJsonPath) ?: String.EMPTY
            Item(itemId, name, image)
        }.toList()

        if(emojiItemList.isEmpty()){
            println("套装 $suitId 无表情包")
        }

        //获取套装背景
        val spaceBgMap:JsonNode = document.read(spaceBgJsonPath)?: mapOf()
        val spaceBgList = spaceBgMap.filter {
            it.key.matches(Regex(spaceBgRegex))
        }.map {
            Item(String.EMPTY, it.key, it.value.toString())
        }.toList()

        if(spaceBgList.isEmpty()){
            println("该套装无背景图")
        }

        //合并下载列表
        val itemList = emojiItemList + spaceBgList
        //下载文件
        val savePath = dirPath ?: "./$suitName/"
        val downloader = ImageDownloader(itemList.toMutableList(), savePath)
        downloader.startDownload()
    }
}

typealias JsonNode = Map<String, Any?>