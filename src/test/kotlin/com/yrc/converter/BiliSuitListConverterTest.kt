package com.yrc.converter

import com.yrc.utils.UrlUtil
import org.apache.commons.io.IOUtils
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.nio.file.Paths
import kotlin.io.path.reader
import kotlin.test.assertEquals

internal class BiliSuitListConverterTest {
    val apiUrl = "https://api.bilibili.com/x/garb/mall/item/suit/v2"
    val mock: UrlUtil
    val converter: BiliSuitListConverter
    init {
        val file = Paths.get("src","test", "resources", "converter", "home.html")
        val html = IOUtils.toString(file.reader(Charsets.UTF_8))
        mock = Mockito.mock(UrlUtil::class.java)
        Mockito.`when`(mock.getString(apiUrl)).thenReturn(html)
        converter = BiliSuitListConverter(apiUrl, mock)
    }
    @Test
    fun getItemList(){
        val itemList = converter.getItems()
        assertEquals(159, itemList.size)
        val regexItemList = converter.getRegexItemList("鹿乃")
        assertEquals(1, regexItemList.size)
    }
}