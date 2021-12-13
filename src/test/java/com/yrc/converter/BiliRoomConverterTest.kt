package com.yrc.converter

import com.yrc.utils.EmojiDataUtil
import org.apache.commons.io.IOUtils
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import java.nio.file.Paths

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class BiliRoomConverterTest{
    val util:EmojiDataUtil
    val roomId:Int;
    init {
        val file = Paths.get("src", "test", "resources", "com", "yrc", "converter", "GetEmoticons.json").toFile()
        val json = IOUtils.toString(file.reader(Charsets.UTF_8))
        roomId = 21452505
        util = mock(EmojiDataUtil::class.java)
        `when`(util.getRoomEmojiListByRoomId(roomId))
            .thenReturn(json)
    }
    @Test
    fun downloadEmojiById() {
        var biliRoomConverter = BiliRoomConverter(util, null)
        biliRoomConverter.downloadEmojiById(roomId)
    }
}