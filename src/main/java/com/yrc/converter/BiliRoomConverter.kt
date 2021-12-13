package com.yrc.converter

import com.jayway.jsonpath.Configuration
import com.jayway.jsonpath.JsonPath
import com.jayway.jsonpath.Option
import com.yrc.pojo.Item
import com.yrc.utils.EmojiDataUtil
import com.yrc.utils.ImageDownloader

class BiliRoomConverter (val util:EmojiDataUtil,private var dirPath:String?){
    companion object{
        //获取表情包列表
        const val emojiListJsonPath = "$.data.data[1].emoticons"
    }
    val String.Companion.EMPTY:String
        get() = ""

    fun downloadEmojiById(roomId:Int) {

        val json = util.getRoomEmojiListByRoomId(roomId)

        //配置JsonPath，使其不会返回异常，找不到的元素返回空
        val conf = Configuration
            .builder()
            .options(Option.SUPPRESS_EXCEPTIONS)
            .options(Option.DEFAULT_PATH_LEAF_TO_NULL)
            .build()

        //解析json
        val document = JsonPath.parse(json,conf)
        val emojiList: List<JsonNode>
            = document.read(emojiListJsonPath)?: listOf()
        val imageList = emojiList.toMutableList()
            .map {
                val emoticonId = (it["emoticon_id"] ?: String.EMPTY).toString()
                val emoji = (it["emoji"] ?: String.EMPTY).toString()
                val url = (it["url"] ?: String.EMPTY).toString()
                Item(emoticonId, emoji, url)
            }
            .toList()

        //下载文件
        val savePath = dirPath ?: "./$roomId/"
        val imageDownloader = ImageDownloader(imageList, savePath)
        imageDownloader.startDownload()
    }
}